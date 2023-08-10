package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;

import java.util.HashMap;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class SymbolToken extends PCLTextToken {
    private static final Map<String, SymbolToken> tokenCache = new HashMap<>();
    public static final char TOKEN1 = '[';
    public static final char TOKEN2 = '†';

    // Called before force icon can be set on the tooltips
    static {
        tokenCache.put("E", new SymbolToken("[E]")); // Energy
        for (PCLAffinity affinity : PCLAffinity.values()) {
            tokenCache.put(affinity.getPowerSymbol(), new SymbolToken(affinity.getFormattedPowerSymbol()));
        }
    }

    protected EUIKeywordTooltip tooltip;

    private SymbolToken(String text) {
        super(PCLTextTokenType.Symbol, text);
        this.tooltip = EUIKeywordTooltip.findByName(text);
    }

    private SymbolToken(EUIKeywordTooltip tooltip) {
        super(PCLTextTokenType.Symbol, tooltip.title);
        this.tooltip = tooltip;
    }

    public static int tryAdd(PCLTextParser parser) {
        if (parser.remaining > 1) {
            builder.setLength(0);

            int i = 1;
            while (true) {
                Character next = parser.nextCharacter(i);
                if (next == null) {
                    break;
                }
                else if (next == ']') {
                    final String key = builder.toString();
                    SymbolToken token = tokenCache.get(key);
                    if (token == null) {
                        final EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByID(key);
                        if (tooltip != null) {
                            token = new SymbolToken(tooltip);
                            tokenCache.put(key, token);
                        }
                        else {
                            EUIUtils.logError(parser.card, "Unknown symbol type: [" + key + "], Raw text is: " + parser.text);
                            return i + 1;
                        }
                    }

                    parser.addToken(token);
                    parser.addTooltip(token.tooltip);

                    return i + 1;
                }
                else {
                    builder.append(next);
                    i += 1;
                }
            }
        }

        return 0;
    }

    @Override
    public int getCharCount() {
        return tooltip.forceIcon || (tooltip.icon != null && (EUIConfiguration.enableDescriptionIcons.get())) ? 1 : rawText.length();
    }

    @Override
    protected float getWidth(BitmapFont font, String text) {
        return tooltip.forceIcon || (tooltip.icon != null && (EUIConfiguration.enableDescriptionIcons.get())) ? font.getLineHeight() * 0.8f : super.getWidth(font, text);// AbstractCard.CARD_ENERGY_IMG_WIDTH
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context) {
        PCLCard card = context.card;

        if (tooltip.icon != null && (EUIConfiguration.enableDescriptionIcons.get() || tooltip.forceIcon)) {
            float size = context.font.getLineHeight() * 0.8f;
            float partial = size / 12f;
            float iconW = size * tooltip.iconmultiW;
            float iconH = size * tooltip.iconmultiH;
            float diff = partial / tooltip.iconmultiW;

            if (tooltip.backgroundColor != null) {
                sb.setColor(tooltip.backgroundColor);
                sb.draw(EUIRM.images.baseBadge.texture(), context.startX - diff * 2.2f, context.startY - (partial * 6) * 1.2f, iconW * 1.2f, iconH * 1.2f);
            }
            sb.setColor(context.color);
            sb.draw(tooltip.icon, context.startX - diff, context.startY - (partial * 6), iconW, iconH);
            context.startX += (size - partial);
        }
        else {
            super.render(sb, context, tooltip.title, Settings.GOLD_COLOR);
        }
    }

}