package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PSkill;

import static pinacolada.skills.PSkill.CAPITAL_CHAR;

public class ConditionToken extends PCLTextToken {
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    public static final char TOKEN = '║';
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
        if (parser.isNext(3, TOKEN)) {
            PSkill<?> move = parser.card != null ? parser.card.getEffectAt(parser.nextCharacter(2)) : null;
            boolean capital = parser.nextCharacter(1) == CAPITAL_CHAR;

            if (move != null) {
                String subText = move.getCapitalSubText(PCLCardTarget.Self, null, capital);
                if (subText != null) {
                    internalParser.initialize(parser.card, subText);
                    for (PCLTextToken token : internalParser.getTokens()) {
                        // When expanding the condition text, intercept word tokens
                        if (token instanceof WordToken) {
                            ColoredString w = ((WordToken) token).coloredString;
                            ConditionToken ct = new ConditionToken(move, token.rawText, w != null ? w.color : Settings.CREAM_COLOR);
                            EUIKeywordTooltip tooltip = (((WordToken) token).tooltip);
                            if (tooltip != null) {
                                parser.addTooltip(tooltip);
                            }
                            parser.addToken(ct);
                        }
                        else {
                            if (token instanceof SymbolToken) {
                                EUIKeywordTooltip tooltip = (((SymbolToken) token).tooltip);
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

            return 4;
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