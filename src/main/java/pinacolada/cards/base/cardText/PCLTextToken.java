package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.utilities.ColoredString;
import pinacolada.dungeon.PCLUseInfo;

// Copied and modified from STS-AnimatorMod
public abstract class PCLTextToken {
    protected static final GlyphLayout layout = new GlyphLayout();
    protected static final StringBuilder builder = new StringBuilder();
    protected static final StringBuilder tempBuilder = new StringBuilder();
    protected static final Color renderColor = Color.WHITE.cpy();

    public final PCLTextTokenType type;
    public String rawText;

    protected PCLTextToken(PCLTextTokenType type, String text) {
        this.type = type;
        this.rawText = text;
    }

    public float getAdditionalWidth(PCLCardText context) {
        return 0;
    }

    public int getCharCount() {
        return rawText != null ? rawText.length() : 1;
    }

    public float getWidth(PCLCardText context) {
        return getWidth(context.font, rawText);
    }

    protected float getWidth(BitmapFont font, String text) {
        layout.setText(font, text);
        return layout.width;
    }

    public void refresh(PCLUseInfo info) {
    }

    public void render(SpriteBatch sb, PCLCardText context) {
        render(sb, context, rawText, context.color);
    }

    protected void render(SpriteBatch sb, PCLCardText context, String text, Color color) {
        float width = getWidth(context.font, text);

        renderColor.r = color.r;
        renderColor.g = color.g;
        renderColor.b = color.b;
        renderColor.a = color.a * context.card.transparency;
        FontHelper.renderRotatedText(sb, context.font, text, context.startX + width / 2f, context.startY, 0, 0, context.card.angle, true, renderColor);

        context.startX += width;
    }

    protected void render(SpriteBatch sb, PCLCardText context, Color color) {
        render(sb, context, rawText, color);
    }

    protected void render(SpriteBatch sb, PCLCardText context, ColoredString string) {
        render(sb, context, string.text, string.color != null ? string.color : context.color);
    }
}