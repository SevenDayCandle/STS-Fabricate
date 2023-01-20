package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static pinacolada.cards.base.cardText.PCLTextContext.isIdeographicLanguage;

// Copied and modified from STS-AnimatorMod
public class WhitespaceToken extends PCLTextToken
{
    protected static final WhitespaceToken Default = new WhitespaceToken();

    private WhitespaceToken()
    {
        super(PCLTextTokenType.Whitespace, " ");
    }

    public static int tryAdd(PCLTextParser parser)
    {
        if (Character.isWhitespace(parser.character))
        {
            parser.addToken(Default);

            return 1;
        }

        return 0;
    }

    @Override
    protected float getWidth(BitmapFont font, String text)
    {
        BitmapFont.BitmapFontData data = font.getData();
        if (isIdeographicLanguage())
        {
            return data.scaleX * data.spaceWidth * text.length() * 0.4f;
        }
        else
        {
            return data.scaleX * data.spaceWidth * text.length();// super.GetWidth(font, text);
        }
    }

    @Override
    public void render(SpriteBatch sb, PCLTextContext context)
    {
        context.startX += getWidth(context.font, rawText);
    }
}
