package pinacolada.cards.base.cardText;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.utilities.ColoredString;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Copied and modified from STS-AnimatorMod
// TODO Move generic logic into other classes
public class PCLCardText
{
    protected final static Color DEFAULT_COLOR = Settings.CREAM_COLOR.cpy();
    protected final static HashMap<AbstractCard.CardRarity, ColoredTexture> panels = new HashMap<>();
    protected final static HashMap<AbstractCard.CardRarity, ColoredTexture> panelsLarge = new HashMap<>();
    protected final static float DESC_OFFSET_X = (AbstractCard.IMG_WIDTH * 0.5f);
    protected final static float DESC_OFFSET_Y = (AbstractCard.IMG_HEIGHT * 0.10f);
    protected final static float IMG_HEIGHT = 420f * Settings.scale;
    protected final static float DESC_OFFSET_SUB_Y = Settings.BIG_TEXT_MODE ? IMG_HEIGHT * 0.24f : IMG_HEIGHT * 0.255f;
    protected final static float IMG_WIDTH = 300f * Settings.scale;
    protected final static float CN_DESC_BOX_WIDTH = IMG_WIDTH * 0.72f;
    protected final static float DESC_BOX_WIDTH = Settings.BIG_TEXT_MODE ? IMG_WIDTH * 0.95f : IMG_WIDTH * 0.79f;
    protected static final GlyphLayout layout = new GlyphLayout();
    private static final ColoredString cs = new ColoredString("", Settings.CREAM_COLOR);
    private static final PCLCoreImages.Badges BADGES = PGR.core.images.badges;
    private static final PCLCoreImages.CardIcons ICONS = PGR.core.images.icons;
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    public final ArrayList<PCLTextLine> lines = new ArrayList<>();
    public Color color;
    public float lineWidth = DESC_BOX_WIDTH;
    public float startX;
    public float startY;
    protected BitmapFont font;
    protected float scaleModifier;
    protected int lineIndex;

    protected final PCLCard card;
    protected String overrideDescription;
    private float badgeAlphaTargetOffset = 1f;
    private float badgeAlphaOffset = -0.2f;

    public PCLCardText(PCLCard card)
    {
        this.card = card;
    }

    public void forceRefresh()
    {
        card.rawDescription = overrideDescription;
        initialize(card.rawDescription);
    }

    protected TextureCache getHPIcon()
    {
        return card.isPopup ? ICONS.hpL : ICONS.hp;
    }

    protected ColoredTexture getPanelByRarity()
    {
        HashMap<AbstractCard.CardRarity, ColoredTexture> map = card.isPopup ? panelsLarge : panels;
        ColoredTexture result = map.getOrDefault(card.rarity, null);
        if (result == null)
        {
            result = card.getCardAttributeBanner();
            if (result != null)
            {
                map.put(card.rarity, result);
            }

        }

        return result;
    }

    public void overrideDescription(String description, boolean forceRefresh)
    {
        overrideDescription = description;

        if (forceRefresh)
        {
            forceRefresh();
        }
    }

    protected void renderAttributes(SpriteBatch sb)
    {
        if (card.type == PCLEnum.CardType.SUMMON)
        {
            renderAttribute(sb, getHPIcon(), card.getSecondaryValueString(), "/" + card.baseHeal, null, 0.9f, true);
            // TODO add priority attribute indicator
            //renderAttribute(sb, getDamageIcon(), card.getDamageString(), card.hitCount > 1 ? ("x" + card.hitCount) : null, card.pclTarget != null ? card.pclTarget.getTag() : null, 1f, false);
        }
    }

    protected void renderAttribute(SpriteBatch sb, TextureCache icon, ColoredString text, String suffix, String iconTag, float scaleMult, boolean leftAlign)
    {
        final float suffix_scale = scaleMult * 0.7f;
        final float cw = AbstractCard.RAW_W;
        final float ch = AbstractCard.RAW_H;
        final float b_w = 126f;
        final float b_h = 85f;
        final float y = -ch * 0.04f;
        final ColoredTexture panel = getPanelByRarity();

        BitmapFont largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);
        largeFont.getData().setScale(card.isPopup ? 0.5f : 1);
        layout.setText(largeFont, text.text);

        float text_width = scaleMult * layout.width / Settings.scale;
        float suffix_width = 0;

        if (suffix != null)
        {
            layout.setText(largeFont, suffix);
            suffix_width = (layout.width / Settings.scale) * suffix_scale;
        }

        largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);

        final float sign = leftAlign ? -1 : +1;
        final float icon_x = sign * (cw * 0.45f);
        float text_x = sign * cw * ((suffix != null || text.text.length() > 2) ? (0.35f - sign * 0.029f) : 0.35f);

        if (panel != null)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, panel.texture, new Vector2(sign * cw * 0.33f, y), b_w, b_h, panel.color, panel.color.a * card.transparency, 1, 0, leftAlign, false);
        }

        PCLRenderHelpers.drawOnCardAuto(sb, card, icon.texture(), icon_x, y, 48, 48);
        PCLRenderHelpers.writeOnCard(sb, card, largeFont, text.text, text_x + (text_width * 0.5f), y, text.color, true);

        if (suffix != null)
        {
            largeFont.getData().setScale(largeFont.getScaleX() * suffix_scale);
            PCLRenderHelpers.writeOnCard(sb, card, largeFont, suffix, text_x + (text_width * 0.81f) + (suffix_width * 0.55f * scaleMult), y, text.color, true);
        }

        if (iconTag != null)
        {
            BitmapFont smallFont = PCLRenderHelpers.getSmallAttributeFont(card, scaleMult);
            PCLRenderHelpers.writeOnCard(sb, card, smallFont, iconTag, icon_x, y - 12, Settings.CREAM_COLOR, true);
            PCLRenderHelpers.resetFont(smallFont);
        }

        PCLRenderHelpers.resetFont(largeFont);
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
        }

        return 30; // y offset
    }

    protected void renderIcons(SpriteBatch sb)
    {
        final float alpha = updateBadgeAlpha();

        float offset_y = PCLCardTag.renderTagsOnCard(sb, card, alpha);

        // Render card footers
        offset_y = 0;
        if (card.isUnique())
        {
            offset_y += renderFooter(sb, card.isPopup ? ICONS.uniqueL.texture() : ICONS.unique.texture(), offset_y);
        }
        if (card.cardData.canToggleFromPopup && (card.upgraded || card.cardData.unUpgradedCanToggleForms))
        {
            offset_y += renderFooter(sb, card.isPopup ? ICONS.multiformL.texture() : ICONS.multiform.texture(), offset_y);
        }

        // Render augments
        offset_y = 0;
        for (PCLAugment augment : card.augments)
        {
            offset_y += renderAugment(sb, augment, offset_y);
        }
    }

    public void renderDescription(SpriteBatch sb)
    {
        if (card.isLocked || !card.isSeen)
        {
            FontHelper.menuBannerFont.getData().setScale(card.drawScale * 1.25f);
            FontHelper.renderRotatedText(sb, FontHelper.menuBannerFont, "? ? ?", card.current_x, card.current_y,
                    0, -200 * Settings.scale * card.drawScale * 0.5f, card.angle, true, EUIColors.cream(card.transparency));
            FontHelper.menuBannerFont.getData().setScale(1f);
            return;
        }

        renderLines(sb);
        renderAttributes(sb);

        if (card.drawScale > 0.3f)
        {
            renderIcons(sb);

            ColoredString bottom = card.bottomText;
            if (bottom != null)
            {
                BitmapFont font = PCLRenderHelpers.getSmallTextFont(card, bottom.text);
                PCLRenderHelpers.writeOnCard(sb, card, font, bottom.text, 0, -0.47f * AbstractCard.RAW_H, bottom.color, true);
                PCLRenderHelpers.resetFont(font);
            }
        }
    }

    private float renderFooter(SpriteBatch sb, Texture texture, float y)
    {
        final float offset_x = -AbstractCard.RAW_W * 0.4595f;
        final float offset_y = y - AbstractCard.RAW_H * 0.46f;
        final float alpha = card.transparency;

        PCLRenderHelpers.drawOnCardAuto(sb, card, PGR.core.images.core.controllableCardPile.texture(), new Vector2(offset_x, offset_y), 40, 40, Color.BLACK, alpha * 0.6f, 0.8f);
        PCLRenderHelpers.drawOnCardAuto(sb, card, texture, new Vector2(offset_x, offset_y), 40, 40, Color.WHITE, alpha, 0.8f);

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

    protected static boolean isIdeographicLanguage()
    {
        switch (Settings.language)
        {
            case ZHS:
            case ZHT:
            case JPN:
            case KOR:
                return true;
        }
        return false;
    }

    protected PCLTextLine addLine()
    {
        PCLTextLine line = new PCLTextLine(this);

        lines.add(line);
        lineIndex += 1;

        return line;
    }

    protected void addToken(PCLTextToken token)
    {
        if (token.type == PCLTextTokenType.NewLine)
        {
            addLine();
        }
        else
        {
            lines.get(lineIndex).add(token);
        }
    }

    public void initialize(String text)
    {
        if (card != null)
        {
            card.getPointers().clear();
            String efStrings = card.getEffectStrings();
            text = (text != null && !text.isEmpty()) ? text : efStrings;
            if (PGR.core.config.displayCardTagDescription.get())
            {
                text = text + EUIUtils.DOUBLE_SPLIT_LINE + card.getTagTipString();
            }
            text = CardModifierManager.onCreateDescription(card, text);
        }

        if (card != null)
        {
            this.card.tooltips.clear();
        }

        this.lines.clear();
        this.scaleModifier = 1;
        this.lineIndex = -1;

        // Obtain the initial set of tokens split into sections
        internalParser.initialize(card, text);

        // Set the predicted scale from the text, excluding newline but including expanded conditionals
        // Use different scaling for Ideographic languages (i.e. Chinese, Japanese)
        this.font = EUIFontHelper.carddescriptionfontNormal;
        int predictedLength = EUIUtils.sumInt(internalParser.getTokens(), PCLTextToken::getCharCount);
        final float max = isIdeographicLanguage() ? 32f : 75f;
        if (predictedLength > max)
        {
            scaleModifier -= (0.1f * (predictedLength / max));
        }
        this.font.getData().setScale(scaleModifier);


        // Initial pass of adding tokens to lines, using greedy approach.
        // Note that we rely on the font to determine how wide a token is
        ArrayList<Integer> indexes = new ArrayList<>();
        for (List<PCLTextToken> tokens : internalParser.tokenLines)
        {
            addLine();
            for (PCLTextToken token : tokens)
            {
                addToken(token);
            }
            indexes.add(lineIndex);
        }
        this.lines.get(lineIndex).trimEnd(); // Remove possible whitespace from the last line

        // Simple rebalancing of last two lines of each section.
        // Move words from the first of them to the second until we can no longer do so without the second line getting longer than the first
        for (Integer index : indexes)
        {
            int first = index - 1;
            final PCLTextLine line2 = lines.get(index);
            if (first >= 0 && line2.width > 0)
            {
                final PCLTextLine line1 = lines.get(first);
                float w = line1.getEndWidth();
                while (line1.width - w > line2.width + w)
                {
                    // Do not add punctuation if the word preceding it would not fit on the next line
                    PCLTextToken end = line1.popEnd();

                    if (end instanceof PunctuationToken)
                    {
                        w = line1.getEndWidth() + end.getWidth(this);
                        if (line1.width - w <= line2.width + w)
                        {
                            line1.pushEnd(end);
                            break;
                        }
                    }

                    // Whitespaces are truncated between lines so we need to add them back when moving two adjacent words
                    PCLTextToken start = line2.getStart();
                    if (!(end instanceof WhitespaceToken) && !(start instanceof WhitespaceToken || start instanceof PunctuationToken))
                    {
                        line2.pushStart(WhitespaceToken.Default);
                    }

                    line2.pushStart(end);
                    w = line1.getEndWidth();
                }
            }
        }


        PCLRenderHelpers.resetFont(font);
    }

    public void renderLines(SpriteBatch sb)
    {
        font = PCLRenderHelpers.getDescriptionFont(card, scaleModifier);

        float height = 0;
        for (PCLTextLine line : lines)
        {
            height += line.calculateHeight(font);
        }

        this.startY = (card.current_y - IMG_HEIGHT * card.drawScale * 0.5f + DESC_OFFSET_SUB_Y * card.drawScale) + (height * 0.775f + font.getCapHeight() * 0.375f) - 6f;
        this.startX = 0;
        this.color = EUIColors.copy(DEFAULT_COLOR, card.transparency);

        for (lineIndex = 0; lineIndex < lines.size(); lineIndex += 1)
        {
            lines.get(lineIndex).render(sb);
        }

        PCLRenderHelpers.resetFont(font);
    }
}
