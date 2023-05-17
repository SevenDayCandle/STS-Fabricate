package pinacolada.relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
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

public class PCLCustomRelicSlot extends PCLCustomEditorLoadable<PCLDynamicRelicData> {
    private static final TypeToken<PCLCustomRelicSlot> TTOKEN = new TypeToken<PCLCustomRelicSlot>() {
    };
    private static final TypeToken<RelicForm> TTOKENFORM = new TypeToken<RelicForm>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomRelicSlot>> CUSTOM_RELICS = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_RELIC_ID = "PCLR";
    public static final String SUBFOLDER = "relics";

    protected transient String filePath;
    protected transient String imagePath;
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

    /**
     * Subscribe a provider that provides a folder to load custom cards from whenever the cards are reloaded
     */
    public static void addProvider(CustomFileProvider provider) {
        PROVIDERS.add(provider);
    }

    // Only allow a card to be copied into a custom card slot if it is a PCLCard and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the card editor)
    public static boolean canFullyRelicCard(AbstractRelic card) {
        if (card instanceof PCLPointerRelic) {
            return EUIUtils.all(((PCLPointerRelic) card).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static PCLCustomRelicSlot get(String id) {
        for (ArrayList<PCLCustomRelicSlot> slots : CUSTOM_RELICS.values()) {
            for (PCLCustomRelicSlot slot : slots) {
                if (slot.ID.equals(id)) {
                    return slot;
                }
            }
        }
        return null;
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_RELIC_ID, color);
    }

    public static ArrayList<PCLCustomRelicSlot> getRelics(AbstractCard.CardColor color) {
        if (color == null) {
            return EUIUtils.flattenList(CUSTOM_RELICS.values());
        }
        if (!CUSTOM_RELICS.containsKey(color)) {
            CUSTOM_RELICS.put(color, new ArrayList<>());
        }
        return CUSTOM_RELICS.get(color);
    }

    public static void initialize() {
        CUSTOM_RELICS.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getCardFolder());
        }
/*        if (PGR.debugCards != null) {
            PGR.debugCards.refreshCards();
        }*/
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
            String jsonString = f.readString();
            PCLCustomRelicSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getRelics(slot.slotColor).add(slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Relic: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getRelics(color));
    }

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

    public void commitBuilder() {
        recordBuilder();
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Relic: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Relic Image: " + imagePath + ", New: " + newImagePath);
        }

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        PCLDynamicRelicData builder = getBuilder(0);
        if (builder != null && builder.portraitImage != null) {
            PixmapIO.writePNG(imgWriter, builder.portraitImage.texture.getTextureData().consumePixmap());
            // Forcibly reload the image
            EUIRM.getLocalTexture(newImagePath, true, true, false);
        }

        // Unlink temporary portrait images to allow the new saved portrait image to be loaded, and set multiform data as necessary
        for (PCLDynamicRelicData b : builders) {
            b.setImage(null);
        }

        filePath = newFilePath;
        imagePath = newImagePath;

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false);
        EUIUtils.logInfo(PCLCustomRelicSlot.class, "Saved Custom Relic: " + filePath);
        if (PGR.debugCards != null) {
            PGR.debugCards.refreshCards();
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

    public PCLDynamicRelic makeRelic() {
        return getBuilder(0).create();
    }

    // Copy down the properties from all builders into this slot
    public void recordBuilder() {
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
            RelicForm f = new RelicForm();
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
            RelicForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
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

    public void wipeBuilder() {
        CUSTOM_RELICS.get(slotColor).remove(this);
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Relic Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Relic: " + filePath);
    }

    public static class RelicForm {
        public String[] effects;
        public String[] powerEffects;
    }
}