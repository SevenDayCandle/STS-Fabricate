package pinacolada.cards.base.fields;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomLoadable;

import java.util.ArrayList;
import java.util.HashMap;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomFlagInfo extends PCLCustomLoadable {
    private static final HashMap<String, PCLCustomFlagInfo> CUSTOM_FLAGS = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    private static final TypeToken<PCLCustomFlagInfo> TTOKEN = new TypeToken<PCLCustomFlagInfo>() {
    };
    private static final TypeToken<HashMap<Settings.GameLanguage, String>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, String>>() {
    };
    public static final String SUBFOLDER = "flag";
    public String languageStrings;
    public String colorString;
    public transient AbstractCard.CardColor color = AbstractCard.CardColor.COLORLESS;
    public transient CardFlag flag;
    public transient HashMap<Settings.GameLanguage, String> languageMap;

    public PCLCustomFlagInfo(String id, HashMap<Settings.GameLanguage, String> languageMap, AbstractCard.CardColor color) {
        ID = id;
        filePath = makeFilePath();
        this.languageMap = languageMap;
        this.color = color;
        linkFlag();
    }

    /**
     * Subscribe a provider that provides a folder to load custom loadouts from whenever the cards are reloaded
     */
    public static void addProvider(CustomFileProvider provider) {
        PROVIDERS.add(provider);
    }

    public static PCLCustomFlagInfo get(String id) {
        return CUSTOM_FLAGS.get(id);
    }

    public static ArrayList<PCLCustomFlagInfo> getFlags(AbstractCard.CardColor color) {
        if (color == null) {
            return new ArrayList<>(CUSTOM_FLAGS.values());
        }
        return EUIUtils.filter(CUSTOM_FLAGS.values(), l -> l.color == color);
    }

    public static void initialize() {
        CUSTOM_FLAGS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder());
        }
    }

    public static boolean isIDDuplicate(String input) {
        return isIDDuplicate(input, CUSTOM_FLAGS.values());
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleFlagImpl(f);
        }
    }

    private static void loadSingleFlagImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomFlagInfo slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setup(path);
            CUSTOM_FLAGS.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Loadout: " + path);
        }
    }

    public static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(StringUtils.lowerCase(String.valueOf(color)), CUSTOM_FLAGS.values());
    }

    public static void register(PCLCustomFlagInfo loadout) {
        CUSTOM_FLAGS.put(loadout.ID, loadout);
    }

    public void commit() {
        flag.setName(languageMap.getOrDefault(Settings.language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG, ID)));
        colorString = String.valueOf(color);
        languageStrings = EUIUtils.serialize(languageMap);

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

    private void linkFlag() {
        flag = new CardFlag(ID, color != null && color != AbstractCard.CardColor.COLORLESS ? EUIUtils.array(color) : EUIUtils.array());
        flag.setName(languageMap.getOrDefault(Settings.language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG, ID)));
    }

    private void setup(String fp) {
        color = AbstractCard.CardColor.valueOf(colorString);
        languageMap = EUIUtils.deserialize(languageStrings, TStrings.getType());
        if (languageMap == null) {
            languageMap = new HashMap<>();
        }
        filePath = fp;
        linkFlag();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Loadout: " + fp);
    }

    public void wipe() {
        CUSTOM_FLAGS.remove(ID);
        FileHandle writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Loadout: " + filePath);
    }
}
