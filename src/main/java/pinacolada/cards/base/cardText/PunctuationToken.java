package pinacolada.cards.base.cardText;

import java.util.HashMap;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class PunctuationToken extends PCLTextToken
{
    private static final Map<String, PunctuationToken> tokenCache = new HashMap<>();

    protected PunctuationToken(String text)
    {
        super(PCLTextTokenType.Punctuation, text);
    }

    protected static boolean isValidCharacter(Character character, boolean firstCharacter)
    {
        if (character == null)
        {
            return false;
        }
        else if (firstCharacter)
        {
            return !Character.isLetterOrDigit(character) && !Character.isWhitespace(character) && "<>".indexOf(character) == -1;
        }
        else
        {
            return ("{[!#<_*@$>]}¦║+-%".indexOf(character) == -1) && !Character.isLetterOrDigit(character) && !Character.isWhitespace(character);
        }
    }

    public static int tryAdd(PCLTextParser parser)
    {
        if (isValidCharacter(parser.character, true))
        {
            builder.setLength(0);
            builder.append(parser.character);

            int i = 1;
            while (true)
            {
                Character next = parser.nextCharacter(i);

                if (isValidCharacter(next, false))
                {
                    builder.append(next);
                }
                else
                {
                    PunctuationToken token;
                    String text = builder.toString();
                    if (text.length() < 4)
                    {
                        token = tokenCache.get(text);
                        if (token == null)
                        {
                            tokenCache.put(text, (token = new PunctuationToken(text)));
                        }
                    }
                    else
                    {
                        token = new PunctuationToken(text);
                    }

                    parser.addToken(token);

                    return i;
                }

                i += 1;
            }
        }

        return 0;
    }
}