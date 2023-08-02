package pinacolada.cards.base.cardText;

import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;

import java.util.ArrayList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public class PCLTextParser {

    public final boolean ignoreKeywords;
    public final ArrayList<ArrayList<PCLTextToken>> tokenLines = new ArrayList<>();
    protected PCLTextToken previous;
    protected Character character;
    protected CharSequence text;
    protected int remaining;
    protected int characterIndex;
    protected int lineIndex;
    protected float scaleModifier;
    public PCLCard card;

    public PCLTextParser() {
        this(false);
    }

    public PCLTextParser(boolean ignoreKeywords) {
        this.ignoreKeywords = ignoreKeywords;
    }

    protected void addLine() {
        tokenLines.add(new ArrayList<>());
        lineIndex += 1;
    }

    protected void addToken(PCLTextToken token) {
        if (token.type == PCLTextTokenType.NewLine) {
            addLine();
            previous = null;
        }
        else {
            tokenLines.get(lineIndex).add(token);
            previous = token;
        }
    }

    protected void addTooltip(EUIKeywordTooltip tooltip) {
        if (card != null && tooltip != null && tooltip.title != null && !card.tooltips.contains(tooltip)) {
            card.tooltips.add(tooltip);
        }
    }

    public List<PCLTextToken> getTokens() {
        return EUIUtils.flattenList(tokenLines);
    }

    public void initialize(PCLCard card, String text) {
        this.card = card;
        this.text = text;
        this.tokenLines.clear();
        this.scaleModifier = 1;

        tokenLines.add(new ArrayList<>());

        this.characterIndex = 0;
        this.lineIndex = 0;

        int amount = 0;
        while (moveIndex(amount)) {
            this.character = this.text.charAt(characterIndex);
            amount = tryAddToken();
        }
    }

    protected boolean isNext(int amount, char character) {
        final Character other = nextCharacter(amount);
        if (other != null) {
            return other == character;
        }

        return false;
    }

    protected boolean isWhitespace(int amount) {
        final Character other = nextCharacter(amount);
        if (other != null) {
            return Character.isWhitespace(other);
        }

        return false;
    }

    protected boolean moveIndex(int amount) {
        characterIndex += amount;
        remaining = text.length() - characterIndex - 1;

        return remaining >= 0;
    }

    protected Character nextCharacter(int amount) {
        if (amount > remaining) {
            return null;
        }

        return text.charAt(characterIndex + amount);
    }

    protected boolean removeLastToken() {
        ArrayList<PCLTextToken> line = tokenLines.get(lineIndex);
        if (line.size() > 0) {
            line.remove(line.size() - 1);
            return true;
        }
        return false;
    }

    protected int tryAddToken() {
        switch (character) {
            case ConditionToken.TOKEN:
                return ConditionToken.tryAdd(this);
            case PointerToken.TOKEN:
                return PointerToken.tryAdd(this);
            case LogicToken.TOKEN:
                return LogicToken.tryAdd(this);
            case SymbolToken.TOKEN1:
            case SymbolToken.TOKEN2:
                return SymbolToken.tryAdd(this);
            case VariableToken.TOKEN:
                return VariableToken.tryAdd(this);
            case HighlightToken.TOKEN:
                return HighlightToken.tryAdd(this);
            case ModifierSplitToken.TOKEN:
                return ModifierSplitToken.add(this);
        }

        int amount = NewLineToken.tryAdd(this);
        if (amount > 0) {
            return amount;
        }
        amount = WhitespaceToken.tryAdd(this);
        if (amount > 0) {
            return amount;
        }
        amount = PunctuationToken.tryAdd(this);
        if (amount > 0) {
            return amount;
        }
        amount = WordToken.tryAdd(this);
        if (amount > 0) {
            return amount;
        }
        EUIUtils.logError(this, "Error parsing card text, Character: " + character + ", Text: " + this.text);
        return 1;
    }

}
