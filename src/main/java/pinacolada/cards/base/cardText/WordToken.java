package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;

// Copied and modified from STS-AnimatorMod
public class WordToken extends PCLTextToken {
    protected EUITooltip tooltip = null;
    protected ColoredString coloredString = new ColoredString(null, null);
    protected int extraLength;

    protected WordToken(String text, int extraLength) {
        super(PCLTextTokenType.Text, text);
        this.extraLength = extraLength;
    }

    protected static boolean isLonger(Character character) {
        return character == '%';
    }

    protected static boolean isValidCharacter(Character character, boolean firstCharacter) {
        if (character == null) {
            return false;
        }

        switch (character) {
            case '~':
            case '<':
            case '>':
                return firstCharacter;
            case '_':
            case '%':
            case '+':
            case '-':
                return true;
            default:
                return Character.isLetterOrDigit(character);
        }
    }

    public static int tryAdd(PCLTextParser parser) {
        if (isValidCharacter(parser.character, true)) {
            int additionalWidth = 0;
            builder.setLength(0);
            builder.append(parser.character);

            int i = 1;
            while (true) {
                Character next = parser.nextCharacter(i);

                if (next == null) {
                    break;
                }
                else if (isValidCharacter(next, false)) {
                    builder.append(next);
                    if (isLonger(next)) {
                        additionalWidth += 1;
                    }
                }
                else {
                    break;
                }

                i += 1;
            }

            String word = builder.toString();

            WordToken token;
            if (word.charAt(0) == '~' && word.length() > 1) {
                token = new WordToken(word.substring(1), additionalWidth);
            }
            else {
                token = new WordToken(word, additionalWidth);
            }

            if (!parser.ignoreKeywords) {
                EUITooltip tooltip = EUITooltip.findByName(word.toLowerCase());
                if (tooltip != null) {
                    parser.addTooltip(tooltip);
                    if (tooltip.canHighlight) {
                        token.coloredString.setColor(Settings.GOLD_COLOR);
                    }
                    token.tooltip = tooltip;
                }
            }

            parser.addToken(token);

            return i;
        }

        return 0;
    }

    @Override
    public float getAdditionalWidth(PCLCardText context) {
        return super.getAdditionalWidth(context) + extraLength;
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        if (text == null) {
            return super.getWidth(font, "_.");
        }
        else {
            return super.getWidth(font, text);
        }
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        if (coloredString.text == null) {
            coloredString.text = this.rawText;
        }
        super.render(sb, context, coloredString);
    }
}
