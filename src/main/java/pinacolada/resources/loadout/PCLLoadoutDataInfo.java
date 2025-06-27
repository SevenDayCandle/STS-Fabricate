package pinacolada.resources.loadout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.resources.PGR;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLLoadoutDataInfo extends PCLCustomLoadable {
    static final long serialVersionUID = 1L;
    protected static final String SUBFOLDER = "preset";
    protected static final FileNameExtensionFilter EXTENSIONS = new FileNameExtensionFilter("*.json", "json");
    public static final TypeToken<PCLLoadoutDataInfo> TInfo = new TypeToken<PCLLoadoutDataInfo>() {
    };
    public String loadout;
    public String name;
    public String values;
    public String[] blights;
    public String[] relics;
    public LoadoutCardInfo[] cards;

    public PCLLoadoutDataInfo(String loadout, PCLLoadoutData data) {
        this.ID = data.ID;
        this.loadout = loadout;
        name = data.name;
        values = EUIUtils.serialize(data.values);
        blights = EUIUtils.arrayMapAsNonnull(data.blightSlots, String.class, d -> d.selected);
        cards = EUIUtils.arrayMapAsNonnull(data.cardSlots, LoadoutCardInfo.class, d -> d.selected != null ? new LoadoutCardInfo(d.selected, d.amount) : null);
        relics = EUIUtils.arrayMapAsNonnull(data.relicSlots, String.class, d -> d.selected);
        filePath = makeFilePath();
    }

    public static String makeNewID(PCLLoadout loadout) {
        ArrayList<String> keys = EUIUtils.flattenList(EUIUtils.map(PCLLoadout.getAll(loadout.color), l -> l.presets.keySet()));
        return makeNewIDByKey(loadout.ID.replace(':','_'), keys);
    }

    public static PCLLoadoutDataInfo loadFreeFile() {
        try {
            File openedFile = EUIUtils.loadFile(EXTENSIONS, PGR.config.lastCSVPath);
            if (openedFile != null) {
                FileHandle f = Gdx.files.absolute(openedFile.getAbsolutePath());
                String path = f.path();
                String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
                PCLLoadoutDataInfo loadoutInfo = EUIUtils.deserialize(jsonString, TInfo.getType());
                return loadoutInfo;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomLoadoutInfo.class, "Failed to load loadout file: " + e.getLocalizedMessage());
        }
        return null;
    }

    private static void loadSingleLoadoutImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLLoadoutDataInfo loadoutInfo = EUIUtils.deserialize(jsonString, TInfo.getType());
            final PCLLoadout loadout = PCLLoadout.get(loadoutInfo.loadout);
            if (loadout != null) {
                final PCLLoadoutData loadoutData = new PCLLoadoutData(loadout, loadoutInfo);
                if (loadoutData.validate().isValid) {
                    loadout.presets.put(loadoutData.ID, loadoutData);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Preset: " + path);
        }
    }

    public static void reloadLoadouts() {
        for (PCLLoadout loadout : PCLLoadout.getAll()) {
            loadout.presets.clear();
        }
        for (FileHandle f : getCustomFolder(SUBFOLDER).list(JSON_FILTER)) {
            loadSingleLoadoutImpl(f);
        }
        for (PCLLoadout loadout : PCLLoadout.getAll()) {
            loadout.initializeData(); // Ensure that necessary unlocks are carried out on new save files
        }
    }

    public void commit() {
        String newFilePath = makeFilePath();
        // If the file path has changed and the original file exists, we should move the file
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Preset: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        filePath = newFilePath;

        writer.writeString(EUIUtils.serialize(this, TInfo.getType()), false, HttpParametersUtils.defaultEncoding);
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved Preset: " + filePath);
    }

    public void saveToFile() {
        File file = EUIUtils.saveFile(EXTENSIONS, PGR.config.lastCSVPath);
        if (file != null) {
            FileHandle handle = Gdx.files.absolute(file.getAbsolutePath());
            if (handle.exists()) {
                handle.delete();
            }
            handle.writeString(EUIUtils.serialize(this, TInfo.getType()), false, HttpParametersUtils.defaultEncoding);
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved to file: " + filePath);
        }
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public static class LoadoutCardInfo implements Serializable {
        static final long serialVersionUID = 1L;
        public String id;
        public Integer count;

        public LoadoutCardInfo(String id, Integer count) {
            this.id = id;
            this.count = count;
        }
    }
}
