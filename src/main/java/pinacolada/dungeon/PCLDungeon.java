package pinacolada.dungeon;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.OnStartBattleSubscriber;
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
import com.megacrit.cardcrawl.rooms.AbstractRoom;
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
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.interfaces.listeners.OnAddToDeckListener;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.FakeLoadout;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
// TODO Rework
public class PCLDungeon implements CustomSavable<PCLDungeon>, PreStartGameSubscriber, StartGameSubscriber, OnStartBattleSubscriber, StartActSubscriber {
    public static final AbstractCard.CardRarity[] poolOrdering = AbstractCard.CardRarity.values();
    public final ArrayList<Integer> ascensionGlyphCounters = new ArrayList<>();
    public transient final ArrayList<PCLLoadout> loadouts = new ArrayList<>();
    public Boolean allowCustomCards = false;
    public HashMap<PCLAffinity, Integer> fragments = new HashMap<>();
    public HashMap<String, Integer> augments = new HashMap<>();
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public String currentForm = null;
    public transient PCLLoadout startingSeries = new FakeLoadout();
    protected ArrayList<String> loadoutIDs = new ArrayList<>();
    protected Integer highestScore = 0;
    protected Integer rNGCounter = 0;
    protected Map<String, String> eventLog = new HashMap<>();
    protected Random rng;
    protected String startingLoadout;
    protected transient PCLAbstractPlayerData data;
    protected transient boolean canJumpAnywhere;
    protected transient boolean canJumpNextFloor;
    protected transient int valueDivisor;
    private transient boolean panelAdded;
    private transient int totalAugmentCount = 0;

    public static PCLDungeon register() {
        final PCLDungeon data = new PCLDungeon();
        BaseMod.addSaveField(PCLDungeon.class.getSimpleName(), data);
        BaseMod.subscribe(data);
        return data;
    }

    public void addAugment(String id, int count) {
        augments.merge(id, count, Integer::sum);
        totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);
        PGR.augmentPanel.flash();
    }

    public void addDivisor(int divisor) {
        valueDivisor = Math.max(1, valueDivisor + divisor);
    }

    public void addRelic(String relicID, AbstractRelic.RelicTier tier) {
        if (!AbstractDungeon.player.hasRelic(relicID)) {
            final ArrayList<String> pool = GameUtilities.getRelicPool(tier);
            if (pool != null && pool.size() > 0 && !pool.contains(relicID)) {
                Random rng = AbstractDungeon.relicRng;
                if (rng == null) {
                    rng = PGR.dungeon.getRNG();
                }

                pool.add(rng.random(pool.size() - 1), relicID);
                bannedRelics.remove(relicID);
            }
        }
    }

    public Random getRNG() {
        if (rng == null) {
            rng = new Random(Settings.seed);
            rng.setCounter(rNGCounter);
        }

        return rng;
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

    private void log(String message) {
        EUIUtils.logInfoIfDebug(this, message);
    }

    public void banRelic(String relicID) {
        removeRelic(relicID);
        bannedRelics.add(relicID);
        log("Banned " + relicID + ", Total: " + bannedRelics.size());
    }

    public void removeRelic(String relicID) {
        final ArrayList<String> pool = GameUtilities.getRelicPool(RelicLibrary.getRelic(relicID).tier);
        if (pool != null) {
            pool.remove(relicID);
        }
    }

    public boolean canJumpAnywhere() {
        return canJumpAnywhere;
    }

    public boolean canJumpNextFloor() {
        return canJumpNextFloor;
    }

    public boolean canObtainMoreCopies(AbstractCard c) {
        if (c instanceof PCLCard) {
            final int copies = GameUtilities.getAllCopies(c.cardID, AbstractDungeon.player.masterDeck).size();
            return copies < ((PCLCard) c).cardData.maxCopies;
        }
        return true;
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

    public void setDivisor(int divisor) {
        valueDivisor = Math.max(1, divisor);
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
                int nextRarityIndex = Math.max(0, rarity.ordinal() - 1);
                return getRandomCard(nextRarityIndex > 1 ? poolOrdering[nextRarityIndex] : null, filterFunc, rng, allowOtherRarities);
            }
        }
        return null;
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool) {
        return getRandomCardFromPool(pool, this::canObtainCopy, AbstractDungeon.cardRng);
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool, FuncT1<Boolean, AbstractCard> filterFunc, Random rng) {
        ArrayList<AbstractCard> choices = EUIUtils.filter(pool.group, filterFunc::invoke);
        return rng != null ? GameUtilities.getRandomElement(choices, rng) : GameUtilities.getTrulyRandomElement(choices);
    }

    public boolean canObtainCopy(AbstractCard card) {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies > 0) {
            return GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size() < pclCard.cardData.maxCopies;
        }
        return true;
    }

    public AbstractCard getRandomCardFromPool(CardGroup pool, AbstractCard.CardType type) {
        return getRandomCardFromPool(pool, c -> c.type == type && canObtainCopy(c), AbstractDungeon.cardRng);
    }

    public AbstractCard getRandomRewardReplacementCard(AbstractCard.CardRarity rarity, ArrayList<AbstractCard> ignore, Random rng, boolean allowOtherRarities) {
        AbstractCard replacement = null;
        boolean searchingCard = true;

        while (searchingCard) {
            searchingCard = false;

            final AbstractCard temp = getRandomCard(rarity, c -> !(EUIUtils.any(ignore, i -> i.cardID.equals(c.cardID))) && canObtainCopy(c), rng, allowOtherRarities);
            if (temp == null) {
                break;
            }

            if (temp instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) temp).shouldCancel()) {
                searchingCard = true;
            }

            if (!searchingCard) {
                replacement = temp.makeCopy();
            }
        }

        for (AbstractRelic r : player.relics) {
            r.onPreviewObtainCard(replacement);
        }

        return replacement;
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

            if (startingSeries != null) {
                startingLoadout = startingSeries.ID;
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
        importBaseData(loaded);
        loadouts.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
        valueDivisor = 1;
        AbstractPlayer player = CombatManager.refreshPlayer();
        this.data = PGR.getPlayerData(player != null ? player.chosenClass : null);

        if (loaded != null) {
            bannedCards.addAll(loaded.bannedCards);
            bannedRelics.addAll(loaded.bannedRelics);
            augments.putAll(loaded.augments);
            fragments.putAll(loaded.fragments);
            totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);
            if (loaded.currentForm != null) {
                setCreature(loaded.currentForm);
            }

            if (this.data != null) {
                startingSeries = PCLLoadout.get(loaded.startingLoadout);
                for (String proxy : loaded.loadoutIDs) {
                    PCLLoadout loadout = PCLLoadout.get(proxy);
                    if (loadout != null) {
                        loadouts.add(loadout);
                    }
                }
            }

        }

        if (startingSeries == null && this.data != null) {
            startingSeries = this.data.selectedLoadout;
        }
        validate();

        fullLog("ON LOAD");
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        if (currentForm != null) {
            GameUtilities.setCreatureAnimation(player, currentForm);
        }
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

    public void initializeCardPool(boolean isActuallyStartingRun) {
        loadouts.clear();
        final AbstractPlayer player = CombatManager.refreshPlayer();
        data = PGR.getPlayerData(player.chosenClass);

        // When playing as a non-PCL character, remove the augment panel and any cards defined in a resource's custom colorless pool
        if (data == null) {
            if (panelAdded) {
                panelAdded = false;
                BaseMod.removeTopPanelItem(PGR.augmentPanel);
            }
            loadCustomCards(player);
            banCards(data);
            return;
        }

        loadCardsForData(data);
        loadCustomCards(player);
        banCards(data);
        data.updateRelicsForDungeon();
        if (!panelAdded) {
            panelAdded = true;
            BaseMod.addTopPanelItem(PGR.augmentPanel);
        }

        if (isActuallyStartingRun) {
            if (Settings.isStandardRun()) {
                data.saveTrophies(true);
            }

            // Modify starting potion slots
            if (data.selectedLoadout != null) {
                player.potionSlots += data.selectedLoadout.getPotionSlots();
                while (player.potions.size() > player.potionSlots && player.potions.get(player.potions.size() - 1) instanceof PotionSlot) {
                    player.potions.remove(player.potions.size() - 1);
                }
                while (player.potionSlots > player.potions.size()) {
                    player.potions.add(new PotionSlot(player.potions.size() - 1));
                }
                player.adjustPotionPositions();
            }

            // Add glyphs
            for (int i = 0; i < PCLAbstractPlayerData.GLYPHS.size(); i++) {
                boolean shouldAdd = true;
                for (AbstractBlight blight : player.blights) {
                    if (PCLAbstractPlayerData.GLYPHS.get(i).getClass().equals(blight.getClass())) {
                        shouldAdd = false;
                        break;
                    }
                }
                int counter = PGR.dungeon.ascensionGlyphCounters.size() > i ? PGR.dungeon.ascensionGlyphCounters.get(i) : 0;
                if (shouldAdd && counter > 0) {
                    AbstractBlight blight = PCLAbstractPlayerData.GLYPHS.get(i).makeCopy();
                    blight.setCounter(counter);
                    GameUtilities.obtainBlightWithoutEffect(blight);
                }
            }
        }

        //PCLCard.ToggleSimpleMode(player.masterDeck.group, SimpleMode);
    }

    private void loadCustomCards(AbstractPlayer player) {
        // Add custom cards if applicable
        if (allowCustomCards) {
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(player.getCardColor())) {
                AbstractCard.CardRarity rarity = c.getBuilder(0).cardRarity;
                CardGroup pool = GameUtilities.getCardPool(rarity);
                if (pool != null) {
                    pool.addToBottom(c.makeFirstCard(true));
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to pool " + rarity);
                }
                CardGroup spool = GameUtilities.getCardPoolSource(rarity);
                if (spool != null) {
                    spool.addToBottom(c.makeFirstCard(true));
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

    private void banCards(PCLAbstractPlayerData data) {
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
                            return !data.resources.containsColorless(card);
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
    }

    private void loadCardsForData(PCLAbstractPlayerData data) {
        // Always include the selected loadout. If for some reason none exists, assign one at random
        if (data.selectedLoadout == null) {
            data.selectedLoadout = EUIUtils.random(EUIUtils.filter(data.getEveryLoadout(), loadout -> data.resources.getUnlockLevel() >= loadout.unlockLevel));
        }

        for (PCLLoadout loadout : data.getEveryLoadout()) {
            // Series must be unlocked to be present in-game
            if (!loadout.isLocked()) {
                loadouts.add(loadout);
            }
        }
    }

    // When playing as a non-PCL character, remove any colorless cards that should be exclusive to a particular PCL character
    // This includes the example cards from the card editor
    public static boolean isColorlessCardExclusive(AbstractCard card) {
        return PGR.core.filterColorless(card) || EUIUtils.any(PGR.getRegisteredResources(), r -> r.filterColorless(card));
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

    public void reset() {
        fullLog("RESETTING...");

        importBaseData(null);
        loadouts.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
        startingSeries = new FakeLoadout();
        startingLoadout = startingSeries.ID;
        loadoutIDs.clear();
        valueDivisor = 1;

        validate();
    }

    private void fullLog(String message) {
        EUIUtils.logInfo(this, message);
        if (Settings.isDebug) {
            EUIUtils.logInfo(this, "Starting Series: " + startingSeries.getName() + ", Preset: " + startingSeries.preset);
            EUIUtils.logInfo(this, "Loadout ID: " + startingLoadout + ", Banned Cards: " + bannedCards.size());
        }
    }

    protected void importBaseData(PCLDungeon data) {
        ascensionGlyphCounters.clear();
        if (data != null) {
            eventLog = new HashMap<>(data.eventLog);
            allowCustomCards = data.allowCustomCards;
            rNGCounter = data.rNGCounter;
            highestScore = data.highestScore;
            ascensionGlyphCounters.addAll(data.ascensionGlyphCounters);
            rng = data.rng;

            if (data.currentForm != null) {
                setCreature(data.currentForm);
            }
        }
        else {
            eventLog = new HashMap<>();
            allowCustomCards = PGR.config.enableCustomCards.get() || (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards);
            highestScore = 0;
            rNGCounter = 0;
            currentForm = null;
            for (AbstractGlyphBlight glyph : PCLAbstractPlayerData.GLYPHS) {
                ascensionGlyphCounters.add(glyph.counter);
            }
            rng = null;
        }
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

        if (allowCustomCards == null) {
            allowCustomCards = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards;
        }

        if (highestScore == null) {
            highestScore = 0;
        }
    }

    public void setCreature(String id) {
        currentForm = id;
        if (currentForm != null) {
            if (GameUtilities.inBattle()) {
                PCLEffects.Queue.add(new SmokeEffect(player.hb.cX, player.hb.cY, player.getCardRenderColor()));
            }
            GameUtilities.setCreatureAnimation(player, currentForm);
        }
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
}
