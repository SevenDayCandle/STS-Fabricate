package pinacolada.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomRelicSlot extends PCLCustomEditorLoadable<PCLDynamicRelicData, PCLDynamicRelic> {
    private static final TypeToken<PCLCustomRelicSlot> TTOKEN = new TypeToken<PCLCustomRelicSlot>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomRelicSlot>> CUSTOM_COLOR_LISTS = new HashMap<>();
    private static final HashMap<String, PCLCustomRelicSlot> CUSTOM_MAPPING = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_RELIC_ID = "PCLR";
    public static final String SUBFOLDER = "relics";

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

    public PCLCustomRelicSlot(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicRelicData builder = (PCLDynamicRelicData) new PCLDynamicRelicData(ID)
                .setText("", new String[]{}, "")
                .setColor(color)
                .setTier(AbstractRelic.RelicTier.COMMON);
        builders.add(builder);
    }

    public PCLCustomRelicSlot(PCLPointerRelic card, AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add((PCLDynamicRelicData) new PCLDynamicRelicData(card.relicData)
                .setID(ID)
                .setColor(color)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
                .setPPower(card.getPowerEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomRelicSlot(PCLCustomRelicSlot other, AbstractCard.CardColor color) {
        this(other);
        slotColor = color;
        for (PCLDynamicRelicData builder : builders) {
            builder.setColor(color);
        }
    }

    public PCLCustomRelicSlot(PCLCustomRelicSlot other) {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicRelicData builder : other.builders) {
            builders.add(new PCLDynamicRelicData(builder)
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

    public static void addSlot(PCLCustomRelicSlot slot) {
        getRelics(slot.slotColor).add(slot);
        CUSTOM_MAPPING.put(slot.ID, slot);
        slot.commitBuilder();
    }

    // Only allow a relic to be copied into a custom slot if it is a PCLPointerRelic and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the editor)
    public static boolean canFullyCopy(AbstractRelic relic) {
        if (relic instanceof PCLPointerRelic) {
            return EUIUtils.all(((PCLPointerRelic) relic).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static void deleteSlot(PCLCustomRelicSlot slot) {
        getRelics(slot.slotColor).remove(slot);
        CUSTOM_MAPPING.remove(slot.ID);
        slot.wipeBuilder();
    }

    public static void editSlot(PCLCustomRelicSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_MAPPING.remove(oldID);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        slot.commitBuilder();
    }

    public static PCLCustomRelicSlot get(String id) {
        return CUSTOM_MAPPING.get(id);
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_RELIC_ID, color);
    }

    public static ArrayList<PCLCustomRelicSlot> getRelics() {
        return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
    }

    public static ArrayList<PCLCustomRelicSlot> getRelics(AbstractCard.CardColor color) {
        if (color == null) {
            return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
        }
        if (!CUSTOM_COLOR_LISTS.containsKey(color)) {
            CUSTOM_COLOR_LISTS.put(color, new ArrayList<>());
        }
        return CUSTOM_COLOR_LISTS.get(color);
    }

    public static void initialize() {
        CUSTOM_COLOR_LISTS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder());
        }
        if (PGR.debugRelics != null) {
            PGR.debugRelics.refresh();
        }
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getRelics(color));
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleRelicImpl(f);
        }
    }

    private static void loadSingleRelicImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomRelicSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getRelics(slot.slotColor).add(slot);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Relic: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getRelics(color));
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicRelicData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomRelicSlot.class, "Saved Custom Relic: " + filePath);
        if (PGR.debugRelics != null) {
            PGR.debugRelics.refresh();
        }
    }

    public PCLDynamicRelicData getBuilder(int i) {
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

    public PCLDynamicRelic make() {
        return getBuilder(0).create();
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicRelicData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            tier = first.tier.name();
            sfx = first.sfx.name();
            color = first.cardColor.name();
            counter = first.counter.clone();
            counterUpgrade = first.counterUpgrade.clone();
            maxUpgradeLevel = first.maxUpgradeLevel;
            branchUpgradeFactor = first.branchFactor;
        }

        for (PCLDynamicRelicData builder : builders) {
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
            PCLDynamicRelicData builder = new PCLDynamicRelicData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicRelicData builder : builders) {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Relic: " + filePath);
    }

    protected void wipeBuilder() {
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Relic Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Relic: " + filePath);
    }
}
