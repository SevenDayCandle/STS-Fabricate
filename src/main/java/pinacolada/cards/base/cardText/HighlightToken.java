package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUIKeywordTooltip;

// Copied and modified from STS-AnimatorMod
public abstract class HighlightToken extends PCLTextToken {
    private static final PCLTextParser internalParser = new PCLTextParser(true);
    public static final char TOKEN = '{';

    private HighlightToken() {
        super(null, null);
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.remaining > 1) {
            builder.setLength(0);

            int index = 1;
            int indentation = 0;
            Color color = Settings.GOLD_COLOR;
            Character next = parser.nextCharacter(index);
            while (next != null) {
                switch (next) {
                    case '#':
                        color = null;
                        break;
                    case '{':
                        indentation += 1;
                        break;
                    case '}':
                        if (indentation > 0) {
                            indentation -= 1;
                            index += 1;
                            continue;
                        }
                        if (color == null) {
                            color = Settings.GOLD_COLOR;
                        }
                        final String word = builder.toString();
                        final EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByName(word
                                .replace(EUIUtils.SPLIT_LINE, " ")
                                .split("\\(")[0] // Ignore modifiers
                                .toLowerCase());

                        if (tooltip != null) {
                            parser.addTooltip(tooltip);
                            internalParser.initialize(parser.card, tooltip.title);
                        }
                        else {
                            internalParser.initialize(parser.card, word);
                        }

                        WordToken lastToken = null;
                        for (PCLTextToken token : internalParser.getTokens()) {
                            if (token instanceof WordToken) {
                                lastToken = (WordToken) token;
                                lastToken.coloredString.setColor(color);
                                lastToken.tooltip = tooltip;
                                parser.addToken(lastToken);
                            }
                            else if (token instanceof PunctuationToken && lastToken != null) {
                                lastToken.rawText = lastToken.rawText + token.rawText;
                                lastToken.coloredString.text = lastToken.rawText;
                                lastToken = null;
                            }
                            else {
                                parser.addToken(token);
                                lastToken = null;
                            }
                        }

                        return index + 1;
                    case ':':
                        if (color == null) {
                            final String cString = builder.toString();
                            color = EUISmartText.getColor(cString);
                            builder.setLength(0);
                            break;
                        }
                    default:
                        builder.append(next);
                }
                index += 1;
                next = parser.nextCharacter(index);
            }
            return 1;
        }

        return 0;
    }
}