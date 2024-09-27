package pinacolada.augments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.annotations.VisibleSkill;
import pinacolada.blights.PCLPointerBlight;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentRenderable;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomAugmentSlot extends PCLCustomEditorLoadable<PCLDynamicAugmentData, PCLDynamicAugment> {
    private static final TypeToken<PCLCustomAugmentSlot> TTOKEN = new TypeToken<PCLCustomAugmentSlot>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomAugmentSlot>> CUSTOM_COLOR_LISTS = new HashMap<>();
    private static final HashMap<String, PCLCustomAugmentSlot> CUSTOM_MAPPING = new HashMap<>();
    private static final ArrayList<TupleT2<URL,String>> PROVIDERS = new ArrayList<>();
    public static final String BASE_BLIGHT_ID = "PCLA";
    public static final String SUBFOLDER = "augments";

    public Boolean permanent;
    public Boolean unique;
    public Integer maxUpgradeLevel = 0;
    public Integer branchUpgradeFactor = 0;
    public Integer[] tier = array(0);
    public Integer[] tierUpgrade = array(0);
    public String reqs;
    public String category;
    public String color;
    public String languageStrings;
    public String[] forms;
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;

    public PCLCustomAugmentSlot(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicAugmentData builder = (PCLDynamicAugmentData) new PCLDynamicAugmentData(ID)
                .setText("", new String[]{})
                .setColor(color);
        builders.add(builder);
    }

    public PCLCustomAugmentSlot(PCLAugment card, AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add((PCLDynamicAugmentData) new PCLDynamicAugmentData(card.data)
                .setID(ID)
                .setColor(color)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
                .setPPower(card.getPowerEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomAugmentSlot(PCLCustomAugmentSlot other, AbstractCard.CardColor color) {
        this(other);
        slotColor = color;
        for (PCLDynamicAugmentData builder : builders) {
            builder.setColor(color);
        }
    }

    public PCLCustomAugmentSlot(PCLCustomAugmentSlot other) {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicAugmentData builder : other.builders) {
            builders.add(new PCLDynamicAugmentData(builder)
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
            EUIUtils.logError(PCLCustomAugmentSlot.class, "Failed to add provider. Invalid mod ID: " + id);
        }
    }

    public static void addProvider(ModInfo info, String path) {
        PROVIDERS.add(new TupleT2<>(info.jarURL, path));
    }

    public static void addSlot(PCLCustomAugmentSlot slot) {
        getAugments(slot.slotColor).add(slot);
        CUSTOM_MAPPING.put(slot.ID, slot);
        slot.commitBuilder();
        slot.registerTooltip();
    }

    // Only allow a blight to be copied into a custom slot if it is a PCLPointerBlight and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the editor)
    public static boolean canFullyCopy(AbstractBlight blight) {
        if (blight instanceof PCLPointerBlight) {
            return EUIUtils.all(((PCLPointerBlight) blight).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static void deleteSlot(PCLCustomAugmentSlot slot) {
        getAugments(slot.slotColor).remove(slot);
        CUSTOM_MAPPING.remove(slot.ID);
        slot.wipeBuilder();

        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static void editSlot(PCLCustomAugmentSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_MAPPING.remove(oldID);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        slot.commitBuilder();

        refreshTooltips(); // Must refresh all power tooltips in case any of them mention this power
    }

    public static PCLCustomAugmentSlot get(String id) {
        return CUSTOM_MAPPING.get(id);
    }

    public static ArrayList<PCLCustomAugmentSlot> getAugments() {
        return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
    }

    public static ArrayList<PCLCustomAugmentSlot> getAugments(AbstractCard.CardColor color) {
        if (!CUSTOM_COLOR_LISTS.containsKey(color)) {
            CUSTOM_COLOR_LISTS.put(color, new ArrayList<>());
        }
        return CUSTOM_COLOR_LISTS.get(color);
    }

    public static ArrayList<PCLCustomAugmentSlot> getAugments(AbstractCard.CardColor... colors) {
        ArrayList<PCLCustomAugmentSlot> res = new ArrayList<>();
        for (AbstractCard.CardColor color : colors) {
            if (!CUSTOM_COLOR_LISTS.containsKey(color)) {
                CUSTOM_COLOR_LISTS.put(color, new ArrayList<>());
            }
            else {
                res.addAll(CUSTOM_COLOR_LISTS.get(color));
            }
        }
        return res;
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_BLIGHT_ID, color);
    }

    public static String getFolder() {
        return EUIUtils.withSlash(FOLDER, SUBFOLDER);
    }

    public static void initialize() {
        CUSTOM_COLOR_LISTS.clear();
        loadFolder(getCustomFolder(SUBFOLDER), null, false);
        for (TupleT2<SteamSearch.WorkshopInfo, FileHandle> workshop : getWorkshopFolders(SUBFOLDER)) {
            loadFolder(workshop.v2, workshop.v1.getInstallPath(), false);
        }
        for (TupleT2<URL,String> provider : PROVIDERS) {
            doForFilesInJar(provider.v1, provider.v2, f -> loadSingleImpl(f, null, true));
        }
        if (PGR.debugAugments != null) {
            PGR.debugAugments.refresh();
        }

        // After initializing all powers, re-initialize tooltips to ensure that tooltips from other powers are captured
        refreshTooltips();
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getAugments(color));
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
            PCLCustomAugmentSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path, workshopPath, isInternal);
            slot.registerTooltip();
            getAugments(slot.slotColor).add(slot);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Blight: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getAugments(color));
    }

    public static void refreshTooltips() {
        for (PCLCustomAugmentSlot slot : CUSTOM_MAPPING.values()) {
            slot.registerTooltip();
        }
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicAugmentData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomAugmentSlot.class, "Saved Custom Augment: " + filePath);
        if (PGR.debugAugments != null) {
            PGR.debugAugments.refresh();
        }
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public PCLDynamicAugment make() {
        return getBuilder(0).create();
    }

    public PCLAugmentRenderable makeRenderable() {
        return getBuilder(0).createRenderable(0, 0);
    }

    public PCLAugmentRenderable makeRenderable(int form, int timesUpgraded) {
        PCLDynamicAugmentData augment = getBuilder(form);
        return augment != null ? augment.createRenderable(form, timesUpgraded) : null;
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicAugmentData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(EUIUtils.hashMap(first.languageMap, s -> s.NAME));
            color = first.cardColor.name();
            unique = first.unique;
            permanent = first.permanent;
            category = first.category.name();
            tier = first.tier.clone();
            tierUpgrade = first.tierUpgrade.clone();
            reqs = EUIUtils.serialize(first.reqs);
            maxUpgradeLevel = first.maxUpgradeLevel;
            branchUpgradeFactor = first.branchFactor;
        }

        for (PCLDynamicAugmentData builder : builders) {
            EffectItemForm f = new EffectItemForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.textMap = EUIUtils.serialize(EUIUtils.hashMap(builder.languageMap, s -> s.DESCRIPTION));

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    protected void registerTooltip() {
        PCLDynamicAugmentData first = getBuilder(0);
        if (first != null) {
            first.updateTooltip();
        }
    }

    protected void setupBuilder(String filePath, String workshopPath, boolean isInternal) {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();
        this.workshopFolder = workshopPath;
        this.isInternal = isInternal;

        for (String fo : forms) {
            EffectItemForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicAugmentData builder = new PCLDynamicAugmentData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicAugmentData builder : builders) {
            builder.setImagePath(imagePath);
        }

        this.filePath = filePath;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Blight: " + filePath);
    }
}
