package pinacolada.cards.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.EUIUtils.array;
import static pinacolada.resources.pcl.PCLCoreConfig.JSON_FILTER;

public class PCLCustomCardSlot
{
    public static final int ID_SIZE = 4;
    public static final String BASE_ID = "pclC";
    public static final String FOLDER = "PCLCustomCards";
    private static final TypeToken<PCLCustomCardSlot> TToken = new TypeToken<PCLCustomCardSlot>() {};
    private static final TypeToken<CardForm> TTokenForm = new TypeToken<CardForm>() {};
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomCardSlot>> CustomCards = new HashMap<>();
    public final String ID;
    public Integer loadout;
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
    public transient ArrayList<PCLCardBuilder> builders = new ArrayList<>();
    public transient AbstractCard.CardColor slotColor = AbstractCard.CardColor.COLORLESS;
    protected transient String filePath;
    protected transient String imagePath;

    public PCLCustomCardSlot(AbstractCard.CardColor color)
    {
        ID = makeNewID(color);
        filePath = FOLDER + "/" + ID + ".json";
        imagePath = FOLDER + "/" + ID + ".png";
        slotColor = color;
        PCLCardBuilder builder = new PCLCardBuilder(ID)
                .setText("", "", "")
                .setColor(color);
        builders.add(builder);
    }

    public PCLCustomCardSlot(PCLCardData data)
    {
        ID = makeNewID(data.cardColor);
        filePath = FOLDER + "/" + ID + ".json";
        imagePath = FOLDER + "/" + ID + ".png";
        slotColor = data.cardColor;
        builders.add(new PCLCardBuilder(data, true)
                .setID(ID)
                .setImagePath(imagePath));
    }

    public PCLCustomCardSlot(PCLCardBuilder builder)
    {
        ID = makeNewID(builder.cardColor);
        filePath = FOLDER + "/" + ID + ".json";
        imagePath = FOLDER + "/" + ID + ".png";
        slotColor = builder.cardColor;
        builders.add(builder
                .setID(ID)
                .setImagePath(imagePath));
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other)
    {
        ID = makeNewID(other.slotColor);
        filePath = FOLDER + "/" + ID + ".json";
        imagePath = FOLDER + "/" + ID + ".png";
        slotColor = other.slotColor;
        for (PCLCardBuilder builder : other.builders)
        {
            builders.add(new PCLCardBuilder(builder)
                    .setID(ID)
                    .setImagePath(imagePath));
        }
    }

    public PCLCustomCardSlot(PCLCustomCardSlot other, AbstractCard.CardColor color)
    {
        this(other);
        slotColor = color;
        for (PCLCardBuilder builder : builders)
        {
            builder.setColor(color);
        }
    }

    public static PCLCustomCardSlot get(String id)
    {
        for (ArrayList<PCLCustomCardSlot> slots : CustomCards.values())
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
            return EUIUtils.flattenList(CustomCards.values());
        }
        if (!CustomCards.containsKey(color))
        {
            CustomCards.put(color, new ArrayList<>());
        }
        return CustomCards.get(color);
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
        CustomCards.clear();
        loadFolder(getBaseCustomCardFolder());
        PGR.core.debugCards.refreshCards();
    }

    public static void loadFolder(FileHandle folder)
    {
        for (FileHandle f : folder.list(JSON_FILTER))
        {
            loadSingleCardImpl(f);
        }
        PGR.core.debugCards.refreshCards();
    }

    private static void loadSingleCardImpl(FileHandle f)
    {
        String path = f.path();
        try
        {
            String jsonString = f.readString();
            PCLCustomCardSlot slot = EUIUtils.deserialize(jsonString, TToken.getType());
            slot.setupBuilder(path);
            getCards(slot.slotColor).add(slot);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(PCLCustomCardSlot.class, "Could not load Custom Card: " + path);
        }
    }

    public static void loadSingleCard(FileHandle f)
    {
        loadSingleCardImpl(f);
        PGR.core.debugCards.refreshCards();
    }

    private static boolean isBuilderDuplicate(StringBuilder sb, AbstractCard.CardColor color)
    {
        String s = sb.toString();
        return EUIUtils.any(getCards(color), c -> c.ID.equals(s));
    }

    protected static String makeNewID(AbstractCard.CardColor color)
    {
        StringBuilder sb = new StringBuilder(BASE_ID + "_" + color.name() + "_");
        for (int i = 0; i < ID_SIZE; i++)
        {
            sb.append(makeRandomCharIndex());
        }

        while (isBuilderDuplicate(sb, color))
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

    public void commitBuilder()
    {

        ArrayList<String> tempForms = new ArrayList<>();

        for (PCLCardBuilder builder : builders)
        {
            CardForm f = new CardForm();

            languageStrings = EUIUtils.serialize(builder.languageMap);
            loadout = builder.loadout != null ? builder.loadout.id : null;
            type = builder.cardType.name();
            rarity = builder.cardRarity.name();
            color = builder.cardColor.name();
            target = builder.cardTarget.name();
            damage = builder.damage.clone();
            damageUpgrade = builder.damageUpgrade.clone();
            block = builder.block.clone();
            blockUpgrade = builder.blockUpgrade.clone();
            tempHP = builder.magicNumber.clone();
            tempHPUpgrade = builder.magicNumberUpgrade.clone();
            heal = builder.hp.clone();
            healUpgrade = builder.hpUpgrade.clone();
            hitCount = builder.hitCount.clone();
            hitCountUpgrade = builder.hitCountUpgrade.clone();
            rightCount = builder.rightCount.clone();
            rightCountUpgrade = builder.rightCountUpgrade.clone();
            cost = builder.cost.clone();
            costUpgrade = builder.costUpgrade.clone();
            affinities = EUIUtils.serialize(builder.affinities);
            tags = EUIUtils.mapAsNonnull(builder.tags.values(), EUIUtils::serialize).toArray(new String[]{});

            f.attackType = builder.attackType.name();
            f.attackEffect = builder.attackEffect.name();
            f.effects = EUIUtils.mapAsNonnull(builder.moves, b -> b != null ? b.serialize() : null).toArray(new String[]{});
            f.powerEffects = EUIUtils.mapAsNonnull(builder.powers, b -> b != null ? b.serialize() : null).toArray(new String[]{});

            tempForms.add(EUIUtils.serialize(f, TTokenForm.getType()));
        }

        forms = tempForms.toArray(new String[]{});

        FileHandle writer = Gdx.files.local(filePath);
        writer.writeString(EUIUtils.serialize(this, TToken.getType()), false);
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Saved Custom Card: " + filePath);
        PGR.core.debugCards.refreshCards();
    }

    public PCLCardBuilder getBuilder(int i)
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

    public void setupBuilder(String fp)
    {
        slotColor = AbstractCard.CardColor.valueOf(color);
        builders = new ArrayList<>();

        for (String fo : forms)
        {
            CardForm f = EUIUtils.deserialize(fo, TTokenForm.getType());
            PCLCardBuilder builder = new PCLCardBuilder(this);
            builder.setAttackType(PCLAttackType.valueOf(f.attackType));
            builder.setAttackEffect(AbstractGameAction.AttackEffect.valueOf(f.attackEffect));
            builder.setPSkill(EUIUtils.mapAsNonnull(f.effects, PSkill::get), true, true);
            builder.setPPower(EUIUtils.mapAsNonnull(f.powerEffects, pe -> EUIUtils.safeCast(PSkill.get(pe), PTrigger.class)));

            builders.add(builder);
        }

        imagePath = FOLDER + "/" + ID + ".png";
        for (PCLCardBuilder builder : builders)
        {
            builder.setImagePath(imagePath);
        }

        filePath = fp;
        EUIUtils.logInfo(PCLCustomCardSlot.class, "Loaded Custom Card: " + filePath);
    }

    public void wipeBuilder()
    {
        CustomCards.get(slotColor).remove(this);
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
        public String attackEffect;
        public String[] effects;
        public String[] powerEffects;

        public CardForm setNumbers(PCLCardData data)
        {

            return this;
        }
    }
}
