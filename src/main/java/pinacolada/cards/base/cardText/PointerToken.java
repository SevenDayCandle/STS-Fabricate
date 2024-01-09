package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;

public class PointerToken extends PCLTextToken {
    public static final char TOKEN = '¦';
    public static final String DUMMY = "_.";
    protected final char variableID;
    protected final PSkill<?> move;
    private final ColoredString coloredString;
    private int cachedValue = PSkill.DEFAULT_MAX;

    private PointerToken(char variableID, PSkill<?> move) {
        super(PCLTextTokenType.Variable, null);

        this.variableID = variableID;
        this.move = move;
        this.coloredString = new ColoredString(EUIUtils.EMPTY_STRING, Settings.CREAM_COLOR);
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
        return move != null ? new PointerToken(c, move) : null;
    }

    // X value will not show text unless in combat, but we need to make sure that it doesn't go over the line
    @Override
    public float getAdditionalWidth(PCLCardText context) {
        if (variableID == PSkill.XVALUE_CHAR && StringUtils.isEmpty(coloredString.text)) {
            return super.getWidth(context.font, DUMMY);
        }
        return super.getAdditionalWidth(context);
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        return super.getWidth(font, coloredString.text);
    }

    @Override
    public void refresh(PCLUseInfo info) {
        int newAmount = move.refreshAmount(info, move.amount, false);
        if (cachedValue != newAmount) {
            cachedValue = newAmount;
            coloredString.setText(move.getAttributeString(variableID, cachedValue))
                    .setColor(move.getAttributeColor(variableID, cachedValue));
        }
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        super.render(sb, context, coloredString);
    }
}