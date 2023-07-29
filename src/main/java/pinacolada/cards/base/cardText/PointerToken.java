package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCard;
import pinacolada.skills.PSkill;

import java.util.ArrayList;

public class PointerToken extends PCLTextToken {
    public static final char TOKEN = '¦';
    public static final String DUMMY = "_.";
    public static final ArrayList<Character> VALID_TOKENS = new ArrayList<>();

    static {
        VALID_TOKENS.add(PSkill.EFFECT_CHAR);
        VALID_TOKENS.add(PSkill.XVALUE_CHAR);
        VALID_TOKENS.add(PSkill.EXTRA_CHAR);
    }

    protected final char variableID;
    protected final PSkill<?> move;
    private ColoredString coloredString;

    private PointerToken(char variableID, PSkill<?> move) {
        super(PCLTextTokenType.Variable, null);

        this.variableID = variableID;
        this.move = move;
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.isNext(3, TOKEN)) {
            final PointerToken token = tryCreateToken(parser.card, parser.nextCharacter(1), parser.nextCharacter(2));
            if (token != null) {
                parser.addToken(token);
            }
            else {
                EUIUtils.logWarning(PointerToken.class, "Invalid pointer: " + parser.nextCharacter(1) + parser.nextCharacter(2) + ", Original text: " + parser.text);
            }

            return 4;
        }

        return 0;
    }

    private static PointerToken tryCreateToken(PCLCard card, Character c, Character i) {
        PSkill<?> move = card != null ? card.getEffectAt(i) : null;
        return VALID_TOKENS.contains(c) && move != null ? new PointerToken(c, move) : null;
    }

    @Override
    public float getAdditionalWidth(PCLCardText context) {
        if (move != null && variableID == PSkill.XVALUE_CHAR) {
            return getWidth(context.font, rawText);
        }
        return super.getAdditionalWidth(context);
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        return super.getWidth(font, coloredString != null ? coloredString.text : DUMMY);
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        if (coloredString == null || EUI.elapsed25()) {
            coloredString = move.getColoredAttributeString(variableID);
        }

        super.render(sb, context, coloredString);
    }
}