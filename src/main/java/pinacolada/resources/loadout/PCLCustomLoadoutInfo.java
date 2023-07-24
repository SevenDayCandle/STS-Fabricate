package pinacolada.resources.loadout;

import com.badlogic.gdx.files.FileHandle;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.misc.PCLCustomLoadable;

import java.util.ArrayList;
import java.util.HashMap;

import static pinacolada.resources.PCLMainConfig.JSON_FILTER;

public class PCLCustomLoadoutInfo extends PCLCustomLoadable {
    private static final TypeToken<PCLCustomLoadoutInfo> TTOKEN = new TypeToken<PCLCustomLoadoutInfo>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomLoadoutInfo>> CUSTOM_LOADOUTS = new HashMap<>();
    public static final String SUBFOLDER = "loadout";
    protected transient String filePath;
    protected transient String imagePath;
    public String ID;
    public String languageStrings;
    public String colorString;
    public int unlockLevel;
    public transient AbstractCard.CardColor color = AbstractCard.CardColor.COLORLESS;

    public PCLCustomLoadoutInfo(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        this.color = color;
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(SUBFOLDER, color);
    }

    public static ArrayList<PCLCustomLoadoutInfo> getLoadouts(AbstractCard.CardColor color) {
        if (color == null) {
            return EUIUtils.flattenList(CUSTOM_LOADOUTS.values());
        }
        if (!CUSTOM_LOADOUTS.containsKey(color)) {
            CUSTOM_LOADOUTS.put(color, new ArrayList<>());
        }
        return CUSTOM_LOADOUTS.get(color);
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getLoadouts(color));
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleLoadoutImpl(f);
        }
    }

    private static void loadSingleLoadoutImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString();
            PCLCustomLoadoutInfo slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setup(path);
            getLoadouts(slot.color).add(slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Loadout: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getLoadouts(color));
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public void setup(String fp) {
        color = AbstractCard.CardColor.valueOf(colorString);
        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Loadout: " + fp);
    }
}
