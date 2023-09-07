package pinacolada.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

import static pinacolada.resources.PCLMainConfig.JSON_FILTER;

public class PCLCustomPowerSlot extends PCLCustomEditorLoadable<PCLDynamicPowerData, PCLDynamicPower> {
    private static final TypeToken<PCLCustomPowerSlot> TTOKEN = new TypeToken<PCLCustomPowerSlot>() {
    };
    private static final HashMap<String, PCLCustomPowerSlot> CUSTOM_POWERS = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_POWER_ID = "PCLW";
    public static final String SUBFOLDER = "powers";

    public boolean isCommon;
    public boolean isMetascaling;
    public boolean isPostActionPower;
    public int maxValue;
    public int minValue;
    public int priority;
    public String type;
    public String endTurnBehavior;
    public String languageStrings;
    public String[][] effects;

    public PCLCustomPowerSlot() {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        PCLDynamicPowerData builder = new PCLDynamicPowerData(ID)
                .setText("", new String[]{});
        builders.add(builder);
    }

    public PCLCustomPowerSlot(PCLDynamicPower card) {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        builders.add((PCLDynamicPowerData) new PCLDynamicPowerData(card.data)
                .setID(ID)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomPowerSlot(PCLCustomPowerSlot other) {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        for (PCLDynamicPowerData builder : other.builders) {
            builders.add(new PCLDynamicPowerData(builder)
                    .setID(ID)
                    .setImagePath(imagePath));
        }
        recordBuilder();
    }

    /**
     * Subscribe a provider that provides a folder to load custom cards from whenever the cards are reloaded
     */
    public static void addProvider(CustomFileProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void addSlot(PCLCustomPowerSlot slot) {
        CUSTOM_POWERS.put(slot.ID, slot);
        slot.commitBuilder();
        slot.registerTooltip();
    }

    public static void deleteSlot(PCLCustomPowerSlot slot) {
        CUSTOM_POWERS.remove(slot.ID);
        EUIKeywordTooltip.removeTemp(slot.ID);
        slot.wipeBuilder();
    }

    public static void editSlot(PCLCustomPowerSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_POWERS.remove(oldID);
            CUSTOM_POWERS.put(slot.ID, slot);
            EUIKeywordTooltip.removeTemp(oldID);
        }
        slot.commitBuilder();
        slot.registerTooltip();
    }

    public static PCLCustomPowerSlot get(String id) {
        return CUSTOM_POWERS.get(id);
    }

    public static HashMap<String, PCLCustomPowerSlot> getAll() {
        return CUSTOM_POWERS;
    }

    /*
    * Clear out custom powers and registered tooltips from those powers, then reload custom items from the custom folders
    * */
    public static void initialize() {
        for (String id : CUSTOM_POWERS.keySet()) {
            EUIKeywordTooltip.removeTemp(id);
        }
        CUSTOM_POWERS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder());
        }
    }

    public static boolean isIDDuplicate(String input) {
        return isIDDuplicateByKey(input, CUSTOM_POWERS.keySet());
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSinglePowerImpl(f);
        }
    }

    private static void loadSinglePowerImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString();
            PCLCustomPowerSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            slot.registerTooltip();
            CUSTOM_POWERS.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomPowerSlot.class, "Could not load Custom Potion: " + path);
        }
    }

    protected static String makeNewID() {
        return makeNewIDByKey(getBaseIDPrefix(BASE_POWER_ID, AbstractCard.CardColor.COLORLESS), CUSTOM_POWERS.keySet());
    }

    protected void commitBuilder() {
        recordBuilder();
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomPowerSlot.class, "Moved Custom Potion: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(PCLCustomPowerSlot.class, "Moved Custom Potion Image: " + imagePath + ", New: " + newImagePath);
        }

        filePath = newFilePath;
        imagePath = newImagePath;

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        PCLDynamicPowerData builder = getBuilder(0);
        if (builder != null && builder.portraitImage != null) {
            PixmapIO.writePNG(imgWriter, builder.portraitImage.getTextureData().consumePixmap());
            // Forcibly reload the image
            EUIRM.reloadTexture(newImagePath, true, false);
        }

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicPowerData b : builders) {
            b.setImagePath(newImagePath).setImage(null);
        }

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false);
        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Saved Custom Potion: " + filePath);
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
    }

    public PCLDynamicPowerData getBuilder(int i) {
        return (builders.size() > i) ? builders.get(i) : null;
    }

    public FileHandle getImageHandle() {
        return Gdx.files.local(imagePath);
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public PCLDynamicPower make() {
        return getBuilder(0).create();
    }

    public PCLPowerRenderable makeRenderable() {
        return getBuilder(0).makeRenderableWithLevel(0);
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String[]> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicPowerData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            isCommon = first.isCommon;
            isMetascaling = first.isMetascaling;
            isPostActionPower = first.isPostActionPower;
            minValue = first.minAmount;
            maxValue = first.maxAmount;
            priority = first.priority;
            type = first.type.toString();
            endTurnBehavior = first.endTurnBehavior.toString();
        }

        for (PCLDynamicPowerData builder : builders) {
            tempForms.add(EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{}));
        }

        effects = tempForms.toArray(new String[][]{});
    }

    protected void registerTooltip() {
        PCLDynamicPowerData first = getBuilder(0);
        if (first != null) {
            first.updateTooltip();
            EUIKeywordTooltip.registerIDTemp(ID, first.tooltip);
        }
    }

    protected void setupBuilder(String fp) {
        builders = new ArrayList<>();

        for (String[] f : effects) {
            PCLDynamicPowerData builder = new PCLDynamicPowerData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicPowerData builder : builders) {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Loaded Custom Power: " + filePath);
    }

    protected void wipeBuilder() {
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Deleted Custom Power Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Deleted Custom Power: " + filePath);
    }

}
