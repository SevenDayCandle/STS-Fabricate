package pinacolada.cards.base.cardText;

import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public class PCLCardText {
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    private final static Color DEFAULT_COLOR = Settings.CREAM_COLOR.cpy();

    protected final static float IMG_HEIGHT = 420f * Settings.scale;
    protected final static float DESC_OFFSET_SUB_Y = Settings.BIG_TEXT_MODE ? IMG_HEIGHT * 0.24f : IMG_HEIGHT * 0.255f;
    protected final static float DESC_BOX_WIDTH = 240f * Settings.scale;
    protected final PCLCard card;
    private final ArrayList<PCLTextLine> lines = new ArrayList<>();
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
        for (PCLTextLine line : lines) {
            line.forceRefresh();
        }
    }

    public void forceReinitialize() {
        card.rawDescription = overrideDescription;
        initialize(card.rawDescription);
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

    public void overrideDescription(String description) {
        overrideDescription = description;
    }

    public void refresh(PCLUseInfo info) {
        for (PCLTextLine line : lines) {
            line.refresh(info);
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
}
