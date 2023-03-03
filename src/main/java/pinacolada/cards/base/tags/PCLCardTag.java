package pinacolada.cards.base.tags;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.BlueCandle;
import com.megacrit.cardcrawl.relics.MedicalKit;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLCardTagInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PCLCardTag implements TooltipProvider
{
    Autoplay(false, new Color(0.33f, 0.33f, 0.45f, 1)),
    Delayed(false, new Color(0.26f, 0.26f, 0.26f, 1)),
    Ephemeral(false, new Color(0.7f, 0.7f, 0.7f, 1)),
    Ethereal(false, new Color(0.51f, 0.69f, 0.6f, 1)),
    Exhaust(false, new Color(0.81f, 0.35f, 0.35f, 1)),
    Fleeting(false, new Color(0.5f, 0.37f, 0.3f, 1)),
    Fragile(false, new Color(0.80f, 0.46f, 0.7f, 1)),
    Grave(false, new Color(0.4f, 0.4f, 0.4f, 1)),
    Haste(true, new Color(0.35f, 0.5f, 0.79f, 1)),
    Innate(true, new Color(0.8f, 0.8f, 0.35f, 1)),
    Loyal(true, new Color(0.81f, 0.51f, 0.3f, 1)),
    Persist(true, new Color(0.6f, 0.66f, 0.33f, 1)),
    Purge(false, new Color(0.71f, 0.3f, 0.55f, 1)),
    Recast(false, new Color(0.6f, 0.51f, 0.69f, 1)),
    Retain(true, new Color(0.49f, 0.78f, 0.35f, 1)),
    Unplayable(false, new Color(0.3f, 0.20f, 0.20f, 1));

    public static final float HEIGHT = 38f;
    public final boolean isPositive;
    public final Color color;

    PCLCardTag(boolean isPositive, Color color)
    {
        this.isPositive = isPositive;
        this.color = color;
    }

    public static PCLCardTag get(String name)
    {
        return PCLCardTag.valueOf(name);
    }

    public static PCLCardTag get(AbstractCard.CardTags tag)
    {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static List<PCLCardTag> getAll()
    {
        PCLCardTag[] values = PCLCardTag.values();
        Arrays.sort(values, (a, b) -> StringUtils.compare(a.getTip().title, b.getTip().title));
        return Arrays.asList(values);
    }

    /* Renders all of the tags on a card. Returns the total height of all the tags rendered */
    public static float renderTagsOnCard(SpriteBatch sb, AbstractCard card, float alpha)
    {
        int offset_y = 0;
        if (!PGR.core.config.displayCardTagDescription.get())
        {
            for (PCLCardTag tag : PCLCardTag.getAll())
            {
                if (tag.has(card) && tag.getTip().icon != null)
                {
                    offset_y -= tag.renderOnCard(sb, card, offset_y, alpha);
                }
            }
        }
        return offset_y;
    }

    public SpireField<Boolean> getFieldBoolean()
    {
        switch (this)
        {
            case Autoplay:
                return AutoplayField.autoplay;
            case Ephemeral:
                return EphemeralField.value;
            case Fleeting:
                return FleetingField.fleeting;
            case Fragile:
                return FragileField.value;
            case Grave:
                return GraveField.grave;
            case Unplayable:
                return UnplayableField.value;
        }
        return null;
    }

    public SpireField<Integer> getFieldInteger()
    {
        switch (this)
        {
            case Delayed:
                return DelayedField.value;
            case Exhaust:
                return ExhaustiveField.ExhaustiveFields.exhaustive;
            case Haste:
                return HasteField.value;
            case Innate:
                return InnateField.value;
            case Loyal:
                return LoyalField.value;
            case Persist:
                return PersistFields.basePersist;
            case Purge:
                return PurgeField.value;
            case Retain:
                return RetainField.value;
            case Recast:
                return RecastField.value;
        }
        return null;
    }

    public int add(AbstractCard card, int amount)
    {
        int targetValue = getInt(card);
        // Do not modify the value for infinite items
        if (targetValue >= 0)
        {
            return set(card, targetValue + amount);
        }
        return targetValue;
    }

    public int getInt(AbstractCard card)
    {
        switch (this)
        {
            case Ephemeral:
                return (card.purgeOnUse || EphemeralField.value.get(card)) ? 1 : 0;
            case Ethereal:
                return card.isEthereal ? 1 : 0;
            case Exhaust:
                return Math.max(ExhaustiveField.ExhaustiveFields.exhaustive.get(card), toInt(card.exhaust || card.exhaustOnUseOnce));
            case Innate:
                return Math.max(InnateField.value.get(card), toInt(card.isInnate));
            case Retain:
                int value = RetainField.value.get(card);
                return card.selfRetain ? -1 : value < 0 ? value : Math.max(value, card.retain ? 1 : 0);
        }
        SpireField<Integer> field2 = getFieldInteger();
        if (field2 != null)
        {
            return field2.get(card);
        }
        return toInt(has(card));
    }

    public TextureCache getTextureCache()
    {
        switch (this)
        {
            case Autoplay:
                return PCLCoreImages.Badges.autoplay;
            case Delayed:
                return PCLCoreImages.Badges.delayed;
            case Ephemeral:
                return PCLCoreImages.Badges.ephemeral;
            case Ethereal:
                return PCLCoreImages.Badges.ethereal;
            case Exhaust:
                return PCLCoreImages.Badges.exhaust;
            case Fleeting:
                return PCLCoreImages.Badges.fleeting;
            case Fragile:
                return PCLCoreImages.Badges.fragile;
            case Grave:
                return PCLCoreImages.Badges.grave;
            case Haste:
                return PCLCoreImages.Badges.haste;
            case Innate:
                return PCLCoreImages.Badges.innate;
            case Loyal:
                return PCLCoreImages.Badges.loyal;
            case Persist:
                return PCLCoreImages.Badges.persist;
            case Purge:
                return PCLCoreImages.Badges.purge;
            case Recast:
                return PCLCoreImages.Badges.recast;
            case Retain:
                return PCLCoreImages.Badges.retain;
            case Unplayable:
                return PCLCoreImages.Badges.unplayable;
        }
        throw new EnumConstantNotPresentException(PCLCardTag.class, this.name());
    }

    public EUITooltip getTip()
    {
        switch (this)
        {
            case Autoplay:
                return PGR.core.tooltips.autoplay;
            case Delayed:
                return PGR.core.tooltips.delayed;
            case Ephemeral:
                return PGR.core.tooltips.ephemeral;
            case Ethereal:
                return PGR.core.tooltips.ethereal;
            case Exhaust:
                return PGR.core.tooltips.exhaust;
            case Fleeting:
                return PGR.core.tooltips.fleeting;
            case Fragile:
                return PGR.core.tooltips.fragile;
            case Grave:
                return PGR.core.tooltips.grave;
            case Haste:
                return PGR.core.tooltips.haste;
            case Innate:
                return PGR.core.tooltips.innate;
            case Loyal:
                return PGR.core.tooltips.loyal;
            case Persist:
                return PGR.core.tooltips.persist;
            case Purge:
                return PGR.core.tooltips.purge;
            case Recast:
                return PGR.core.tooltips.recast;
            case Retain:
                return PGR.core.tooltips.retain;
            case Unplayable:
                return PGR.core.tooltips.unplayable;
        }
        return new EUITooltip(this.name());
    }

    public String getName()
    {
        return getTip().title;
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return Collections.singletonList(getTip());
    }

    public boolean has(AbstractCard card)
    {
        switch (this)
        {
            case Ephemeral:
                return card.purgeOnUse || EphemeralField.value.get(card);
            case Ethereal:
                return card.isEthereal;
            case Exhaust:
                return card.exhaust || card.exhaustOnUseOnce || ExhaustiveField.ExhaustiveFields.exhaustive.get(card) > -1;
            case Innate:
                return card.isInnate;
            case Retain:
                return card.retain || card.selfRetain;
            case Unplayable:
                return (UnplayableField.value.get(card) && !((GameUtilities.hasRelicEffect(BlueCandle.ID) && card.type == AbstractCard.CardType.CURSE) ||
                        (GameUtilities.hasRelicEffect(MedicalKit.ID) && card.type == AbstractCard.CardType.STATUS))) || GameUtilities.isUnplayableThisTurn(card);
        }
        SpireField<Boolean> field = getFieldBoolean();
        if (field != null)
        {
            return field.get(card);
        }
        SpireField<Integer> field2 = getFieldInteger();
        if (field2 != null)
        {
            return field2.get(card) > 0;
        }
        return false;
    }

    public PCLCardTagInfo make()
    {
        return new PCLCardTagInfo(this, 1);
    }

    public PCLCardTagInfo make(Integer value)
    {
        return new PCLCardTagInfo(this, value, value);
    }

    public PCLCardTagInfo make(Integer value, Integer upVals)
    {
        return new PCLCardTagInfo(this, value, upVals);
    }

    public PCLCardTagInfo make(Integer value, Integer[] upVals)
    {
        return new PCLCardTagInfo(this, value, upVals);
    }

    public PCLCardTagInfo make(Integer[] values, Integer[] upVals)
    {
        return new PCLCardTagInfo(this, values, upVals);
    }

    public float renderOnCard(SpriteBatch sb, AbstractCard card, float offset_y, float alpha)
    {
        Vector2 offset = new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y);

        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBadge.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, color, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, getTip().icon.getTexture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBorder.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);

        int tagCount = getInt(card);
        if (tagCount < 0)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.Badges.baseInfinite.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        }
        else if (tagCount > 1)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.Badges.baseMulti.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        }

        return HEIGHT;
    }

    private void set(AbstractCard card, boolean value)
    {
        switch (this)
        {
            case Ethereal:
                card.isEthereal = value;
                return;
            // These tags also need to set the field
            case Ephemeral:
                card.purgeOnUse = value;
                break;
        }
        SpireField<Boolean> field = getFieldBoolean();
        if (field != null)
        {
            field.set(card, value);
        }
    }

    public int set(AbstractCard card, int amount)
    {
        // These tags also need to set the field
        switch (this)
        {
            case Retain:
                card.retain = amount != 0;
                if (amount < 0)
                {
                    card.selfRetain = true;
                }
                break;
            case Innate:
                card.isInnate = amount != 0;
                break;
            // Only set exhaustive if value is greater than 1, because setting it to 0 still exhausts the card
            case Exhaust:
                if (amount > 1)
                {
                    card.exhaust = false;
                    ExhaustiveField.ExhaustiveFields.exhaustive.set(card, amount);
                }
                else
                {
                    card.exhaust = amount == 1;
                }

                return amount;
        }
        SpireField<Integer> field = getFieldInteger();
        if (field != null)
        {
            field.set(card, amount);
        }
        else
        {
            set(card, toBoolean(amount));
        }
        return amount;
    }

    public boolean tryProgress(AbstractCard card)
    {
        SpireField<Integer> field = getFieldInteger();
        if (field != null)
        {
            int value = field.get(card);
            if (value > 0)
            {
                value -= 1;
                field.set(card, value);
            }
            return value == 0;
        }
        return false;
    }

    private boolean toBoolean(int amount)
    {
        return amount > 0;
    }

    private int toInt(boolean amount)
    {
        return amount ? 1 : 0;
    }
}
