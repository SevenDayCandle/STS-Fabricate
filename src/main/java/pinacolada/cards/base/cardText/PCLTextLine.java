package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;

import java.util.LinkedList;

import static pinacolada.cards.base.cardText.PCLCardText.DESC_BOX_WIDTH;

// Copied and modified from STS-AnimatorMod
public class PCLTextLine {
    protected final LinkedList<PCLTextToken> tokens = new LinkedList<>();
    protected final PCLCardText context;

    public float width = 0;
    public float additionalWidth = 0;

    public PCLTextLine(PCLCardText context) {
        this.context = context;
    }

    // Greedily add as many tokens to first line as possible, then move to the next
    public void add(PCLTextToken token) {
        float tokenWidth = token.getWidth(context);
        if ((tokens.isEmpty() && token.type != PCLTextTokenType.Whitespace) ||
                ((tokenWidth + width) < DESC_BOX_WIDTH || (token.type == PCLTextTokenType.Punctuation && token.rawText.length() == 1))) {
            tokens.add(token);
            width += tokenWidth;
            additionalWidth += token.getAdditionalWidth(context);
        }
        else {
            PCLTextLine newLine = context.addLine();

            if (token.type != PCLTextTokenType.Whitespace) {
                newLine.tokens.add(token);
                newLine.width += tokenWidth;
                newLine.additionalWidth += token.getAdditionalWidth(context);
            }

            trimEnd();
        }
    }

    public float calculateHeight(BitmapFont font) {
        if (tokens.isEmpty()) {
            return font.getCapHeight() * 0.5f;
        }
        else {
            return font.getCapHeight();
        }
    }

    public PCLTextToken getEnd() {
        return tokens.getLast();
    }

    public float getEndWidth() {
        return !tokens.isEmpty() ? tokens.getLast().getWidth(context) : 0;
    }

    public PCLTextToken getStart() {
        return !tokens.isEmpty() ? tokens.getFirst() : null;
    }

    public PCLTextToken popEnd() {
        PCLTextToken popped = tokens.pollLast();
        if (popped != null) {
            width -= popped.getWidth(context);
            additionalWidth -= popped.getAdditionalWidth(context);
        }
        return popped;
    }

    public void pushEnd(PCLTextToken token) {
        tokens.add(token);
        width += token.getWidth(context);
        additionalWidth += token.getAdditionalWidth(context);
    }

    public void pushStart(PCLTextToken token) {
        tokens.push(token);
        width += token.getWidth(context);
        additionalWidth += token.getAdditionalWidth(context);
    }

    public void refresh(PCLUseInfo info) {
        for (PCLTextToken token : tokens) {
            token.refresh(info);
        }
    }

    public void render(SpriteBatch sb) {
        final PCLCard card = context.card;

        // Additional width is only shown in battle
        context.startX = card.current_x - ((width + (CombatManager.inBattle() ? additionalWidth : 0)) * card.drawScale * 0.5f);
        context.startY = context.startY - (calculateHeight(context.font) * 1.45f);

        for (PCLTextToken token : tokens) {
            token.render(sb, context);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PCLTextToken token : tokens) {
            sb.append(token.rawText);
        }

        return sb.toString();
    }

    protected void trimEnd() {
        int size = tokens.size();
        if (size > 0 && tokens.getLast().type == PCLTextTokenType.Whitespace) {
            popEnd();
            trimEnd();
        }
    }
}