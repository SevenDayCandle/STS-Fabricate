package pinacolada.cards.base.cardText;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
public class PCLCardText {
    private static final ColoredString cs = new ColoredString("", Settings.CREAM_COLOR);
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    private final static Color DEFAULT_COLOR = Settings.CREAM_COLOR.cpy();
    private final static HashMap<AbstractCard.CardRarity, ColoredTexture> panels = new HashMap<>();
    private final static HashMap<AbstractCard.CardRarity, ColoredTexture> panelsLarge = new HashMap<>();
    private final static float AUGMENT_OFFSET_X = -AbstractCard.RAW_W * 0.4695f;
    private final static float AUGMENT_OFFSET_Y = AbstractCard.RAW_H * 0.08f;
    private final static float BANNER_OFFSET_X = -AbstractCard.RAW_W * 0.349f;
    private final static float BANNER_OFFSET_X2 = AbstractCard.RAW_W * 0.345f;
    private final static float BANNER_OFFSET_Y = -AbstractCard.RAW_H * 0.04f;
    private final static float BANNER_OFFSET_Y2 = -AbstractCard.RAW_H * 0.06f;
    private final static float FOOTER_SIZE = 52f;
    protected final static float IMG_HEIGHT = 420f * Settings.scale;
    protected final static float DESC_OFFSET_SUB_Y = Settings.BIG_TEXT_MODE ? IMG_HEIGHT * 0.24f : IMG_HEIGHT * 0.255f;
    protected final static float DESC_BOX_WIDTH = 240f * Settings.scale;
    protected static final GlyphLayout layout = new GlyphLayout();
    protected final PCLCard card;
    private final ArrayList<PCLTextLine> lines = new ArrayList<>();
    private float badgeAlphaTargetOffset = 1f;
    private float badgeAlphaOffset = -0.2f;
    protected BitmapFont font;
    protected float scaleModifier;
    protected int lineIndex;
    protected String overrideDescription = EUIUtils.EMPTY_STRING;
    public Color color;
    public float startX;
    public float startY;

    public PCLCardText(PCLCard card) {
        this.card = card;
    }

    public static boolean isIdeographicLanguage() {
        switch (Settings.language) {
            case ZHS:
            case ZHT:
            case JPN:
            case KOR:
                return true;
        }
        return false;
    }

    protected PCLTextLine addLine() {
        PCLTextLine line = new PCLTextLine(this);

        lines.add(line);
        lineIndex += 1;

        return line;
    }

    protected void addToken(PCLTextToken token) {
        if (token.type == PCLTextTokenType.NewLine) {
            addLine();
        }
        else {
            lines.get(lineIndex).add(token);
        }
    }

    public void forceRefresh() {
        card.rawDescription = overrideDescription;
        initialize(card.rawDescription);
    }

    protected TextureCache getHPIcon() {
        return card.isPopup ? PCLCoreImages.CardIcons.hpL : PCLCoreImages.CardIcons.hp;
    }

    protected ColoredTexture getPanelByRarity() {
        HashMap<AbstractCard.CardRarity, ColoredTexture> map = card.isPopup ? panelsLarge : panels;
        ColoredTexture result = map.getOrDefault(card.rarity, null);
        if (result == null) {
            result = card.getCardAttributeBanner();
            if (result != null) {
                map.put(card.rarity, result);
            }
        }

        return result;
    }

    protected TextureCache getPriorityIcon() {
        return card.isPopup ?
                (card.timing.movesBeforePlayer() ? PCLCoreImages.CardIcons.priorityPlusL : PCLCoreImages.CardIcons.priorityMinusL)
                : (card.timing.movesBeforePlayer() ? PCLCoreImages.CardIcons.priorityPlus : PCLCoreImages.CardIcons.priorityMinus);
    }

    public void initialize(String text) {
        if (card != null) {
            card.getPointers().clear();
            text = (text != null && !text.isEmpty()) ? text : card.getEffectStrings();
            text = CardModifierManager.onCreateDescription(card, text);
            if (PGR.config.displayCardTagDescription.get()) {
                String preString = PCLCardTag.getTagTipPreString(card);
                if (!preString.isEmpty()) {
                    text = preString + EUIUtils.DOUBLE_SPLIT_LINE + text;
                }
                String postString = PCLCardTag.getTagTipPostString(card);
                if (!postString.isEmpty()) {
                    text = text + EUIUtils.DOUBLE_SPLIT_LINE + postString;
                }
            }
        }

        if (card != null) {
            this.card.tooltips.clear();
        }

        this.lines.clear();
        this.scaleModifier = 1;
        this.lineIndex = -1;

        // Obtain the initial set of tokens split into sections
        internalParser.initialize(card, text);

        // Set the predicted scale from the text, excluding newline but including expanded conditionals
        // Use different scaling for Ideographic languages (i.e. Chinese, Japanese)
        this.font = EUIFontHelper.cardDescriptionFontNormal;
        int predictedLength = EUIUtils.sumInt(internalParser.getTokens(), PCLTextToken::getCharCount);
        final float max = isIdeographicLanguage() ? 32f : 75f;
        if (predictedLength > max) {
            scaleModifier -= (0.1f * (predictedLength / max));
        }
        this.font.getData().setScale(scaleModifier);


        // Initial pass of adding tokens to lines, using greedy approach.
        // Note that we rely on the font to determine how wide a token is
        ArrayList<Integer> indexes = new ArrayList<>();
        for (List<PCLTextToken> tokens : internalParser.tokenLines) {
            addLine();
            for (PCLTextToken token : tokens) {
                addToken(token);
            }
            indexes.add(lineIndex);
        }
        this.lines.get(lineIndex).trimEnd(); // Remove possible whitespace from the last line

        // Simple rebalancing of last two lines of each section.
        // Move words from the first of them to the second until we can no longer do so without the second line getting longer than the first
        // This is a lot faster than trying to balance all lines perfectly (O(log n) vs O(n^2) if using dynamic programming)
        for (Integer index : indexes) {
            int first = index - 1;
            final PCLTextLine line2 = lines.get(index);
            if (first >= 0 && line2.width > 0) {
                final PCLTextLine line1 = lines.get(first);
                float w = line1.getEndWidth();
                while (line1.width - w > line2.width + w) {
                    // Do not add punctuation if the word preceding it would not fit on the next line
                    PCLTextToken end = line1.popEnd();

                    if (end.type == PCLTextTokenType.Punctuation) {
                        w = line1.getEndWidth() + end.getWidth(this);
                        if (line1.width - w <= line2.width + w) {
                            line1.pushEnd(end);
                            break;
                        }
                    }

                    // Whitespaces are truncated between lines so we need to add them back when moving two adjacent words
                    PCLTextToken start = line2.getStart();
                    if (!(end instanceof WhitespaceToken) && !(start instanceof WhitespaceToken || start.type == PCLTextTokenType.Punctuation)) {
                        line2.pushStart(WhitespaceToken.Default);
                    }

                    line2.pushStart(end);
                    w = line1.getEndWidth();
                }
            }
        }


        PCLRenderHelpers.resetFont(font);
    }

    public void overrideDescription(String description, boolean forceRefresh) {
        overrideDescription = description;

        if (forceRefresh) {
            forceRefresh();
        }
    }

    protected void renderAttributeBanner(SpriteBatch sb, TextureCache icon, float sign, float offsetIconX) {
        final ColoredTexture panel = getPanelByRarity();

        if (panel != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, card, panel.texture, sign * AbstractCard.RAW_W * 0.33f, BANNER_OFFSET_Y, 120f, 54f, panel.color, panel.color.a * card.transparency, 1, 0, sign < 0, false);
            if (icon != null) {
                final float icon_x = offsetIconX + (sign * (AbstractCard.RAW_W * 0.43f));
                PCLRenderHelpers.drawOnCardAuto(sb, card, icon.texture(), icon_x, BANNER_OFFSET_Y, 48, 48);
            }
        }
    }

    protected void renderAttributeBannerWithText(SpriteBatch sb, TextureCache icon, String text, String suffix, Color textColor, float offsetX, float offsetY, float offsMult, float scaleMult, float sign, float offsetIconX) {
        renderAttributeBanner(sb, icon, sign, offsetIconX);

        final float suffix_scale = scaleMult * 0.7f;
        BitmapFont largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);
        largeFont.getData().setScale(card.isPopup ? 0.5f : 1);
        layout.setText(largeFont, text);

        float text_width = offsMult * layout.width / Settings.scale;
        float suffix_width = 0;

        if (suffix != null) {
            layout.setText(largeFont, suffix);
            suffix_width = (layout.width / Settings.scale) * suffix_scale;
        }

        largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);
        float text_x = sign * AbstractCard.RAW_W * (0.32f - sign * offsetX);

        PCLRenderHelpers.writeOnCard(sb, card, largeFont, text, offsetX + (text_width * 0.5f), offsetY, textColor, true);

        if (suffix != null) {
            largeFont.getData().setScale(largeFont.getScaleX() * suffix_scale);
            PCLRenderHelpers.writeOnCard(sb, card, largeFont, suffix, offsetX + (text_width * 0.81f) + (suffix_width * 0.55f * scaleMult), offsetY, textColor, true);
        }

        PCLRenderHelpers.resetFont(largeFont);
    }

    protected void renderAttributes(SpriteBatch sb) {
        if (card.showTypeText) {
            if (card.type == PCLEnum.CardType.SUMMON) {
                renderAttributeBannerWithText(sb, getHPIcon(), card.getHPString(), "/" + card.heal, card.getHPStringColor(), BANNER_OFFSET_X, BANNER_OFFSET_Y,0.85f,0.85f, -1, 0);
                renderAttributeBannerWithText(sb, getPriorityIcon(), card.pclTarget.getShortStringForTag(), null, Settings.CREAM_COLOR, BANNER_OFFSET_X2, BANNER_OFFSET_Y2,0,0.45f, 1, -25f);
            }
            else if (card.shouldUsePCLFrame() || PGR.config.showCardTarget.get()) {
                renderAttributeBannerWithText(sb, null, card.pclTarget.getShortStringForTag(), null, Settings.CREAM_COLOR, BANNER_OFFSET_X2, BANNER_OFFSET_Y,0,0.45f, 1, 0);
            }
        }
    }

    private float renderAugment(SpriteBatch sb, PCLAugment augment, float y) {
        PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.CardUI.augmentSlot.texture(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, card.transparency, 1);
        if (augment != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, card, augment.getTextureBase(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, card.transparency, 1);
            PCLRenderHelpers.drawOnCardAuto(sb, card, augment.getTexture(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, card.transparency, 1);
        }
        else {
            PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.CardUI.augmentSlot.texture(), AUGMENT_OFFSET_X, AUGMENT_OFFSET_Y + y, 28, 28, Color.WHITE, card.transparency, 1);
        }

        return 30; // y offset
    }

    public void renderDescription(SpriteBatch sb) {
        if (card.isLocked || !card.isSeen) {
            FontHelper.menuBannerFont.getData().setScale(card.drawScale * 1.25f);
            FontHelper.renderRotatedText(sb, FontHelper.menuBannerFont, "? ? ?", card.current_x, card.current_y,
                    0, -200 * Settings.scale * card.drawScale * 0.5f, card.angle, true, EUIColors.cream(card.transparency));
            FontHelper.menuBannerFont.getData().setScale(1f);
            return;
        }

        renderLines(sb);
        renderAttributes(sb);

        if (card.drawScale > 0.3f) {
            renderIcons(sb);

            ColoredString bottom = card.bottomText;
            if (bottom != null) {
                BitmapFont font = PCLRenderHelpers.getSmallTextFont(card, bottom.text);
                PCLRenderHelpers.writeOnCard(sb, card, font, bottom.text, 0, -0.47f * AbstractCard.RAW_H, bottom.color, true);
                PCLRenderHelpers.resetFont(font);
            }
        }
    }

    private float renderFooter(SpriteBatch sb, Texture texture, float y) {
        final float offset_y = y - AbstractCard.RAW_H * 0.46f;
        final float alpha = card.transparency;

        PCLRenderHelpers.drawOnCardAuto(sb, card, PCLCoreImages.Core.controllableCardPile.texture(), AUGMENT_OFFSET_X, offset_y, FOOTER_SIZE, FOOTER_SIZE, Color.BLACK, alpha * 0.6f, 0.8f);
        PCLRenderHelpers.drawOnCardAuto(sb, card, texture, AUGMENT_OFFSET_X, offset_y, FOOTER_SIZE, FOOTER_SIZE, Color.WHITE, alpha, 0.8f);

        return 38; // y offset
    }

    protected void renderIcons(SpriteBatch sb) {
        final float alpha = updateBadgeAlpha();

        float offset_y = PCLCardTag.renderTagsOnCard(sb, card, alpha);

        // Render card footers
        offset_y = 0;
        if (card.isSoulbound()) {
            offset_y += renderFooter(sb, card.isPopup ? PCLCoreImages.CardIcons.soulboundL.texture() : PCLCoreImages.CardIcons.soulbound.texture(), offset_y);
        }
        if (card.isUnique()) {
            offset_y += renderFooter(sb, card.isPopup ? PCLCoreImages.CardIcons.uniqueL.texture() : PCLCoreImages.CardIcons.unique.texture(), offset_y);
        }
        if (card.cardData.canToggleFromPopup) {
            offset_y += renderFooter(sb, card.isPopup ? PCLCoreImages.CardIcons.multiformL.texture() : PCLCoreImages.CardIcons.multiform.texture(), offset_y);
        }

        // Render augments
        offset_y = 0;
        for (PCLAugment augment : card.augments) {
            offset_y += renderAugment(sb, augment, offset_y);
        }
    }

    public void renderLines(SpriteBatch sb) {
        font = PCLRenderHelpers.getDescriptionFont(card, scaleModifier);

        float height = 0;
        for (PCLTextLine line : lines) {
            height += line.calculateHeight(font);
        }

        this.startY = (card.current_y - IMG_HEIGHT * card.drawScale * 0.5f + DESC_OFFSET_SUB_Y * card.drawScale) + (height * 0.775f + font.getCapHeight() * 0.375f) - 6f;
        this.startX = 0;
        this.color = EUIColors.copy(DEFAULT_COLOR, card.transparency);

        for (lineIndex = 0; lineIndex < lines.size(); lineIndex += 1) {
            lines.get(lineIndex).render(sb);
        }

        PCLRenderHelpers.resetFont(font);
    }

    protected float updateBadgeAlpha() {
        if (card.isPreview) {
            return card.transparency - badgeAlphaOffset;
        }

        if (card.cardsToPreview instanceof PCLCard) {
            ((PCLCard) card.cardsToPreview).cardText.badgeAlphaOffset = badgeAlphaOffset;
        }

        if (card.canRenderTip() && !card.isPopup) {
            if (badgeAlphaOffset < badgeAlphaTargetOffset) {
                badgeAlphaOffset += EUI.delta(0.33f);
                if (badgeAlphaOffset > badgeAlphaTargetOffset) {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = -0.9f;
                }
            }
            else {
                badgeAlphaOffset -= EUI.delta(0.5f);
                if (badgeAlphaOffset < badgeAlphaTargetOffset) {
                    badgeAlphaOffset = badgeAlphaTargetOffset;
                    badgeAlphaTargetOffset = 0.5f;
                }
            }

            if (card.transparency >= 1 && badgeAlphaOffset > 0) {
                return card.transparency - badgeAlphaOffset;
            }
        }
        else {
            badgeAlphaOffset = -0.2f;
            badgeAlphaTargetOffset = 0.5f;
        }

        return card.transparency;
    }
}
