package pinacolada.blights;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomBlightSlot extends PCLCustomEditorLoadable<PCLDynamicBlightData, PCLDynamicBlight> {
    private static final TypeToken<PCLCustomBlightSlot> TTOKEN = new TypeToken<PCLCustomBlightSlot>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomBlightSlot>> CUSTOM_COLOR_LISTS = new HashMap<>();
    private static final HashMap<String, PCLCustomBlightSlot> CUSTOM_MAPPING = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_BLIGHT_ID = "PCLB";
    public static final String SUBFOLDER = "blights";

    public Boolean unique;
    public Integer maxUpgradeLevel = 0;
    public Integer branchUpgradeFactor = 0;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public String tier;
    public String sfx;
    public String color;
    public String languageStrings;
    public String[] forms;
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;

    public PCLCustomBlightSlot(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicBlightData builder = (PCLDynamicBlightData) new PCLDynamicBlightData(ID)
                .setText("", new String[]{})
                .setColor(color);
        builders.add(builder);
    }

    public PCLCustomBlightSlot(PCLPointerBlight card, AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add((PCLDynamicBlightData) new PCLDynamicBlightData(card.blightData)
                .setID(ID)
                .setColor(color)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
                .setPPower(card.getPowerEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomBlightSlot(PCLCustomBlightSlot other, AbstractCard.CardColor color) {
        this(other);
        slotColor = color;
        for (PCLDynamicBlightData builder : builders) {
            builder.setColor(color);
        }
    }

    public PCLCustomBlightSlot(PCLCustomBlightSlot other) {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicBlightData builder : other.builders) {
            builders.add(new PCLDynamicBlightData(builder)
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

    public static void addSlot(PCLCustomBlightSlot slot) {
        getBlights(slot.slotColor).add(slot);
        CUSTOM_MAPPING.put(slot.ID, slot);
        slot.commitBuilder();
    }

    // Only allow a blight to be copied into a custom slot if it is a PCLPointerBlight and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the editor)
    public static boolean canFullyCopy(AbstractBlight blight) {
        if (blight instanceof PCLPointerBlight) {
            return EUIUtils.all(((PCLPointerBlight) blight).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static void deleteSlot(PCLCustomBlightSlot slot) {
        getBlights(slot.slotColor).remove(slot);
        CUSTOM_MAPPING.remove(slot.ID);
        slot.wipeBuilder();
    }

    public static void editSlot(PCLCustomBlightSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_MAPPING.remove(oldID);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        slot.commitBuilder();
    }

    public static PCLCustomBlightSlot get(String id) {
        return CUSTOM_MAPPING.get(id);
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_BLIGHT_ID, color);
    }

    public static ArrayList<PCLCustomBlightSlot> getBlights() {
        return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
    }

    public static ArrayList<PCLCustomBlightSlot> getBlights(AbstractCard.CardColor color) {
        if (!CUSTOM_COLOR_LISTS.containsKey(color)) {
            CUSTOM_COLOR_LISTS.put(color, new ArrayList<>());
        }
        return CUSTOM_COLOR_LISTS.get(color);
    }

    public static ArrayList<PCLCustomBlightSlot> getBlights(AbstractCard.CardColor... colors) {
        ArrayList<PCLCustomBlightSlot> res = new ArrayList<>();
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

    public static void initialize() {
        CUSTOM_COLOR_LISTS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder());
        }
        if (PGR.debugBlights != null) {
            PGR.debugBlights.refresh();
        }
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getBlights(color));
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleBlightImpl(f);
        }
    }

    private static void loadSingleBlightImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomBlightSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getBlights(slot.slotColor).add(slot);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Blight: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getBlights(color));
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicBlightData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomBlightSlot.class, "Saved Custom Blight: " + filePath);
        if (PGR.debugBlights != null) {
            PGR.debugBlights.refresh();
        }
    }

    public PCLDynamicBlightData getBuilder(int i) {
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

    public PCLDynamicBlight make() {
        return getBuilder(0).create();
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicBlightData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            color = first.cardColor.name();
            unique = first.unique;
            tier = first.tier.toString();
            counter = first.counter.clone();
            counterUpgrade = first.counterUpgrade.clone();
            maxUpgradeLevel = first.maxUpgradeLevel;
            branchUpgradeFactor = first.branchFactor;
        }

        for (PCLDynamicBlightData builder : builders) {
            EffectItemForm f = new EffectItemForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    protected void setupBuilder(String fp) {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();

        for (String fo : forms) {
            EffectItemForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicBlightData builder = new PCLDynamicBlightData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicBlightData builder : builders) {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Blight: " + filePath);
    }

    protected void wipeBuilder() {
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Blight Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Blight: " + filePath);
    }
}
