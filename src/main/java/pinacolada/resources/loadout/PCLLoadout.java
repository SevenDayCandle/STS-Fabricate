package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardAffinityStatistics;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLDynamicData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.characters.PCLCharacter;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.pcl.*;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import static pinacolada.ui.characterSelection.PCLLoadoutEditor.MAX_RELIC_SLOTS;

// Copied and modified from STS-AnimatorMod
// TODO rework
public abstract class PCLLoadout
{
    public static final AbstractCard.CardType UNSELECTABLE_TYPE = AbstractCard.CardType.CURSE;
    public static final int MAX_PRESETS = 5;
    public static final int MAX_VALUE = 40;
    public static final int MIN_CARDS = 10;
    public static final int CARD_SLOTS = 4;
    public static final HashMap<AbstractCard.CardColor, ArrayList<PCLLoadout>> LOADOUTS = new HashMap<>();
    public final AbstractCard.CardColor color;
    public final int id;
    public int preset;
    public int unlockLevel = 0;
    public ArrayList<PCLCardData> cardData = new ArrayList<>();
    public ArrayList<PCLCardData> colorlessData = new ArrayList<>();
    public PCLLoadoutData[] presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];
    protected ArrayList<String> startingDeck = new ArrayList<>();
    protected String shortDescription = "";

    public static PCLLoadout register(AbstractCard.CardColor color, FuncT1<PCLLoadout, AbstractCard.CardColor> loadoutFunc)
    {
        PCLLoadout loadout = loadoutFunc.invoke(color);
        ArrayList<PCLLoadout> l = LOADOUTS.getOrDefault(color, new ArrayList<>());
        l.add(loadout);
        LOADOUTS.put(color, l);
        return loadout;
    }

    public static ArrayList<PCLLoadout> getAll(AbstractCard.CardColor cardColor)
    {
        return LOADOUTS.getOrDefault(cardColor, new ArrayList<>());
    }

    public static PCLLoadout get(AbstractCard.CardColor cardColor, int id)
    {
        ArrayList<PCLLoadout> loadouts = getAll(cardColor);
        return EUIUtils.find(loadouts, l -> l.id == id);
    }

    public static int getBaseDraw(AbstractCard.CardColor color)
    {
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            return data.baseDraw;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null)
            {
                return info.cardDraw;
            }
        }
        return PCLAbstractPlayerData.DEFAULT_DRAW;
    }

    public static int getBaseEnergy(AbstractCard.CardColor color)
    {
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            return data.baseEnergy;
        }
        return PCLAbstractPlayerData.DEFAULT_ENERGY;
    }

    public static int getBaseGold(AbstractCard.CardColor color)
    {
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            return data.baseGold;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null)
            {
                return info.gold;
            }
        }
        return PCLAbstractPlayerData.DEFAULT_GOLD;
    }

    public static int getBaseHP(AbstractCard.CardColor color)
    {
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            return data.baseHP;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null)
            {
                return info.maxHp;
            }
        }
        return PCLAbstractPlayerData.DEFAULT_HP;
    }

    public static int getBaseOrbs(AbstractCard.CardColor color)
    {
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            return data.baseOrbs;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null)
            {
                return info.maxOrbs;
            }
        }
        // Assume no orbs if player has no data
        return 0;
    }

    private static CharSelectInfo getCharSelectInfo(AbstractCard.CardColor color)
    {
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters())
        {
            // PCLCharacter getLoadout will cause an infinite loop
            if (!(p instanceof PCLCharacter) && p.getCardColor() == color)
            {
                return p.getLoadout();
            }
        }
        return null;
    }

    public PCLLoadout(AbstractCard.CardColor data, int id, int unlockLevel)
    {
        this.id = id;
        this.unlockLevel = unlockLevel;
        this.color = data;
    }

    public void addBasicDefends(PCLCardSlot slot)
    {
        slot.addItem(getDefend(), -2);
    }

    public void addBasicStrikes(PCLCardSlot slot)
    {
        slot.addItem(getStrike(), -2);
    }

    public void addLoadoutCards(PCLCardSlot slot)
    {
        for (PCLCardData data : cardData)
        {
            if (data.cardRarity == AbstractCard.CardRarity.COMMON)
            {
                slot.addItem(data, 6);
            }
        }

        for (PCLCardData data : getPlayerData().getCoreLoadout().cardData)
        {
            if (data.cardRarity == AbstractCard.CardRarity.COMMON)
            {
                slot.addItem(data, 4);
            }
        }

        // Dynamically add non-special curses
        for (PCLCardData data : PCLCardData.getAllData(false, true, d -> d.cardType == AbstractCard.CardType.CURSE && d.cardRarity != AbstractCard.CardRarity.SPECIAL))
        {
            slot.addItem(data, -7);
        }
    }

    public void addStarterRelic(ArrayList<String> res, String id)
    {
        if (!UnlockTracker.isRelicSeen(id))
        {
            UnlockTracker.markRelicAsSeen(id);
        }
        res.add(id);
    }

    public boolean canChangePreset(int preset)
    {
        return preset >= 0 && preset < MAX_PRESETS;
    }

    public int getBaseGold()
    {
        return getBaseGold(color);
    }

    public int getBaseDraw()
    {
        return getBaseDraw(color);
    }

    public int getBaseEnergy()
    {
        return getBaseEnergy(color);
    }

    public int getBaseHP()
    {
        return getBaseHP(color);
    }

    public String getDeckPreviewString(boolean forceRefresh)
    {
        if (shortDescription == null || forceRefresh)
        {
            final StringJoiner sj = new StringJoiner(", ");
            for (String s : getStartingDeck())
            {
                AbstractCard card = CardLibrary.getCard(s);
                if (card.rarity != AbstractCard.CardRarity.BASIC)
                {
                    sj.add(card.originalName);
                }
            }

            shortDescription = EUIUtils.format("{0} #{1}", getName(), preset + 1);
        }

        return shortDescription;
    }

    public PCLLoadoutData getDefaultData(int preset)
    {
        final PCLLoadoutData data = new PCLLoadoutData(this);
        data.preset = preset;
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values())
        {
            data.values.put(type, 0);
        }
        data.getCardSlot(0).select(0, 5).markAllSeen();
        data.getCardSlot(1).select(0, 5).markAllSeen();
        data.getCardSlot(2).select(0, 1).markCurrentSeen();
        data.getCardSlot(3).select(1, 1).markCurrentSeen();
        data.getCardSlot(4).select(null);
        data.getCardSlot(5).select(null);
        data.getRelicSlot(0).select((PCLRelic) null);
        data.getRelicSlot(1).select((PCLRelic) null);
        return data;
    }

    protected abstract PCLCardData getDefend();

    public int getDraw()
    {
        return PCLBaseStatEditor.StatType.CardDraw.getAmount(this, getPreset());
    }

    public int getEnergy()
    {
        return PCLBaseStatEditor.StatType.Energy.getAmount(this, getPreset());
    }

    public int getGold()
    {
        return PCLBaseStatEditor.StatType.Gold.getAmount(this, getPreset());
    }

    public int getHP()
    {
        return PCLBaseStatEditor.StatType.HP.getAmount(this, getPreset());
    }

    public CharSelectInfo getLoadout(String name, String description, PCLCharacter c)
    {
        int hp = getHP();
        return new CharSelectInfo(name + "-" + id, description, hp, hp, getOrbSlots(), getGold(), getDraw(), c, getStartingRelics(), getStartingDeck(), false);
    }

    public String getName()
    {
        String[] options = null;
        CharacterStrings cString = PGR.getCharacterStrings(color);
        if (cString != null)
        {
            options = cString.OPTIONS;
        }
        return id >= 0 && options != null && options.length > id ? options[id] : "";
    }

    // PCL characters use summons instead of orbs
    public int getOrbSlots()
    {
        return getPlayerData().useSummons ? 0 : PCLBaseStatEditor.StatType.Energy.getAmount(this, getPreset());
    }

    public int getPotionSlots()
    {
        return PCLBaseStatEditor.StatType.PotionSlot.getAmount(this, getPreset());
    }

    public PCLAbstractPlayerData getPlayerData() {return PGR.getPlayerData(color);}

    public PCLLoadoutData getPreset()
    {
        return getPreset(preset);
    }

    public PCLLoadoutData getPreset(int preset)
    {
        final PCLLoadoutData data = presets[preset];
        if (data != null)
        {
            return data;
        }

        return presets[preset] = getDefaultData(preset);
    }

    public PCLResources<?,?,?> getResources() {return PGR.getResources(color);}

    public ArrayList<String> getStartingDeck()
    {
        final ArrayList<String> cards = new ArrayList<>();
        for (PCLCardSlot slot : getPreset().cardSlots)
        {
            PCLCardData data = slot.getData();
            if (data != null)
            {
                for (int i = 0; i < slot.amount; i++)
                {
                    cards.add(data.ID);
                }
            }
        }

        if (cards.isEmpty())
        {
            EUIUtils.logWarning(this, "Starting loadout was empty");
            for (int i = 0; i < 5; i++)
            {
                cards.add(getStrike().ID);
                cards.add(getDefend().ID);
            }
        }

        return cards;
    }

    public ArrayList<String> getStartingRelics()
    {
        final ArrayList<String> res = new ArrayList<>();

        PCLAbstractPlayerData data = getPlayerData();
        if (data != null)
        {
            String starterRelic = data.getStartingRelicID();
            if (starterRelic != null)
            {
                addStarterRelic(res, starterRelic);
            }
        }

        addStarterRelic(res, UsefulBox.ID);
        addStarterRelic(res, FoolishCubes.ID);

        for (PCLRelicSlot rSlot : getPreset().relicSlots)
        {
            if (rSlot.selected != null && rSlot.selected.relic != null)
            {
                String relicID = rSlot.selected.relic.relicId;
                if (SpitefulCubes.ID.equals(relicID) || MagicEraser.ID.equals(relicID))
                {
                    res.set(1, relicID);
                }
                else if (VeryUsefulBox.ID.equals(relicID))
                {
                    res.set(2, relicID);
                }
                else
                {
                    res.add(rSlot.selected.relic.relicId);
                }
            }
        }
        return res;
    }

    protected abstract PCLCardData getStrike();

    public PCLCardData getSymbolicCard()
    {
        if (cardData.size() > 0)
        {
            return cardData.get(0);
        }
        return QuestionMark.DATA;
    }

    public PCLTrophies getTrophies()
    {
        PCLAbstractPlayerData data = getPlayerData();
        if (data == null)
        {
            return null;
        }

        PCLTrophies trophies = data.getTrophies(id);
        if (trophies == null)
        {
            trophies = new PCLTrophies(id);
            data.trophies.put(id, trophies);
        }

        return trophies;
    }

    public void initializeData(PCLLoadoutData data)
    {
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values())
        {
            data.values.put(type, 0);
        }

        PCLCardSlot strikeSlot = data.addCardSlot(1, PCLCardSlot.MAX_LIMIT);
        PCLCardSlot defendSlot = data.addCardSlot(1, PCLCardSlot.MAX_LIMIT);
        addBasicStrikes(strikeSlot);
        addBasicDefends(defendSlot);

        for (int i = 0; i < CARD_SLOTS; i++)
        {
            PCLCardSlot slot = data.addCardSlot(0, PCLCardSlot.MAX_LIMIT);
            addLoadoutCards(slot);
        }

        for (int i = 0; i < MAX_RELIC_SLOTS; i++)
        {
            PCLRelicSlot r1 = data.addRelicSlot();
            r1.addItem(new Macroscope(), 2);
            r1.addItem(new SpitefulCubes(), 2);
        }
    }

    public boolean isCardFromLoadout(AbstractCard card)
    {
        return isCardFromLoadout(card.cardID);
    }

    public boolean isCardFromLoadout(String cardID)
    {
        PCLCardData data = PCLCardData.getStaticData(cardID);
        return data != null && data.loadout == this;
    }

    /* Core loadouts:
    *   - Cards can be used in any other loadout
    *   - Cannot be unselected in the loadout screen
    *   - You cannot actually select this loadout, unless no other loadouts exist
    * */
    public boolean isCore()
    {
        return id < 0;
    }

    public boolean isLocked()
    {
        PCLResources<?,?,?> resources = getResources();
        return resources != null && resources.getUnlockLevel() < unlockLevel;
    }

    public void onVictory(int ascensionLevel, int trophyLevel, int score)
    {
        PCLTrophies trophies = getTrophies();
        PCLAbstractPlayerData data = getPlayerData();
        if (data != null && data.selectedLoadout.id == id)
        {
            if (trophyLevel >= 2)
            {
                trophies.trophy2 = Math.max(trophies.trophy2, ascensionLevel);
            }
            trophies.trophy1 = Math.max(trophies.trophy1, ascensionLevel);

            trophies.glyph0 = Math.max(trophies.glyph0, PGR.core.dungeon.ascensionGlyphCounters.get(0));
            trophies.glyph1 = Math.max(trophies.glyph1, PGR.core.dungeon.ascensionGlyphCounters.get(1));
            trophies.glyph2 = Math.max(trophies.glyph2, PGR.core.dungeon.ascensionGlyphCounters.get(2));
            trophies.highScore = Math.max(trophies.highScore, score);
        }
    }

    public PCLLoadoutValidation validate()
    {
        return getPreset().validate();
    }

    public PCLCard buildCard()
    {
        final PCLCardData data = getSymbolicCard();
        if (data == null)
        {
            EUIUtils.logWarning(this, getName() + " has no symbolic card.");
            return null;
        }

        PCLCard card = ((PCLDynamicData) new PCLDynamicData(String.valueOf(id), isCore() ? PGR.core : data.resources)
                .setImagePath(data.imagePath)
                .showTypeText(false)
                .setMaxUpgrades(0))
                .build();

        card.name = isCore() ? PGR.core.strings.seriesUI.core : getName();
        card.clearSkills();

        if (isLocked())
        {
            card.isSeen = false;
            card.cardText.overrideDescription(PGR.core.strings.charSelect.unlocksAtLevel(unlockLevel, data.resources.getUnlockLevel()), false);
            card.setCardRarityType(AbstractCard.CardRarity.COMMON, AbstractCard.CardType.STATUS);
        }
        else
        {
            card.addUseMove(new FakeSkill());
            if (isCore())
            {
                card.color = AbstractCard.CardColor.COLORLESS;
                card.setCardRarityType(AbstractCard.CardRarity.CURSE, UNSELECTABLE_TYPE);
            }
            else
            {
                card.color = data.cardColor;
                card.setCardRarityType(AbstractCard.CardRarity.COMMON, AbstractCard.CardType.SKILL);
            }
        }

        if (!isCore())
        {
            int i = 0;
            int maxLevel = 2;
            float maxPercentage = 0;
            PCLCardAffinityStatistics affinityStatistics = new PCLCardAffinityStatistics(cardData);
            for (PCLCardAffinityStatistics.Group g : affinityStatistics)
            {
                float percentage = g.getPercentage(0);
                if (percentage == 0 || i > 2)
                {
                    break;
                }

                if (percentage < maxPercentage || (maxLevel == 2 && percentage < 0.3f))
                {
                    maxLevel -= 1;
                }
                if (maxLevel > 0)
                {
                    card.affinities.add(g.affinity, maxLevel);
                }

                maxPercentage = percentage;
                i += 1;
            }
            card.affinities.collapseDuplicates = true;
            card.affinities.updateSortedList();
        }

        card.initializeDescription();

        return card;
    }

    // This is used to show the number of cards currently selected. We update the amount of this skill to update the card description without rebuilding it from scratch
    protected class FakeSkill extends PSpecialSkill
    {
        public FakeSkill()
        {
            super("", PGR.core.strings.seriesSelection.selected, (a, b) -> {
            }, 0, cardData.size());
        }
    }

    protected class FakeSkill2 extends PSpecialSkill
    {
        public FakeSkill2()
        {
            super("", PGR.core.strings.seriesSelection.unlocked, (a, b) -> {
            }, 0, cardData.size());
        }
    }
}