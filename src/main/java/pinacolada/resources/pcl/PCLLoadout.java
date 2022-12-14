package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.utilities.TupleT2;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.pcl.curse.*;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.characters.PCLCharacter;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.pcl.*;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import static pinacolada.ui.characterSelection.PCLLoadoutEditor.MAX_RELIC_SLOTS;

public abstract class PCLLoadout
{
    public static final int GOLD_AND_HP_EDITOR_ASCENSION_REQUIRED = 7;
    public static final int MAX_PRESETS = 5;
    public static final int MAX_VALUE = 40;
    public static final int MIN_CARDS = 10;
    public static final int CARD_SLOTS = 4;
    public static final HashMap<AbstractCard.CardColor, ArrayList<PCLLoadout>> LOADOUTS = new HashMap<>();
    public final AbstractCard.CardColor color;
    public PCLLoadoutData[] presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];
    public PCLCardSlot specialSlot1;
    public PCLCardSlot specialSlot2;
    public int id;
    public int preset;
    public int cardDraw = 5;
    public int unlockLevel = 0;
    public ArrayList<PCLCardData> cardData = new ArrayList<>();
    public ArrayList<PCLCardData> colorlessData = new ArrayList<>();
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

        for (PCLCardData data : PGR.getPlayerData(color).getCoreLoadout().cardData)
        {
            if (data.cardRarity == AbstractCard.CardRarity.COMMON)
            {
                slot.addItem(data, 4);
            }
        }
        slot.addItem(Curse_Clumsy.DATA, -3);
        slot.addItem(Curse_Injury.DATA, -5);
        slot.addItem(Curse_Writhe.DATA, -5);
        slot.addItem(Curse_SearingBurn.DATA, -6);
        slot.addItem(Curse_Depression.DATA, -7);
        slot.addItem(Curse_Avarice.DATA, -7);
        slot.addItem(Curse_Parasite.DATA, -7);
        slot.addItem(Curse_Doubt.DATA, -7);
        slot.addItem(Curse_Decay.DATA, -7);
        slot.addItem(Curse_Shame.DATA, -7);
        slot.addItem(Curse_Regret.DATA, -9);
        slot.addItem(Curse_Pain.DATA, -10);
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

    public int getCommonUpgrades()
    {
        return PCLBaseStatEditor.StatType.CommonUpgrade.getAmount(getPreset());
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

    public int getGold()
    {
        return PCLBaseStatEditor.StatType.Gold.getAmount(getPreset());
    }

    public int getHP()
    {
        return PCLBaseStatEditor.StatType.HP.getAmount(getPreset());
    }

    public CharSelectInfo getLoadout(String name, String description, PCLCharacter c)
    {
        int hp = getHP();
        return new CharSelectInfo(name + "-" + id, description, hp, hp, getOrbSlots(), getGold(), cardDraw, c, getStartingRelics(), getStartingDeck(), false);
    }

    public String getName()
    {
        String[] options = PGR.getCharacterStrings(color).OPTIONS;
        return id >= 0 && options != null && options.length > id ? options[id] : "";
    }

    public int getOrbSlots()
    {
        return PCLBaseStatEditor.StatType.OrbSlot.getAmount(getPreset());
    }

    public int getPotionSlots()
    {
        return PCLBaseStatEditor.StatType.PotionSlot.getAmount(getPreset());
    }

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

        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null)
        {
            String starterRelic = data.getStartingRelicID();
            if (starterRelic != null)
            {
                addStarterRelic(res, starterRelic);
            }
        }

        addStarterRelic(res, FoolishCubes.ID);
        addStarterRelic(res, UsefulBox.ID);

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
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
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

    public String getTrophyMessage(int trophy)
    {
        if (trophy == 1)
        {
            return PGR.core.strings.trophies.bronzeDescription;
        }
        else if (trophy == 2)
        {
            return PGR.core.strings.trophies.silverDescription;
        }
        else if (trophy == 3)
        {
            return PGR.core.strings.trophies.goldDescription;
        }

        return null;
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
            r1.addItem(new MagicEraser(), 3);
        }

    }

    public void onVictory(int ascensionLevel, int trophyLevel, int score)
    {
        PCLTrophies trophies = getTrophies();
        PCLAbstractPlayerData data = PGR.getPlayerData(color);
        if (data != null && data.selectedLoadout.id == id)
        {
            switch (trophyLevel)
            {
                case 2:
                    trophies.trophy2 = Math.max(trophies.trophy2, ascensionLevel);
                    break;
                case 3:
                    trophies.trophy3 = Math.max(trophies.trophy3, ascensionLevel);
                    break;
            }
            trophies.trophy1 = Math.max(trophies.trophy1, ascensionLevel);

            trophies.glyph0 = Math.max(trophies.glyph0, PGR.core.dungeon.ascensionGlyphCounters.get(0));
            trophies.glyph1 = Math.max(trophies.glyph1, PGR.core.dungeon.ascensionGlyphCounters.get(1));
            trophies.glyph2 = Math.max(trophies.glyph2, PGR.core.dungeon.ascensionGlyphCounters.get(2));
            trophies.highScore = Math.max(trophies.highScore, score);
        }
    }

    public Validation validate()
    {
        return getPreset().validate();
    }

    public static class Validation
    {
        public final TupleT2<Integer, Boolean> cardsCount = new TupleT2<>();
        public final TupleT2<Integer, Boolean> totalValue = new TupleT2<>();
        public final HashMap<PCLBaseStatEditor.StatType, Integer> values = new HashMap<>();
        public int hindranceLevel;
        public boolean allCardsSeen;
        public boolean isValid;

        public Validation()
        {

        }

        public Validation(PCLLoadoutData data)
        {
            refresh(data);
        }

        public static Validation For(PCLLoadoutData data)
        {
            return new Validation(data);
        }

        public Validation refresh(PCLLoadoutData data)
        {
            if (data == null || data.preset < 0 || data.preset >= MAX_PRESETS)
            {
                isValid = false;
                return this;
            }

            cardsCount.set(0, false);
            totalValue.set(MAX_VALUE, false);
            allCardsSeen = true;
            int weakHindrances = 0;
            int strongHindrances = 0;
            for (PCLCardSlot slot : data.cardSlots)
            {
                if (slot == null)
                {
                    continue;
                }

                totalValue.v1 += slot.getEstimatedValue();
                cardsCount.v1 += slot.amount;

                if (slot.selected != null)
                {
                    if (slot.selected.data.isNotSeen())
                    {
                        allCardsSeen = false;
                    }

                    if (slot.selected.estimatedValue < -2)
                    {
                        strongHindrances += slot.amount;
                    }
                    else if (slot.selected.estimatedValue < 0)
                    {
                        weakHindrances += slot.amount;
                    }
                }
            }
            for (PCLRelicSlot slot : data.relicSlots)
            {
                if (slot == null)
                {
                    continue;
                }

                totalValue.v1 += slot.getEstimatedValue();
            }

            // Hindrance level is determined by the proportion of your deck that is "bad"
            // Strikes/Defends and harmless hindrances have a weaker influence
            // Curses and damaging hindrances have a stronger influence
            if (cardsCount.v1 > 0)
            {
                int strongHindranceLevel = (int) Math.max(0, (30 * Math.pow(strongHindrances, 1.5) / cardsCount.v1) - 7);
                int weakHindranceLevel = Math.max(0, (30 * (weakHindrances + strongHindrances) / cardsCount.v1) - 20);
                hindranceLevel = -strongHindranceLevel - weakHindranceLevel;
            }
            else
            {
                hindranceLevel = 0;
            }

            values.putAll(data.values);
            totalValue.v1 += (int) EUIUtils.sum(values.values(), Float::valueOf) + hindranceLevel;
            totalValue.v2 = totalValue.v1 <= MAX_VALUE;
            cardsCount.v2 = cardsCount.v1 >= MIN_CARDS;
            isValid = totalValue.v2 && cardsCount.v2 && allCardsSeen;

            return this;
        }
    }
}