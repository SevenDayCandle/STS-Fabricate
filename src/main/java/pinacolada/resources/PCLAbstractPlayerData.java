package pinacolada.resources;

import basemod.BaseMod;
import basemod.abstracts.CustomUnlock;
import basemod.abstracts.CustomUnlockBundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.unlock.AbstractUnlock;
import extendedui.EUIUtils;
import pinacolada.blights.common.AbstractGlyphBlight;
import pinacolada.blights.common.GlyphBlight;
import pinacolada.blights.common.GlyphBlight1;
import pinacolada.blights.common.GlyphBlight2;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutData;
import pinacolada.resources.loadout.PCLRuntimeLoadout;
import pinacolada.resources.loadout.PCLTrophies;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import static pinacolada.resources.loadout.PCLLoadoutData.TInfo;
import static pinacolada.resources.pcl.PCLCoreConfig.JSON_FILTER;

// Copied and modified from STS-AnimatorMod
public abstract class PCLAbstractPlayerData
{
    public static final int ASCENSION_GLYPH1_LEVEL_STEP = 2;
    public static final int ASCENSION_GLYPH1_UNLOCK = 16;
    public static final int MAX_UNLOCK_LEVEL = 8;
    public static final ArrayList<AbstractGlyphBlight> Glyphs = new ArrayList<>();
    public final HashMap<Integer, PCLLoadout> loadouts = new HashMap<>();
    public final HashMap<Integer, PCLTrophies> trophies = new HashMap<>();
    public final PCLResources<?,?,?> resources;
    public PCLLoadout selectedLoadout;

    public PCLAbstractPlayerData(PCLResources<?,?,?> resources)
    {
        this.resources = resources;
        this.selectedLoadout = getCoreLoadout();
    }

    public static String getLoadoutPrefix(PCLResources<?,?,?> resources)
    {
        return resources.config.getConfigPath() + resources.id + "_slot";
    }

    public static String getLoadoutPath(PCLResources<?,?,?> resources, int id, int slot)
    {
        return getLoadoutPrefix(resources) + "_" + id + "_" + slot + ".json";
    }

    public static Random getRNG()
    {
        return PGR.core.dungeon.getRNG();
    }

    public static void postInitialize()
    {
        Glyphs.add(new GlyphBlight());
        Glyphs.add(new GlyphBlight1());
        Glyphs.add(new GlyphBlight2());
    }

    protected final void addBaseLoadouts()
    {
        loadouts.clear();
        PCLLoadout core = getCoreLoadout();
        loadouts.put(core.id, core);
        List<PCLLoadout> availableLoadouts = getAvailableLoadouts();
        if (availableLoadouts.size() > 0)
        {
            this.selectedLoadout = availableLoadouts.get(0);
            for (PCLLoadout loadout : getAvailableLoadouts())
            {
                loadouts.put(loadout.id, loadout);
            }
        }


        for (PCLLoadout loadout : loadouts.values())
        {
            if (loadout.unlockLevel <= 0)
            {
                continue;
            }

            final String cardID = loadout.cardData.get(0).ID;
            final CustomUnlock unlock = new CustomUnlock(AbstractUnlock.UnlockType.MISC, cardID);
            unlock.type = AbstractUnlock.UnlockType.CARD;
            unlock.card = new PCLRuntimeLoadout(loadout).buildCard();
            unlock.key = unlock.card.cardID = PGR.core.createID("series:" + loadout.getName());

            CustomUnlockBundle bundle = BaseMod.getUnlockBundleFor(resources.playerClass, loadout.unlockLevel - 1);
            if (bundle == null)
            {
                bundle = new CustomUnlockBundle(AbstractUnlock.UnlockType.MISC, "", "", "");
                bundle.getUnlocks().clear();
                bundle.getUnlockIDs().clear();
                bundle.getUnlockIDs().add(unlock.key);
                bundle.getUnlocks().add(unlock);
                bundle.unlockType = AbstractUnlock.UnlockType.CARD;
            }
            else
            {
                bundle.getUnlockIDs().add(unlock.key);
                bundle.getUnlocks().add(unlock);
            }

            BaseMod.addUnlockBundle(bundle, resources.playerClass, loadout.unlockLevel - 1);
        }
    }

    private void deserializeCustomLoadouts()
    {
        for (FileHandle f : resources.config.getConfigFolder().list(JSON_FILTER))
        {
            String path = f.path();
            try
            {
                String jsonString = f.readString();
                PCLLoadoutData.LoadoutInfo loadoutInfo = EUIUtils.deserialize(jsonString, TInfo.getType());
                final PCLLoadout loadout = getLoadout(loadoutInfo.loadout);
                final PCLLoadoutData loadoutData = new PCLLoadoutData(loadout, loadoutInfo);

                if (loadoutData.validate().isValid)
                {
                    loadout.presets[loadoutInfo.preset] = loadoutData;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                EUIUtils.logError(PCLCustomCardSlot.class, "Could not load loadout : " + path);
            }
        }
    }

    private void deserializeTrophies(String data)
    {
        trophies.clear();

        if (data != null && data.length() > 0)
        {
            //final String decoded = Base64Coder.decodeString(data);
            final String[] items = EUIUtils.splitString("|", data);

            if (items.length > 0)
            {
                int loadoutID = EUIUtils.parseInt(items[0], -1);
                if (loadoutID >= 0)
                {
                    selectedLoadout = getLoadout(loadoutID);
                }

                for (int i = 1; i < items.length; i++)
                {
                    final PCLTrophies trophies = new PCLTrophies(items[i]);
                    this.trophies.put(trophies.ID, trophies);
                }
            }
        }
    }

    public abstract List<PCLLoadout> getAvailableLoadouts();

    public abstract PCLLoadout getCoreLoadout();

    public List<PCLLoadout> getEveryLoadout()
    {
        return new ArrayList<>(loadouts.values());
    }

    public PCLLoadout getLoadout(int id)
    {
        return loadouts.get(id);
    }

    public String getStartingRelicID() {return null;}

    public PCLTrophies getTrophies(int id)
    {
        return trophies.get(id);
    }

    public void initialize()
    {
        addBaseLoadouts();
        reload();
    }

    public void initializeCardPool()
    {
        // Always include the selected loadout. If for some reason none exists, assign one at random
        if (selectedLoadout == null)
        {
            selectedLoadout = EUIUtils.random(EUIUtils.filter(getEveryLoadout(), loadout -> resources.getUnlockLevel() >= loadout.unlockLevel));
        }

        for (PCLLoadout loadout : getEveryLoadout())
        {
            final PCLRuntimeLoadout rloadout = PCLRuntimeLoadout.tryCreate(loadout);
            // Series must be unlocked to be present in-game
            if (rloadout != null && !rloadout.isLocked)
            {
                PGR.core.dungeon.addLoadout(rloadout);
            }
        }

        if (Settings.isDebug)
        {
            EUIUtils.logInfo(this, "Starting Loadout: " + (selectedLoadout != null ? selectedLoadout.id : "N/A"));
            EUIUtils.logInfo(this, "Starting Series: " + EUIUtils.joinStrings(",", EUIUtils.map(PGR.core.dungeon.loadouts, l -> l.loadout.getName())));
        }

        PGR.core.dungeon.bannedCards.addAll(resources.config.bannedCards.get());
        PGR.core.dungeon.bannedRelics.addAll(resources.config.bannedRelics.get());
    }

    public PCLLoadout prepareLoadout()
    {
        int unlockLevel = resources.getUnlockLevel();
        if (selectedLoadout == null || unlockLevel < selectedLoadout.unlockLevel)
        {
            RandomizedList<PCLLoadout> list = new RandomizedList<>();
            for (PCLLoadout loadout : loadouts.values())
            {
                if (unlockLevel >= loadout.unlockLevel)
                {
                    list.add(loadout);
                }
            }

            selectedLoadout = list.retrieve(new com.megacrit.cardcrawl.random.Random());
        }

        return selectedLoadout;
    }

    public void recordTrueVictory(int ascensionLevel, int trophyLevel, int score)
    {
        if (ascensionLevel < 0) // Ascension reborn mod adds negative ascension levels
        {
            return;
        }

        if (selectedLoadout != null)
        {
            selectedLoadout.onVictory(ascensionLevel, trophyLevel, score);
        }

        saveTrophies(true);
    }

    public void recordVictory(int ascensionLevel)
    {
        if (ascensionLevel < 0) // Ascension reborn mod adds negative ascension levels
        {
            return;
        }

        if (selectedLoadout != null)
        {
            selectedLoadout.onVictory(ascensionLevel, 1, 0); // Do not record score unless you are actually at the gameover screen
        }

        saveTrophies(true);
    }

    public void reload()
    {
        deserializeTrophies(resources.config.trophies.get());
        deserializeCustomLoadouts();

        if (selectedLoadout == null)
        {
            selectedLoadout = getCoreLoadout();
        }
    }

    public void saveTrophies(boolean flush)
    {
        EUIUtils.logInfoIfDebug(this, "Saving Trophies");

        resources.config.trophies.set(serializeTrophies(), flush);
    }

    public void saveLoadouts()
    {
        final int level = resources.getUnlockLevel();
        for (PCLLoadout loadout : loadouts.values())
        {
            if (loadout.unlockLevel <= level)
            {
                for (PCLLoadoutData data : loadout.presets)
                {
                    if (data == null)
                    {
                        continue;
                    }

                    FileHandle writer = Gdx.files.absolute(getLoadoutPath(resources, loadout.id, data.preset));
                    writer.writeString(EUIUtils.serialize(new PCLLoadoutData.LoadoutInfo(loadout.id, data), TInfo.getType()), false);
                }
            }
        }
    }

    // SelectedLoadout|Series_1,Trophy1,Trophy2,Trophy3|Series_2,Trophy1,Trophy2,Trophy3|...
    private String serializeTrophies()
    {
        final StringJoiner sj = new StringJoiner("|");

        if (selectedLoadout == null)
        {
            selectedLoadout = EUIUtils.random(EUIUtils.filter(getEveryLoadout(), loadout -> resources.getUnlockLevel() >= loadout.unlockLevel));
        }
        sj.add(String.valueOf(selectedLoadout.id));

        for (PCLTrophies t : trophies.values())
        {
            sj.add(t.serialize());
        }

        return sj.toString();
    }
}
