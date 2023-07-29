package pinacolada.cards.base.cardText;

// Copied and modified from STS-AnimatorMod
public class ModifierSplitToken extends PCLTextToken {
    public static final char TOKEN = ':';
    public PCLTextToken previous;

    protected ModifierSplitToken(String text, PCLTextToken previous) {
        super(PCLTextTokenType.Punctuation, text);
        this.previous = previous;
    }

    public static int add(PCLTextParser parser) {
        parser.addToken(new ModifierSplitToken(parser.character.toString(), parser.previous));
        return 1;
    }
}