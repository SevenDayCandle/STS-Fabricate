package pinacolada.potions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.resources.PCLMainConfig.JSON_FILTER;

public class PCLCustomPotionSlot extends PCLCustomEditorLoadable<PCLDynamicPotionData, PCLDynamicPotion> {
    private static final TypeToken<PCLCustomPotionSlot> TTOKEN = new TypeToken<PCLCustomPotionSlot>() {
    };
    private static final TypeToken<PotionForm> TTOKENFORM = new TypeToken<PotionForm>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomPotionSlot>> CUSTOM_POTIONS = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_POTION_ID = "PCLP";
    public static final String SUBFOLDER = "potions";

    protected transient String filePath;
    protected transient String imagePath;
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

    // Only allow a potion to be copied into a custom slot if it is a PCLPotion and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the editor)
    public static boolean canFullyCopy(AbstractPotion potion) {
        if (potion instanceof PCLPotion) {
            return EUIUtils.all(((PCLPotion) potion).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static PCLCustomPotionSlot get(String id) {
        for (ArrayList<PCLCustomPotionSlot> slots : CUSTOM_POTIONS.values()) {
            for (PCLCustomPotionSlot slot : slots) {
                if (slot.ID.equals(id)) {
                    return slot;
                }
            }
        }
        return null;
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_POTION_ID, color);
    }

    public static ArrayList<PCLCustomPotionSlot> getPotions(AbstractCard.CardColor color) {
        if (color == null) {
            return EUIUtils.flattenList(CUSTOM_POTIONS.values());
        }
        if (!CUSTOM_POTIONS.containsKey(color)) {
            CUSTOM_POTIONS.put(color, new ArrayList<>());
        }
        return CUSTOM_POTIONS.get(color);
    }

    public static void initialize() {
        CUSTOM_POTIONS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getCardFolder());
        }
/*        if (PGR.debugCards != null) {
            PGR.debugCards.refreshCards();
        }*/
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getPotions(color));
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSinglePotionImpl(f);
        }
    }

    private static void loadSinglePotionImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString();
            PCLCustomPotionSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getPotions(slot.slotColor).add(slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Potion: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getPotions(color));
    }

    public void commitBuilder() {
        recordBuilder();
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Potion: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Potion Image: " + imagePath + ", New: " + newImagePath);
        }

        filePath = newFilePath;
        imagePath = newImagePath;

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        PCLDynamicPotionData builder = getBuilder(0);
        if (builder != null && builder.portraitImage != null) {
            PixmapIO.writePNG(imgWriter, builder.portraitImage.getTextureData().consumePixmap());
            // Forcibly reload the image
            EUIRM.reloadTexture(newImagePath, true, false);
        }

        // Point all builders to the new path, or nullify it out if no image was saved
        for (PCLDynamicPotionData b : builders) {
            b.setImagePath(newImagePath).setImage(null);
        }

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false);
        EUIUtils.logInfo(PCLCustomPotionSlot.class, "Saved Custom Potion: " + filePath);
        if (PGR.debugPotions != null) {
            PGR.debugPotions.refresh();
        }
    }

    public PCLDynamicPotionData getBuilder(int i) {
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

    public PCLDynamicPotion make() {
        return getBuilder(0).create();
    }

    // Copy down the properties from all builders into this slot
    public void recordBuilder() {
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
            PotionForm f = new PotionForm();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    public void setupBuilder(String fp) {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();

        for (String fo : forms) {
            PotionForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicPotionData builder = new PCLDynamicPotionData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicPotionData builder : builders) {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Potion: " + filePath);
    }

    public void wipeBuilder() {
        CUSTOM_POTIONS.get(slotColor).remove(this);
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Potion Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Potion: " + filePath);
    }

    public static class PotionForm {
        public String[] effects;
        public String[] powerEffects;
    }
}
