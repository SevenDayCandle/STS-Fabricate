package pinacolada.potions;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.evacipated.cardcrawl.modthespire.steam.SteamSearch;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.utilities.TupleT2;
import pinacolada.annotations.VisiblePotion;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.patches.basemod.PotionPoolPatches;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomPotionSlot extends PCLCustomEditorLoadable<PCLDynamicPotionData, PCLDynamicPotion> {
    private static final TypeToken<PCLCustomPotionSlot> TTOKEN = new TypeToken<PCLCustomPotionSlot>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomPotionSlot>> CUSTOM_COLOR_LISTS = new HashMap<>();
    private static final HashMap<String, PCLCustomPotionSlot> CUSTOM_MAPPING = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_POTION_ID = "PCLP";
    public static final String SUBFOLDER = "potions";

    public Integer maxUpgradeLevel = 0;
    public Integer branchUpgradeFactor = 0;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public String effect;
    public String rarity;
    public String size;
    public String color;
    public String liquidColor;
    public String hybridColor;
    public String spotsColor;
    public String languageStrings;
    public String[] forms;
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;

    public PCLCustomPotionSlot(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicPotionData builder = (PCLDynamicPotionData) new PCLDynamicPotionData(ID)
                .setText("", new String[]{})
                .setColor(color)
                .setRarity(AbstractPotion.PotionRarity.COMMON);
        builders.add(builder);
    }

    public PCLCustomPotionSlot(PCLPotion card, AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add((PCLDynamicPotionData) new PCLDynamicPotionData(card.potionData)
                .setID(ID)
                .setColor(color)
                .setImagePath(imagePath)
                .setPSkill(card.getEffects(), true, true)
                .setPPower(card.getPowerEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomPotionSlot(PCLCustomPotionSlot other, AbstractCard.CardColor color) {
        this(other);
        slotColor = color;
        for (PCLDynamicPotionData builder : builders) {
            builder.setColor(color);
        }
    }

    public PCLCustomPotionSlot(PCLCustomPotionSlot other) {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicPotionData builder : other.builders) {
            builders.add(new PCLDynamicPotionData(builder)
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

    public static void addSlot(PCLCustomPotionSlot slot) {
        getPotions(slot.slotColor).add(slot);
        CUSTOM_MAPPING.put(slot.ID, slot);
        slot.commitBuilder();
    }

    // Only allow a potion to be copied into a custom slot if it is a PCLPotion and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the editor)
    public static boolean canFullyCopy(AbstractPotion potion) {
        if (potion instanceof PCLPotion) {
            return EUIUtils.all(((PCLPotion) potion).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static void deleteSlot(PCLCustomPotionSlot slot) {
        getPotions(slot.slotColor).remove(slot);
        CUSTOM_MAPPING.remove(slot.ID);
        slot.wipeBuilder();
    }

    public static void editSlot(PCLCustomPotionSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_MAPPING.remove(oldID);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        slot.commitBuilder();
    }

    public static PCLCustomPotionSlot get(String id) {
        return CUSTOM_MAPPING.get(id);
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_POTION_ID, color);
    }

    public static String getFolder() {
        return EUIUtils.withSlash(FOLDER, SUBFOLDER);
    }

    public static ArrayList<PCLCustomPotionSlot> getPotions() {
        return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
    }

    public static ArrayList<PCLCustomPotionSlot> getPotions(AbstractCard.CardColor color) {
        if (!CUSTOM_COLOR_LISTS.containsKey(color)) {
            CUSTOM_COLOR_LISTS.put(color, new ArrayList<>());
        }
        return CUSTOM_COLOR_LISTS.get(color);
    }

    public static ArrayList<PCLCustomPotionSlot> getPotions(AbstractCard.CardColor... colors) {
        ArrayList<PCLCustomPotionSlot> res = new ArrayList<>();
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
        loadFolder(getCustomFolder(SUBFOLDER), null, false);
        for (TupleT2<SteamSearch.WorkshopInfo, FileHandle> workshop : getWorkshopFolders(SUBFOLDER)) {
            loadFolder(workshop.v2, workshop.v1.getInstallPath(), false);
        }
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder(), null, true);
        }
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getPotions(color));
    }

    private static void loadFolder(FileHandle folder, String workshopPath, boolean isInternal) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSinglePotionImpl(f, workshopPath, isInternal);
        }
    }

    private static void loadSinglePotionImpl(FileHandle f, String workshopPath, boolean isInternal) {
        String path = f.path();
        try {
            String jsonString = f.readString(HttpParametersUtils.defaultEncoding);
            PCLCustomPotionSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path, workshopPath, isInternal);
            getPotions(slot.slotColor).add(slot);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Potion: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getPotions(color));
    }

    protected void commitBuilder() {
        recordBuilder();
        writeFiles(TTOKEN.getType());

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicPotionData b : builders) {
            b.setImagePath(imagePath).setImage(null);
        }

        EUIUtils.logInfo(PCLCustomPotionSlot.class, "Saved Custom Potion: " + filePath);
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }

    public PCLDynamicPotion make() {
        return getBuilder(0).create();
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicPotionData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            rarity = first.rarity.name();
            effect = first.effect.name();
            size = first.size.name();
            hybridColor = first.hybridColor.toString();
            liquidColor = first.liquidColor.toString();
            spotsColor = first.spotsColor.toString();
            color = first.cardColor.name();
            counter = first.counter.clone();
            counterUpgrade = first.counterUpgrade.clone();
            maxUpgradeLevel = first.maxUpgradeLevel;
            branchUpgradeFactor = first.branchFactor;
        }

        for (PCLDynamicPotionData builder : builders) {
            EffectItemForm f = new EffectItemForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    protected void setupBuilder(String filePath, String workshopPath, boolean isInternal) {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();
        this.workshopFolder = workshopPath;
        this.isInternal = isInternal;

        for (String fo : forms) {
            EffectItemForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicPotionData builder = new PCLDynamicPotionData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicPotionData builder : builders) {
            builder.setImagePath(imagePath);
        }

        this.filePath = filePath;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Potion: " + filePath);
    }
}
