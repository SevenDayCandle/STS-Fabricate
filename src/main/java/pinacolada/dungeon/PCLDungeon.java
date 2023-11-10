package pinacolada.dungeon;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.interfaces.delegates.FuncT3;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.augments.PCLDynamicAugmentData;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.listeners.OnAddToDeckListener;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.FakeLoadout;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.util.*;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class PCLDungeon implements CustomSavable<PCLDungeon>, PostDungeonInitializeSubscriber {
    private static final AbstractCard.CardRarity[] poolOrdering = AbstractCard.CardRarity.values();
    public static int DEFAULT_AUGMENT_CHANCE = 5;
    public final ArrayList<Integer> ascensionGlyphCounters = new ArrayList<>();
    public transient final ArrayList<PCLLoadout> loadouts = new ArrayList<>();
    private transient boolean panelAdded;
    private ArrayList<String> loadoutIDs = new ArrayList<>();
    private Integer highestScore = 0;
    private Integer rNGCounter = 0;
    private Map<String, String> eventLog = new HashMap<>();
    private Random rng;
    private String startingLoadout;
    private transient PCLPlayerData<?, ?, ?> data;
    private transient ArrayList<AbstractCard> anyColorCards;
    private transient boolean canJumpAnywhere;
    private transient boolean canJumpNextFloor;
    private transient boolean hasSummons;
    private transient Long lastSeed;
    private transient int valueDivisor = 1;
    public Boolean allowAugments = false;
    public Boolean allowCustomAugments = false;
    public Boolean allowCustomBlights = false;
    public Boolean allowCustomCards = false;
    public Boolean allowCustomPotions = false;
    public Boolean allowCustomRelics = false;
    public Integer augmentChance = DEFAULT_AUGMENT_CHANCE;
    public ArrayList<PCLAugment.SaveData> augmentList = new ArrayList<>();
    public HashSet<String> bannedAugments = new HashSet<>();
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public transient PCLLoadout loadout;

    public static AbstractCard.CardRarity getNextRarity(AbstractCard.CardRarity rarity) {
        int nextRarityIndex = Math.max(0, rarity.ordinal() - 1);
        return nextRarityIndex > 1 ? poolOrdering[nextRarityIndex] : null;
    }

    private static int getAugmentWeight(int tier) {
        return Math.max(1, (49 - (tier * tier)));
    }

    // When playing as a non-PCL character, remove any colorless cards that should be exclusive to a particular PCL character
    // This includes the example cards from the card editor
    public static boolean isColorlessCardExclusive(AbstractCard card) {
        return PGR.core.filterColorless(card) || EUIUtils.any(PGR.getRegisteredResources(), r -> r.filterColorless(card));
    }

    public static PCLDungeon register() {
        final PCLDungeon data = new PCLDungeon();
        BaseMod.addSaveField(PCLDungeon.class.getSimpleName(), data);
        BaseMod.subscribe(data);
        return data;
    }

    public void addAugment(PCLAugment.SaveData data) {
        augmentList.add(data);
        PGR.augmentPanel.flash();
    }

    public void addCard(AbstractCard card, AbstractCard.CardRarity rarity) {
        CardGroup pool = GameUtilities.getCardPool(rarity);
        if (pool != null && !EUIUtils.any(pool.group, c -> c.cardID.equals(card.cardID))) {
            pool.addToBottom(card);
        }
        CardGroup spool = GameUtilities.getCardPoolSource(rarity);
        if (spool != null && !EUIUtils.any(spool.group, c -> c.cardID.equals(card.cardID))) {
            spool.addToBottom(card);
        }
        bannedCards.remove(card.cardID);
    }

    public void addCard(String cardID) {
        AbstractCard c = CardLibrary.getCard(cardID);
        if (c != null) {
            addCard(c, c.rarity);
        }
    }

    public void addDivisor(int divisor) {
        valueDivisor = Math.max(1, valueDivisor + divisor);
    }

    public void addRelic(String relicID, AbstractRelic.RelicTier tier) {
        if (!AbstractDungeon.player.hasRelic(relicID)) {
            ArrayList<String> pool = GameUtilities.getRelicPool(tier);
            if (pool == null) {
                pool = AbstractDungeon.shopRelicPool;
            }
            if (pool.size() > 0 && !pool.contains(relicID)) {
                Random rng = AbstractDungeon.relicRng;
                if (rng == null) {
                    rng = PGR.dungeon.getRNG();
                }

                pool.add(rng.random(pool.size() - 1), relicID);
                bannedRelics.remove(relicID);
            }
        }
    }

    public void atBattleStart() {
        for (int i = 0; i < Math.min(PCLPlayerData.GLYPHS.size(), ascensionGlyphCounters.size()); i++) {
            int count = ascensionGlyphCounters.get(i);
            if (count > 0) {
                PCLPlayerData.GLYPHS.get(i).atBattleStart(count);
            }
        }
    }

    public void ban(String cardID) {
        final AbstractCard card = CardLibrary.getCard(cardID);
        if (card == null) {
            return;
        }

        removeCardFromPools(card);
        bannedCards.add(card.cardID);
        log("Banned " + card.cardID + ", Total: " + bannedCards.size());
    }

    public void banRelic(String relicID) {
        removeRelic(relicID);
        bannedRelics.add(relicID);
        log("Banned " + relicID + ", Total: " + bannedRelics.size());
    }

    public boolean canAugmentSpawn(PCLAugmentData aug, int tier) {
        switch (aug.category) {
            case Special:
            case Hindrance:
                return false;
            case Summon:
                return hasSummons;
        }
        return true;
    }

    public boolean canJumpAnywhere() {
        return canJumpAnywhere;
    }

    public boolean canJumpNextFloor() {
        return canJumpNextFloor;
    }

    public boolean canObtainCopy(AbstractCard card) {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies >= 0) {
            return GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size() < pclCard.cardData.maxCopies;
        }
        return true;
    }

    private void fullLog(String message) {
        EUIUtils.logInfo(this, message);
        if (Settings.isDebug && loadout != null) {
            EUIUtils.logInfo(this, "Starting Series: " + loadout.getName() + ", Preset: " + loadout.preset);
            EUIUtils.logInfo(this, "Loadout ID: " + startingLoadout + ", Banned Cards: " + bannedCards.size());
        }
    }

    public AbstractCard getAnyColorRewardCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type) {
        return getAnyColorRewardCard(rarity, type, false, false);
    }

    public AbstractCard getAnyColorRewardCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean allowOtherRarities, boolean allowHealing) {
        ArrayList<AbstractCard> available = getAnyColorRewardCards(rarity, type, allowHealing);
        if (!available.isEmpty()) {
            return GameUtilities.getRandomElement(available, AbstractDungeon.cardRng);
        }
        else if (allowOtherRarities && rarity != null) {
            EUIUtils.logInfo(null, "No cards found for Rarity " + rarity + ", Type " + type);
            int nextRarityIndex = Math.max(0, rarity.ordinal() - 1);
            return getAnyColorRewardCard(nextRarityIndex > 1 ? poolOrdering[nextRarityIndex] : null, type, allowOtherRarities, allowHealing);
        }
        else {
            return null;
        }
    }

    public ArrayList<AbstractCard> getAnyColorRewardCards(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean allowHealing) {
        if (anyColorCards == null) {
            anyColorCards = new ArrayList<>();
            for (AbstractCard c : EUIGameUtils.getEveryColorCardForPoolDisplay()) {
                // Prevent replaced cards from appearing in rewards
                if ((data == null ? PGR.core : data.resources).getReplacement(c.cardID) == null &&
                        (allowHealing || GameUtilities.isObtainableInCombat(c)) &&
                        (rarity == null || c.rarity == rarity) &&
                        ((type == null || c.type == type))) {
                    anyColorCards.add(c);
                }
            }
        }

        return anyColorCards;
    }

    public int getAugmentTotal() {
        return augmentList.size();
    }

    public int getCurrentHealth(AbstractPlayer player) {
        return player.currentHealth / valueDivisor;
    }

    public PCLPlayerData<?,?,?> getPlayerData() {return data;}

    public int getDivisor() {
        return valueDivisor;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public String getMapData(String eventID) {
        return (eventLog != null ? eventLog.getOrDefault(eventID, null) : null);
    }

    public int getMaxHealth(AbstractPlayer player) {
        return player.maxHealth / valueDivisor;
    }

    public Random getRNG() {
        if (rng == null || !Objects.equals(lastSeed, Settings.seed)) {
            lastSeed = Settings.seed != null ? Settings.seed : 0;
            rng = new Random(lastSeed);
            rng.setCounter(rNGCounter);
        }

        return rng;
    }

    /*
     * Obtain a random augment with the specified tier restrictions. Higher tier augments will appear less often
     * */
    public PCLAugment getAugment(FuncT2<Boolean, PCLAugmentData, Integer> evalFunc) {
        WeightedList<PCLAugment> weightedList = getAugmentsChoices(evalFunc);
        return weightedList.retrieve(AbstractDungeon.treasureRng, false);
    }

    public ArrayList<PCLAugment> getAugments(FuncT2<Boolean, PCLAugmentData, Integer> evalFunc, int picks) {
        WeightedList<PCLAugment> weightedList = getAugmentsChoices(evalFunc);
        ArrayList<PCLAugment> ret = new ArrayList<>();
        for (int i = 0; i < picks; i++) {
            ret.add(weightedList.retrieve(AbstractDungeon.treasureRng, true));
        }
        return ret;
    }

    private WeightedList<PCLAugment> getAugmentsChoices(FuncT2<Boolean, PCLAugmentData, Integer> evalFunc) {
        WeightedList<PCLAugment> weightedList = new WeightedList<>();
        for (PCLAugmentData data : PCLAugmentData.getAvailable()) {
            if (!bannedAugments.contains(data.ID)) {
                if (data.branchFactor > 0) {
                    getAugmentsChoicesBranching(weightedList, evalFunc, data, 0, 0);
                }
                else {
                    for (int i = 0; i < data.maxForms; i++) {
                        for (int j = 0; j < data.maxUpgradeLevel; j++) {
                            int tier = data.getTier(i) + data.getTierUpgrade(i) * j;
                            if (evalFunc.invoke(data, tier)) {
                                weightedList.add(data.create(i, j), getAugmentWeight(tier));
                            }
                        }
                    }
                }
            }
        }
        if (allowCustomAugments) {
            for (PCLCustomAugmentSlot slot : PCLCustomAugmentSlot.getAugments()) {
                if (!bannedAugments.contains(slot.ID)) {
                    if (slot.branchUpgradeFactor > 0) {
                        getAugmentsChoicesBranching(weightedList, evalFunc, slot, 0, 0);
                    }
                    else {
                        for (int i = 0; i < slot.builders.size(); i++) {
                            PCLDynamicAugmentData data = slot.getBuilder(i);
                            if (data != null) {
                                for (int j = 0; j < data.maxUpgradeLevel; j++) {
                                    int tier = data.getTier(i) + data.getTierUpgrade(i) * j;
                                    if (evalFunc.invoke(data, tier)) {
                                        weightedList.add(data.create(i, j), getAugmentWeight(tier));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return weightedList;
    }

    private void getAugmentsChoicesBranching(WeightedList<PCLAugment> weightedList, FuncT2<Boolean, PCLAugmentData, Integer> evalFunc, PCLAugmentData data, int form, int upgrade) {
        for (int i = form; i < Math.min(form + data.branchFactor, data.maxForms); i++) {
            int tier = data.getTier(i) + data.getTierUpgrade(i) * upgrade;
            if (evalFunc.invoke(data, tier)) {
                weightedList.add(data.create(i, upgrade), getAugmentWeight(tier));
            }
        }
        int minFormNext = GridCardSelectScreenPatches.getFormMin(form, data.branchFactor, upgrade);
        if (minFormNext < data.maxForms) {
            getAugmentsChoicesBranching(weightedList, evalFunc, data, minFormNext, upgrade + 1);
        }
    }

    private void getAugmentsChoicesBranching(WeightedList<PCLAugment> weightedList, FuncT2<Boolean, PCLAugmentData, Integer> evalFunc, PCLCustomAugmentSlot slot, int form, int upgrade) {
        for (int i = form; i < Math.min(form + slot.branchUpgradeFactor, slot.builders.size()); i++) {
            PCLDynamicAugmentData data = slot.getBuilder(i);
            if (data != null) {
                int tier = data.getTier(i) + data.getTierUpgrade(i) * upgrade;
                if (evalFunc.invoke(data, tier)) {
                    weightedList.add(data.create(i, upgrade), getAugmentWeight(tier));
                }
            }
        }
        int minFormNext = GridCardSelectScreenPatches.getFormMin(form, slot.branchUpgradeFactor, upgrade);
        if (minFormNext < slot.builders.size()) {
            getAugmentsChoicesBranching(weightedList, evalFunc, slot, minFormNext, upgrade + 1);
        }
    }

    public AbstractCard getRandomCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type) {
        return getRandomCard(rarity, type, AbstractDungeon.cardRng, false);
    }

    public AbstractCard getRandomCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type, Random rng, boolean allowOtherRarities) {
        return getRandomCard(rarity, c -> c.type == type && canObtainCopy(c), rng, allowOtherRarities);
    }

    public AbstractCard getRandomCard(AbstractCard.CardRarity rarity, Random rng, boolean allowOtherRarities) {
        return getRandomCard(rarity, this::canObtainCopy, rng, allowOtherRarities);
    }

    public AbstractCard getRandomCard(AbstractCard.CardRarity rarity, FuncT1<Boolean, AbstractCard> filterFunc, Random rng, boolean allowOtherRarities) {
        CardGroup pool = GameUtilities.getCardPool(rarity);
        if (pool != null) {
            AbstractCard c = getRandomCardFromPool(pool, filterFunc, rng);
            if (!allowOtherRarities || c != null) {
                return c;
            }
            // Try to get a card from a different rarity pool. If you exhaust all the pools, fall back on the colorless pool
            // Note that the basic and special rarities have no pools so we ignore them
            if (rarity != null) {
                EUIUtils.logInfoIfDebug(this, "No cards found for Rarity " + rarity);
                return getRandomCard(getNextRarity(rarity), filterFunc, rng, allowOtherRarities);
            }
        }
        return null;
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool) {
        return getRandomCardFromPool(pool, this::canObtainCopy, AbstractDungeon.cardRng);
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool, FuncT1<Boolean, AbstractCard> filterFunc, Random rng) {
        ArrayList<AbstractCard> choices = EUIUtils.filter(pool.group, filterFunc);
        return rng != null ? GameUtilities.getRandomElement(choices, rng) : GameUtilities.getTrulyRandomElement(choices);
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool, AbstractCard.CardType type) {
        return getRandomCardFromPool(pool, c -> c.type == type && canObtainCopy(c), AbstractDungeon.cardRng);
    }

    public AbstractCard getRandomRewardReplacementCard(AbstractCard.CardRarity rarity, FuncT1<Boolean, AbstractCard> filterFunc, Random rng, boolean allowOtherRarities) {
        AbstractCard replacement = null;
        boolean searchingCard = true;

        while (searchingCard) {
            final AbstractCard temp = getRandomCard(rarity, c -> filterFunc.invoke(c) && canObtainCopy(c), rng, allowOtherRarities);
            if (temp == null) {
                break;
            }

            searchingCard = tryCancelCardReward(temp);

            if (!searchingCard) {
                replacement = temp.makeCopy();
            }
        }

        for (AbstractRelic r : player.relics) {
            r.onPreviewObtainCard(replacement);
        }

        return replacement;
    }

    public boolean hasSummons() {
        return hasSummons;
    }

    private void importBaseData(PCLDungeon dungeon) {
        loadouts.clear();
        bannedAugments.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augmentList.clear();
        ascensionGlyphCounters.clear();
        valueDivisor = 1;
        if (dungeon != null) {
            eventLog = new HashMap<>(dungeon.eventLog);
            allowAugments = dungeon.allowAugments;
            allowCustomAugments = dungeon.allowCustomAugments;
            allowCustomBlights = dungeon.allowCustomBlights;
            allowCustomCards = dungeon.allowCustomCards;
            allowCustomPotions = dungeon.allowCustomPotions;
            allowCustomRelics = dungeon.allowCustomRelics;
            augmentChance = dungeon.augmentChance;
            rNGCounter = dungeon.rNGCounter;
            highestScore = dungeon.highestScore;
            ascensionGlyphCounters.addAll(dungeon.ascensionGlyphCounters);
            rng = dungeon.rng;
            bannedAugments.addAll(dungeon.bannedAugments);
            bannedCards.addAll(dungeon.bannedCards);
            bannedRelics.addAll(dungeon.bannedRelics);
            augmentList.addAll(dungeon.augmentList);
            if (this.data != null) {
                loadout = PCLLoadout.get(dungeon.startingLoadout);
                for (String proxy : dungeon.loadoutIDs) {
                    PCLLoadout loadout = PCLLoadout.get(proxy);
                    if (loadout != null) {
                        loadouts.add(loadout);
                    }
                }
            }
        }
        else {
            eventLog = new HashMap<>();
            allowCustomAugments = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowAugments;
            allowAugments = allowCustomAugments || (data != null && data.useAugments);
            augmentChance = CardCrawlGame.trial instanceof PCLCustomTrial ? ((PCLCustomTrial) CardCrawlGame.trial).augmentChance : PGR.config.augmentChance.get();
            allowCustomBlights = PGR.config.enableCustomBlights.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomBlights);
            allowCustomCards = PGR.config.enableCustomCards.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards);
            allowCustomPotions = PGR.config.enableCustomPotions.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomPotions);
            allowCustomRelics = PGR.config.enableCustomRelics.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomRelics);
            highestScore = 0;
            rNGCounter = 0;

            // TODO add ascension glyph information to ascension panel tooltip
            rng = null;
        }
    }

    // Also handle adding banned augments here
    public void initializeCardPool() {
        final AbstractPlayer player = AbstractDungeon.player;
        final ArrayList<CardGroup> groups = new ArrayList<>();
        groups.addAll(EUIGameUtils.getGameCardPools());
        groups.addAll(EUIGameUtils.getSourceCardPools());

        if (CardCrawlGame.trial instanceof PCLCustomTrial) {
            bannedCards.addAll(((PCLCustomTrial) CardCrawlGame.trial).bannedCards);
            bannedAugments.addAll(((PCLCustomTrial) CardCrawlGame.trial).bannedAugments);
        }
        else if (data != null) {
            bannedCards.addAll(data.config.bannedCards.get());
        }

        if (data != null) {
            HashSet<String> replaced = new HashSet<>();
            ArrayList<AbstractCard> toAdd = new ArrayList<>();
            for (CardGroup group : groups) {
                replaced.clear();
                toAdd.clear();
                for (AbstractCard c : group.group) {
                    String replacement = data.resources.getReplacement(c.cardID);
                    if (replacement != null) {
                        replaced.add(c.cardID);
                        toAdd.add(CardLibrary.getCard(replacement));
                    }
                }
                group.group.addAll(toAdd);
                group.group.removeIf(card ->
                {
                    if (!bannedCards.contains(card.cardID) && !replaced.contains(card.cardID)) {
                        if (GameUtilities.isColorlessCardColor(card.color)) {
                            return !data.resources.containsColorless(card);
                        }
                        // Prevent removal of cards added by "multiclass" relics from Replay the Spire
                        else if (card.color != data.resources.cardColor || loadouts.isEmpty()) {
                            return false;
                        }
                        for (PCLLoadout loadout1 : loadouts) {
                            if (loadout1.isCardFromLoadout(card.cardID)) {
                                return false;
                            }
                        }
                    }
                    return true;
                });
            }

            String[] additional = data.getAdditionalCardIDs(allowCustomCards);
            if (additional != null) {
                for (String s : additional) {
                    if (!bannedCards.contains(s)) {
                        AbstractCard c = CardLibrary.getCard(s);
                        if (c != null) {
                            if (GameUtilities.isColorlessCardColor(c.color)) {
                                AbstractDungeon.srcColorlessCardPool.addToBottom(c);
                                AbstractDungeon.colorlessCardPool.addToBottom(c);
                            }
                            else {
                                CardGroup pool = GameUtilities.getCardPool(c.rarity);
                                if (pool != null) {
                                    pool.addToBottom(c);
                                }
                                CardGroup spool = GameUtilities.getCardPoolSource(c.rarity);
                                if (spool != null) {
                                    spool.addToBottom(c);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            for (CardGroup group : groups) {
                group.group.removeIf(card -> bannedCards.contains(card.cardID) || isColorlessCardExclusive(card));
            }
        }

        if (allowCustomCards) {
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(player.getCardColor())) {
                if (!bannedCards.contains(c.ID)) {
                    AbstractCard.CardRarity rarity = c.getFirstBuilder().cardRarity;
                    CardGroup pool = GameUtilities.getCardPool(rarity);
                    if (pool != null) {
                        pool.addToBottom(c.make());
                        EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to pool " + rarity);
                    }
                    CardGroup spool = GameUtilities.getCardPoolSource(rarity);
                    if (spool != null) {
                        spool.addToBottom(c.make());
                        EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to source pool " + rarity);
                    }
                }
            }
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                if (!bannedCards.contains(c.ID)) {
                    AbstractCard.CardRarity rarity = c.getFirstBuilder().cardRarity;
                    switch (rarity) {
                        case COMMON:
                        case UNCOMMON:
                        case RARE:
                            AbstractDungeon.srcColorlessCardPool.addToBottom(c.getFirstBuilder().create());
                            AbstractDungeon.colorlessCardPool.addToBottom(c.getFirstBuilder().create());
                            break;
                        case CURSE:
                            AbstractDungeon.srcCurseCardPool.addToBottom(c.getFirstBuilder().create());
                            AbstractDungeon.curseCardPool.addToBottom(c.getFirstBuilder().create());
                    }
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to Colorless pool");
                }
            }
        }

        hasSummons = EUIUtils.any(groups, g -> EUIUtils.any(g.group, c -> c.type == PCLEnum.CardType.SUMMON));
    }

    private void initializeCharacterBlight(String id) {
        for (AbstractBlight blight : player.blights) {
            if (blight.blightID.equals(id)) {
                return;
            }
        }
        AbstractBlight blight = BlightHelper.getBlight(id);
        if (blight != null) {
            GameUtilities.obtainBlightWithoutEffect(blight);
        }
    }

    public void initializeData() {
        loadouts.clear();
        final AbstractPlayer player = AbstractDungeon.player;
        data = PGR.getPlayerData(player.chosenClass);

        // Add or remove the augment panel
        if (allowAugments && !panelAdded) {
            panelAdded = true;
            BaseMod.addTopPanelItem(PGR.augmentPanel);
        }
        else if (panelAdded) {
            panelAdded = false;
            BaseMod.removeTopPanelItem(PGR.augmentPanel);
        }

        if (data != null) {
            // Always include the selected loadout. If for some reason none exists, assign one at random
            if (data.selectedLoadout == null) {
                data.selectedLoadout = EUIUtils.random(EUIUtils.filter(data.getEveryLoadout(), loadout -> data.resources.getUnlockLevel() >= loadout.unlockLevel));
            }
            loadout = data.selectedLoadout;

            for (PCLLoadout l : data.getEveryLoadout()) {
                // Series must be unlocked and enabled to be present in-game
                if (l == loadout || l.isEnabled()) {
                    loadouts.add(l);
                }
            }

            // Glyph settings
            for (AbstractGlyph glyph : PCLPlayerData.GLYPHS) {
                ascensionGlyphCounters.add(glyph.configOption.get());
            }
        }

        // Custom loadout
        if (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).fakeLoadout != null) {
            loadout = ((PCLCustomTrial) CardCrawlGame.trial).fakeLoadout;
        }
    }

    // Modify starting potion slots and energy
    private void initializePotions() {
        if (loadout != null) {
            player.potionSlots += loadout.getPotionSlots();
            while (player.potions.size() > player.potionSlots && player.potions.get(player.potions.size() - 1) instanceof PotionSlot) {
                player.potions.remove(player.potions.size() - 1);
            }
            while (player.potionSlots > player.potions.size()) {
                player.potions.add(new PotionSlot(player.potions.size()));
            }
            player.adjustPotionPositions();
        }
    }

    // Add custom relics if applicable. Note that this is loaded in AbstractDungeonPatches_InitializeRelicList
    public void initializeRelicPool(AbstractPlayer player) {
        if (CardCrawlGame.trial instanceof PCLCustomTrial) {
            bannedRelics.addAll(((PCLCustomTrial) CardCrawlGame.trial).bannedRelics);
        }
        else if (data != null) {
            bannedRelics.addAll(data.config.bannedRelics.get());
        }

        if (data != null) {
            for (ArrayList<String> relicPool : EUIGameUtils.getGameRelicPools()) {
                relicPool.removeIf(relic -> {
                    if (bannedRelics.contains(relic)) {
                        return true;
                    }
                    for (PCLLoadout loadout : data.loadouts.values()) {
                        if (loadout.isRelicFromLoadout(relic) && (!loadout.isEnabled())) {
                            return true;
                        }
                    }
                    return false;
                });
            }

            String[] additional = data.getAdditionalRelicIDs(allowCustomRelics);
            if (additional != null) {
                for (String id : additional) {
                    if (!bannedRelics.contains(id)) {
                        AbstractRelic r = RelicLibrary.getRelic(id);
                        // Circlet means that the relic didn't exist
                        if (!(r instanceof Circlet)) {
                            PGR.dungeon.addRelic(id, r.tier);
                        }
                    }
                }
            }
        }
        else {
            for (ArrayList<String> relicPool : EUIGameUtils.getGameRelicPools()) {
                relicPool.removeIf(relic -> bannedRelics.contains(relic));
            }
        }

        if (allowCustomRelics) {
            for (PCLCustomRelicSlot c : PCLCustomRelicSlot.getRelics(player.getCardColor())) {
                loadCustomRelicImpl(c);
            }
            for (PCLCustomRelicSlot c : PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS)) {
                loadCustomRelicImpl(c);
            }
        }
    }

    // Add custom cards if applicable

    private void initializeRun() {
        if (loadout instanceof FakeLoadout) {
            for (LoadoutRelicSlot rSlot : loadout.getPreset().relicSlots) {
                if (rSlot.selected != null) {
                    GameUtilities.obtainRelicFromEvent(RelicLibrary.getRelic(rSlot.selected).makeCopy());
                }
            }

            initializePotions();
        }
        else if (data != null) {
            if (Settings.isStandardRun()) {
                data.saveStats();
            }

            // Add character blights if applicable
            for (String id : loadout.getStartingBlights()) {
                initializeCharacterBlight(id);
            }

            initializePotions();
        }
    }

    // Add custom relics if applicable. Note that this is loaded in PotionPoolPatches
    public void loadCustomPotions(ArrayList<String> result, AbstractPlayer.PlayerClass p, boolean getAll) {
        if (allowCustomPotions || getAll) {
            if (getAll) {
                for (PCLCustomPotionSlot c : PCLCustomPotionSlot.getPotions()) {
                    result.add(c.ID);
                }
            }
            else {
                for (PCLCustomPotionSlot c : PCLCustomPotionSlot.getPotions(player.getCardColor())) {
                    result.add(c.ID);
                }
                for (PCLCustomPotionSlot c : PCLCustomPotionSlot.getPotions(AbstractCard.CardColor.COLORLESS)) {
                    result.add(c.ID);
                }
            }
        }
    }

    private void loadCustomRelicImpl(PCLCustomRelicSlot c) {
        AbstractRelic.RelicTier tier = c.getFirstBuilder().tier;
        ArrayList<String> relicPool = GameUtilities.getRelicPool(tier);
        if (relicPool != null && !relicPool.contains(c.ID) && !bannedRelics.contains(c.ID)) {
            relicPool.add(c.ID);
            EUIUtils.logInfoIfDebug(this, "Added Custom Relic " + c.ID + " to pool " + tier);
        }
    }

    private void log(String message) {
        EUIUtils.logInfoIfDebug(this, message);
    }

    public void onCardObtained(AbstractCard card) {
        if (card instanceof PCLCard && ((PCLCard) card).isUnique()) {
            AbstractCard first = null;
            final ArrayList<AbstractCard> toRemove = new ArrayList<>();
            final ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
            for (AbstractCard c : cards) {
                if (c.cardID.equals(card.cardID)) {
                    if (first == null) {
                        first = c;
                    }
                    else {
                        toRemove.add(c);
                        for (int i = 0; i <= c.timesUpgraded; i++) {
                            first.upgrade();
                        }
                    }
                }
            }

            for (AbstractCard c : toRemove) {
                cards.remove(c);
            }

            if (first != null && toRemove.size() > 0 && PCLEffects.TopLevelQueue.count() < 5) {
                PCLEffects.TopLevelQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 4f, (float) Settings.HEIGHT / 2f));
                PCLEffects.TopLevelQueue.showCardBriefly(first.makeStatEquivalentCopy(), (float) Settings.WIDTH / 4f, (float) Settings.HEIGHT / 2f);
            }
        }
    }

    @Override
    public void onLoad(PCLDungeon loaded) {
        AbstractPlayer player = AbstractDungeon.player;
        this.data = PGR.getPlayerData(player != null ? player.chosenClass : null);
        importBaseData(loaded);

        if (loadout == null && this.data != null) {
            loadout = this.data.selectedLoadout;
        }
        validate();

        fullLog("ON LOAD");
    }

    @Override
    public PCLDungeon onSave() {
        loadoutIDs.clear();

        if (data != null) {
            for (PCLLoadout loadout : loadouts) {
                loadoutIDs.add(loadout.ID);
            }

            if (loadout != null) {
                startingLoadout = loadout.ID;
            }
            else {
                startingLoadout = data.selectedLoadout.ID;
            }
        }

        validate();

        fullLog("ON SAVE");


        return this;
    }

    @Override
    public void receivePostDungeonInitialize() {
        // Gets called after receiveStartGame
        initializeRun();
    }

    public void removeAugment(PCLAugment.SaveData data) {
        augmentList.remove(data);
        PGR.augmentPanel.flash();
    }

    private void removeCardFromPools(AbstractCard card) {
        final AbstractCard.CardRarity rarity = card.color == AbstractCard.CardColor.COLORLESS ? null : card.rarity;
        final CardGroup srcPool = GameUtilities.getCardPoolSource(rarity);
        if (srcPool != null) {
            srcPool.removeCard(card.cardID);
        }
        final CardGroup pool = GameUtilities.getCardPool(rarity);
        if (pool != null) {
            pool.removeCard(card.cardID);
        }
    }

    public void removeRelic(String relicID) {
        final ArrayList<String> pool = GameUtilities.getRelicPool(RelicLibrary.getRelic(relicID).tier);
        if (pool != null) {
            pool.remove(relicID);
        }
        else {
            for (ArrayList<String> list : EUIGameUtils.getGameRelicPools()) {
                list.remove(relicID);
            }
        }
    }

    public void reset() {
        fullLog("RESETTING...");

        importBaseData(null);
        loadouts.clear();
        bannedAugments.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augmentList.clear();
        loadout = new FakeLoadout();
        startingLoadout = loadout.ID;
        loadoutIDs.clear();
        valueDivisor = 1;
        anyColorCards = null;

        validate();
    }

    public void setDivisor(int divisor) {
        valueDivisor = Math.max(1, divisor);
    }

    public void setJumpAnywhere(boolean value) {
        canJumpAnywhere = value;
    }

    public void setJumpNextFloor(boolean value) {
        canJumpNextFloor = value;
    }

    public void setMapData(String eventID, Object value) {
        eventLog.put(eventID, value.toString());
    }

    // TODO check master deck
    public boolean tryCancelCardReward(AbstractCard temp) {
        for (AbstractRelic r : player.relics) {
            if (r instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) r).shouldCancel(temp)) {
                return true;
            }
        }
        for (AbstractBlight r : player.blights) {
            if (r instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) r).shouldCancel(temp)) {
                return true;
            }
        }
        return false;
    }

    public boolean tryCreateAugmentReward(ArrayList<RewardItem> rewards) {
        return tryCreateAugmentReward(rewards, augmentChance);
    }

    public boolean tryCreateAugmentReward(ArrayList<RewardItem> rewards, float chance) {
        if (allowAugments && GameUtilities.chance(chance)) {
            PCLAugment found = getAugment(this::canAugmentSpawn);
            if (found != null) {
                rewards.add(new AugmentReward(found));
                return true;
            }
        }
        return false;
    }

    public boolean tryObtainCard(AbstractCard card) {
        boolean canAdd = !(card instanceof OnAddToDeckListener) || ((OnAddToDeckListener) card).onAddToDeck(card);

        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof OnAddToDeckListener) {
                canAdd &= ((OnAddToDeckListener) relic).onAddToDeck(card);
            }
        }

        return canAdd;
    }

    public void updateHighestScore(int newCombo) {
        highestScore = Math.max(highestScore, newCombo);
    }

    protected void validate() {
        if (eventLog == null) {
            eventLog = new HashMap<>();
        }

        if (rng != null) {
            rNGCounter = rng.counter;
        }
        else if (rNGCounter == null) {
            rNGCounter = 0;
        }

        if (allowCustomAugments == null) {
            allowCustomAugments = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowAugments;
        }

        if (allowAugments == null) {
            allowAugments = allowCustomAugments || (data != null && data.useAugments);
        }

        if (allowCustomBlights == null) {
            allowCustomBlights = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomBlights;
        }

        if (allowCustomCards == null) {
            allowCustomCards = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards;
        }

        if (allowCustomPotions == null) {
            allowCustomPotions = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomPotions;
        }

        if (allowCustomRelics == null) {
            allowCustomRelics = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomRelics;
        }

        if (augmentChance == null) {
            augmentChance = CardCrawlGame.trial instanceof PCLCustomTrial ? ((PCLCustomTrial) CardCrawlGame.trial).augmentChance : PGR.config.augmentChance.get();
        }

        if (highestScore == null) {
            highestScore = 0;
        }
    }
}
