package pinacolada.dungeon;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.PreStartGameSubscriber;
import basemod.interfaces.StartActSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.listeners.OnAddToDeckListener;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.FakeLoadout;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class PCLDungeon implements CustomSavable<PCLDungeon>, PreStartGameSubscriber, StartGameSubscriber, StartActSubscriber {
    public static final AbstractCard.CardRarity[] poolOrdering = AbstractCard.CardRarity.values();
    public final ArrayList<Integer> ascensionGlyphCounters = new ArrayList<>();
    public transient final ArrayList<PCLLoadout> loadouts = new ArrayList<>();
    private transient boolean panelAdded;
    private transient int totalAugmentCount = 0;
    protected ArrayList<String> loadoutIDs = new ArrayList<>();
    protected Integer highestScore = 0;
    protected Integer rNGCounter = 0;
    protected Map<String, String> eventLog = new HashMap<>();
    protected Random rng;
    protected String startingLoadout;
    protected transient AbstractPlayerData<?, ?> data;
    protected transient ArrayList<AbstractCard> anyColorCards;
    protected transient boolean canJumpAnywhere;
    protected transient boolean canJumpNextFloor;
    protected transient int valueDivisor = 1;
    public Boolean allowAugments = false;
    public Boolean allowCustomCards = false;
    public Boolean allowCustomPotions = false;
    public Boolean allowCustomRelics = false;
    public HashMap<PCLAffinity, Integer> fragments = new HashMap<>();
    public HashMap<String, Integer> augments = new HashMap<>();
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public transient PCLLoadout loadout;

    public static AbstractCard.CardRarity getNextRarity(AbstractCard.CardRarity rarity) {
        int nextRarityIndex = Math.max(0, rarity.ordinal() - 1);
        return nextRarityIndex > 1 ? poolOrdering[nextRarityIndex] : null;
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

    public void addAugment(String id, int count) {
        augments.merge(id, count, Integer::sum);
        totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);
        if (augments.get(id) <= 0) {
            augments.remove(id);
        }
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

    public void ban(String cardID) {
        final AbstractCard card = CardLibrary.getCard(cardID);
        if (card == null) {
            return;
        }

        removeCardFromPools(card);
        bannedCards.add(card.cardID);
        log("Banned " + card.cardID + ", Total: " + bannedCards.size());
    }

    private void banItems(AbstractPlayerData<?, ?> data) {
        final ArrayList<CardGroup> groups = new ArrayList<>();
        groups.addAll(EUIGameUtils.getGameCardPools());
        groups.addAll(EUIGameUtils.getSourceCardPools());

        if (CardCrawlGame.trial instanceof PCLCustomTrial) {
            bannedCards.addAll(((PCLCustomTrial) CardCrawlGame.trial).bannedCards);
            bannedRelics.addAll(((PCLCustomTrial) CardCrawlGame.trial).bannedRelics);
        }
        else if (data != null) {
            bannedCards.addAll(data.config.bannedCards.get());
            bannedRelics.addAll(data.config.bannedRelics.get());
        }

        if (data != null) {
            for (CardGroup group : groups) {
                group.group.removeIf(card ->
                {
                    if (!bannedCards.contains(card.cardID)) {
                        if (GameUtilities.isColorlessCardColor(card.color)) {
                            if (data.resources.containsColorless(card)) {
                                for (PCLLoadout loadout : data.loadouts.values()) {
                                    if (loadout.isCardFromLoadout(card.cardID) && loadout.isLocked()) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                            return true;
                        }
                        else if (card.color != data.resources.cardColor || loadouts.isEmpty()) {
                            return false;
                        }
                        for (PCLLoadout loadout : loadouts) {
                            if (loadout.isCardFromLoadout(card.cardID)) {
                                return false;
                            }
                        }
                    }
                    return true;
                });
            }
        }
        else {
            for (CardGroup group : groups) {
                group.group.removeIf(card -> bannedCards.contains(card.cardID) || isColorlessCardExclusive(card));
            }
        }

        for (ArrayList<String> relicPool : EUIGameUtils.getGameRelicPools()) {
            relicPool.removeIf(relic -> bannedRelics.contains(relic));
        }
    }

    public void banRelic(String relicID) {
        removeRelic(relicID);
        bannedRelics.add(relicID);
        log("Banned " + relicID + ", Total: " + bannedRelics.size());
    }

    public boolean canJumpAnywhere() {
        return canJumpAnywhere;
    }

    public boolean canJumpNextFloor() {
        return canJumpNextFloor;
    }

    public boolean canObtainCopy(AbstractCard card) {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies > 0) {
            return GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size() < pclCard.cardData.maxCopies;
        }
        return true;
    }

    public boolean canObtainMoreCopies(AbstractCard c) {
        if (c instanceof PCLCard) {
            final int copies = GameUtilities.getAllCopies(c.cardID, AbstractDungeon.player.masterDeck).size();
            return copies < ((PCLCard) c).cardData.maxCopies;
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
            return GameUtilities.getRandomElement(available);
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
        return totalAugmentCount;
    }

    public HashMap<PCLAugmentCategory, Integer> getAugmentTotals() {
        HashMap<PCLAugmentCategory, Integer> counts = new HashMap<>();
        for (String key : augments.keySet()) {
            counts.merge(PCLAugmentData.get(key).category, augments.get(key), Integer::sum);
        }
        return counts;
    }

    public int getCurrentHealth(AbstractPlayer player) {
        return player.currentHealth / valueDivisor;
    }

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
        if (rng == null) {
            rng = new Random(Settings.seed);
            rng.setCounter(rNGCounter);
        }

        return rng;
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
                EUIUtils.logInfo(null, "No cards found for Rarity " + rarity);
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

    public AbstractCard getRandomRewardReplacementCard(AbstractCard.CardRarity rarity, ArrayList<AbstractCard> ignore, Random rng, boolean allowOtherRarities) {
        AbstractCard replacement = null;
        boolean searchingCard = true;

        while (searchingCard) {
            final AbstractCard temp = getRandomCard(rarity, c -> !(EUIUtils.any(ignore, i -> i.cardID.equals(c.cardID))) && canObtainCopy(c), rng, allowOtherRarities);
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

    protected void importBaseData(PCLDungeon dungeon) {
        ascensionGlyphCounters.clear();
        if (dungeon != null) {
            eventLog = new HashMap<>(dungeon.eventLog);
            allowCustomCards = dungeon.allowCustomCards;
            allowCustomPotions = dungeon.allowCustomPotions;
            allowCustomRelics = dungeon.allowCustomRelics;
            rNGCounter = dungeon.rNGCounter;
            highestScore = dungeon.highestScore;
            ascensionGlyphCounters.addAll(dungeon.ascensionGlyphCounters);
            rng = dungeon.rng;
        }
        else {
            eventLog = new HashMap<>();
            allowAugments = CardCrawlGame.trial instanceof PCLCustomTrial ? ((PCLCustomTrial) CardCrawlGame.trial).allowAugments : data != null && data.useAugments;
            allowCustomCards = PGR.config.enableCustomCards.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards);
            allowCustomPotions = PGR.config.enableCustomPotions.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomPotions);
            allowCustomRelics = PGR.config.enableCustomRelics.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomRelics);
            highestScore = 0;
            rNGCounter = 0;
            for (AbstractGlyphBlight glyph : AbstractPlayerData.GLYPHS) {
                ascensionGlyphCounters.add(glyph.counter);
            }
            rng = null;
        }
    }

    public void initializeCardPool(boolean isActuallyStartingRun) {
        loadouts.clear();
        final AbstractPlayer player = CombatManager.refreshPlayer();
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
            loadCardsForData(data);
        }

        // Custom loadout
        PCLLoadout fake = null;
        if (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).fakeLoadout != null) {
            loadout = fake = ((PCLCustomTrial) CardCrawlGame.trial).fakeLoadout;
        }

        if (isActuallyStartingRun) {
            loadCustomCards(player);

            if (fake != null) {
                for (LoadoutRelicSlot rSlot : fake.getPreset().relicSlots) {
                    if (rSlot.selected != null && rSlot.selected.relic != null) {
                        GameUtilities.obtainRelicFromEvent(RelicLibrary.getRelic(rSlot.selected.relic.relicId).makeCopy());
                    }
                }

                initializePotions();
            }
            else if (data != null) {
                data.updateRelicsForDungeon();
                if (Settings.isStandardRun()) {
                    data.saveTrophies();
                }

                // Add glyphs
                for (int i = 0; i < AbstractPlayerData.GLYPHS.size(); i++) {
                    boolean shouldAdd = true;
                    for (AbstractBlight blight : player.blights) {
                        if (AbstractPlayerData.GLYPHS.get(i).getClass().equals(blight.getClass())) {
                            shouldAdd = false;
                            break;
                        }
                    }
                    int counter = PGR.dungeon.ascensionGlyphCounters.size() > i ? PGR.dungeon.ascensionGlyphCounters.get(i) : 0;
                    if (shouldAdd && counter > 0) {
                        AbstractBlight blight = AbstractPlayerData.GLYPHS.get(i).makeCopy();
                        blight.setCounter(counter);
                        GameUtilities.obtainBlightWithoutEffect(blight);
                    }
                }

                initializePotions();
            }
        }

        banItems(data);
    }

    // Modify starting potion slots and energy
    private void initializePotions() {
        if (loadout != null) {
            player.potionSlots += loadout.getPotionSlots();
            while (player.potions.size() > player.potionSlots && player.potions.get(player.potions.size() - 1) instanceof PotionSlot) {
                player.potions.remove(player.potions.size() - 1);
            }
            while (player.potionSlots > player.potions.size()) {
                player.potions.add(new PotionSlot(player.potions.size() - 1));
            }
            player.adjustPotionPositions();
        }
    }

    private void loadCardsForData(AbstractPlayerData<?, ?> data) {
        // Always include the selected loadout. If for some reason none exists, assign one at random
        if (data.selectedLoadout == null) {
            data.selectedLoadout = EUIUtils.random(EUIUtils.filter(data.getEveryLoadout(), loadout -> data.resources.getUnlockLevel() >= loadout.unlockLevel));
        }
        loadout = data.selectedLoadout;

        for (PCLLoadout loadout : data.getEveryLoadout()) {
            // Series must be unlocked to be present in-game
            if (!loadout.isLocked()) {
                loadouts.add(loadout);
            }
        }
    }

    // Add custom cards if applicable
    public void loadCustomCards(AbstractPlayer player) {
        if (allowCustomCards) {
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(player.getCardColor())) {
                AbstractCard.CardRarity rarity = c.getBuilder(0).cardRarity;
                CardGroup pool = GameUtilities.getCardPool(rarity);
                if (pool != null) {
                    pool.addToBottom(c.make(true));
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to pool " + rarity);
                }
                CardGroup spool = GameUtilities.getCardPoolSource(rarity);
                if (spool != null) {
                    spool.addToBottom(c.make(true));
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to source pool " + rarity);
                }
            }
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                AbstractCard.CardRarity rarity = c.getBuilder(0).cardRarity;
                // Do not add basic/special/curse rarity items into the colorless pool
                if (rarity == AbstractCard.CardRarity.COMMON || rarity == AbstractCard.CardRarity.UNCOMMON || rarity == AbstractCard.CardRarity.RARE) {
                    AbstractDungeon.srcColorlessCardPool.addToBottom(c.getBuilder(0).create());
                    AbstractDungeon.colorlessCardPool.addToBottom(c.getBuilder(0).create());
                }
                EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to Colorless pool");
            }
        }
    }

    // Add custom relics if applicable. Note that this is loaded in PotionPoolPatches
    public void loadCustomPotions(ArrayList<String> result, AbstractPlayer.PlayerClass p, boolean getAll) {
        if (allowCustomPotions || getAll) {
            if (getAll) {
                for (PCLCustomPotionSlot c : PCLCustomPotionSlot.getPotions(null)) {
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
        AbstractRelic.RelicTier tier = c.getBuilder(0).tier;
        ArrayList<String> relicPool = GameUtilities.getRelicPool(tier);
        // Mark this relic as seen so it shows up properly in menus, etc.
        UnlockTracker.markRelicAsSeen(c.ID);
        if (relicPool != null && !relicPool.contains(c.ID)) {
            relicPool.add(c.ID);
            EUIUtils.logInfoIfDebug(this, "Added Custom Relic " + c.ID + " to pool " + tier);
        }
    }

    // Add custom relics if applicable. Note that this is loaded in AbstractDungeonPatches_InitializeRelicList
    public void loadCustomRelics(AbstractPlayer player) {
        if (allowCustomRelics) {
            for (PCLCustomRelicSlot c : PCLCustomRelicSlot.getRelics(player.getCardColor())) {
                loadCustomRelicImpl(c);
            }
            for (PCLCustomRelicSlot c : PCLCustomRelicSlot.getRelics(AbstractCard.CardColor.COLORLESS)) {
                loadCustomRelicImpl(c);
            }
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
    public void onLoad(PCLDungeon loaded) {
        AbstractPlayer player = CombatManager.refreshPlayer();
        this.data = PGR.getPlayerData(player != null ? player.chosenClass : null);
        importBaseData(loaded);
        loadouts.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
        valueDivisor = 1;

        if (loaded != null) {
            bannedCards.addAll(loaded.bannedCards);
            bannedRelics.addAll(loaded.bannedRelics);
            augments.putAll(loaded.augments);
            fragments.putAll(loaded.fragments);
            totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);

            if (this.data != null) {
                loadout = PCLLoadout.get(loaded.startingLoadout);
                for (String proxy : loaded.loadoutIDs) {
                    PCLLoadout loadout = PCLLoadout.get(proxy);
                    if (loadout != null) {
                        loadouts.add(loadout);
                    }
                }
            }

        }

        if (loadout == null && this.data != null) {
            loadout = this.data.selectedLoadout;
        }
        validate();

        fullLog("ON LOAD");
    }

    @Override
    public void receivePreStartGame() {
        fullLog("PRE START GAME");
    }

    @Override
    public void receiveStartAct() {
        initializeCardPool(false);
    }

    @Override
    public void receiveStartGame() {
        initializeCardPool(true);
        fullLog("INITIALIZE GAME");
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

    private void removeExtraCopies(AbstractCard card) {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies > 0) {
            final int copies = GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size();
            if (copies >= pclCard.cardData.maxCopies) {
                removeCardFromPools(pclCard);
            }
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
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
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

        if (allowAugments == null) {
            allowAugments = CardCrawlGame.trial instanceof PCLCustomTrial ? ((PCLCustomTrial) CardCrawlGame.trial).allowAugments : data != null && data.useAugments;
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

        if (highestScore == null) {
            highestScore = 0;
        }
    }
}
