package pinacolada.cards.base.cardText;

import java.util.HashMap;
import java.util.Map;

import static pinacolada.cards.base.cardText.ConditionToken.CONDITION_TOKEN;
import static pinacolada.cards.base.cardText.PointerToken.BOUND_TOKEN;

// Copied and modified from STS-AnimatorMod
public class PunctuationToken extends PCLTextToken
{
    private static final Map<String, PunctuationToken> tokenCache = new HashMap<>();

    protected PunctuationToken(String text)
    {
        super(PCLTextTokenType.Punctuation, text);
    }

    protected static boolean isValidCharacter(Character character)
    {
        if (character == null || Character.isLetterOrDigit(character) || Character.isWhitespace(character))
        {
            return false;
        }

        // Characters used by other tokens are not allowed
        switch (character)
        {
            case BOUND_TOKEN:
            case CONDITION_TOKEN:
            case '<':
            case '>':
            case '{':
            case '[':
            case '#':
            case '*':
            case '@':
            case '$':
            case ']':
            case '}':
            case '+':
            case '-':
            case '%':
                return false;
        }
        return true;
    }

    public static int tryAdd(PCLTextParser parser)
    {
        if (isValidCharacter(parser.character))
        {
            builder.setLength(0);
            builder.append(parser.character);

            int i = 1;
            while (true)
            {
                Character next = parser.nextCharacter(i);

                if (isValidCharacter(next))
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