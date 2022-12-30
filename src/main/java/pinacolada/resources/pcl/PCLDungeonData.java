package pinacolada.resources.pcl;

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
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.EUIUtils;
import pinacolada.augments.PCLAugment;
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.PermanentUpgradeEffect;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.interfaces.listeners.OnAddToDeckListener;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.interfaces.listeners.OnCardPoolChangedListener;
import pinacolada.misc.CombatManager;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class PCLDungeonData implements CustomSavable<PCLDungeonData>, PreStartGameSubscriber, StartGameSubscriber, StartActSubscriber, OnStartBattleSubscriber
{

    private transient boolean panelAdded;
    private transient int totalAugmentCount = 0;
    protected ArrayList<PCLLoadoutProxy> proxies = new ArrayList<>();
    protected Integer longestMatchCombo = 0;
    protected Integer rNGCounter = 0;
    protected Map<String, String> eventLog = new HashMap<>();
    protected Random rng;
    protected int startingLoadout = -1;
    protected transient PCLAbstractPlayerData data;
    protected transient boolean canJumpAnywhere;
    protected transient boolean canJumpNextFloor;
    protected transient int valueDivisor;
    public Boolean allowCustomCards = false;
    public Boolean simpleMode = false;
    public HashMap<PCLAffinity, Integer> fragments = new HashMap<>();
    public HashMap<String, Integer> augments = new HashMap<>();
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public String currentForm = null;
    public final ArrayList<Integer> ascensionGlyphCounters = new ArrayList<>();
    public transient PCLLoadout startingSeries = new FakeLoadout();
    public transient final ArrayList<PCLRuntimeLoadout> loadouts = new ArrayList<>();

    public static PCLDungeonData register(String id)
    {
        final PCLDungeonData data = new PCLDungeonData();
        BaseMod.addSaveField(id, data);
        BaseMod.subscribe(data);
        return data;
    }

    public void addAugment(String id, int count)
    {
        augments.merge(id, count, Integer::sum);
        totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);
        PGR.core.augmentPanel.flash();
    }

    public void addLoadout(PCLRuntimeLoadout loadout)
    {
        loadouts.add(loadout);

        log("Adding series: " + loadout.loadout.getName());
    }

    public void addRelic(String relicID, AbstractRelic.RelicTier tier)
    {
        if (!AbstractDungeon.player.hasRelic(relicID))
        {
            final ArrayList<String> pool = GameUtilities.getRelicPool(tier);
            if (pool != null && pool.size() > 0 && !pool.contains(relicID))
            {
                Random rng = AbstractDungeon.relicRng;
                if (rng == null)
                {
                    rng = PGR.core.dungeon.getRNG();
                }

                pool.add(rng.random(pool.size() - 1), relicID);
            }
        }
    }

    public void ban(String cardID)
    {
        final AbstractCard card = CardLibrary.getCard(cardID);
        if (card == null)
        {
            return;
        }

        removeCardFromPools(card);
        bannedCards.add(card.cardID);
        log("Banned " + card.cardID + ", Total: " + bannedCards.size());
    }

    // TODO Use this
    public void banRelic(String relicID)
    {
        final ArrayList<String> pool = GameUtilities.getRelicPool(RelicLibrary.getRelic(relicID).tier);
        if (pool != null)
        {
            pool.remove(relicID);
            bannedRelics.add(relicID);
            log("Banned " + relicID + ", Total: " + bannedRelics.size());
        }
    }

    public boolean canJumpAnywhere()
    {
        return canJumpAnywhere;
    }

    public boolean canJumpNextFloor()
    {
        return canJumpNextFloor;
    }

    public boolean canObtainCopy(AbstractCard card)
    {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies > 0)
        {
            return GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size() < pclCard.cardData.maxCopies;
        }
        return true;
    }

    private void fullLog(String message)
    {
        EUIUtils.logInfo(this, message);
        if (Settings.isDebug)
        {
            EUIUtils.logInfo(this, "Starting Series: " + startingSeries.getName() + ", Preset: " + startingSeries.preset);
            EUIUtils.logInfo(this, "Loadout ID: " + startingLoadout + ", Banned Cards: " + bannedCards.size());
        }
    }

    public HashMap<PCLAffinity, Integer> getAffinityAugmentTotal()
    {
        HashMap<PCLAffinity, Integer> counts = new HashMap<>();
        for (String key : augments.keySet())
        {
            counts.merge(PCLAugment.get(key).affinity, augments.get(key), Integer::sum);
        }
        return counts;
    }

    public int getAugmentTotal()
    {
        return totalAugmentCount;
    }

    public PCLRuntimeLoadout getLoadout(PCLLoadout series)
    {
        for (PCLRuntimeLoadout loadout : loadouts)
        {
            if (loadout.id == series.id)
            {
                return loadout;
            }
        }

        return null;
    }

    public int getLongestMatchCombo()
    {
        return longestMatchCombo;
    }

    public String getMapData(String eventID)
    {
        return (eventLog != null ? eventLog.getOrDefault(eventID, null) : null);
    }

    public Random getRNG()
    {
        if (rng == null)
        {
            rng = new Random(Settings.seed);
            rng.setCounter(rNGCounter);
        }

        return rng;
    }

    public AbstractCard getRandomRewardCard(ArrayList<AbstractCard> ignore, boolean includeRares, boolean ignoreCurrentRoom)
    {
        AbstractCard replacement = null;
        boolean searchingCard = true;

        while (searchingCard)
        {
            searchingCard = false;

            final AbstractCard temp = getRandomRewardCard(includeRares, ignoreCurrentRoom);
            if (temp == null)
            {
                break;
            }

            if (ignore != null)
            {
                for (AbstractCard c : ignore)
                {
                    if (temp.cardID.equals(c.cardID))
                    {
                        searchingCard = true;
                        break;
                    }
                }
            }

            if (temp instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) temp).shouldCancel())
            {
                searchingCard = true;
            }

            if (!searchingCard)
            {
                replacement = temp.makeCopy();
            }
        }

        for (AbstractRelic r : player.relics)
        {
            r.onPreviewObtainCard(replacement);
        }

        return replacement;
    }

    private AbstractCard getRandomRewardCard(boolean includeRares, boolean ignoreCurrentRoom)
    {
        ArrayList<AbstractCard> list;

        int roll = AbstractDungeon.cardRng.random(100);
        if (includeRares && (roll <= 4 || (!ignoreCurrentRoom && GameUtilities.getCurrentRoom() instanceof MonsterRoomBoss)))
        {
            list = AbstractDungeon.srcRareCardPool.group;
        }
        else if (roll < 40)
        {
            list = AbstractDungeon.srcUncommonCardPool.group;
        }
        else
        {
            list = AbstractDungeon.srcCommonCardPool.group;
        }

        if (list != null && list.size() > 0)
        {
            return list.get(AbstractDungeon.cardRng.random(list.size() - 1));
        }

        return null;
    }

    protected void importBaseData(PCLDungeonData data)
    {
        ascensionGlyphCounters.clear();
        if (data != null)
        {
            eventLog = new HashMap<>(data.eventLog);
            allowCustomCards = data.allowCustomCards;
            simpleMode = data.simpleMode;
            rNGCounter = data.rNGCounter;
            longestMatchCombo = data.longestMatchCombo;
            ascensionGlyphCounters.addAll(data.ascensionGlyphCounters);
            rng = data.rng;

            if (data.currentForm != null)
            {
                setCreature(data.currentForm);
            }
        }
        else
        {
            eventLog = new HashMap<>();
            allowCustomCards = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards;
            simpleMode = PGR.core.config.simpleMode.get();
            longestMatchCombo = 0;
            rNGCounter = 0;
            currentForm = null;
            for (AbstractGlyphBlight glyph : PCLAbstractPlayerData.Glyphs)
            {
                ascensionGlyphCounters.add(glyph.counter);
            }
            rng = null;
        }
    }

    public void updateCardCopies()
    {
        for (AbstractCard card : player.masterDeck.group)
        {
            if (card instanceof OnCardPoolChangedListener)
            {
                ((OnCardPoolChangedListener) card).onCardPoolChanged();
            }

            removeExtraCopies(card);
        }
    }

    public void initializeCardPool()
    {
        loadouts.clear();
        final AbstractPlayer player = CombatManager.refreshPlayer();
        data = PGR.getPlayerData(player.chosenClass);

        if (data != null)
        {
            data.initializeCardPool();
            if (!panelAdded)
            {
                panelAdded = true;
                BaseMod.addTopPanelItem(PGR.core.augmentPanel);
            }
        }
        else
        {
            if (panelAdded)
            {
                panelAdded = false;
                BaseMod.removeTopPanelItem(PGR.core.augmentPanel);
            }
            AbstractDungeon.srcCurseCardPool.group.removeIf(PCLCard.class::isInstance);
            AbstractDungeon.curseCardPool.group.removeIf(PCLCard.class::isInstance);
            AbstractDungeon.srcColorlessCardPool.group.removeIf(PCLCard.class::isInstance);
            AbstractDungeon.colorlessCardPool.group.removeIf(PCLCard.class::isInstance);
            PCLRelic.updateRelics(false);
            loadCustomCards(player);
            return;
        }

        PCLRelic.updateRelics(true);

        if (Settings.isStandardRun())
        {
            data.saveTrophies(true);
        }

        final ArrayList<CardGroup> groups = new ArrayList<>();
        final AbstractCard.CardColor cColor = player.getCardColor();
        groups.addAll(GameUtilities.getCardPools());
        groups.addAll(GameUtilities.getSourceCardPools());
        for (CardGroup group : groups)
        {
            group.group.removeIf(card ->
            {
                if (card.color == AbstractCard.CardColor.COLORLESS || card.color == AbstractCard.CardColor.CURSE)
                {
                    return !(card instanceof PCLCard);
                }
                else if (card.color != cColor || loadouts.isEmpty())
                {
                    return false;
                }
                else if (!bannedCards.contains(card.cardID))
                {
                    for (PCLRuntimeLoadout loadout : loadouts)
                    {
                        if (loadout.getCardPoolInPlay().containsKey(card.cardID))
                        {
                            return false;
                        }
                    }
                }
                return true;
            });
        }
        loadCustomCards(player);

        updateCardCopies();

        if (data != null)
        {
            if (data.selectedLoadout != null)
            {
                for (int i = 0; i < data.selectedLoadout.getCommonUpgrades(); i++)
                {
                    PCLEffects.TopLevelQueue.add(new PermanentUpgradeEffect()).setFilter(c -> AbstractCard.CardRarity.COMMON.equals(c.rarity));
                }

                player.potionSlots += data.selectedLoadout.getPotionSlots();
                while (player.potions.size() > player.potionSlots && player.potions.get(player.potions.size() - 1) instanceof PotionSlot)
                {
                    player.potions.remove(player.potions.size() - 1);
                }
                while (player.potionSlots > player.potions.size())
                {
                    player.potions.add(new PotionSlot(player.potions.size() - 1));
                }
                player.adjustPotionPositions();
            }


            for (int i = 0; i < PCLAbstractPlayerData.Glyphs.size(); i++)
            {
                boolean shouldAdd = true;
                for (AbstractBlight blight : player.blights)
                {
                    if (PCLAbstractPlayerData.Glyphs.get(i).getClass().equals(blight.getClass()))
                    {
                        shouldAdd = false;
                        break;
                    }
                }
                int counter = PGR.core.dungeon.ascensionGlyphCounters.size() > i ? PGR.core.dungeon.ascensionGlyphCounters.get(i) : 0;
                if (shouldAdd && counter > 0)
                {
                    AbstractBlight blight = PCLAbstractPlayerData.Glyphs.get(i).makeCopy();
                    blight.setCounter(counter);
                    GameUtilities.obtainBlightWithoutEffect(blight);
                }
            }
        }

        //PCLCard.ToggleSimpleMode(player.masterDeck.group, SimpleMode);
    }

    private void loadCustomCards(AbstractPlayer player)
    {
        // Add custom cards if applicable
        if (allowCustomCards)
        {
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(player.getCardColor()))
            {
                AbstractCard.CardRarity rarity = c.getBuilder(0).cardRarity;
                CardGroup pool = GameUtilities.getCardPool(rarity);
                if (pool != null)
                {
                    pool.addToBottom(c.getBuilder(0).build());
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to pool " + rarity);
                }
                CardGroup spool = GameUtilities.getCardPoolSource(rarity);
                if (spool != null)
                {
                    spool.addToBottom(c.getBuilder(0).build());
                    EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to source pool " + rarity);
                }
            }
            for (PCLCustomCardSlot c : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS))
            {
                AbstractDungeon.srcColorlessCardPool.addToBottom(c.getBuilder(0).build());
                AbstractDungeon.colorlessCardPool.addToBottom(c.getBuilder(0).build());
                EUIUtils.logInfoIfDebug(this, "Added Custom Card " + c.ID + " to Colorless pool");
            }
        }
    }

    private void log(String message)
    {
        EUIUtils.logInfoIfDebug(this, message);
    }

    public void onCardObtained(AbstractCard card)
    {
        removeExtraCopies(card);

        if (card instanceof PCLCard && ((PCLCard) card).isUnique())
        {
            AbstractCard first = null;
            final ArrayList<AbstractCard> toRemove = new ArrayList<>();
            final ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
            for (AbstractCard c : cards)
            {
                if (c.cardID.equals(card.cardID))
                {
                    if (first == null)
                    {
                        first = c;
                    }
                    else
                    {
                        toRemove.add(c);
                        for (int i = 0; i <= c.timesUpgraded; i++)
                        {
                            first.upgrade();
                        }
                    }
                }
            }

            for (AbstractCard c : toRemove)
            {
                cards.remove(c);
            }

            if (first != null && toRemove.size() > 0 && PCLEffects.TopLevelQueue.count() < 5)
            {
                PCLEffects.TopLevelQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 4f, (float) Settings.HEIGHT / 2f));
                PCLEffects.TopLevelQueue.showCardBriefly(first.makeStatEquivalentCopy(), (float) Settings.WIDTH / 4f, (float) Settings.HEIGHT / 2f);
            }
        }
    }

    private void removeCardFromPools(AbstractCard card)
    {
        final AbstractCard.CardRarity rarity = card.color == AbstractCard.CardColor.COLORLESS ? null : card.rarity;
        final CardGroup srcPool = GameUtilities.getCardPoolSource(rarity);
        if (srcPool != null)
        {
            srcPool.removeCard(card.cardID);
        }
        final CardGroup pool = GameUtilities.getCardPool(rarity);
        if (pool != null)
        {
            pool.removeCard(card.cardID);
        }
    }

    private void removeExtraCopies(AbstractCard card)
    {
        final PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (!Settings.isEndless && pclCard != null && pclCard.cardData.maxCopies > 0)
        {
            final int copies = GameUtilities.getAllCopies(pclCard.cardID, AbstractDungeon.player.masterDeck).size();
            if (copies >= pclCard.cardData.maxCopies)
            {
                removeCardFromPools(pclCard);
            }
        }
    }

    public void reset()
    {
        fullLog("RESETTING...");

        importBaseData(null);
        loadouts.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
        startingSeries = new FakeLoadout();
        proxies.clear();
        startingLoadout = -1;
        valueDivisor = 1;

        validate();
    }

    public void setCreature(String id)
    {
        currentForm = id;
        if (currentForm != null)
        {
            if (GameUtilities.inBattle())
            {
                PCLEffects.Queue.add(new SmokeEffect(player.hb.cX, player.hb.cY, player.getCardRenderColor()));
            }
            GameUtilities.setCreatureAnimation(player, currentForm);
        }
    }

    public void setJumpAnywhere(boolean value)
    {
        canJumpAnywhere = value;
    }

    public void setJumpNextFloor(boolean value)
    {
        canJumpNextFloor = value;
    }

    public void setMapData(String eventID, Object value)
    {
        eventLog.put(eventID, value.toString());
    }

    public boolean tryObtainCard(AbstractCard card)
    {
        boolean canAdd = !(card instanceof OnAddToDeckListener) || ((OnAddToDeckListener) card).onAddToDeck(card);

        for (AbstractRelic relic : AbstractDungeon.player.relics)
        {
            if (relic instanceof OnAddToDeckListener)
            {
                canAdd &= ((OnAddToDeckListener) relic).onAddToDeck(card);
            }
        }

        return canAdd;
    }

    // TODO Use different score measurements for different pinacolada.characters
    public void updateLongestMatchCombo(int newCombo)
    {
        longestMatchCombo = Math.max(longestMatchCombo, newCombo);
    }

    protected void validate()
    {
        if (eventLog == null)
        {
            eventLog = new HashMap<>();
        }

        if (rng != null)
        {
            rNGCounter = rng.counter;
        }
        else if (rNGCounter == null)
        {
            rNGCounter = 0;
        }

        if (allowCustomCards == null)
        {
            allowCustomCards = CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowCustomCards;
        }

        if (simpleMode == null)
        {
            simpleMode = false;
        }

        if (longestMatchCombo == null)
        {
            longestMatchCombo = 0;
        }
    }

    public int getDivisor()
    {
        return valueDivisor;
    }

    public int getCurrentHealth(AbstractPlayer player)
    {
        return player.currentHealth / valueDivisor;
    }

    public int getMaxHealth(AbstractPlayer player)
    {
        return player.maxHealth / valueDivisor;
    }

    public void addDivisor(int divisor)
    {
        valueDivisor = Math.max(1, valueDivisor + divisor);
    }

    public void setDivisor(int divisor)
    {
        valueDivisor = Math.max(1, divisor);
    }

    @Override
    public PCLDungeonData onSave()
    {
        proxies.clear();

        if (data != null)
        {
            for (PCLRuntimeLoadout loadout : loadouts)
            {
                final PCLLoadoutProxy proxy = new PCLLoadoutProxy();
                proxy.id = loadout.id;
                proxy.bonus = loadout.bonus;
                proxies.add(proxy);
            }

            if (startingSeries.id > 0)
            {
                startingLoadout = startingSeries.id;
            }
            else
            {
                startingLoadout = data.selectedLoadout.id;
            }
        }

        validate();

        fullLog("ON SAVE");


        return this;
    }

    @Override
    public void onLoad(PCLDungeonData data)
    {
        importBaseData(data);
        loadouts.clear();
        bannedCards.clear();
        bannedRelics.clear();
        augments.clear();
        fragments.clear();
        valueDivisor = 1;
        AbstractPlayer player = CombatManager.refreshPlayer();
        this.data = PGR.getPlayerData(player != null ? player.chosenClass : null);

        if (data != null)
        {
            bannedCards.addAll(data.bannedCards);
            bannedRelics.addAll(data.bannedRelics);
            augments.putAll(data.augments);
            fragments.putAll(data.fragments);
            totalAugmentCount = EUIUtils.sumInt(augments.values(), i -> i);
            if (data.currentForm != null)
            {
                setCreature(data.currentForm);
            }

            if (this.data != null)
            {
                startingSeries = this.data.getLoadout(data.startingLoadout);
                for (PCLLoadoutProxy proxy : data.proxies)
                {
                    final PCLRuntimeLoadout loadout = PCLRuntimeLoadout.tryCreate(this.data.getLoadout(proxy.id));
                    if (loadout != null)
                    {
                        loadout.bonus = proxy.bonus;
                        loadout.buildCard();
                        loadouts.add(loadout);
                    }
                }
            }

        }

        if (startingSeries == null && this.data != null)
        {
            startingSeries = this.data.selectedLoadout;
        }
        validate();

        fullLog("ON LOAD");
    }

    @Override
    public void receivePreStartGame()
    {
        PCLCard.toggleSimpleMode(CardLibrary.getAllCards(), simpleMode);
        //PCLCard.ToggleSimpleMode(player.masterDeck.group, SimpleMode);
        fullLog("PRE START GAME");
    }

    @Override
    public void receiveStartAct()
    {
        updateCardCopies();
        fullLog("INITIALIZE ACT");
    }

    @Override
    public void receiveStartGame()
    {
        initializeCardPool();
        fullLog("INITIALIZE GAME");
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        if (currentForm != null)
        {
            GameUtilities.setCreatureAnimation(player, currentForm);
        }
    }

    protected static class PCLLoadoutProxy
    {
        public int id;
        public int bonus;
    }
}
