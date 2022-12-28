package pinacolada.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CustomCardLibraryScreen;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CSVExporter
{
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String DEFAULT_EXPORT_PATH = "exported.csv";

    public static void export(List<? extends AbstractCard> cards)
    {
        ArrayList<CardRow> rows = EUIUtils.map(cards, c -> {
            if (c instanceof PCLCard)
            {
                return new CardRow((PCLCard) c);
            }
            else
            {
                return new CardRow(c);
            }
        });
        exportRows(rows);
    }

    public static void export(AbstractCard.CardColor color)
    {
        CardGroup group = CustomCardLibraryScreen.CardLists.get(color);
        if (group != null)
        {
            export(group.group);
        }
    }

    public static void exportRows(List<CardRow> cards)
    {
        cards.sort(CardRow::compareTo);
        try
        {
            FileHandle handle = getExportFile();
            handle.writeString(getHeaderRow(), true);
            for (CardRow row : cards)
            {
                handle.writeString(row.toString(), true);
            }
            EUIUtils.logInfo(PCLCustomCardSlot.class, "Exported " + cards.size() + " cards to " + handle.path());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(CSVExporter.class, "Failed to export cards.");
        }
    }

    private static FileHandle getExportFile()
    {
        FileHandle handle = Gdx.files.local(DEFAULT_EXPORT_PATH);
        if (handle.exists())
        {
            handle.delete();
        }
        return handle;
    }

    private static String getHeaderRow()
    {
        return EUIUtils.joinStrings(",", EUIUtils.map(CardRow.class.getDeclaredFields(), Field::getName)) + NEWLINE;
    }

    private static String parseCardString(AbstractCard card)
    {
        return card.rawDescription
                .replace("!D!", String.valueOf(card.baseDamage))
                .replace("!B!", String.valueOf(card.baseBlock))
                .replace("!M!", String.valueOf(card.baseMagicNumber))
                .replace(" NL ", " ");
    }

    public static class CardRow implements Comparable<CardRow>
    {
        public String ID;
        public String name;
        public int form;
        public String series;
        public String type;
        public String rarity;
        public String color;
        public String cardTarget;
        public String attackType;
        public int damage;
        public int damageUpgrade;
        public int block;
        public int blockUpgrade;
        public int tempHP;
        public int tempHPUpgrade;
        public int heal;
        public int healUpgrade;
        public int hitCount;
        public int hitCountUpgrade;
        public int rightCount;
        public int rightCountUpgrade;
        public int cost;
        public int costUpgrade;
        public int red;
        public int redUpgrade;
        public int redScaling;
        public int green;
        public int greenUpgrade;
        public int greenScaling;
        public int blue;
        public int blueUpgrade;
        public int blueScaling;
        public int orange;
        public int orangeUpgrade;
        public int orangeScaling;
        public int light;
        public int lightUpgrade;
        public int lightScaling;
        public int dark;
        public int darkUpgrade;
        public int darkScaling;
        public int silver;
        public int silverUpgrade;
        public int silverScaling;
        public int star;
        public int starUpgrade;
        public int starScaling;
        public String tags;
        public String effects;

        public CardRow(PCLCard card)
        {
            PCLCardData data = card.cardData;
            int form = card.getForm();
            this.form = form;
            ID = data.ID;
            name = card.name;
            series = data.loadout != null ? data.loadout.getName() : null;
            type = String.valueOf(data.cardType);
            rarity = String.valueOf(data.cardRarity);
            color = String.valueOf(data.cardColor);
            cardTarget = String.valueOf(data.cardTarget);
            attackType = String.valueOf(data.attackType);
            damage = data.getDamage(form);
            damageUpgrade = data.getDamageUpgrade(form);
            block = data.getBlock(form);
            blockUpgrade = data.getBlockUpgrade(form);
            tempHP = data.getMagicNumber(form);
            tempHPUpgrade = data.getMagicNumberUpgrade(form);
            heal = data.getHp(form);
            healUpgrade = data.getHpUpgrade(form);
            hitCount = data.getHitCount(form);
            hitCountUpgrade = data.getHitCountUpgrade(form);
            rightCount = data.getRightCount(form);
            rightCountUpgrade = data.getRightCountUpgrade(form);
            cost = data.getCost(form);
            costUpgrade = data.getCostUpgrade(form);

            PCLCardDataAffinityGroup affinities = data.affinities;
            red = affinities.getLevel(PCLAffinity.Red, form);
            redUpgrade = affinities.getUpgrade(PCLAffinity.Red, form);
            green = affinities.getLevel(PCLAffinity.Green, form);
            greenUpgrade = affinities.getUpgrade(PCLAffinity.Green, form);
            blue = affinities.getLevel(PCLAffinity.Blue, form);
            blueUpgrade = affinities.getUpgrade(PCLAffinity.Blue, form);
            orange = affinities.getLevel(PCLAffinity.Orange, form);
            orangeUpgrade = affinities.getUpgrade(PCLAffinity.Orange, form);
            light = affinities.getLevel(PCLAffinity.Yellow, form);
            lightUpgrade = affinities.getUpgrade(PCLAffinity.Yellow, form);
            dark = affinities.getLevel(PCLAffinity.Purple, form);
            darkUpgrade = affinities.getUpgrade(PCLAffinity.Purple, form);
            silver = affinities.getLevel(PCLAffinity.Silver, form);
            silverUpgrade = affinities.getUpgrade(PCLAffinity.Silver, form);
            star = affinities.getLevel(PCLAffinity.Star, form);
            starUpgrade = affinities.getUpgrade(PCLAffinity.Star, form);

            tags = EUIUtils.joinStrings("/", EUIUtils.map(data.tags.values(), tagInfo -> tagInfo.tag));
            effects = EUIUtils.joinStrings(PSkill.EFFECT_SEPARATOR, EUIUtils.map(card.getFullEffects(), PSkill::getExportText));
        }

        public CardRow(AbstractCard card)
        {
            form = 0;
            ID = card.cardID;
            name = card.name;
            series = null;
            type = String.valueOf(card.type);
            rarity = String.valueOf(card.rarity);
            color = String.valueOf(card.color);
            cardTarget = String.valueOf(card.target);
            attackType = PCLAttackType.Normal.toString();
            damage = card.baseDamage;
            damageUpgrade = 0;
            block = card.baseBlock;
            blockUpgrade = 0;
            tempHP = 0;
            tempHPUpgrade = 0;
            heal = card.baseHeal;
            healUpgrade = 0;
            hitCount = 1;
            hitCountUpgrade = 0;
            rightCount = 0;
            rightCountUpgrade = 0;
            cost = card.cost;
            costUpgrade = 0;
            tags = EUIUtils.joinStrings("/", EUIUtils.filter(PCLCardTag.getAll(), tagInfo -> tagInfo.has(card)));
            effects = parseCardString(card);
        }

        @Override
        public int compareTo(CardRow o)
        {
            int value = ID.compareTo(o.ID);
            return value == 0 ? form - o.form : value;
        }

        @Override
        public final String toString()
        {
            return EUIUtils.joinStrings(",", EUIUtils.map(CardRow.class.getDeclaredFields(), field -> {
                try
                {
                    return field.get(this);
                }
                catch (IllegalAccessException e)
                {
                    return "";
                }
            })) + NEWLINE;
        }
    }

}
