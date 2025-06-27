package pinacolada.resources.loadout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.resources.PGR;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomLoadoutInfo extends PCLCustomLoadable {
    private static final HashMap<String, PCLCustomLoadoutInfo> CUSTOM_LOADOUTS = new HashMap<>();
    private static final ArrayList<TupleT2<URL,String>> PROVIDERS = new ArrayList<>();
    private static final TypeToken<PCLCustomLoadoutInfo> TTOKEN = new TypeToken<PCLCustomLoadoutInfo>() {
    };
    protected static final String SUBFOLDER = "loadout";
    public String languageStrings;
    public String colorString;
    public int unlockLevel;
    public transient AbstractCard.CardColor color = AbstractCard.CardColor.COLORLESS;
    public transient PCLCustomLoadout loadout;

    public PCLCustomLoadoutInfo(String id, String languageStrings, AbstractCard.CardColor color) {
        ID = id;
        filePath = makeFilePath();
        this.languageStrings = languageStrings;
        this.color = color;
        linkLoadout();
    }

    /**
     * Subscribe a provider that provides a folder to load custom loadouts from whenever the cards are reloaded
     */
    public static void addProvider(String id, String path) {
        ModInfo info = EUIGameUtils.getModInfoFromID(id);
        if (info != null) {
            addProvider(info, path);
        }
        else {
            EUIUtils.logError(PCLCustomLoadoutInfo.class, "Failed to add provider. Invalid mod ID: " + id);
        }
    }

    public static void addProvider(ModInfo info, String path) {
        PROVIDERS.add(new TupleT2<>(info.jarURL, path));
    }

    public static PCLCustomLoadoutInfo get(String id) {
        return CUSTOM_LOADOUTS.get(id);
    }

    public static ArrayList<PCLCustomLoadoutInfo> getLoadouts(AbstractCard.CardColor color) {
        if (color == null) {
            return new ArrayList<>(CUSTOM_LOADOUTS.values());
        }
        return EUIUtils.filter(CUSTOM_LOADOUTS.values(), l -> l.color == color);
    }

    public static void initialize() {
        CUSTOM_LOADOUTS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (TupleT2<URL,String> provider : PROVIDERS) {
            doForFilesInJar(provider.v1, provider.v2, PCLCustomLoadoutInfo::loadSingleImpl);
        }
    }

    public static boolean isIDDuplicate(String input) {
        return isIDDuplicate(input, CUSTOM_LOADOUTS.values());
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleImpl(f);
        }
    }

    private static void loadSingleImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomLoadoutInfo slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setup(path);
            CUSTOM_LOADOUTS.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomLoadoutInfo.class, "Could not load Custom Loadout: " + path);
        }
    }

    public static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(StringUtils.lowerCase(String.valueOf(color)), CUSTOM_LOADOUTS.values());
    }

    public static void register(PCLCustomLoadoutInfo loadout) {
        CUSTOM_LOADOUTS.put(loadout.ID, loadout);
    }

    public void commit() {
        colorString = String.valueOf(color);

        String newFilePath = makeFilePath();
        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Loadout: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        filePath = newFilePath;

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false, HttpParametersUtils.defaultEncoding);
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved Custom Loadout: " + filePath);
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    private void linkLoadout() {
        loadout = new PCLCustomLoadout(this);
    }

    private void setup(String fp) {
        color = AbstractCard.CardColor.valueOf(colorString);
        filePath = fp;
        linkLoadout();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Loadout: " + fp);
    }

    public void wipe() {
        CUSTOM_LOADOUTS.remove(ID);
        FileHandle writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Loadout: " + filePath);
    }
}
