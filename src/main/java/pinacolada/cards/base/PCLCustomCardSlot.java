package pinacolada.cards.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.interfaces.providers.CustomFileProvider;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.utilities.GameUtilities.JSON_FILTER;

public class PCLCustomCardSlot extends PCLCustomEditorLoadable<PCLDynamicCardData, PCLDynamicCard> {
    private static final TypeToken<PCLCustomCardSlot> TTOKEN = new TypeToken<PCLCustomCardSlot>() {
    };
    private static final TypeToken<CardForm> TTOKENFORM = new TypeToken<CardForm>() {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomCardSlot>> CUSTOM_COLOR_LISTS = new HashMap<>();
    private static final HashMap<String, PCLCustomCardSlot> CUSTOM_MAPPING = new HashMap<>();
    private static final ArrayList<CustomFileProvider> PROVIDERS = new ArrayList<>();
    public static final String BASE_CARD_ID = "PCLC";
    public static final String SUBFOLDER = "cards";

    public String loadout;
    public Integer maxUpgradeLevel = 1;
    public Integer maxCopies = -1;
    public Integer branchUpgradeFactor = 0;
    public Boolean removableFromDeck;
    public Boolean unique;
    public String type;
    public String rarity;
    public String color;
    public String languageStrings;
    public String affinities;
    public Integer[] damage = array(0);
    public Integer[] damageUpgrade = array(0);
    public Integer[] block = array(0);
    public Integer[] blockUpgrade = array(0);
    public Integer[] tempHP = array(0);
    public Integer[] tempHPUpgrade = array(0);
    public Integer[] heal = array(0);
    public Integer[] healUpgrade = array(0);
    public Integer[] hitCount = array(1);
    public Integer[] hitCountUpgrade = array(0);
    public Integer[] rightCount = array(1);
    public Integer[] rightCountUpgrade = array(0);
    public Integer[] cost = array(0);
    public Integer[] costUpgrade = array(0);
    public String[] tags;
    public String[] flags;
    public String[] forms;
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;

    public PCLCustomCardSlot(AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicCardData builder = (PCLDynamicCardData) new PCLDynamicCardData(ID)
                .setText("", "", "")
                .setColor(color)
                .setRarity(AbstractCard.CardRarity.COMMON)
                .setCosts(0);
        builders.add(builder);
    }

    public PCLCustomCardSlot(PCLCard card, AbstractCard.CardColor color) {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add((PCLDynamicCardData) new PCLDynamicCardData(card.cardData, true)
                .setColor(color)
                .setID(ID)
                .setImagePath(imagePath)
                .setAttackSkill(card.onAttackEffect)
                .setBlockSkill(card.onBlockEffect)
                .setFlags(CardFlag.getFromCard(card))
                .setPSkill(card.getEffects(), true, true)
                .setPPower(card.getPowerEffects(), true, true)
        );
        recordBuilder();
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other, AbstractCard.CardColor color) {
        this(other);
        slotColor = color;
        for (PCLDynamicCardData builder : builders) {
            builder.setColor(color);
        }
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other) {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicCardData builder : other.builders) {
            builders.add(new PCLDynamicCardData(builder)
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

    public static void addSlot(PCLCustomCardSlot slot) {
        getCards(slot.slotColor).add(slot);
        CUSTOM_MAPPING.put(slot.ID, slot);
        slot.commitBuilder();
    }

    // Only allow a card to be copied into a custom card slot if it is a PCLCard and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the card editor)
    public static boolean canFullyCopy(AbstractCard card) {
        if (card instanceof PCLCard) {
            return EUIUtils.all(((PCLCard) card).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static void deleteSlot(PCLCustomCardSlot slot) {
        getCards(slot.slotColor).remove(slot);
        CUSTOM_MAPPING.remove(slot.ID);
        slot.wipeBuilder();
    }

    public static void editSlot(PCLCustomCardSlot slot, String oldID) {
        if (!oldID.equals(slot.ID)) {
            CUSTOM_MAPPING.remove(oldID);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        slot.commitBuilder();
    }

    /**
     * Get the custom card that matches slot ID
     */
    public static PCLCustomCardSlot get(String id) {
        return CUSTOM_MAPPING.get(id);
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(BASE_CARD_ID, color);
    }

    public static ArrayList<PCLCustomCardSlot> getCards() {
        return EUIUtils.flattenList(CUSTOM_COLOR_LISTS.values());
    }

    /**
     * Obtain a list of all of the custom cards created for a specific color. If color is NULL, returns ALL custom cards
     */
    public static ArrayList<PCLCustomCardSlot> getCards(AbstractCard.CardColor color) {
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
        CUSTOM_MAPPING.clear();
        loadFolder(getCustomFolder(SUBFOLDER));
        for (CustomFileProvider provider : PROVIDERS) {
            loadFolder(provider.getFolder());
        }
        if (PGR.debugCards != null) {
            PGR.debugCards.refresh();
        }
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getCards(color));
    }

    private static void loadFolder(FileHandle folder) {
        for (FileHandle f : folder.list(JSON_FILTER)) {
            loadSingleCardImpl(f);
        }
    }

    private static void loadSingleCardImpl(FileHandle f) {
        String path = f.path();
        try {
            String jsonString = f.readString();
            PCLCustomCardSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getCards(slot.slotColor).add(slot);
            CUSTOM_MAPPING.put(slot.ID, slot);
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Card: " + path);
        }
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getCards(color));
    }

    protected void commitBuilder() {
        recordBuilder();
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath)) {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Card: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath)) {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Card Image: " + imagePath + ", New: " + newImagePath);
        }

        filePath = newFilePath;
        imagePath = newImagePath;

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        PCLDynamicCardData builder = getBuilder(0);
        if (builder != null && builder.portraitImage != null) {
            PixmapIO.writePNG(imgWriter, builder.portraitImage.texture.getTextureData().consumePixmap());
            // Forcibly reload the image
            EUIRM.reloadTexture(newImagePath, true, PGR.config.lowVRAM.get());
        }

        // Unlink temporary portrait images to allow the new saved portrait image to be loaded, and set multiform data as necessary
        for (PCLDynamicCardData b : builders) {
            b.setImagePath(newImagePath).setImage(null).setMultiformData(forms.length, false, forms.length > 1, false);
        }

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false);
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved Custom Card: " + filePath);
        if (PGR.debugCards != null) {
            PGR.debugCards.refresh();
        }
    }

    public PCLDynamicCardData getBuilder(int i) {
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

    /**
     * Create the card from the first builder; i.e. the base card for this slot. Useful when showing card previews, etc.
     */
    public PCLDynamicCard make() {
        return getBuilder(0).createImplWithForms(0, 0);
    }

    // Copy down the properties from all builders into this slot
    protected void recordBuilder() {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicCardData first = getBuilder(0);
        if (first != null) {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            loadout = first.loadout != null ? first.loadout.ID : null;
            type = first.cardType.name();
            rarity = first.cardRarity.name();
            color = first.cardColor.name();
            damage = first.damage.clone();
            damageUpgrade = first.damageUpgrade.clone();
            block = first.block.clone();
            blockUpgrade = first.blockUpgrade.clone();
            tempHP = first.magicNumber.clone();
            tempHPUpgrade = first.magicNumberUpgrade.clone();
            heal = first.hp.clone();
            healUpgrade = first.hpUpgrade.clone();
            hitCount = first.hitCount.clone();
            hitCountUpgrade = first.hitCountUpgrade.clone();
            rightCount = first.rightCount.clone();
            rightCountUpgrade = first.rightCountUpgrade.clone();
            cost = first.cost.clone();
            costUpgrade = first.costUpgrade.clone();
            maxUpgradeLevel = first.maxUpgradeLevel;
            maxCopies = first.maxCopies;
            unique = first.unique;
            removableFromDeck = first.removableFromDeck;
            branchUpgradeFactor = first.branchFactor;
            affinities = EUIUtils.serialize(first.affinities);
            tags = EUIUtils.mapAsNonnull(first.tags.values(), EUIUtils::serialize).toArray(new String[]{});
            flags = EUIUtils.mapAsNonnull(first.flags, f -> f.ID).toArray(new String[]{});
        }

        for (PCLDynamicCardData builder : builders) {
            CardForm f = new CardForm();
            f.target = builder.cardTarget.name();
            f.timing = builder.timing.name();
            f.attackType = builder.attackType.name();
            f.damageEffect = builder.attackSkill != null ? builder.attackSkill.serialize() : null;
            f.blockEffect = builder.blockSkill != null ? builder.blockSkill.serialize() : null;
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
            CardForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicCardData builder = new PCLDynamicCardData(this, f);
            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicCardData builder : builders) {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Card: " + filePath);
    }

    protected void wipeBuilder() {
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Card Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Card: " + filePath);
    }

    public static class CardForm implements Serializable {
        static final long serialVersionUID = 1L;
        public String attackType;
        public String damageEffect;
        public String blockEffect;
        public String target;
        public String timing;
        public String[] effects;
        public String[] powerEffects;
    }
}
