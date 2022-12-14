package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.attributes.PCLAttribute;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCardText
{
    private static final PCLCoreImages.Badges BADGES = PGR.core.images.badges;
    private static final PCLCoreImages.CardIcons ICONS = PGR.core.images.icons;
    private static final ColoredString cs = new ColoredString("", Settings.CREAM_COLOR);

    private static AbstractPlayer player;
    protected final PCLTextContext context = new PCLTextContext();
    protected final PCLCard card;
    protected String overrideDescription;
    private float badgeAlphaTargetOffset = 1f;
    private float badgeAlphaOffset = -0.2f;

    public PCLCardText(PCLCard card)
    {
        this.card = card;
    }

    public static CardStrings processCardStrings(CardStrings strings)
    {
        final String placeholder = "<DESCRIPTION>";
        if (StringUtils.isNotEmpty(strings.UPGRADE_DESCRIPTION))
        {
            strings.UPGRADE_DESCRIPTION = strings.UPGRADE_DESCRIPTION.replace("<DESCRIPTION>", strings.DESCRIPTION);
        }

        return strings;
    }

    public void forceRefresh()
    {
        forceRefresh(false);
    }

    public void forceRefresh(boolean ignoreEffects)
    {
        if (overrideDescription != null)
        {
            card.rawDescription = overrideDescription;
        }
        else
        {
            card.rawDescription = card.getRawDescription();
        }

        context.initialize(card, card.rawDescription, ignoreEffects);
    }

    public void overrideDescription(String description, boolean forceRefresh)
    {
        overrideDescription = description;

        if (forceRefresh)
        {
            forceRefresh(true);
        }
    }

    protected void renderAttributes(SpriteBatch sb)
    {
        PCLAttribute.leftAlign = true;
        PCLAttribute temp;
        if ((temp = card.getPrimaryInfo()) != null)
        {
            temp.render(sb, card);
            PCLAttribute.leftAlign = false;
        }
        if ((temp = card.getSecondaryInfo()) != null)
        {
            temp.render(sb, card);
            PCLAttribute.leftAlign = false;
        }
    }

    private float renderAugment(SpriteBatch sb, PCLAugment augment, float y)
    {
        final float offset_x = -AbstractCard.RAW_W * 0.4695f;
        final float offset_y = AbstractCard.RAW_H * 0.08f;//+0.28f;
        final float alpha = card.transparency;

        PCLRenderHelpers.drawOnCardAuto(sb, card, PGR.core.images.augments.augment.texture(), new Vector2(offset_x, offset_y + y), 28, 28, Color.WHITE, alpha, 1);
        if (augment != null)
        {
            PCLRenderHelpers.drawColorized(sb, augment.getColor(), s ->
                    PCLRenderHelpers.drawOnCardAuto(s, card, augment.getTexture(), new Vector2(offset_x, offset_y + y), 28, 28, augment.getColor(), alpha, 1));
            //final BitmapFont font = EUIFontHelper.CardIconFont_Small;
            //font.getData().setScale(0.9f * card.drawScale);
            //PCLRenderHelpers.WriteOnCard(sb, card, font, scaling.text, offset_x, offset_y + y - 6, scaling.color, true);
            //PCLRenderHelpers.ResetFont(font);
        }

        return 30; // y offset
    }

    private float renderBadge(SpriteBatch sb, PCLCardTag tag, float offset_y, float alpha)
    {
        Vector2 offset = new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y);

        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBadge.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, tag.color, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, tag.getTip().icon.getTexture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        PCLRenderHelpers.drawOnCardAuto(sb, card, EUIRM.images.baseBorder.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);

        int tagCount = tag.getInt(card);
        if (tagCount < 0)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PGR.core.images.badges.baseInfinite.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        }
        else if (tagCount > 1)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PGR.core.images.badges.baseMulti.texture(), new Vector2(AbstractCard.RAW_W * 0.45f, AbstractCard.RAW_H * 0.45f + offset_y), 64, 64, Color.WHITE, alpha, 1);
        }

        return 38;
    }

    protected void renderBadges(SpriteBatch sb, boolean inHand)
    {
        final float alpha = updateBadgeAlpha();

        int offset_y = 0;
        for (PCLCardTag tag : PCLCardTag.getAll())
        {
            if (tag.has(card) && tag.getTip().icon != null)
            {
                offset_y -= renderBadge(sb, tag, offset_y, alpha);
            }
        }

        // Render card footers
        offset_y = 0;
        if (card.isUnique())
        {
            offset_y += renderFooter(sb, card.isPopup ? ICONS.uniqueL.texture() : ICONS.unique.texture(), offset_y, Color.WHITE, null);
        }
        if (card.cardData.canToggleFromPopup && (card.upgraded || card.cardData.unUpgradedCanToggleForms))
        {
            offset_y += renderFooter(sb, card.isPopup ? ICONS.multiformL.texture() : ICONS.multiform.texture(), offset_y, Color.WHITE, null);
        }
/*        if (card.hasTag(PGR.Enums.CardTags.EXPANDED))
        {
            offset_y += RenderFooter(sb, card.isPopup ? ICONS.BranchUpgrade_L.Texture() : ICONS.BranchUpgrade.Texture(), offset_y, Color.WHITE,
                    card.cardData.MaxForms > 2 && card.auxiliaryData.form != 0 ? String.valueOf(card.auxiliaryData.form) : null);
        }*/

        // Render augments
        offset_y = 0;
        for (PCLAugment augment : card.augments)
        {
            offset_y += renderAugment(sb, augment, offset_y);
        }
    }

    public void renderDescription(SpriteBatch sb)
    {
        player = PCLCard.player;

        if (card.isLocked || !card.isSeen)
        {
            FontHelper.menuBannerFont.getData().setScale(card.drawScale * 1.25f);
            FontHelper.renderRotatedText(sb, FontHelper.menuBannerFont, "? ? ?", card.current_x, card.current_y,
                    0, -200 * Settings.scale * card.drawScale * 0.5f, card.angle, true, EUIColors.cream(card.transparency));
            FontHelper.menuBannerFont.getData().setScale(1f);
            return;
        }

        context.render(sb);

        renderAttributes(sb);

        final boolean inHand = player != null && player.hand.contains(card);
        if (card.drawScale > 0.3f)
        {
            renderBadges(sb, inHand);

            ColoredString header = card.getHeaderText();
            if (header != null)
            {
                BitmapFont font = PCLRenderHelpers.getSmallTextFont(card, header.text);
                PCLRenderHelpers.writeOnCard(sb, card, font, header.text, 0, AbstractCard.RAW_H * 0.48f, header.color, true);
                PCLRenderHelpers.resetFont(font);
            }

            ColoredString bottom = card.getBottomText();
            if (bottom != null)
            {
                BitmapFont font = PCLRenderHelpers.getSmallTextFont(card, bottom.text);
                PCLRenderHelpers.writeOnCard(sb, card, font, bottom.text, 0, -0.47f * AbstractCard.RAW_H, bottom.color, true);
                PCLRenderHelpers.resetFont(font);
            }
        }
    }

    private float renderFooter(SpriteBatch sb, Texture texture, float y, Color iconColor, String text)
    {
        final float offset_x = -AbstractCard.RAW_W * 0.4595f;
        final float offset_y = y - AbstractCard.RAW_H * 0.46f;
        final float alpha = card.transparency;

        PCLRenderHelpers.drawOnCardAuto(sb, card, PGR.core.images.core.controllableCardPile.texture(), new Vector2(offset_x, offset_y), 40, 40, Color.BLACK, alpha * 0.6f, 0.8f);
        PCLRenderHelpers.drawOnCardAuto(sb, card, texture, new Vector2(offset_x, offset_y), 40, 40, iconColor, alpha, 0.8f);

        if (text != null)
        {
            final BitmapFont font = EUIFontHelper.cardiconfontLarge;

            font.getData().setScale(0.5f * card.drawScale);
            PCLRenderHelpers.writeOnCard(sb, card, font, text, offset_x, offset_y, Settings.CREAM_COLOR, true);
            PCLRenderHelpers.resetFont(font);
        }

        return 38; // y offset
    }

    protected float updateBadgeAlpha()
    {
        if (card.isPreview)
        {
            return card.transparency - badgeAlphaOffset;
        }

        if (card.cardsToPreview instanceof PCLCard)
        {
            ((PCLCard) card.cardsToPreview).cardText.badgeAlphaOffset = badgeAlphaOffset;
        }

        if (card.renderTip && !card.isPopup)
        {
            if (badgeAlphaOffset < badgeAlphaTargetOffset)
            {
                badgeAlphaOffset += EUI.delta(0.33f);
                if (badgeAlphaOffset > badgeAlphaTargetOffset)
                {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = -0.9f;
                }
            }
            else
            {
                badgeAlphaOffset -= EUI.delta(0.5f);
                if (badgeAlphaOffset < badgeAlphaTargetOffset)
                {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = 0.5f;
                }
            }

            if (card.transparency >= 1 && badgeAlphaOffset > 0)
            {
                return card.transparency - badgeAlphaOffset;
            }
        }
        else
        {
            badgeAlphaOffset = -0.2f;
            badgeAlphaTargetOffset = 0.5f;
        }

        return card.transparency;
    }
}
