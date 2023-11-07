package pinacolada.orbs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.TupleT2;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.PCLPowerRenderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomOrbSlot extends PCLCustomEditorLoadable<PCLDynamicOrbData, PCLDynamicOrb> {
    private static final TypeToken<PCLCustomOrbSlot> TTOKEN = new TypeToken<PCLCustomOrbSlot>() {
    };
    private static final HashMap<String, PCLCustomOrbSlot> CUSTOM_ORBS = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_POWER_ID = "PCLO";
    public static final String SUBFOLDER = "orbs";

    public String timing;
    public String flareColor1;
    public String flareColor2;
    public String sfx;
    public boolean applyFocusToEvoke;
    public boolean applyFocusToPassive;
    public int baseEvokeValue;
    public int basePassiveValue;
    public float rotationSpeed;
    public String languageStrings;
    public String[] forms;

    public PCLCustomOrbSlot() {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        PCLDynamicOrbData builder = new PCLDynamicOrbData(ID)
                .setText("", new String[]{});
        builders.add(builder);
    }

    public PCLCustomOrbSlot(PCLDynamicOrb card) {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        builders.add((PCLDynamicOrbData) new PCLDynamicOrbData(card.data)
                .setID(ID)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomOrbSlot(PCLCustomOrbSlot other) {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        for (PCLDynamicOrbData builder : other.builders) {
            builders.add(new PCLDynamicOrbData(builder)
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

    public static void addSlot(PCLCustomOrbSlot slot) {
        CUSTOM_ORBS.put(slot.ID, slot);
        slot.commitBuilder();
        slot.registerTooltip();
    }

    public static void deleteSlot(PCLCustomOrbSlot slot) {
        CUSTOM_ORBS.remove(slot.ID);
        EUIKeywordTooltip.removeTemp(slot.ID);
        slot.wipeBuilder();
        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static void editSlot(PCLCustomOrbSlot slot, String oldID) {
        if (!Objects.equals(oldID, slot.ID)) {
            CUSTOM_ORBS.remove(oldID);
            CUSTOM_ORBS.put(slot.ID, slot);
            EUIKeywordTooltip.removeTemp(oldID);
        }
        slot.commitBuilder();
        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static PCLCustomOrbSlot get(String id) {
        return CUSTOM_ORBS.get(id);
    }

    public static HashMap<String, PCLCustomOrbSlot> getAll() {
        return CUSTOM_ORBS;
    }

    public static String getFolder() {
        return EUIUtils.withSlash(FOLDER, SUBFOLDER);
    }

    /*
     * Clear out custom powers and registered tooltips from those powers, then reload custom items from the custom folders
     * */
    public static void initialize() {
        for (String id : CUSTOM_ORBS.keySet()) {
            EUIKeywordTooltip.removeTemp(id);
        }
        CUSTOM_ORBS.clear();
        loadFolder(getCustomFolder(SUBFOLDER), null, false);
        for (TupleT2<SteamSearch.WorkshopInfo, FileHandle> workshop : getWorkshopFolders(SUBFOLDER)) {
            loadFolder(workshop.v2, workshop.v1.getInstallPath(), false);
        }
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder(), null, true);
        }

        // After initializing all powers, re-initialize tooltips to ensure that tooltips from other powers are captured
        refreshTooltips();
    }

    public static boolean isIDDuplicate(String input) {
        return isIDDuplicateByKey(input, CUSTOM_ORBS.keySet());
    }

    private static void loadFolder(FileHandle folder, String workshopPath, boolean isInternal) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleImpl(f, workshopPath, isInternal);
        }
    }

    private static void loadSingleImpl(FileHandle f, String workshopPath, boolean isInternal) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomOrbSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path, workshopPath, isInternal);
            slot.registerTooltip();
            CUSTOM_ORBS.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomOrbSlot.class, "Could not load Custom Potion: " + path);
        }
    }

    protected static String makeNewID() {
        return makeNewIDByKey(getBaseIDPrefix(BASE_POWER_ID, AbstractCard.CardColor.COLORLESS), CUSTOM_ORBS.keySet());
    }

    public static void refreshTooltips() {
        for (PCLCustomOrbSlot slot : CUSTOM_ORBS.values()) {
            slot.registerTooltip();
        }
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicOrbData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomOrbSlot.class, "Saved Custom Potion: " + filePath);
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public PCLDynamicOrb make() {
        return getBuilder(0).create();
    }

    public PCLOrbRenderable makeRenderable() {
        return getBuilder(0).makeRenderableWithLevel(0);
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicOrbData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            applyFocusToEvoke = first.applyFocusToEvoke;
            applyFocusToPassive = first.applyFocusToPassive;
            baseEvokeValue = first.baseEvokeValue;
            basePassiveValue = first.basePassiveValue;
            flareColor1 = first.flareColor1.toString();
            flareColor2 = first.flareColor2.toString();
            rotationSpeed = first.rotationSpeed;
            sfx = first.sfx;
            timing = first.timing.name();
        }

        for (PCLDynamicOrbData builder : builders) {
            EffectItemForm f = new EffectItemForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    protected void registerTooltip() {
        PCLDynamicOrbData first = getBuilder(0);
        if (first != null) {
            first.updateTooltip();
        }
    }

    protected void setupBuilder(String filePath, String workshopPath, boolean isInternal) {
        builders = new ArrayList<>();
        this.workshopFolder = workshopPath;
        this.isInternal = isInternal;

        for (String fo : forms) {
            EffectItemForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicOrbData builder = new PCLDynamicOrbData(this, f);
            builders.add(builder);
        }


        imagePath = makeImagePath();
        for (PCLDynamicOrbData builder : builders) {
            builder.setImagePath(imagePath);
        }

        this.filePath = filePath;
        EUIUtils.logInfo(PCLCustomOrbSlot.class, "Loaded Custom Power: " + filePath);
    }
}
