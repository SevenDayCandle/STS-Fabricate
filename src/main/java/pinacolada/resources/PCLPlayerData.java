package pinacolada.resources;

import basemod.BaseMod;
import basemod.abstracts.CustomUnlock;
import basemod.abstracts.CustomUnlockBundle;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.characters.PCLCharacter;
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.dungeon.modifiers.Glyph0;
import pinacolada.dungeon.modifiers.Glyph1;
import pinacolada.effects.PCLEffect;
import pinacolada.monsters.PCLCreatureData;
import pinacolada.monsters.PCLTutorialMonster;
import pinacolada.resources.loadout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public abstract class PCLPlayerData<T extends PCLResources<?, ?, ?, ?>, U extends PCLCharacterConfig, V extends PCLCharacter> {
    private static final TypeToken<HashMap<String, PCLLoadoutStats>> TStats = new TypeToken<HashMap<String, PCLLoadoutStats>>() {
    };
    public static final int ASCENSION_GLYPH1_LEVEL_STEP = 2;
    public static final int ASCENSION_GLYPH1_UNLOCK = 16;
    public static final int MAX_UNLOCK_LEVEL = 8;
    public static final int DEFAULT_HP = 70;
    public static final int DEFAULT_GOLD = 99;
    public static final int DEFAULT_DRAW = 5;
    public static final int DEFAULT_ENERGY = 3;
    public static final int DEFAULT_ORBS = 0;
    public static final int MINIMUM_CARDS = 70;
    public static final int MINIMUM_COLORLESS = 30;
    public static final ArrayList<AbstractGlyph> GLYPHS = new ArrayList<>();
    public final HashMap<String, PCLLoadout> loadouts = new HashMap<>();
    public final HashMap<String, PCLLoadoutStats> stats = new HashMap<>();
    public final T resources;
    public final U config;
    public final int baseHP;
    public final int baseGold;
    public final int baseDraw;
    public final int baseEnergy;
    public final int baseOrbs;
    public final int minimumCards;
    public final int minimumColorless;
    protected boolean hasTutorials;
    public final String characterID;
    public PCLLoadout selectedLoadout;

    public PCLPlayerData(T resources) {
        this(resources, DEFAULT_HP, DEFAULT_GOLD, DEFAULT_DRAW, DEFAULT_ENERGY, DEFAULT_ORBS, MINIMUM_CARDS, MINIMUM_COLORLESS);
    }

    public PCLPlayerData(T resources, int hp, int gold, int draw, int energy, int orbs, int minCards, int minColorless) {
        this.resources = resources;
        this.config = getConfig();
        this.selectedLoadout = getCoreLoadout();
        this.baseHP = hp;
        this.baseGold = gold;
        this.baseDraw = draw;
        this.baseEnergy = energy;
        this.baseOrbs = orbs;
        this.minimumCards = minCards;
        this.minimumColorless = minColorless;
        this.characterID = resources.createID(getCharacterClass().getSimpleName());
    }

    public static Random getRNG() {
        return PGR.dungeon.getRNG();
    }

    public static void postInitialize() {
        GLYPHS.add(new Glyph0());
        GLYPHS.add(new Glyph1());
    }

    public void addTutorial(PCLCreatureData data) {
        PCLTutorialMonster.register(data, config.seenTutorial, p -> p.chosenClass == this.resources.playerClass);
        hasTutorials = true;
    }

    public void addTutorial(PCLCreatureData data, FuncT1<Boolean, AbstractPlayer> canShow) {
        PCLTutorialMonster.register(data, config.seenTutorial, canShow);
        hasTutorials = true;
    }

    public void addTutorial(PCLCreatureData data, STSConfigItem<Boolean> configItem, FuncT1<Boolean, AbstractPlayer> canShow) {
        PCLTutorialMonster.register(data, configItem, canShow);
        hasTutorials = true;
    }

    public boolean canChangeSkin() {
        return false;
    }

    public boolean canEditCore() {
        return false;
    }

    public boolean canEditPool() {
        return true;
    }

    public boolean canUseAugments() {
        return true;
    }

    public boolean canUseCustom() {
        return false;
    }

    public boolean canUseCustomColorless() {
        return false;
    }

    public boolean canUseSummons() {
        return true;
    }

    protected void addUnlockBundle(PCLLoadout loadout) {
        if (loadout.unlockLevel > 0) {
            final String cardID = loadout.cardDatas.get(0).ID;
            final CustomUnlock unlock = new CustomUnlock(AbstractUnlock.UnlockType.MISC, cardID);
            unlock.type = AbstractUnlock.UnlockType.CARD;
            unlock.card = loadout.buildCard(false, true);
            unlock.card.isSeen = true;
            unlock.card.isLocked = false;
            unlock.key = unlock.card.cardID = PGR.core.createID(loadout.getName());

            CustomUnlockBundle bundle = BaseMod.getUnlockBundleFor(resources.playerClass, loadout.unlockLevel - 1);
            if (bundle == null) {
                bundle = new CustomUnlockBundle(AbstractUnlock.UnlockType.MISC, "", "", "");
                bundle.getUnlocks().clear();
                bundle.getUnlockIDs().clear();
                bundle.getUnlockIDs().add(unlock.key);
                bundle.getUnlocks().add(unlock);
                bundle.unlockType = AbstractUnlock.UnlockType.CARD;
            }
            else {
                bundle.getUnlockIDs().add(unlock.key);
                bundle.getUnlocks().add(unlock);
            }

            BaseMod.addUnlockBundle(bundle, resources.playerClass, loadout.unlockLevel - 1);
        }
    }

    public V createCharacter() {
        try {
            return getCharacterClass().newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void deserializeSelectedLoadout() {
        selectedLoadout = PCLLoadout.get(config.lastLoadout.get());
        if (selectedLoadout == null) {
            selectedLoadout = prepareLoadout();
        }
        selectedLoadout.preset = config.lastPreset.get();
    }

    protected void deserializeStats() {
        stats.clear();
        HashMap<String, PCLLoadoutStats> stats = EUIUtils.deserialize(config.trophies.get(), TStats.getType());
        if (stats != null) {
            stats.putAll(EUIUtils.deserialize(config.trophies.get(), TStats.getType()));
        }
    }

    public String[] getAdditionalBlightIDs(boolean customEnabled) {
        return null;
    }

    public String[] getAdditionalPotionIDs(boolean customEnabled) {
        return null;
    }

    public String[] getAdditionalRelicIDs(boolean customEnabled) {
        return null;
    }

    public List<PCLLoadout> getAvailableLoadouts() {
        return EUIUtils.filter(PCLLoadout.getAll(resources.cardColor), l -> !l.isCore() && l.cardDatas.size() > 0 && l.unlockLevel >= 0);
    }

    public CharacterStrings getCharacterStrings() {
        return PGR.getCharacterStrings(characterID);
    }

    public PCLEffect getCharSelectScreenAnimation() {
        return null;
    }

    public List<PCLLoadout> getEveryLoadout() {
        ArrayList<PCLLoadout> base = new ArrayList<>(loadouts.values());
        if (canUseCustom()) {
            for (PCLCustomLoadoutInfo lo : PCLCustomLoadoutInfo.getLoadouts(resources.cardColor)) {
                base.add(lo.loadout);
            }
        }
        return base;
    }

    public String getLoadoutPath(String id, int slot) {
        return config.getConfigPath() + "_" + id.replace(':', '-') + "_" + slot + ".json";
    }

    public List<String> getStartingBlights() {
        return null;
    }

    public PCLLoadoutStats getStats(String id) {
        return stats.get(id);
    }

    public boolean hasLoadouts() {
        int baseCount = loadouts.size();
        if (canUseCustom()) {
            baseCount += PCLCustomLoadoutInfo.getLoadouts(resources.cardColor).size();
        }
        return baseCount > 1;
    }

    public boolean hasTutorials() {
        return hasTutorials;
    }

    public void initialize() {
        initializeLoadouts();
        reload();
        config.initializeOptions();
    }

    protected final void initializeLoadouts() {
        loadouts.clear();
        PCLLoadout core = getCoreLoadout();
        loadouts.put(core.ID, core);
        for (PCLLoadout loadout : getAvailableLoadouts()) {
            loadouts.put(loadout.ID, loadout);
        }

        for (PCLLoadout loadout : loadouts.values()) {
            addUnlockBundle(loadout);
            loadout.sortItems();
        }
    }

    public PCLLoadout prepareLoadout() {
        int unlockLevel = resources.getUnlockLevel();
        if (selectedLoadout == null || unlockLevel < selectedLoadout.unlockLevel || selectedLoadout.isCore()) {
            for (PCLLoadout loadout : loadouts.values()) {
                if (unlockLevel >= loadout.unlockLevel && !loadout.isCore()) {
                    selectedLoadout = loadout;
                    break;
                }
            }
            if (selectedLoadout == null || unlockLevel < selectedLoadout.unlockLevel) {
                selectedLoadout = getCoreLoadout();
            }
        }

        return selectedLoadout;
    }

    public void recordTrueVictory(int ascensionLevel, int score, boolean postAct3) {
        if (selectedLoadout != null) {
            selectedLoadout.onVictory(ascensionLevel, score, true);
        }

        saveStats();
    }

    public void recordVictory(int ascensionLevel) {
        if (selectedLoadout != null) {
            selectedLoadout.onVictory(ascensionLevel, 1, false); // Do not record score unless you are actually at the gameover screen
        }

        saveStats();
    }

    public void registerCharacter() {
        BaseMod.addCharacter(createCharacter(), resources.images.getCharButtonPath(), resources.images.getCharBackgroundPath(), resources.playerClass);
    }

    public void reload() {
        if (config != null) {
            config.load(CardCrawlGame.saveSlot);
            deserializeStats();
            deserializeSelectedLoadout();
        }
    }

    public void saveSelectedLoadout() {
        if (selectedLoadout == null || selectedLoadout == getCoreLoadout()) {
            selectedLoadout = prepareLoadout();
        }
        config.lastLoadout.set(selectedLoadout.ID);
        config.lastPreset.set(selectedLoadout.preset != null ? selectedLoadout.preset : EUIUtils.EMPTY_STRING);
    }

    public void saveStats() {
        config.trophies.set(EUIUtils.serialize(stats));
    }

    public abstract Class<V> getCharacterClass();

    public abstract U getConfig();

    public abstract PCLLoadout getCoreLoadout();
}
