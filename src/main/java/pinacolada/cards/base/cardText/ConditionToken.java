package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.skills.PSkill;

public class ConditionToken extends PCLTextToken {
    public static final char CONDITION_TOKEN = '║';
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    private final PSkill<?> move;
    private final ColoredString coloredString;
    private final Color originalColor;

    private ConditionToken(PSkill<?> move, String text) {
        this(move, text, Settings.CREAM_COLOR);
    }

    private ConditionToken(PSkill<?> move, String text, Color originalColor) {
        super(PCLTextTokenType.Text, text);

        this.coloredString = new ColoredString(text);
        this.move = move;
        this.originalColor = originalColor != null ? originalColor : Settings.CREAM_COLOR;
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.character == CONDITION_TOKEN && parser.compareNext(2, CONDITION_TOKEN)) {
            PSkill<?> move = parser.card != null ? parser.card.getEffectAt(parser.nextCharacter(1)) : null;

            if (move != null) {
                String subText = move.getCapitalSubText(true);
                if (subText != null) {
                    internalParser.initialize(parser.card, subText);
                    for (PCLTextToken token : internalParser.getTokens()) {
                        // When expanding the condition text, intercept word tokens
                        if (token instanceof WordToken) {
                            ColoredString w = ((WordToken) token).coloredString;
                            ConditionToken ct = new ConditionToken(move, token.rawText, w != null ? w.color : Settings.CREAM_COLOR);
                            EUITooltip tooltip = (((WordToken) token).tooltip);
                            if (tooltip != null) {
                                parser.addTooltip(tooltip);
                            }
                            parser.addToken(ct);
                        }
                        else {
                            if (token instanceof SymbolToken) {
                                EUITooltip tooltip = (((SymbolToken) token).tooltip);
                                if (tooltip != null) {
                                    parser.addTooltip(tooltip);
                                }
                            }
                            parser.addToken(token);
                        }
                    }
                }
            }
            else {
                EUIUtils.logWarning(PointerToken.class, "Invalid pointer: " + parser.text);
            }

            return 3;
        }

        return 0;
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        if (EUI.elapsed25() && move != null) {
            Color c = move.getConditionColor();
            coloredString.setColor(c != null ? c : originalColor);
        }
        super.render(sb, context, coloredString);
    }
}