package pinacolada.powers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.TupleT2;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLPowerRenderable;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomPowerSlot extends PCLCustomEditorLoadable<PCLDynamicPowerData, PCLDynamicPower> {
    private static final TypeToken<PCLCustomPowerSlot> TTOKEN = new TypeToken<PCLCustomPowerSlot>() {
    };
    private static final HashMap<String, PCLCustomPowerSlot> CUSTOM_POWERS = new HashMap<>();
    private static final ArrayList<TupleT2<URL,String>> PROVIDERS = new ArrayList<>();
    public static final String BASE_POWER_ID = "PCLW";
    public static final String SUBFOLDER = "powers";

    public boolean isCommon;
    public boolean isMetascaling;
    public boolean isPostActionPower;
    public int maxValue;
    public int minValue;
    public int priority;
    public int turns;
    public String type;
    public String endTurnBehavior;
    public String languageStrings;
    public String[] forms;
    @Deprecated
    public String[][] effects;

    public PCLCustomPowerSlot() {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        PCLDynamicPowerData builder = new PCLDynamicPowerData(ID)
                .setText("", new String[]{});
        builders.add(builder);
    }

    public PCLCustomPowerSlot(PCLDynamicPowerData card) {
        ID = makeNewID();
        filePath = makeFilePath();
        imagePath = makeImagePath();
        builders.add((PCLDynamicPowerData) new PCLDynamicPowerData(card)
                .setID(ID)
                .setImagePath(imagePath)
                .setPSkill(card.moves, true, true)
                .setPPower(card.powers, true, true)
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
    public static void addProvider(String id, String path) {
        ModInfo info = EUIGameUtils.getModInfoFromID(id);
        if (info != null) {
            addProvider(info, path);
        }
        else {
            EUIUtils.logError(PCLCustomPowerSlot.class, "Failed to add provider. Invalid mod ID: " + id);
        }
    }

    public static void addProvider(ModInfo info, String path) {
        PROVIDERS.add(new TupleT2<>(info.jarURL, path));
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
        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static void editSlot(PCLCustomPowerSlot slot, String oldID) {
        if (!Objects.equals(oldID, slot.ID)) {
            CUSTOM_POWERS.remove(oldID);
            CUSTOM_POWERS.put(slot.ID, slot);
            EUIKeywordTooltip.removeTemp(oldID);
        }
        slot.commitBuilder();
        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static PCLCustomPowerSlot get(String id) {
        return CUSTOM_POWERS.get(id);
    }

    public static HashMap<String, PCLCustomPowerSlot> getAll() {
        return CUSTOM_POWERS;
    }

    public static String getFolder() {
        return EUIUtils.withSlash(FOLDER, SUBFOLDER);
    }

    /*
     * Clear out custom powers and registered tooltips from those powers, then reload custom items from the custom folders
     * */
    public static void initialize() {
        for (String id : CUSTOM_POWERS.keySet()) {
            EUIKeywordTooltip.removeTemp(id);
        }
        CUSTOM_POWERS.clear();
        loadFolder(getCustomFolder(SUBFOLDER), null, false);
        for (TupleT2<SteamSearch.WorkshopInfo, FileHandle> workshop : getWorkshopFolders(SUBFOLDER)) {
            loadFolder(workshop.v2, workshop.v1.getInstallPath(), false);
        }
        for (TupleT2<URL,String> provider : PROVIDERS) {
            doForFilesInJar(provider.v1, provider.v2, f -> loadSingleImpl(f, provider.v2, null, true));
        }

        // After initializing all powers, re-initialize tooltips to ensure that tooltips from other powers are captured
        refreshTooltips();
    }

    public static boolean isIDDuplicate(String input) {
        return isIDDuplicateByKey(input, CUSTOM_POWERS.keySet());
    }

    private static void loadFolder(FileHandle folder, String workshopPath, boolean isInternal) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleImpl(f, folder.path(), workshopPath, isInternal);
        }
    }

    private static void loadSingleImpl(FileHandle f, String folder, String workshopPath, boolean isInternal) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomPowerSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path, folder, workshopPath, isInternal);
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

    public static void refreshTooltips() {
        for (PCLCustomPowerSlot slot : CUSTOM_POWERS.values()) {
            slot.registerTooltip();
        }
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicPowerData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Saved Custom Potion: " + filePath);
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
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
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicPowerData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(EUIUtils.hashMap(first.languageMap, s -> s.NAME));
            isCommon = first.isCommon;
            isMetascaling = first.isMetascaling;
            isPostActionPower = first.isPostActionPower;
            minValue = first.minAmount;
            maxValue = first.maxAmount;
            priority = first.priority;
            turns = first.turns;
            type = first.type.toString();
            endTurnBehavior = first.endTurnBehavior.toString();
        }

        for (PCLDynamicPowerData builder : builders) {
            EffectItemForm f = new EffectItemForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.textMap = EUIUtils.serialize(EUIUtils.hashMap(builder.languageMap, s -> s.DESCRIPTIONS));

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
        effects = null;
    }

    protected void registerTooltip() {
        PCLDynamicPowerData first = getBuilder(0);
        if (first != null) {
            first.updateTooltip();
        }
    }

    protected void setupBuilder(String filePath, String folder, String workshopPath, boolean isInternal) {
        builders = new ArrayList<>();
        this.workshopFolder = workshopPath;
        this.isInternal = isInternal;

        if (forms != null) {
            for (String fo : forms) {
                EffectItemForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
                PCLDynamicPowerData builder = new PCLDynamicPowerData(this, f);
                builders.add(builder);
            }
        }
        else if (effects != null) {
            for (String[] f : effects) {
                EffectItemForm fo = new EffectItemForm();
                fo.effects = f;
                fo.powerEffects = new String[] {};
                fo.textMap = EUIUtils.EMPTY_STRING;
                PCLDynamicPowerData builder = new PCLDynamicPowerData(this, fo);
                builders.add(builder);
            }
        }

        imagePath = makeImagePath(folder);
        for (PCLDynamicPowerData builder : builders) {
            builder.setImagePath(imagePath);
        }

        this.filePath = filePath;
        EUIUtils.logInfo(PCLCustomPowerSlot.class, "Loaded Custom Power: " + filePath);
    }
}
