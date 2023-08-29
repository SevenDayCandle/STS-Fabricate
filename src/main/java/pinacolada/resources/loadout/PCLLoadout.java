package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CountingPanelStats;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.characters.PCLCharacter;
import pinacolada.misc.LoadoutStrings;
import pinacolada.relics.PCLRelicData;
import pinacolada.relics.pcl.GenericDice;
import pinacolada.relics.pcl.HeartShapedBox;
import pinacolada.relics.pcl.Macroscope;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;
import pinacolada.utilities.GameUtilities;

import java.util.*;

// Copied and modified from STS-AnimatorMod
public abstract class PCLLoadout {
    private static final HashMap<String, PCLLoadout> LOADOUTS = new HashMap<>();
    public static final int DEFAULT_CARD_SLOTS = 6;
    public static final int DEFAULT_RELIC_SLOTS = 2;
    public static final AbstractCard.CardType SELECTABLE_TYPE = AbstractCard.CardType.SKILL;
    public static final int MAX_PRESETS = 5;
    public static final int MAX_VALUE = 20;
    public static final int MIN_CARDS = 10;
    public static final int COMMON_LOADOUT_VALUE = 6;
    public static final int CURSE_VALUE = -6;
    public static final int CARD_SLOTS = 4;
    public final String ID;
    protected String shortDescription = GameUtilities.EMPTY_STRING;
    public AbstractCard.CardColor color;
    public int preset;
    public int unlockLevel = 0;
    public int maxValue;
    public int minCards;
    public ArrayList<PCLCardData> cardDatas = new ArrayList<>();
    public ArrayList<PCLCardData> colorlessData = new ArrayList<>();
    public ArrayList<PCLCardData> defends = new ArrayList<>();
    public ArrayList<PCLCardData> strikes = new ArrayList<>();
    public ArrayList<PCLRelicData> relics = new ArrayList<>();
    public PCLLoadoutData[] presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];

    public PCLLoadout(AbstractCard.CardColor color, String id, int unlockLevel) {
        this(color, id, unlockLevel, MAX_VALUE, MIN_CARDS);
    }

    public PCLLoadout(AbstractCard.CardColor color, String id, int unlockLevel, int maxValue, int minCards) {
        this.ID = id;
        this.unlockLevel = unlockLevel;
        this.color = color;
        this.maxValue = maxValue;
        this.minCards = minCards;
    }

    public static String createID(Class<? extends PCLLoadout> type) {
        return createID(PGR.BASE_PREFIX, type);
    }

    public static String createID(String prefix, Class<? extends PCLLoadout> type) {
        return PGR.createID(prefix, type.getSimpleName());
    }

    public static PCLLoadout get(String id) {
        PCLLoadout loadout = LOADOUTS.get(id);
        if (loadout == null) {
            PCLCustomLoadoutInfo info = PCLCustomLoadoutInfo.get(id);
            if (info != null) {
                loadout = info.loadout;
            }
        }
        return loadout;
    }

    public static ArrayList<PCLLoadout> getAll(AbstractCard.CardColor cardColor) {
        ArrayList<PCLLoadout> base = EUIUtils.filter(LOADOUTS.values(), l -> l.color == cardColor);
        for (PCLCustomLoadoutInfo custom : PCLCustomLoadoutInfo.getLoadouts(cardColor)) {
            if (custom.loadout != null) {
                base.add(custom.loadout);
            }
        }
        return base;
    }

    public static int getBaseDraw(AbstractCard.CardColor color) {
        AbstractPlayerData<?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseDraw;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null) {
                return info.cardDraw;
            }
        }
        return AbstractPlayerData.DEFAULT_DRAW;
    }

    public static int getBaseEnergy(AbstractCard.CardColor color) {
        AbstractPlayerData<?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseEnergy;
        }
        return AbstractPlayerData.DEFAULT_ENERGY;
    }

    public static int getBaseGold(AbstractCard.CardColor color) {
        AbstractPlayerData<?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseGold;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null) {
                return info.gold;
            }
        }
        return AbstractPlayerData.DEFAULT_GOLD;
    }

    public static int getBaseHP(AbstractCard.CardColor color) {
        AbstractPlayerData<?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseHP;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null) {
                return info.maxHp;
            }
        }
        return AbstractPlayerData.DEFAULT_HP;
    }

    public static int getBaseOrbs(AbstractCard.CardColor color) {
        AbstractPlayerData<?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseOrbs;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        {
            if (info != null) {
                return info.maxOrbs;
            }
        }
        // Assume no orbs if player has no data
        return 0;
    }

    private static CharSelectInfo getCharSelectInfo(AbstractCard.CardColor color) {
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            // PCLCharacter getLoadout will cause an infinite loop
            if (!(p instanceof PCLCharacter) && p.getCardColor() == color) {
                return p.getLoadout();
            }
        }
        return null;
    }

    public static <T extends PCLLoadout> T register(T loadout) {
        LOADOUTS.put(loadout.ID, loadout);
        return loadout;
    }

    public void addBasicDefends(LoadoutCardSlot slot) {
        for (PCLCardData card : getPlayerData().getCoreLoadout().defends) {
            slot.addItem(card, 0);
        }
    }

    public void addBasicStrikes(LoadoutCardSlot slot) {
        for (PCLCardData card : getPlayerData().getCoreLoadout().strikes) {
            slot.addItem(card, 0);
        }
    }

    public void addLoadoutBlights(LoadoutBlightSlot r1) {
    }

    public void addLoadoutCards(LoadoutCardSlot slot) {
        for (PCLCardData card : cardDatas) {
            if (card.cardRarity == AbstractCard.CardRarity.COMMON) {
                slot.addItem(card, COMMON_LOADOUT_VALUE);
            }
        }

        for (PCLCardData card : getPlayerData().getCoreLoadout().cardDatas) {
            if (card.cardRarity == AbstractCard.CardRarity.COMMON) {
                slot.addItem(card, COMMON_LOADOUT_VALUE);
            }
        }

        // Dynamically add non-special curses
        for (PCLCardData data : PCLCardData.getAllData(false, true, d -> d.cardType == AbstractCard.CardType.CURSE && d.cardRarity != AbstractCard.CardRarity.SPECIAL)) {
            slot.addItem(data, CURSE_VALUE);
        }
    }

    public void addLoadoutRelics(LoadoutRelicSlot r1) {
        r1.addItem(new GenericDice(), 3);
        r1.addItem(new Macroscope(), 5);
        r1.addItem(new HeartShapedBox(), 8);
        r1.markAllSeen();
    }

    public void addStarterRelic(ArrayList<String> res, String id) {
        if (!UnlockTracker.isRelicSeen(id)) {
            UnlockTracker.markRelicAsSeen(id);
        }
        res.add(id);
    }

    public boolean allowCustoms() {
        return false;
    }

    public PCLCard buildCard(boolean selected, boolean inPool) {
        final PCLCardData data = getSymbolicCard();
        if (data == null) {
            EUIUtils.logWarning(this, getName() + " has no symbolic card.");
            return null;
        }

        PCLDynamicCardData cd = ((PCLDynamicCardData) new PCLDynamicCardData(String.valueOf(ID))
                .setColor(color)
                .showTypeText(false)
                .setMaxUpgrades(0));
        if (!isLocked()) {
            cd.setImagePath(data.imagePath);
        }

        PCLDynamicCard card = cd.createImplWithForms(0, 0, false);

        card.name = isCore() ? PGR.core.strings.sui_core : getName();
        card.clearSkills();

        if (isLocked()) {
            card.isSeen = false;
            card.color = AbstractCard.CardColor.CURSE;
            card.setCardRarityType(AbstractCard.CardRarity.CURSE, AbstractCard.CardType.CURSE);
        }
        else {
            card.addUseMove(new FakeSkill());
            card.addUseMove(new FakeSkill2());
            if (selected) {
                card.color = data.cardColor;
                card.setCardRarityType(AbstractCard.CardRarity.SPECIAL, SELECTABLE_TYPE);
            }
            else if (inPool) {
                card.color = data.cardColor;
                card.setCardRarityType(AbstractCard.CardRarity.UNCOMMON, SELECTABLE_TYPE);
            }
            else {
                card.color = AbstractCard.CardColor.COLORLESS;
                card.setCardRarityType(AbstractCard.CardRarity.COMMON, SELECTABLE_TYPE);
            }
        }

        if (!isCore() && card.isSeen) {
            int i = 0;
            int maxLevel = 2;
            float maxPercentage = 0;
            CountingPanelStats<PCLAffinity, PCLAffinity, PCLCardData> affinityStatistics = CountingPanelStats.basic(d -> d.affinities.getAffinities(), cardDatas);
            for (Map.Entry<PCLAffinity, Integer> g : affinityStatistics) {
                float percentage = affinityStatistics.getPercentage(g.getKey());
                if (percentage == 0 || i > 2) {
                    break;
                }

                if (percentage < maxPercentage || (maxLevel == 2 && percentage < 0.3f)) {
                    maxLevel -= 1;
                }
                if (maxLevel > 0) {
                    card.affinities.add(g.getKey(), maxLevel);
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

    public boolean canChangePreset(int preset) {
        return preset >= 0 && preset < MAX_PRESETS;
    }

    public void clearPresets() {
        presets = new PCLLoadoutData[PCLLoadout.MAX_PRESETS];
    }

    public PCLLoadoutValidation createValidation() {
        return getPreset().validate();
    }

    public String getAuthor() {
        LoadoutStrings strings = PGR.getLoadoutStrings(ID);
        return strings != null ? strings.AUTHOR : "";
    }

    public int getBaseDraw() {
        return getBaseDraw(color);
    }

    public int getBaseEnergy() {
        return getBaseEnergy(color);
    }

    public int getBaseGold() {
        return getBaseGold(color);
    }

    public int getBaseHP() {
        return getBaseHP(color);
    }

    public int getBaseOrbs() {
        return getBaseOrbs(color);
    }

    public String getDeckPreviewString(boolean forceRefresh) {
        if (shortDescription == null || forceRefresh) {
            final StringJoiner sj = new StringJoiner(", ");
            for (String s : getStartingDeck()) {
                AbstractCard card = CardLibrary.getCard(s);
                if (card.rarity != AbstractCard.CardRarity.BASIC) {
                    sj.add(card.originalName);
                }
            }

            shortDescription = EUIUtils.format("{0} #{1}", getName(), preset + 1);
        }

        return shortDescription;
    }

    public PCLLoadoutData getDefaultData(int preset) {
        final PCLLoadoutData data = new PCLLoadoutData(this);
        data.preset = preset;
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values()) {
            data.values.put(type, 0);
        }

        setDefaultBlightsForData(data);
        setDefaultCardsForData(data);
        setDefaultRelicsForData(data);
        return data;
    }

    public int getDraw() {
        return PCLBaseStatEditor.StatType.CardDraw.getAmount(this, getPreset());
    }

    public int getEnergy() {
        return PCLBaseStatEditor.StatType.Energy.getAmount(this, getPreset());
    }

    public int getGold() {
        return PCLBaseStatEditor.StatType.Gold.getAmount(this, getPreset());
    }

    public int getHP() {
        return PCLBaseStatEditor.StatType.HP.getAmount(this, getPreset());
    }

    public CharSelectInfo getLoadout(String name, String description, PCLCharacter c) {
        int hp = getHP();
        return new CharSelectInfo(name + "-" + ID, description, hp, hp, getOrbSlots(), getGold(), getDraw(), c, getStartingRelics(), getStartingDeck(), false);
    }

    public String getName() {
        LoadoutStrings strings = PGR.getLoadoutStrings(ID);
        return strings != null ? strings.NAME : "";
    }

    public String getNameForFilter() {
        String base = getName();
        return base.isEmpty() ? PGR.core.strings.sui_core : base;
    }

    public int getOrbSlots() {
        return PCLBaseStatEditor.StatType.OrbSlot.getAmount(this, getPreset());
    }

    public AbstractPlayerData<?, ?> getPlayerData() {
        return PGR.getPlayerData(color);
    }

    public int getPotionSlots() {
        return PCLBaseStatEditor.StatType.PotionSlot.getAmount(this, getPreset());
    }

    public PCLLoadoutData getPreset() {
        return getPreset(preset);
    }

    public PCLLoadoutData getPreset(int preset) {
        final PCLLoadoutData data = presets[preset];
        if (data != null) {
            return data;
        }

        return presets[preset] = getDefaultData(preset);
    }

    public PCLResources<?, ?, ?, ?> getResources() {
        return PGR.getResources(color);
    }

    public int getSlotsForAbility() {
        return 0;
    }

    public int getSlotsForCard() {
        return DEFAULT_CARD_SLOTS;
    }

    public int getSlotsForRelic() {
        return DEFAULT_RELIC_SLOTS;
    }

    public ArrayList<String> getStartingBlights() {
        final ArrayList<String> res = new ArrayList<>();

        AbstractPlayerData<?, ?> data = getPlayerData();
        if (data != null) {
            List<String> starterRelics = data.getStartingBlights();
            if (starterRelics != null) {
                res.addAll(starterRelics);
            }
        }

        for (LoadoutBlightSlot rSlot : getPreset().blightSlots) {
            if (rSlot.selected != null && rSlot.selected.item != null) {
                res.add(rSlot.selected.item.blightID);
            }
        }
        return res;
    }

    public ArrayList<String> getStartingDeck() {
        final ArrayList<String> cards = new ArrayList<>();
        for (LoadoutCardSlot slot : getPreset().cardSlots) {
            String cardID = slot.getSelectedID();
            if (cardID != null) {
                for (int i = 0; i < slot.amount; i++) {
                    cards.add(cardID);
                }
            }
        }

        if (cards.isEmpty()) {
            EUIUtils.logWarning(this, "Starting loadout was empty");
            AbstractPlayerData<?, ?> data = getPlayerData();
            if (data != null) {
                for (int i = 0; i < 2; i++) {
                    for (PCLCardData card : data.getCoreLoadout().strikes) {
                        cards.add(card.ID);
                    }
                    for (PCLCardData card : data.getCoreLoadout().defends) {
                        cards.add(card.ID);
                    }
                }
            }

        }

        return cards;
    }

    public ArrayList<String> getStartingRelics() {
        final ArrayList<String> res = new ArrayList<>();

        AbstractPlayerData<?, ?> data = getPlayerData();
        if (data != null) {
            List<String> starterRelics = data.getStartingRelics();
            for (String starterRelic : starterRelics) {
                addStarterRelic(res, starterRelic);
            }
        }

        for (LoadoutRelicSlot rSlot : getPreset().relicSlots) {
            if (rSlot.selected != null && rSlot.selected.item != null) {
                res.add(rSlot.selected.item.relicId);
            }
        }
        return res;
    }

    public PCLCardData getSymbolicCard() {
        if (cardDatas.size() > 0) {
            return cardDatas.get(0);
        }
        return QuestionMark.DATA;
    }

    public PCLTrophies getTrophies() {
        AbstractPlayerData<?, ?> data = getPlayerData();
        if (data == null) {
            return null;
        }

        PCLTrophies trophies = data.getTrophies(ID);
        if (trophies == null) {
            trophies = new PCLTrophies(ID);
            data.trophies.put(ID, trophies);
        }

        return trophies;
    }

    public void initializeData(PCLLoadoutData data) {
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values()) {
            data.values.put(type, 0);
        }

        LoadoutCardSlot strikeSlot = data.addCardSlot(1, LoadoutCardSlot.MAX_LIMIT);
        LoadoutCardSlot defendSlot = data.addCardSlot(1, LoadoutCardSlot.MAX_LIMIT);
        addBasicStrikes(strikeSlot);
        addBasicDefends(defendSlot);

        // 2 slots are taken by strikes/defends by default
        for (int i = 2; i < getSlotsForCard(); i++) {
            LoadoutCardSlot slot = data.addCardSlot(0, LoadoutCardSlot.MAX_LIMIT);
            addLoadoutCards(slot);
        }

        for (int i = 0; i < getSlotsForRelic(); i++) {
            LoadoutRelicSlot r1 = data.addRelicSlot();
            addLoadoutRelics(r1);
        }

        for (int i = 0; i < getSlotsForAbility(); i++) {
            LoadoutBlightSlot r1 = data.addBlightSlot();
            addLoadoutBlights(r1);
        }
    }

    public boolean isCardFromLoadout(AbstractCard card) {
        return isCardFromLoadout(card.cardID);
    }

    public boolean isCardFromLoadout(String cardID) {
        PCLCardData data = PCLCardData.getStaticData(cardID);
        return data != null && data.loadout == this;
    }

    /* Core loadouts:
     *   - Cards can be used in any other loadout
     *   - Cannot be unselected in the loadout screen
     *   - You cannot actually select this loadout, unless no other loadouts exist
     * */
    public boolean isCore() {
        PCLResources<?, ?, ?, ?> resources = getResources();
        return resources.data == null || resources.data.getCoreLoadout() == this;
    }

    public boolean isEnabled() {
        PCLResources<?, ?, ?, ?> resources = getResources();
        return resources == null || (resources.getUnlockLevel() >= unlockLevel &&
                (resources.data == null || resources.data.getCoreLoadout() == this || resources.data.config.selectedLoadouts.get().contains(this.ID)));
    }

    public boolean isLocked() {
        PCLResources<?, ?, ?, ?> resources = getResources();
        return resources != null && resources.getUnlockLevel() < unlockLevel;
    }

    public boolean isRelicFromLoadout(String relicID) {
        PCLRelicData data = PCLRelicData.getStaticData(relicID);
        return data != null && data.loadout == this;
    }

    public void onOpen(CharacterOption option) {

    }

    public void onVictory(int ascensionLevel, int trophyLevel, int score) {
        PCLTrophies trophies = getTrophies();
        AbstractPlayerData<?, ?> data = getPlayerData();
        if (data != null && data.selectedLoadout.ID.equals(ID)) {
            if (trophyLevel >= 2) {
                trophies.trophy2 = Math.max(trophies.trophy2, ascensionLevel);
            }
            trophies.trophy1 = Math.max(trophies.trophy1, ascensionLevel);

            trophies.glyph0 = Math.max(trophies.glyph0, PGR.dungeon.ascensionGlyphCounters.get(0));
            trophies.glyph1 = Math.max(trophies.glyph1, PGR.dungeon.ascensionGlyphCounters.get(1));
            trophies.glyph2 = Math.max(trophies.glyph2, PGR.dungeon.ascensionGlyphCounters.get(2));
            trophies.highScore = Math.max(trophies.highScore, score);
        }
    }

    protected void setDefaultBlightsForData(PCLLoadoutData data) {
        for (int i = 0; i < data.blightsSize(); i++) {
            data.getBlightSlot(i).select(i);
        }
    }

    protected void setDefaultCardsForData(PCLLoadoutData data) {
        if (data.cardsSize() > 5) {
            int firstCommonIndex = Math.max(0, data.getCardSlot(3).findIndex(i -> i.getLoadout() != this));
            data.getCardSlot(0).select(0, 4).markAllSeen();
            data.getCardSlot(1).select(0, 4).markAllSeen();
            data.getCardSlot(2).select(0, 1).markCurrentSeen();
            data.getCardSlot(3).select(firstCommonIndex, 1).markCurrentSeen();
            data.getCardSlot(4).select(firstCommonIndex + 1, 1).markCurrentSeen();
            data.getCardSlot(5).clear();
        }
        else {
            data.getCardSlot(0).select(0, 4).markAllSeen();
            data.getCardSlot(1).select(0, 4).markAllSeen();
        }
    }

    protected void setDefaultRelicsForData(PCLLoadoutData data) {
        if (data.relicsSize() > 0) {
            data.getRelicSlot(0).select(0);
            for (int i = 1; i < data.relicsSize(); i++) {
                data.getRelicSlot(i).clear();
            }
        }
    }

    public void sortItems() {
        strikes.sort((a, b) -> b.affinities.getLevel(PCLAffinity.General) - a.affinities.getLevel(PCLAffinity.General));
        defends.sort((a, b) -> b.affinities.getLevel(PCLAffinity.General) - a.affinities.getLevel(PCLAffinity.General));
        cardDatas.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
        colorlessData.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
    }

    // This is used to show the number of cards currently selected. We update the amount of this skill to update the card description without rebuilding it from scratch
    protected class FakeSkill extends PSpecialSkill {
        public FakeSkill() {
            super("", PGR.core.strings.sui_unlocked, (a, b, c) -> {
            }, 0, cardDatas.size());
        }
    }

    protected class FakeSkill2 extends PSpecialSkill {
        public FakeSkill2() {
            super("", PGR.core.strings.sui_selected, (a, b, c) -> {
            }, 0, cardDatas.size());
        }
    }
}