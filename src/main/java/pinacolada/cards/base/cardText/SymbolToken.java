package pinacolada.cards.base.cardText;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;

import java.util.HashMap;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class SymbolToken extends PCLTextToken
{
    public static final Map<String, SymbolToken> tokenCache = new HashMap<>();

    static
    {
//        tokenCache.put("R", new SymbolToken("[R]"));
//        tokenCache.put("G", new SymbolToken("[G]"));
        tokenCache.put("E", new SymbolToken("[E]")); // Energy
        tokenCache.put(PCLAffinity.Red.getPowerSymbol(), new SymbolToken(PCLAffinity.Red.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Green.getPowerSymbol(), new SymbolToken(PCLAffinity.Green.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Blue.getPowerSymbol(), new SymbolToken(PCLAffinity.Blue.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Orange.getPowerSymbol(), new SymbolToken(PCLAffinity.Orange.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Yellow.getPowerSymbol(), new SymbolToken(PCLAffinity.Yellow.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Purple.getPowerSymbol(), new SymbolToken(PCLAffinity.Purple.getFormattedPowerSymbol()));
        tokenCache.put(PCLAffinity.Silver.getPowerSymbol(), new SymbolToken(PCLAffinity.Silver.getFormattedPowerSymbol()));
    }

    protected EUITooltip tooltip;

    private SymbolToken(String text)
    {
        super(PCLTextTokenType.Symbol, text);
        this.tooltip = EUITooltip.findByName(text);
    }

    private SymbolToken(EUITooltip tooltip)
    {
        super(PCLTextTokenType.Symbol, tooltip.title);
        this.tooltip = tooltip;
    }

    public static int tryAdd(PCLTextParser parser)
    {
        if (parser.character == '[' && parser.remaining > 1)
        {
            builder.setLength(0);

            int i = 1;
            while (true)
            {
                Character next = parser.nextCharacter(i);
                if (next == null)
                {
                    break;
                }
                else if (next == ']')
                {
                    final String key = builder.toString();
                    SymbolToken token = tokenCache.get(key);
                    if (token == null)
                    {
                        final EUITooltip tooltip = EUITooltip.findByID(key);
                        if (tooltip != null)
                        {
                            token = new SymbolToken(tooltip);
                            tokenCache.put(key, token);
                        }
                        else
                        {
                            EUIUtils.logError(parser.card, "Unknown symbol type: [" + key + "], Raw text is: " + parser.text);
                            return i + 1;
                        }
                    }

                    parser.addToken(token);
                    parser.addTooltip(token.tooltip);

                    return i + 1;
                }
                else
                {
                    builder.append(next);
                    i += 1;
                }
            }
        }

        return 0;
    }

    @Override
    public int getCharCount()
    {
        return (EUIConfiguration.disableDescrptionIcons.get()) ? rawText.length() : 1;
    }

    @Override
    protected float getWidth(BitmapFont font, String text)
    {
        return EUIConfiguration.disableDescrptionIcons.get() ? super.getWidth(font, text) : font.getLineHeight() * 0.8f;// AbstractCard.CARD_ENERGY_IMG_WIDTH
    }

    @Override
    public void render(SpriteBatch sb, PCLCardText context)
    {
        PCLCard card = context.card;
        float size = getWidth(context);// 24f * Settings.scale * card.drawScale * context.scaleModifier;
        float partial = size / 12f;

        if (tooltip.icon != null && !EUIConfiguration.disableDescrptionIcons.get())
        {
            float iconW = size * tooltip.iconmultiW;
            float iconH = size * tooltip.iconmultiH;
            float diff = partial / tooltip.iconmultiW;

            if (tooltip.backgroundColor != null)
            {
                sb.setColor(tooltip.backgroundColor);
                sb.draw(EUIRM.images.baseBadge.texture(), context.startX - diff * 2.2f, context.startY - (partial * 6) * 1.2f, iconW * 1.2f, iconH * 1.2f);
            }
            sb.setColor(context.color);
            sb.draw(tooltip.icon, context.startX - diff, context.startY - (partial * 6), iconW, iconH);
            context.startX += (size - partial);
        }
        else
        {
            super.render(sb, context, tooltip.title, Settings.GOLD_COLOR);
        }
    }
}