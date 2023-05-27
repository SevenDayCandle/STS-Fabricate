package pinacolada.cards.base.cardText;

// Copied and modified from STS-AnimatorMod
public class ModifierSplitToken extends PCLTextToken {
    public PCLTextToken previous;

    protected ModifierSplitToken(String text, PCLTextToken previous) {
        super(PCLTextTokenType.Punctuation, text);
        this.previous = previous;
    }

    protected static boolean isValidCharacter(Character character) {
        return character == ':';
    }

    public static int tryAdd(PCLTextParser parser) {
        if (isValidCharacter(parser.character)) {
            parser.addToken(new ModifierSplitToken(parser.character.toString(), parser.previous));

            return 1;
        }

        return 0;
    }
}