package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIUtils;
import extendedui.text.EUITextHelper;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pinacolada.skills.PSkill.EFFECT_CHAR;

public class LogicToken extends PCLTextToken {
    private static final PCLTextParser internalParser = new PCLTextParser(false);
    public static final char TOKEN = '$';
    private final List<LogicTokenBlock> blocks;
    private final char variableID;
    private final PSkill<?> move;
    private int cachedValue;
    private LogicTokenBlock cachedResult;

    protected LogicToken(char variableID, PSkill<?> move, List<LogicTokenBlock> blocks, int initialValue) {
        super(PCLTextTokenType.Text, null);
        this.variableID = variableID;
        this.blocks = blocks;
        this.move = move;
        cachedValue = initialValue;
        for (LogicTokenBlock block : blocks) {
            if (block.evaluate(cachedValue)) {
                cachedResult = block;
                break;
            }
        }
    }

    protected static String getMinString(Collection<LogicTokenBlock> blocks) {
        LogicTokenBlock min = EUIUtils.findMin(blocks, block -> block.token.rawText.length());
        return min != null && min.token.rawText != null ? min.token.rawText : "";
    }

    private static LogicToken makeToken(PCLCard card, PointerToken pointer, List<LogicTokenBlock> blocks, int initialValue) {
        return pointer != null && pointer.move != null ?
                new LogicToken(pointer.variableID, pointer.move, blocks, pointer.move.getAttribute(pointer.variableID)) :
                new LogicToken(EFFECT_CHAR, null, blocks, initialValue);
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.remaining > 1) {
            builder.setLength(0);
            ArrayList<LogicTokenBlock> blockConds = new ArrayList<>();
            PointerToken pointer = null;
            LogicTokenBlock currentBlock = null;
            EUITextHelper.LogicCondition current = null;
            int staticValue = 0;

            int i = 1;
            while (true) {
                final Character next = parser.nextCharacter(i);
                if (next == null) {
                    break;
                }
                else {
                    switch (next) {
                        // < > signals start of condition. If there already was one, add it to the block and make a new one
                        case '<':
                        case '>':
                        case '%':
                        case '&':
                        case '!':
                        case '?':
                            if (currentBlock == null) {
                                currentBlock = new LogicTokenBlock();
                            }
                            else {
                                String condOutput = EUIUtils.popBuilder(builder);
                                current.value = StringUtils.isNumeric(condOutput) ? Integer.parseInt(condOutput) : 0;
                                currentBlock.conditions.add(current);
                            }
                            current = new EUITextHelper.LogicCondition(EUITextHelper.LogicComparison.typeFor(next));
                            break;
                        // : signals end of condition definition. An empty condition means that this block always returns
                        case ':':
                            if (currentBlock == null) {
                                currentBlock = new LogicTokenBlock();
                            }
                            if (current == null) {
                                current = new EUITextHelper.LogicCondition(EUITextHelper.LogicComparison.True);
                            }
                            String condOutput = EUIUtils.popBuilder(builder);
                            current.value = StringUtils.isNumeric(condOutput) ? Integer.parseInt(condOutput) : 0;
                            currentBlock.conditions.add(current);
                            break;
                        // @ $ signals end of block. If there was no conditionBlock, this defines the pointer
                        case '$':
                        case '@':
                            PCLTextToken token = null;
                            internalParser.initialize(parser.card, EUIUtils.popBuilder(builder));
                            List<PCLTextToken> tokens = internalParser.getTokens();
                            if (tokens.size() > 0) {
                                token = tokens.get(0);
                            }

                            // Only word tokens are allowed in blocks
                            if (currentBlock != null && token instanceof WordToken) {
                                currentBlock.token = (WordToken) token;
                                EUIKeywordTooltip tooltip = currentBlock.token.tooltip;
                                if (tooltip != null) {
                                    parser.addTooltip(tooltip);
                                }
                                blockConds.add(currentBlock);

                                // Subsequent word tokens need to be coalesced into the first to display properly
                                if (tokens.size() > 1) {
                                    StringBuilder temp = new StringBuilder(currentBlock.token.rawText);
                                    for (int j = 1; j < tokens.size(); j++) {
                                        temp.append(tokens.get(j).rawText);
                                    }
                                    currentBlock.token.modifyText(temp.toString());
                                }

                                currentBlock = null;
                                current = null;
                            }
                            else if (token instanceof PointerToken) {
                                pointer = (PointerToken) token;
                            }
                            else if (token != null && StringUtils.isNumeric(token.rawText)) {
                                staticValue = Integer.parseInt(token.rawText);
                            }
                            break;
                        default:
                            builder.append(next);
                    }
                }
                i += 1;
                // $ signals both the end of the block and end of parsing
                if (next == '$') {
                    break;
                }
            }

            parser.addToken(makeToken(parser.card, pointer, blockConds, staticValue));
            return i;
        }

        return 0;
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        if (cachedResult != null) {
            return super.getWidth(font, cachedResult.token.rawText);
        }
        return super.getWidth(font, "_.");
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        if (move != null) {
            int value = move.getAttribute(variableID);
            if (cachedValue != value) {
                cachedValue = value;
                for (LogicTokenBlock block : blocks) {
                    if (block.evaluate(value)) {
                        cachedResult = block;
                        break;
                    }
                }
            }
        }
        if (cachedResult != null) {
            cachedResult.token.render(sb, context);
        }
    }

    protected static class LogicTokenBlock {
        final public ArrayList<EUITextHelper.LogicCondition> conditions;
        public WordToken token;

        LogicTokenBlock() {
            conditions = new ArrayList<>();
        }

        public boolean evaluate(int input) {
            return EUIUtils.any(conditions, block -> block.evaluate(input));
        }
    }
}
