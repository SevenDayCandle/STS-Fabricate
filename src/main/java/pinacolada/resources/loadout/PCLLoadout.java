package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CountingPanelStats;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.characters.PCLCharacter;
import pinacolada.misc.LoadoutStrings;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelicData;
import pinacolada.relics.pcl.GenericDice;
import pinacolada.relics.pcl.HeartShapedBox;
import pinacolada.relics.pcl.Macroscope;
import pinacolada.resources.PCLPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.ui.characterSelection.PCLBaseStatEditor;
import pinacolada.utilities.GameUtilities;

import java.util.*;

// Copied and modified from STS-AnimatorMod
public abstract class PCLLoadout {
    private static final HashMap<String, PCLLoadout> LOADOUTS = new HashMap<>();
    public static final AbstractCard.CardType SELECTABLE_TYPE = AbstractCard.CardType.SKILL;
    public static final int MAX_LIMIT = 6;
    public static final int MAX_VALUE = 20;
    public static final int MIN_CARDS = 10;
    public final String ID;
    public AbstractCard.CardColor color;
    public String preset;
    public int unlockLevel = 0;
    public int maxValue;
    public int maxCardsPerSlot;
    public int minTotalCards;
    public ArrayList<PCLCardData> cardDatas = new ArrayList<>();
    public ArrayList<PCLCardData> colorlessData = new ArrayList<>();
    public ArrayList<PCLCardData> miscData = new ArrayList<>();
    public ArrayList<PCLRelicData> relics = new ArrayList<>();
    public HashMap<String, PCLLoadoutData> presets = new HashMap<>();

    public PCLLoadout(AbstractCard.CardColor color, String id, int unlockLevel) {
        this(color, id, unlockLevel, MAX_VALUE, MIN_CARDS, MAX_LIMIT);
    }

    public PCLLoadout(AbstractCard.CardColor color, String id, int unlockLevel, int maxValue, int minTotalCards, int maxCardsPerSlot) {
        this.ID = id;
        this.unlockLevel = unlockLevel;
        this.color = color;
        this.maxValue = maxValue;
        this.maxCardsPerSlot = maxCardsPerSlot;
        this.minTotalCards = minTotalCards;
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

    public static ArrayList<PCLLoadout> getAll() {
        return new ArrayList<>(LOADOUTS.values());
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
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseDraw;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        if (info != null) {
            return info.cardDraw;
        }
        return PCLPlayerData.DEFAULT_DRAW;
    }

    public static int getBaseEnergy(AbstractCard.CardColor color) {
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseEnergy;
        }
        return PCLPlayerData.DEFAULT_ENERGY;
    }

    public static int getBaseGold(AbstractCard.CardColor color) {
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseGold;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        if (info != null) {
            return info.gold;
        }
        return PCLPlayerData.DEFAULT_GOLD;
    }

    public static int getBaseHP(AbstractCard.CardColor color) {
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseHP;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        if (info != null) {
            return info.maxHp;
        }
        return PCLPlayerData.DEFAULT_HP;
    }

    public static int getBaseOrbs(AbstractCard.CardColor color) {
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null) {
            return data.baseOrbs;
        }
        CharSelectInfo info = getCharSelectInfo(color);
        if (info != null) {
            return info.maxOrbs;
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

    public boolean allowCustoms() {
        return false;
    }

    public ChoiceCard<PCLLoadout> buildCard(boolean selected, boolean inPool) {
        final PCLCardData data = getSymbolicCard();
        ChoiceCardData<PCLLoadout> cd = new ChoiceCardData<>(String.valueOf(ID), this);
        cd.setColor(color);
        cd.setMaxUpgrades(0);
        if (!isLocked()) {
            cd.setImagePath(data.imagePath);
        }

        ChoiceCard<PCLLoadout> card = cd.create();
        card.name = card.originalName = isCore() ? PGR.core.strings.sui_core : getName();
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

        card.initializeDescription();

        return card;
    }

    public void clearPresets() {
        presets.clear();
    }

    public PCLLoadoutValidation createValidation() {
        return getPreset().validate();
    }

    public String getAuthor() {
        LoadoutStrings strings = PGR.getLoadoutStrings(ID);
        return strings != null ? strings.AUTHOR : "";
    }

    public ArrayList<String> getAvailableBlightIDs() {
        return EUIUtils.arrayList();
    }

    public ArrayList<String> getAvailableCardIDs() {
        ArrayList<String> values = new ArrayList<>();
        PCLLoadout coreLoadout = getPlayerData().getCoreLoadout();
        for (PCLCardData card : getCards()) {
            if (card.cardRarity == AbstractCard.CardRarity.BASIC || card.cardRarity == AbstractCard.CardRarity.COMMON) {
                values.add(card.ID);
            }
        }
        for (PCLCardData card : getCardMiscs()) {
            if (card.cardRarity == AbstractCard.CardRarity.BASIC || card.cardRarity == AbstractCard.CardRarity.COMMON) {
                values.add(card.ID);
            }
        }
        for (PCLCardData card : coreLoadout.getCards()) {
            if (card.cardRarity == AbstractCard.CardRarity.BASIC || card.cardRarity == AbstractCard.CardRarity.COMMON) {
                values.add(card.ID);
            }
        }
        for (PCLCardData card : coreLoadout.getCardMiscs()) {
            if (card.cardRarity == AbstractCard.CardRarity.BASIC || card.cardRarity == AbstractCard.CardRarity.COMMON) {
                values.add(card.ID);
            }
        }

        // Dynamically add non-special curses
        for (PCLCardData data : PCLCardData.getAllData(false, true, d -> d.cardType == AbstractCard.CardType.CURSE && d.cardRarity != AbstractCard.CardRarity.SPECIAL)) {
            PCLResources<?,?,?,?> resources = getResources();
            String replacement = resources.getReplacement(data.ID);
            values.add(replacement != null ? replacement : data.ID);
        }
        return values;
    }

    public ArrayList<String> getAvailableRelicIDs() {
        ArrayList<String> base = EUIUtils.arrayList(GenericDice.DATA.ID, Macroscope.DATA.ID, HeartShapedBox.DATA.ID);
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null && data.canUseCustom()) {
            for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(color)) {
                if ((ID.equals(slot.loadout) || (slot.loadout == null)) && slot.getFirstBuilder().tier == AbstractRelic.RelicTier.STARTER) {
                    base.add(slot.ID);
                }
            }
        }
        return base;
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

    public ArrayList<PCLCardData> getCards() {
        ArrayList<PCLCardData> base = new ArrayList<>(cardDatas);
        PCLPlayerData<?, ?, ?> data = PGR.getPlayerData(color);
        if (data != null && data.canUseCustom()) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(color)) {
                if (ID.equals(slot.loadout) || (isCore() && slot.loadout == null)) {
                    base.add(slot.getFirstBuilder());
                }
            }
        }
        return base;
    }

    public ArrayList<PCLCardData> getCardMiscs() {
        return miscData;
    }

    public ArrayList<PCLCardData> getColorlessCards() {
        return colorlessData;
    }

    public String getDeckPreviewString() {
        PCLLoadoutData data = getPreset();
        return data != null ? data.name : getName();
    }

    public PCLLoadoutData getDefaultData() {
        final PCLLoadoutData data = new PCLLoadoutData(this);
        resetPreset(data);
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

    public PCLLoadoutStats getLoadoutStats() {
        PCLPlayerData<?, ?, ?> data = getPlayerData();
        if (data == null) {
            return null;
        }

        PCLLoadoutStats trophies = data.getStats(ID);
        if (trophies == null) {
            trophies = new PCLLoadoutStats();
            data.stats.put(ID, trophies);
        }

        return trophies;
    }

    public String getName() {
        LoadoutStrings strings = PGR.getLoadoutStrings(ID);
        return strings != null ? strings.NAME : EUIUtils.EMPTY_STRING;
    }

    public String getNameForFilter() {
        String base = getName();
        return base.isEmpty() ? PGR.core.strings.sui_core : base;
    }

    public int getOrbSlots() {
        return PCLBaseStatEditor.StatType.OrbSlot.getAmount(this, getPreset());
    }

    public PCLPlayerData<?, ?, ?> getPlayerData() {
        return PGR.getPlayerData(color);
    }

    public int getPotionSlots() {
        return PCLBaseStatEditor.StatType.PotionSlot.getAmount(this, getPreset());
    }

    public PCLLoadoutData getPreset() {
        PCLLoadoutData data = presets.get(preset);
        if (data != null) {
            return data;
        }

        if (!presets.isEmpty()) {
            data = EUIUtils.random(presets.values());
            if (data != null) {
                preset = data.ID;
            }
            return data;
        }
        return initializeData();
    }

    // This will never be null because the fallback will be core
    public final PCLResources<?, ?, ?, ?> getResources() {
        return PGR.getResources(color);
    }

    public final ArrayList<String> getStartingBlights() {
        final ArrayList<String> res = new ArrayList<>();

        PCLPlayerData<?, ?, ?> data = getPlayerData();
        if (data != null) {
            List<String> starterRelics = data.getStartingBlights();
            if (starterRelics != null) {
                res.addAll(starterRelics);
            }
        }

        for (LoadoutBlightSlot rSlot : getPreset().blightSlots) {
            res.add(rSlot.selected);
        }
        return res;
    }

    public final ArrayList<String> getStartingDeck() {
        final ArrayList<String> cards = new ArrayList<>();
        for (LoadoutCardSlot slot : getPreset().cardSlots) {
            String cardID = slot.selected;
            if (cardID != null) {
                for (int i = 0; i < slot.getAmount(); i++) {
                    cards.add(cardID);
                }
            }
        }

        return cards;
    }

    public final ArrayList<String> getStartingRelics() {
        ArrayList<String> res = getBaseStartingRelics();
        for (String s : res) {
            if (!UnlockTracker.isRelicSeen(s)) {
                UnlockTracker.markRelicAsSeen(s);
            }
        }

        for (LoadoutRelicSlot rSlot : getPreset().relicSlots) {
            res.add(rSlot.selected);
        }
        return res;
    }

    public PCLCardData getSymbolicCard() {
        if (!cardDatas.isEmpty()) {
            return cardDatas.get(0);
        }
        PCLPlayerData<?, ?, ?> playerData = getPlayerData();
        if (playerData != null && playerData.canUseCustom()) {
            ArrayList<PCLCustomCardSlot> slots = EUIUtils.filter(PCLCustomCardSlot.getCards(color), c -> ID.equals(c.loadout));
            if (!slots.isEmpty()) {
                return slots.get(0).getFirstBuilder();
            }
        }
        return QuestionMark.DATA;
    }

    public PCLLoadoutData initializeData() {
        PCLLoadoutData initData = getDefaultData(); // Ensure this is called and created, because some initialization and unlocking is done at this step
        if (presets.isEmpty()) {
            EUIUtils.logInfo(this, "Presets are empty, creating new temporary default preset");
            preset = initData.ID;
            presets.put(initData.ID, initData);
        }
        return initData;
    }

    public boolean isCardBanned(String cardID) {
        PCLPlayerData<?, ?, ?> playerData = getPlayerData();
        return playerData != null && playerData.config.bannedCards.get().contains(cardID);
    }

    public boolean isCardFromLoadout(AbstractCard card) {
        if (card instanceof PCLCard) {
            return ((PCLCard) card).cardData.loadout == this;
        }
        return isCardFromLoadout(card.cardID);
    }

    public boolean isCardFromLoadout(String cardID) {
        PCLCardData data = PCLCardData.getStaticData(cardID);
        if (data == null) {
            PCLCustomCardSlot slot = PCLCustomCardSlot.get(cardID);
            if (slot != null) {
                data = slot.getFirstBuilder();
            }
        }
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

    public boolean isEditorAllowed(PCLBaseStatEditor beditor, CharacterOption option) {
        return GameUtilities.getMaxAscensionLevel(option.c) >= beditor.type.unlockLevel;
    }

    public boolean isEnabled() {
        PCLResources<?, ?, ?, ?> resources = getResources();
        return (resources.getUnlockLevel() >= unlockLevel &&
                (resources.data == null || resources.data.getCoreLoadout() == this || this.ID.equals(resources.data.config.lastPreset.get()) || resources.data.config.selectedLoadouts.get().contains(this.ID)));
    }

    public boolean isLocked() {
        PCLResources<?, ?, ?, ?> resources = getResources();
        return resources != null && resources.getUnlockLevel() < unlockLevel;
    }

    public boolean isRelicBanned(String id) {
        PCLPlayerData<?, ?, ?> playerData = getPlayerData();
        return playerData != null && playerData.config.bannedRelics.get().contains(id);
    }

    public boolean isRelicFromLoadout(String relicID) {
        PCLRelicData data = PCLRelicData.getStaticData(relicID);
        return data != null && data.loadout == this;
    }

    public void onVictory(int ascensionLevel, int score, boolean postAct3) {
        PCLLoadoutStats trophies = getLoadoutStats();
        PCLPlayerData<?, ?, ?> data = getPlayerData();
        if (data != null && data.selectedLoadout.ID.equals(ID)) {
            if (postAct3) {
                trophies.act4completion = Math.max(trophies.act4completion, ascensionLevel);
            }
            trophies.act3completion = Math.max(trophies.act3completion, ascensionLevel);
            trophies.highScore = Math.max(trophies.highScore, score);
        }
    }

    public void resetPreset(PCLLoadoutData data) {
        data.clear();
        for (PCLBaseStatEditor.StatType type : PCLBaseStatEditor.StatType.values()) {
            data.values.put(type, 0);
        }
        setDefaultBlightsForData(data);
        setDefaultCardsForData(data);
        setDefaultRelicsForData(data);
    }

    protected void setDefaultBlightsForData(PCLLoadoutData data) {
        ArrayList<String> relics = getAvailableBlightIDs();
        if (!relics.isEmpty()) {
            data.addBlightSlot(relics.get(0));
        }
    }

    protected void setDefaultCardsForData(PCLLoadoutData data) {
        ArrayList<String> cards = getAvailableCardIDs();
        if (!cards.isEmpty()) {
            data.addCardSlot(cards.get(0), 4);
        }
        if (cards.size() > 1) {
            data.addCardSlot(cards.get(1), 4);
        }
    }

    protected void setDefaultRelicsForData(PCLLoadoutData data) {
        ArrayList<String> relics = getAvailableRelicIDs();
        if (!relics.isEmpty()) {
            data.addRelicSlot(relics.get(0));
        }
    }

    public void sortItems() {
        cardDatas.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
        colorlessData.sort((a, b) -> StringUtils.compare(a.ID, b.ID));
    }

    public abstract ArrayList<String> getBaseStartingRelics();

    // This is used to show the number of cards currently selected. We update the amount of this skill to update the card description without rebuilding it from scratch
    // TODO use custom choice items instead of cards
    protected class FakeSkill extends PSpecialSkill {
        public FakeSkill() {
            super("", PGR.core.strings.sui_unlocked, (a, b, c) -> {
            }, 0, getCards().size());
        }
    }

    protected class FakeSkill2 extends PSpecialSkill {
        public FakeSkill2() {
            super("", PGR.core.strings.sui_selected, (a, b, c) -> {
            }, 0, getCards().size());
        }
    }
}