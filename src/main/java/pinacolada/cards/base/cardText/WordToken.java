package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;

// Copied and modified from STS-AnimatorMod
public class WordToken extends PCLTextToken
{
    protected EUITooltip tooltip = null;
    protected ColoredString coloredString = new ColoredString(null, null);

    protected WordToken(String text)
    {
        super(PCLTextTokenType.Text, text);
    }

    protected static boolean isValidCharacter(Character character, boolean firstCharacter)
    {
        if (character == null)
        {
            return false;
        }
        else if (firstCharacter)
        {
            return Character.isLetterOrDigit(character) || ("~<>".indexOf(character) >= 0);
        }
        else
        {
            return Character.isLetterOrDigit(character) || ("_+-".indexOf(character) >= 0);
        }
    }

    public static int tryAdd(PCLTextParser parser)
    {
        if (isValidCharacter(parser.character, true))
        {
            builder.setLength(0);
            builder.append(parser.character);

            int i = 1;
            boolean skip = false;
            while (true)
            {
                Character next = parser.nextCharacter(i);

                if (next == null)
                {
                    break;
                }
                else if (next == '|')
                {
                    if (parser.card.upgraded)
                    {
                        builder.setLength(0);
                    }
                    else
                    {
                        skip = true;
                    }
                }
                else if (isValidCharacter(next, false))
                {
                    if (!skip)
                    {
                        builder.append(next);
                    }
                }
                else
                {
                    break;
                }

                i += 1;
            }

            String word = builder.toString();

            WordToken token;
            if (word.charAt(0) == '~' && word.length() > 1)
            {
                token = new WordToken(word.substring(1));
            }
            else
            {
                token = new WordToken(word);
            }

            if (!parser.ignoreKeywords)
            {
                EUITooltip tooltip = EUITooltip.findByName(word.toLowerCase());
                if (tooltip != null)
                {
                    parser.addTooltip(tooltip);
                    if (tooltip.canHighlight)
                    {
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
    protected float getWidth(BitmapFont font, String text)
    {
        if (text == null)
        {
            return super.getWidth(font, "_.");
        }
        else
        {
            return super.getWidth(font, text);
        }
    }

    @Override
    public void render(SpriteBatch sb, PCLTextContext context)
    {
        if (coloredString.text == null)
        {
            coloredString.text = this.rawText;
        }
        super.render(sb, context, coloredString);
    }
}
