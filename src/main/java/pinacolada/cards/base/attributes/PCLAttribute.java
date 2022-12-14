package pinacolada.cards.base.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.HashMap;

public abstract class PCLAttribute
{
    protected final static HashMap<AbstractCard.CardRarity, ColoredTexture> panels = new HashMap<>();
    protected final static HashMap<AbstractCard.CardRarity, ColoredTexture> panelsLarge = new HashMap<>();
    protected final static PCLCoreImages.CardIcons ICONS = PGR.core.images.icons;
    protected final static float DESC_OFFSET_X = (AbstractCard.IMG_WIDTH * 0.5f);
    protected final static float DESC_OFFSET_Y = (AbstractCard.IMG_HEIGHT * 0.10f);
    protected static final GlyphLayout layout = new GlyphLayout();

    public static boolean leftAlign;

    public Texture icon;
    public Texture largeIcon;
    public ColoredString pclText;
    public String iconTag;
    public String suffix;
    public float scaleMult = 1f;

    protected ColoredTexture getPanelByRarity(PCLCard card)
    {
        if (PGR.core.config.simplifyCardUI.get())
        {
            return null;
        }

        HashMap<AbstractCard.CardRarity, ColoredTexture> map = card.isPopup ? panelsLarge : panels;
        ColoredTexture result = map.getOrDefault(card.rarity, null);
        if (result == null)
        {
            result = card.getCardAttributeBanner();
            if (result != null)
            {
                map.put(card.rarity, result);
            }

        }

        return result;
    }

    public int getParsedValue()
    {
        if (this.pclText == null)
        {
            return 0;
        }

        try
        {
            return Integer.parseInt(pclText.text);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public void render(SpriteBatch sb, PCLCard card)
    {
        if (pclText == null)
        {
            return;
        }

        final float suffix_scale = 0.66f;
        final float cw = AbstractCard.RAW_W;
        final float ch = AbstractCard.RAW_H;
        final float b_w = 126f;
        final float b_h = 85f;
        final float y = -ch * 0.04f;
        final ColoredTexture panel = getPanelByRarity(card);

        BitmapFont largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);
        largeFont.getData().setScale(card.isPopup ? 0.5f : 1);
        layout.setText(largeFont, pclText.text);

        float text_width = layout.width / Settings.scale;
        float suffix_width = 0;

        if (suffix != null)
        {
            layout.setText(largeFont, suffix);
            suffix_width = (layout.width / Settings.scale) * suffix_scale;
        }

        largeFont = PCLRenderHelpers.getLargeAttributeFont(card, scaleMult);

        final float sign = leftAlign ? -1 : +1;
        final float icon_x = sign * (cw * 0.45f);
        float text_x = sign * cw * ((suffix != null || pclText.text.length() > 2) ? 0.375f : 0.35f);

        if (panel != null)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, panel.texture, new Vector2(sign * cw * 0.33f, y), b_w, b_h, panel.color, panel.color.a * card.transparency, 1, 0, leftAlign, false);
        }

        PCLRenderHelpers.drawOnCardAuto(sb, card, card.isPopup ? largeIcon : icon, icon_x, y, 48, 48);
        PCLRenderHelpers.writeOnCard(sb, card, largeFont, pclText.text, text_x - (sign * text_width * 0.5f), y, pclText.color, true);

        if (suffix != null)
        {
            largeFont.getData().setScale(largeFont.getScaleX() * suffix_scale);
            PCLRenderHelpers.writeOnCard(sb, card, largeFont, suffix, text_x - (sign * text_width) - (sign * suffix_width * 0.6f), y, pclText.color, true);
        }

        if (iconTag != null)
        {
            BitmapFont smallFont = PCLRenderHelpers.getSmallAttributeFont(card, scaleMult);
            PCLRenderHelpers.writeOnCard(sb, card, smallFont, iconTag, icon_x, y - 12, Settings.CREAM_COLOR, true);
            PCLRenderHelpers.resetFont(smallFont);
        }

        PCLRenderHelpers.resetFont(largeFont);
    }

    public PCLAttribute setCard(PCLCard card) {
        return this;
    }

    public PCLAttribute setIconTag(String iconTag)
    {
        this.iconTag = iconTag;

        return this;
    }

    public PCLAttribute setIcon(Texture icon)
    {
        this.icon = icon;

        return this;
    }

    public PCLAttribute setText(Object text, Color color)
    {
        this.pclText = new ColoredString(text, color);

        return this;
    }

    public PCLAttribute addMultiplier(int times)
    {
        this.suffix = leftAlign ? ("x" + times) : (times + "x");

        return this;
    }

    public PCLAttribute addSuffix(String suffix)
    {
        this.suffix = suffix;

        return this;
    }

    public PCLAttribute clear()
    {
        this.suffix = null;
        this.iconTag = null;
        this.icon = null;
        this.largeIcon = null;
        this.pclText = null;

        return this;
    }

    public PCLAttribute setLargeIcon(Texture icon)
    {
        this.largeIcon = icon;

        return this;
    }

    public PCLAttribute setText(ColoredString string)
    {
        this.pclText = string;

        return this;
    }
}