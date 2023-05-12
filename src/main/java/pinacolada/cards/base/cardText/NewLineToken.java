package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIUtils;

// Copied and modified from STS-AnimatorMod
public class NewLineToken extends PCLTextToken {
    public static final char NEWLINE = '|';
    protected static NewLineToken instance = new NewLineToken();

    private NewLineToken() {
        super(PCLTextTokenType.NewLine, EUIUtils.SPLIT_LINE);
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.character == NEWLINE) {
            parser.addToken(instance);
            return 1;
        }
        else if (parser.character == 'N' && parser.isNext(1, 'L') && parser.isWhitespace(2)) {
            parser.addToken(instance);
            return 2;
        }

        return 0;
    }

    public int getCharCount() {
        return 15;
    } // Accounts for the reduced space available on the card with newlines present

    @Override
    public float getWidth(PCLCardText context) {
        return 0;
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        // Do not render newlines
    }
}