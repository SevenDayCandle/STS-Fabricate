package pinacolada.resources;

import basemod.BaseMod;
import basemod.abstracts.CustomUnlock;
import basemod.abstracts.CustomUnlockBundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.dungeon.modifiers.AbstractGlyph;
import pinacolada.dungeon.modifiers.Glyph0;
import pinacolada.dungeon.modifiers.Glyph1;
import pinacolada.effects.PCLEffect;
import pinacolada.monsters.PCLCreatureData;
import pinacolada.monsters.PCLTutorialMonster;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutData;
import pinacolada.resources.loadout.PCLLoadoutStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pinacolada.resources.loadout.PCLLoadoutData.TInfo;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

// Copied and modified from STS-AnimatorMod
public abstract class AbstractPlayerData<T extends PCLResources<?, ?, ?, ?>, U extends PCLCharacterConfig> {
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
    public final boolean useSummons;
    public final boolean useAugments;
    public PCLLoadout selectedLoadout;

    public AbstractPlayerData(T resources) {
        this(resources, DEFAULT_HP, DEFAULT_GOLD, DEFAULT_DRAW, DEFAULT_ENERGY, DEFAULT_ORBS, true, true);
    }

    public AbstractPlayerData(T resources, int hp, int gold, int draw, int energy, int orbs, boolean useSummons, boolean useAugments) {
        this.resources = resources;
        this.config = getConfig();
        this.selectedLoadout = getCoreLoadout();
        this.baseHP = hp;
        this.baseGold = gold;
        this.baseDraw = draw;
        this.baseEnergy = energy;
        this.baseOrbs = orbs;
        this.useSummons = useSummons;
        this.useAugments = useAugments;
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
    }

    public void addTutorial(PCLCreatureData data, FuncT1<Boolean, AbstractPlayer> canShow) {
        PCLTutorialMonster.register(data, config.seenTutorial, canShow);
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

    private void deserializeCustomLoadouts() {
        for (FileHandle f : config.getConfigFolder().list(JSON_FILTER)) {
            String path = f.path();
            try {
                String jsonString = f.readString();
                PCLLoadoutData.LoadoutInfo loadoutInfo = EUIUtils.deserialize(jsonString, TInfo.getType());
                final PCLLoadout loadout = getLoadout(loadoutInfo.loadout);
                final PCLLoadoutData loadoutData = new PCLLoadoutData(loadout, loadoutInfo);

                if (loadoutData.validate().isValid) {
                    loadout.presets[loadoutInfo.preset] = loadoutData;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(PCLCustomCardSlot.class, "Could not load loadout : " + path);
            }
        }
    }

    protected void deserializeSelectedLoadout() {
        selectedLoadout = getLoadout(config.lastLoadout.get());
        if (selectedLoadout == null) {
            selectedLoadout = prepareLoadout();
        }
    }

    protected void deserializeStats() {
        stats.clear();
        HashMap<String, PCLLoadoutStats> stats = EUIUtils.deserialize(config.trophies.get(), TStats.getType());
        if (stats != null) {
            stats.putAll(EUIUtils.deserialize(config.trophies.get(), TStats.getType()));
        }
    }

    public String[] getAdditionalCardIDs() {
        return null;
    }

    public String[] getAdditionalRelicIDs() {
        return null;
    }

    public List<PCLLoadout> getAvailableLoadouts() {
        return EUIUtils.filter(PCLLoadout.getAll(resources.cardColor), l -> !l.isCore() && l.cardDatas.size() > 0 && l.unlockLevel >= 0);
    }

    public PCLEffect getCharSelectScreenAnimation() {
        return null;
    }

    public List<PCLLoadout> getEveryLoadout() {
        return new ArrayList<>(loadouts.values());
    }

    public PCLLoadout getLoadout(String id) {
        return loadouts.get(id);
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

    public void initialize() {
        initializeLoadouts();
        reload();
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

    public void reload() {
        if (config != null) {
            config.load(CardCrawlGame.saveSlot);
            deserializeStats();
            deserializeCustomLoadouts();
            deserializeSelectedLoadout();
        }
    }

    public void saveLoadouts() {
        final int level = resources.getUnlockLevel();
        for (PCLLoadout loadout : loadouts.values()) {
            if (loadout.unlockLevel <= level) {
                for (PCLLoadoutData data : loadout.presets) {
                    if (data == null) {
                        continue;
                    }

                    FileHandle writer = Gdx.files.absolute(getLoadoutPath(loadout.ID, data.preset));
                    writer.writeString(EUIUtils.serialize(new PCLLoadoutData.LoadoutInfo(loadout.ID, data), TInfo.getType()), false);
                }
            }
        }
        saveSelectedLoadout();
    }

    public void saveSelectedLoadout() {
        if (selectedLoadout == null || selectedLoadout == getCoreLoadout()) {
            selectedLoadout = prepareLoadout();
        }
        config.lastLoadout.set(selectedLoadout.ID);
    }

    public void saveStats() {
        config.trophies.set(EUIUtils.serialize(stats));
    }

    public void updateRelicsForDungeon() {
        String[] additional = getAdditionalRelicIDs();
        if (additional != null) {
            for (String id : additional) {
                AbstractRelic r = RelicLibrary.getRelic(id);
                // Circlet means that the relic didn't exist
                if (!(r instanceof Circlet)) {
                    PGR.dungeon.addRelic(id, r.tier);
                }
            }
        }
    }

    public abstract U getConfig();

    public abstract PCLLoadout getCoreLoadout();
}
