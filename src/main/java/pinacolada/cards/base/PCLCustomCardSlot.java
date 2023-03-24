package pinacolada.cards.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.tags.CardTagItem;
import pinacolada.interfaces.providers.CustomCardFileProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.resources.PCLMainConfig.JSON_FILTER;

public class PCLCustomCardSlot
{
    public static final int ID_SIZE = 4;
    public static final String BASE_CARD_ID = "PCLC";
    public static final String FOLDER = "custom";
    private static final TypeToken<PCLCustomCardSlot> TTOKEN = new TypeToken<PCLCustomCardSlot>()
    {
    };
    private static final TypeToken<CardForm> TTOKENFORM = new TypeToken<CardForm>()
    {
    };
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomCardSlot>> CUSTOM_CARDS = new HashMap<>();
    private static final ArrayList<CustomCardFileProvider> PROVIDERS = new ArrayList<>();
    public String ID;
    public Integer loadout;
    public Integer maxUpgradeLevel = 1;
    public Integer maxCopies = -1;
    public Boolean linearUpgrade;
    public Boolean removableFromDeck;
    public Boolean unique;
    public String type;
    public String rarity;
    public String color;
    public String target;
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
    public Integer[] cost = array(-2);
    public Integer[] costUpgrade = array(0);
    public String[] tags;
    public String[] forms;
    public transient ArrayList<PCLDynamicData> builders = new ArrayList<>();
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;
    protected transient String filePath;
    protected transient String imagePath;

    public PCLCustomCardSlot(AbstractCard.CardColor color)
    {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        PCLDynamicData builder = new PCLDynamicData(ID, PGR.getResources(slotColor))
                .setText("", "", "")
                .setColor(color);
        builders.add(builder);
    }

    public PCLCustomCardSlot(PCLCard card, AbstractCard.CardColor color)
    {
        ID = makeNewID(color);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = color;
        builders.add(new PCLDynamicData(card.cardData, true)
                        .setColor(color)
                        .setID(ID)
                        .setImagePath(imagePath)
                        .setPSkill(card.getEffects(), true, true)
                        .setPPower(card.getPowerEffects(), true, true)
                        .setAttackSkill(card.onAttackEffect)
                        .setBlockSkill(card.onBlockEffect)
                        .setExtraTags(CardTagItem.getFromCard(card))
        );
        recordBuilder();
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other)
    {
        ID = makeNewID(other.slotColor);
        filePath = makeFilePath();
        imagePath = makeImagePath();
        slotColor = other.slotColor;
        for (PCLDynamicData builder : other.builders)
        {
            builders.add(new PCLDynamicData(builder)
                    .setID(ID)
                    .setImagePath(imagePath));
        }
        recordBuilder();
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other, AbstractCard.CardColor color)
    {
        this(other);
        slotColor = color;
        for (PCLDynamicData builder : builders)
        {
            builder.setColor(color);
        }
    }

    /** Subscribe a provider that provides a folder to load custom cards from whenever the cards are reloaded */
    public static void addProvider(CustomCardFileProvider provider)
    {
        PROVIDERS.add(provider);
    }

    // Only allow a card to be copied into a custom card slot if it is a PCLCard and if all of its skills are in AVAILABLE_SKILLS (i.e. selectable in the card editor)
    public static boolean canFullyCopyCard(AbstractCard card)
    {
        if (card instanceof PCLCard)
        {
            return EUIUtils.all(((PCLCard) card).getFullSubEffects(), skill -> skill != null && skill.getClass().isAnnotationPresent(VisibleSkill.class));
        }
        return false;
    }

    public static PCLCustomCardSlot get(String id)
    {
        for (ArrayList<PCLCustomCardSlot> slots : CUSTOM_CARDS.values())
        {
            for (PCLCustomCardSlot slot : slots)
            {
                if (slot.ID.equals(id))
                {
                    return slot;
                }
            }
        }
        return null;
    }

    public static ArrayList<PCLCustomCardSlot> getCards(AbstractCard.CardColor color)
    {
        if (color == null)
        {
            return EUIUtils.flattenList(CUSTOM_CARDS.values());
        }
        if (!CUSTOM_CARDS.containsKey(color))
        {
            CUSTOM_CARDS.put(color, new ArrayList<>());
        }
        return CUSTOM_CARDS.get(color);
    }

    private static FileHandle getBaseCustomCardFolder()
    {
        FileHandle folder = Gdx.files.local(FOLDER);
        if (!folder.exists())
        {
            folder.mkdirs();
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Created Custom Card Folder: " + folder.path());
        }
        return folder;
    }

    public static void initialize()
    {
        CUSTOM_CARDS.clear();
        loadFolder(getBaseCustomCardFolder());
        for (CustomCardFileProvider provider : PROVIDERS)
        {
            loadFolder(provider.getCardFolder());
        }
        if (PGR.debugCards != null)
        {
            PGR.debugCards.refreshCards();
        }
    }

    private static void loadFolder(FileHandle folder)
    {
        for (FileHandle f : folder.list(JSON_FILTER))
        {
            loadSingleCardImpl(f);
        }
    }

    private static void loadSingleCardImpl(FileHandle f)
    {
        String path = f.path();
        try
        {
            String jsonString = f.readString();
            PCLCustomCardSlot slot = EUIUtils.deserialize(jsonString, TTOKEN.getType());
            slot.setupBuilder(path);
            getCards(slot.slotColor).add(slot);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Card: " + path);
        }
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color)
    {
        return EUIUtils.any(getCards(color), c -> c.ID.equals(input));
    }

    public static String getBaseIDPrefix(AbstractCard.CardColor color)
    {
        return BASE_CARD_ID + "_" + color.name() + "_";
    }

    protected static String makeNewID(AbstractCard.CardColor color)
    {
        StringBuilder sb = new StringBuilder(getBaseIDPrefix(color));
        for (int i = 0; i < ID_SIZE; i++)
        {
            sb.append(makeRandomCharIndex());
        }

        while (isIDDuplicate(sb.toString(), color))
        {
            sb.append(makeRandomCharIndex());
        }
        return sb.toString();
    }

    private static char makeRandomCharIndex()
    {
        int i = MathUtils.random(65, 100);
        return (char) (i > 90 ? i - 43 : i);
    }

    private String makeFilePath()
    {
        return FOLDER + "/" + ID + ".json";
    }

    private String makeImagePath()
    {
        return FOLDER + "/" + ID + ".png";
    }

    // Copy down the properties from all builders into this slot
    public void recordBuilder()
    {
        ArrayList<String> tempForms = new ArrayList<>();

        // All builders should have identical sets of these properties
        PCLDynamicData first = getBuilder(0);
        if (first != null)
        {
            ID = first.ID;
            languageStrings = EUIUtils.serialize(first.languageMap);
            loadout = first.loadout != null ? first.loadout.id : null;
            type = first.cardType.name();
            rarity = first.cardRarity.name();
            color = first.cardColor.name();
            target = first.cardTarget.name();
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
            linearUpgrade = first.linearUpgrade;
            affinities = EUIUtils.serialize(first.affinities);
            tags = EUIUtils.mapAsNonnull(first.tags.values(), EUIUtils::serialize).toArray(new String[]{});
        }

        for (PCLDynamicData builder : builders)
        {
            CardForm f = new CardForm();

            f.attackType = builder.attackType.name();
            f.damageEffect = builder.attackSkill != null ? builder.attackSkill.serialize() : null;
            f.blockEffect = builder.blockSkill != null ? builder.blockSkill.serialize() : null;
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTOKENFORM.getType()));
        }

        forms = tempForms.toArray(new String[]{});
    }

    public void commitBuilder()
    {
        recordBuilder();
        String newFilePath = makeFilePath();
        String newImagePath = makeImagePath();

        // If the file path has changed and the original file exists, we should move the file and its image
        FileHandle writer = Gdx.files.local(filePath);
        if (writer.exists() && !newFilePath.equals(filePath))
        {
            writer.moveTo(Gdx.files.local(newFilePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Card: " + filePath + ", New: " + newFilePath);
        }
        writer = Gdx.files.local(newFilePath);

        // The image should have the same file name as the file path
        FileHandle imgWriter = Gdx.files.local(imagePath);
        if (imgWriter.exists() && !newImagePath.equals(imagePath))
        {
            imgWriter.moveTo(Gdx.files.local(newImagePath));
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Moved Custom Card Image: " + imagePath + ", New: " + newImagePath);
        }

        // If the image in the builder was updated, we need to overwrite the existing image
        // All builders should have the same image
        PCLDynamicData builder = getBuilder(0);
        if (builder != null && builder.portraitImage != null)
        {
            PixmapIO.writePNG(imgWriter, builder.portraitImage.texture.getTextureData().consumePixmap());
            // Forcibly reload the image
            EUIRM.getLocalTexture(newImagePath, true, true, false);
        }

        // Unlink temporary portrait images to allow the new saved portrait image to be loaded, and set multiform data as necessary
        for (PCLDynamicData b : builders)
        {
            b.setImage(null).setMultiformData(forms.length, false, forms.length > 1, false);
        }

        filePath = newFilePath;
        imagePath = newImagePath;

        writer.writeString(EUIUtils.serialize(this, TTOKEN.getType()), false);
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved Custom Card: " + filePath);
        if (PGR.debugCards != null)
        {
            PGR.debugCards.refreshCards();
        }
    }

    public PCLDynamicData getBuilder(int i)
    {
        return (builders.size() > i) ? builders.get(i) : null;
    }

    public FileHandle getImageHandle()
    {
        return Gdx.files.local(imagePath);
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public PCLDynamicCard makeFirstCard(boolean shouldFindForms)
    {
        return getBuilder(0).createImplWithForms(shouldFindForms);
    }

    public void setupBuilder(String fp)
    {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();

        for (String fo : forms)
        {
            CardForm f = EUIUtils.deserialize(fo, TTOKENFORM.getType());
            PCLDynamicData builder = new PCLDynamicData(this);
            builder.setAttackType(PCLAttackType.valueOf(f.attackType));
            if (f.damageEffect != null)
            {
                builder.setAttackSkill(EUIUtils.safeCast(PSkill.get(f.damageEffect), PCardPrimary_DealDamage.class));
            }
            if (f.blockEffect != null)
            {
                builder.setBlockSkill(EUIUtils.safeCast(PSkill.get(f.blockEffect), PCardPrimary_GainBlock.class));
            }
            builder.setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true);
            builder.setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class)));

            builders.add(builder);
        }

        imagePath = makeImagePath();
        for (PCLDynamicData builder : builders)
        {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Card: " + filePath);
    }

    public void wipeBuilder()
    {
        CUSTOM_CARDS.get(slotColor).remove(this);
        FileHandle writer = getImageHandle();
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Card Image: " + imagePath);
        writer = Gdx.files.local(filePath);
        writer.delete();
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Deleted Custom Card: " + filePath);
    }

    public static class CardForm
    {
        public String attackType;
        public String damageEffect;
        public String blockEffect;
        public String[] effects;
        public String[] powerEffects;

        public CardForm setNumbers(PCLCardData data)
        {

            return this;
        }
    }
}
