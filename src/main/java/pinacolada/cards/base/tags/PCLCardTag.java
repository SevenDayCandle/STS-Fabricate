package pinacolada.cards.base.tags;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.AutoplayField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.ExhaustiveField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.GraveField;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLCardTagInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pinacolada.skills.PSkill.DEFAULT_EXTRA_MIN;

public enum PCLCardTag implements TooltipProvider {
    Autoplay(new Color(0.33f, 0.33f, 0.45f, 1), 0, 1, false),
    Bounce(new Color(0.6f, 0.66f, 0.33f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, false),
    Delayed(new Color(0.26f, 0.26f, 0.26f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, true),
    Ephemeral(new Color(0.7f, 0.7f, 0.7f, 1), 0, 1, true),
    Ethereal(new Color(0.51f, 0.69f, 0.6f, 1), 0, 1, true),
    Exhaust(new Color(0.81f, 0.35f, 0.35f, 1), 0, Integer.MAX_VALUE, false),
    Fleeting(new Color(0.5f, 0.37f, 0.3f, 1), 0, 1, false),
    Fragile(new Color(0.80f, 0.46f, 0.7f, 1), 0, 1, true),
    Grave(new Color(0.4f, 0.4f, 0.4f, 1), 0, 1, true),
    Haste(new Color(0.35f, 0.5f, 0.79f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, true),
    Innate(new Color(0.8f, 0.8f, 0.35f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, true),
    Loyal(new Color(0.81f, 0.51f, 0.3f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, true),
    Purge(new Color(0.71f, 0.3f, 0.55f, 1), 0, Integer.MAX_VALUE, false),
    Recast(new Color(0.6f, 0.51f, 0.69f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, false),
    Retain(new Color(0.49f, 0.78f, 0.35f, 1), DEFAULT_EXTRA_MIN, Integer.MAX_VALUE, true),
    Suspensive(new Color(0.5f, 0.65f, 0.75f, 1), 0, 1, true),
    Unplayable(new Color(0.3f, 0.20f, 0.20f, 1), 0, 1, true);

    public static final float HEIGHT = 38f;
    public static final float OFF_X = AbstractCard.RAW_W * 0.45f;
    private static ArrayList<PCLCardTag> PRE;
    private static ArrayList<PCLCardTag> POST;
    public final boolean preText;
    public final int minValue;
    public final int maxValue;
    public final Color color;

    PCLCardTag(Color color, int minValue, int maxValue, boolean preText) {
        this.color = color;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.preText = preText;
    }

    public static PCLCardTag get(AbstractCard.CardTags tag) {
        return get(EUIUtils.capitalize(tag.toString()));
    }

    public static PCLCardTag get(String name) {
        return PCLCardTag.valueOf(name);
    }

    public static List<PCLCardTag> getAll() {
        PCLCardTag[] values = PCLCardTag.values();
        Arrays.sort(values, (a, b) -> StringUtils.compare(a.getTooltip().title, b.getTooltip().title));
        return Arrays.asList(values);
    }

    public static List<PCLCardTag> getPost() {
        if (POST == null) {
            initializePreAndPost();
        }
        return POST;
    }

    public static List<PCLCardTag> getPre() {
        if (PRE == null) {
            initializePreAndPost();
        }
        return PRE;
    }

    public static String getTagTipPostString(AbstractCard card) {
        return getTagTipString(card, getPost());
    }

    public static String getTagTipPreString(AbstractCard card) {
        return getTagTipString(card, getPre());
    }

    private static String getTagTipString(AbstractCard card, List<PCLCardTag> tags) {
        ArrayList<String> tagNames = new ArrayList<>();
        for (PCLCardTag tag : tags) {
            int value = tag.getInt(card);
            switch (value) {
                case -1:
                    // For cards that allow infinite, just show the tag name, imitating vanilla behavior
                    if (tag.minValue == -1) {
                        // Except for innate/delayed
                        if (tag == Innate || tag == Delayed) {
                            tagNames.add(EUIRM.strings.generic2(tag.getTooltip().title, PGR.core.strings.subjects_infinite));
                        }
                        else {
                            tagNames.add(tag.getTooltip().title);
                        }
                    }
                    break;
                case 0:
                    break;
                case 1:
                    // Do not show numerical values for Exhaust, Innate, Delayed or tags that cannot go beyond 1
                    if (tag.maxValue == 1 || tag == Exhaust || tag == Innate || tag == Delayed) {
                        tagNames.add(tag.getTooltip().title);
                        break;
                    }
                default:
                    tagNames.add(EUIRM.strings.generic2(tag.getTooltip().title, value));
                    break;
            }
        }
        return tagNames.size() > 0 ? EUIUtils.joinStrings(PSkill.EFFECT_SEPARATOR, tagNames) + LocalizedStrings.PERIOD : "";
    }

    private static void initializePreAndPost() {
        PRE = new ArrayList<>();
        POST = new ArrayList<>();
        for (PCLCardTag tag : getAll()) {
            if (tag.preText) {
                PRE.add(tag);
            }
            else {
                POST.add(tag);
            }
        }
    }

    /* Renders all of the tags on a card. Returns the total height of all the tags rendered */
    public static float renderTagsOnCard(SpriteBatch sb, AbstractCard card, float alpha) {
        int offset_y = 0;
        if (!PGR.config.displayCardTagDescription.get()) {
            for (PCLCardTag tag : PCLCardTag.getAll()) {
                if (tag.has(card) && tag.getTooltip().icon != null) {
                    offset_y -= tag.renderOnCard(sb, card, offset_y, alpha);
                }
            }
        }
        return offset_y;
    }

    public int add(AbstractCard card, int amount) {
        int targetValue = getInt(card);
        // Do not modify the value for infinite items
        if (targetValue >= 0) {
            return set(card, targetValue + amount);
        }
        return targetValue;
    }

    public SpireField<Boolean> getFieldBoolean() {
        switch (this) {
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
            case Suspensive:
                return SuspensiveField.value;
            case Unplayable:
                return UnplayableField.value;
        }
        return null;
    }

    public SpireField<Integer> getFieldInteger() {
        switch (this) {
            case Bounce:
                return BounceField.value;
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
            case Purge:
                return PurgeField.value;
            case Retain:
                return RetainField.value;
            case Recast:
                return RecastField.value;
        }
        return null;
    }

    public int getInt(AbstractCard card) {
        switch (this) {
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
        if (field2 != null) {
            return field2.get(card);
        }
        return toInt(has(card));
    }

    public String getName() {
        return getTooltip().title;
    }

    public TextureCache getTextureCache() {
        switch (this) {
            case Autoplay:
                return PCLCoreImages.Badges.autoplay;
            case Bounce:
                return PCLCoreImages.Badges.bounce;
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
            case Purge:
                return PCLCoreImages.Badges.purge;
            case Recast:
                return PCLCoreImages.Badges.recast;
            case Retain:
                return PCLCoreImages.Badges.retain;
            case Suspensive:
                return PCLCoreImages.Badges.suspensive;
            case Unplayable:
                return PCLCoreImages.Badges.unplayable;
        }
        throw new EnumConstantNotPresentException(PCLCardTag.class, this.name());
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        switch (this) {
            case Autoplay:
                return PGR.core.tooltips.autoplay;
            case Bounce:
                return PGR.core.tooltips.bounce;
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
            case Purge:
                return PGR.core.tooltips.purge;
            case Recast:
                return PGR.core.tooltips.recast;
            case Retain:
                return PGR.core.tooltips.retain;
            case Suspensive:
                return PGR.core.tooltips.suspensive;
            case Unplayable:
                return PGR.core.tooltips.unplayable;
        }
        return new EUIKeywordTooltip(this.name());
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }

    public boolean has(AbstractCard card) {
        switch (this) {
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
        }
        SpireField<Boolean> field = getFieldBoolean();
        if (field != null) {
            return field.get(card);
        }
        SpireField<Integer> field2 = getFieldInteger();
        if (field2 != null) {
            int val = field2.get(card);
            return val > 0 || (minValue < 0 && val < 0);
        }
        return false;
    }

    public PCLCardTagInfo make() {
        return new PCLCardTagInfo(this, 1);
    }

    public PCLCardTagInfo make(Integer value) {
        return new PCLCardTagInfo(this, value, value);
    }

    public PCLCardTagInfo make(Integer value, Integer upVals) {
        return new PCLCardTagInfo(this, value, upVals);
    }

    public PCLCardTagInfo make(Integer value, Integer[] upVals) {
        return new PCLCardTagInfo(this, value, upVals);
    }

    public PCLCardTagInfo make(Integer[] values, Integer[] upVals) {
        return new PCLCardTagInfo(this, values, upVals);
    }

    public float renderOnCard(SpriteBatch sb, AbstractCard card, float offset_y, float alpha) {
        float offY = AbstractCard.RAW_H * 0.45f + offset_y;
        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBadge.texture(), OFF_X, offY, 64, 64, color, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, getTooltip().icon.getTexture(), OFF_X, offY, 64, 64, Color.WHITE, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBorder.texture(), OFF_X, offY, 64, 64, Color.WHITE, alpha, 1);

        int tagCount = getInt(card);
        if (tagCount < 0) {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.Badges.baseInfinite.texture(), AbstractCard.RAW_W * 0.5f, AbstractCard.RAW_H * 0.42f + offset_y, 96, 64, Color.WHITE, alpha, 0.35f);
        }
        else if (tagCount > 1) {
            String text = String.valueOf(tagCount);
            EUIFontHelper.cardTitleFontSmall.getData().setScale(card.drawScale * 0.73f);
            float offX = AbstractCard.RAW_W * 0.5f;
            offY = AbstractCard.RAW_H * 0.42f + offset_y;
            PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.Badges.baseMulti.texture(), offX, offY, 96, 64, Color.WHITE, alpha, 0.35f);
            PCLRenderHelpers.writeOnCard(sb, card, EUIFontHelper.cardTitleFontSmall, text, offX, offY, Settings.BLUE_TEXT_COLOR, false);
            PCLRenderHelpers.resetFont(EUIFontHelper.cardTitleFontSmall);
        }

        return HEIGHT;
    }

    public int set(AbstractCard card, int amount) {
        // These tags also need to set the field
        switch (this) {
            case Retain:
                card.retain = amount != 0;
                if (amount < 0) {
                    card.selfRetain = true;
                }
                break;
            case Innate:
                card.isInnate = amount != 0;
                break;
            // Only set exhaustive if value is greater than 1, because setting it to 0 still exhausts the card
            case Exhaust:
                if (amount > 1) {
                    card.exhaust = false;
                    ExhaustiveField.ExhaustiveFields.exhaustive.set(card, amount);
                }
                else {
                    card.exhaust = amount == 1;
                }

                return amount;
        }
        SpireField<Integer> field = getFieldInteger();
        if (field != null) {
            field.set(card, amount);
        }
        else {
            set(card, toBoolean(amount));
        }
        return amount;
    }

    private void set(AbstractCard card, boolean value) {
        switch (this) {
            case Ethereal:
                card.isEthereal = value;
                return;
            // These tags also need to set the field
            case Ephemeral:
                card.purgeOnUse = value;
                break;
        }
        SpireField<Boolean> field = getFieldBoolean();
        if (field != null) {
            field.set(card, value);
        }
    }

    private boolean toBoolean(int amount) {
        return amount > 0;
    }

    private int toInt(boolean amount) {
        return amount ? 1 : 0;
    }

    public boolean tryProgress(AbstractCard card) {
        SpireField<Integer> field = getFieldInteger();
        if (field != null) {
            int value = field.get(card);
            if (value > 0) {
                value -= 1;
                field.set(card, value);
            }
            return value == 0;
        }
        return false;
    }
}
