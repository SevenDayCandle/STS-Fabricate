package pinacolada.cards.base.cardText;

import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;

import java.util.ArrayList;
import java.util.List;

public class PCLTextParser
{

    public final boolean ignoreKeywords;
    public final ArrayList<ArrayList<PCLTextToken>> tokenLines = new ArrayList<>();
    public PCLCard card;
    protected Character character;
    protected CharSequence text;
    protected int remaining;
    protected int characterIndex;
    protected int lineIndex;
    protected float scaleModifier;

    public PCLTextParser()
    {
        this(false);
    }

    public PCLTextParser(boolean ignoreKeywords)
    {
        this.ignoreKeywords = ignoreKeywords;
    }

    protected void addLine()
    {
        tokenLines.add(new ArrayList<>());
        lineIndex += 1;
    }

    protected void addToken(PCLTextToken token)
    {
        if (token.type == PCLTextTokenType.NewLine)
        {
            addLine();
        }
        else
        {
            tokenLines.get(lineIndex).add(token);
        }
    }

    protected void addTooltip(EUITooltip tooltip)
    {
        if (card != null && tooltip != null && tooltip.title != null && !card.tooltips.contains(tooltip))
        {
            card.tooltips.add(tooltip);
        }
    }

    protected boolean compareNext(int amount, char character)
    {
        final Character other = nextCharacter(amount);
        if (other != null)
        {
            return other == character;
        }

        return false;
    }

    public List<PCLTextToken> getTokens()
    {
        return EUIUtils.flattenList(tokenLines);
    }

    public void initialize(PCLCard card, String text)
    {
        this.card = card;
        this.text = text;
        this.tokenLines.clear();
        this.scaleModifier = 1;

        tokenLines.add(new ArrayList<>());

        this.characterIndex = 0;
        this.lineIndex = 0;

        int amount = 0;
        while (moveIndex(amount))
        {
            this.character = this.text.charAt(characterIndex);

            // The order matters
            if ((amount = ConditionToken.tryAdd(this)) == 0 // ║0║
                    && (amount = PointerToken.tryAdd(this)) == 0 // ¦E5¦
                    && (amount = LogicToken.tryAdd(this)) == 0 // $E5$
                    && (amount = SymbolToken.tryAdd(this)) == 0 // [E]
                    && (amount = SpecialToken.tryAdd(this)) == 0 // {code}
                    && (amount = NewLineToken.tryAdd(this)) == 0 // |
                    && (amount = WhitespaceToken.tryAdd(this)) == 0 //
                    && (amount = PunctuationToken.tryAdd(this)) == 0 // .,-.:; etc
                    && (amount = WordToken.tryAdd(this)) == 0)// Letters/Digits
            {
                EUIUtils.logError(this, "Error parsing card text, Character: " + character + ", Text: " + this.text);
                amount = 1;
            }
        }

    }

    protected boolean moveIndex(int amount)
    {
        characterIndex += amount;
        remaining = text.length() - characterIndex - 1;

        return remaining >= 0;
    }

    protected Character nextCharacter(int amount)
    {
        if (amount > remaining)
        {
            return null;
        }

        return text.charAt(characterIndex + amount);
    }

}
