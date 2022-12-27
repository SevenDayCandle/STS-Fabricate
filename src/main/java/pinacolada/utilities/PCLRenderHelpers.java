package pinacolada.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import org.imgscalr.Scalr;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

public class PCLRenderHelpers extends EUIRenderHelpers
{
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    public static ShapeRenderer renderer = new ShapeRenderer();

    public static String decimalFormat(float number)
    {
        return DECIMAL_FORMAT.format(number);
    }

    public static void drawGrayscaleIf(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc, boolean var)
    {
        if (var)
        {
            drawGrayscale(sb, drawFunc);
        }
        else
        {
            drawFunc.invoke(sb);
        }
    }

    public static BitmapFont getDescriptionFont(PCLCard card, float scaleModifier)
    {
        BitmapFont result;
        if (card.isPopup)
        {
            result = EUIFontHelper.carddescriptionfontLarge;
            result.getData().setScale(card.drawScale * scaleModifier * 0.5f);
        }
        else
        {
            result = EUIFontHelper.carddescriptionfontNormal;
            result.getData().setScale(card.drawScale * scaleModifier);
        }

        return result;
    }

    public static BitmapFont getEnergyFont(PCLCard card)
    {
        BitmapFont result;
        if (card.isPopup)
        {
            result = FontHelper.SCP_cardEnergyFont;
            result.getData().setScale(card.drawScale * 0.5f);
        }
        else
        {
            result = FontHelper.cardEnergyFont_L;
            result.getData().setScale(card.drawScale);
        }

        return result;
    }

    public static BitmapFont getLargeAttributeFont(PCLCard card, float scaleMult)
    {
        BitmapFont result;
        if (card.isPopup)
        {
            result = EUIFontHelper.cardiconfontVerylarge;
            result.getData().setScale(card.drawScale * 0.5f * scaleMult);
        }
        else
        {
            result = EUIFontHelper.cardiconfontLarge;
            result.getData().setScale(card.drawScale * scaleMult);
        }

        return result;
    }

    public static BitmapFont getSmallAttributeFont(PCLCard card, float scaleMult)
    {
        BitmapFont result;
        if (card.isPopup)
        {
            result = EUIFontHelper.cardiconfontLarge;
            result.getData().setScale(card.drawScale * 0.45f * scaleMult);
        }
        else
        {
            result = EUIFontHelper.cardiconfontSmall;
            result.getData().setScale(card.drawScale * 0.9f * scaleMult);
        }

        return result;
    }

    public static TextureRegion getSmallIcon(String id)
    {
        switch (id)
        {
            case "E":
                return AbstractDungeon.player != null ? AbstractDungeon.player.getOrb() : PGR.core.tooltips.energy.icon;
            case "CARD":
                return AbstractCard.orb_card;
            case "POTION":
                return AbstractCard.orb_potion;
            case "RELIC":
                return AbstractCard.orb_relic;
            case "SPECIAL":
                return AbstractCard.orb_special;

            default:
                EUITooltip tooltip = EUITooltip.findByID(id);
                return (tooltip != null) ? tooltip.icon : null;
        }
    }

    public static BitmapFont getSmallTextFont(PCLCard card, String text)
    {
        float scaleModifier = 0.8f;
        int length = text.length();
        if (length > 20)
        {
            scaleModifier -= 0.02f * (length - 20);
            if (scaleModifier < 0.5f)
            {
                scaleModifier = 0.5f;
            }
        }

        BitmapFont result;
        if (card.isPopup)
        {
            result = EUIFontHelper.cardtitlefontLarge;
            result.getData().setScale(card.drawScale * scaleModifier * 0.5f);
        }
        else
        {
            // NOTE: this was FontHelper.cardTitleFont_small
            result = EUIFontHelper.cardtitlefontSmall;
            result.getData().setScale(card.drawScale * scaleModifier);
        }

        return result;
    }

    public static BitmapFont getsmalltextfontLegacy(PCLCard card, String text)
    {
        float scaleModifier = 0.8f;
        int length = text.length();
        if (length > 20)
        {
            scaleModifier -= 0.02f * (length - 20);
            if (scaleModifier < 0.5f)
            {
                scaleModifier = 0.5f;
            }
        }

        BitmapFont result;
        if (card.isPopup)
        {
            result = FontHelper.SCP_cardTitleFont_small;
            result.getData().setScale(card.drawScale * scaleModifier * 0.5f);
        }
        else
        {
            // NOTE: this was FontHelper.cardTitleFont_small
            result = EUIFontHelper.cardtitlefontSmall;
            result.getData().setScale(card.drawScale * scaleModifier);
        }

        return result;
    }

    public static BitmapFont getTitleFont(PCLCard card)
    {
        BitmapFont result;
        final float scale = 1 / (Math.max(14f, card.name.length()) / 14f);
        if (card.isPopup)
        {
            result = EUIFontHelper.cardtitlefontLarge;
            result.getData().setScale(card.drawScale * 0.5f * scale);
        }
        else
        {
            result = EUIFontHelper.cardtitlefontNormal;
            result.getData().setScale(card.drawScale * scale);
        }

        return result;
    }

    public static void drawCurve(SpriteBatch sb, Texture texture, Color c1, Hitbox h1, Hitbox h2, float vDist, float startScale, float scaleGrowth, int points) {
        drawCurve(sb, texture, c1, new Vector2(h1.cX, h1.cY), new Vector2(h2.cX, h2.cY), vDist, startScale, scaleGrowth, points);
    }

    public static void drawCurve(SpriteBatch sb, Texture texture, Color c1, Color c2, Hitbox h1, Hitbox h2, float vDist, float startScale, float scaleGrowth, int points) {
        drawCurve(sb, texture, c1, c2, new Vector2(h1.cX, h1.cY), new Vector2(h2.cX, h2.cY), vDist, startScale, scaleGrowth, points);
    }

    public static void drawCurve(SpriteBatch sb, Texture texture, Color c1, Vector2 h1, Vector2 h2, float vDist, float startScale, float scaleGrowth, int points) {
        drawCurve(sb, texture, c1, c1, h1, h2, new Vector2((h1.x + h2.x) / 2, (h1.y + h2.y) / 2 + vDist), startScale, scaleGrowth, points);
    }

    public static void drawCurve(SpriteBatch sb, Texture texture, Color c1, Color c2, Vector2 h1, Vector2 h2, float vDist, float startScale, float scaleGrowth, int points) {
        drawCurve(sb, texture, c1, c2, h1, h2, new Vector2((h1.x + h2.x) / 2, (h1.y + h2.y) / 2 + vDist), startScale, scaleGrowth, points);
    }

    public static void drawCurve(SpriteBatch sb, Texture texture, Color c1, Color c2, Vector2 h1, Vector2 h2, Vector2 c, float startScale, float scaleGrowth, int points) {
        Vector2 cur = new Vector2();
        Vector2 prev = new Vector2();
        float origin = texture.getWidth() / 2f;
        float scale = Settings.scale * startScale;

        for (int i = 0; i < points; ++i)
        {
            float divisor = (float) i / points;
            prev.x = cur.x;
            prev.y = cur.y;
            cur = Bezier.quadratic(cur, divisor, h1, c, h2, new Vector2());
            float angle;
            Vector2 tmp;
            if (i != 0)
            {
                tmp = new Vector2(prev.x - cur.x, prev.y - cur.y);
                angle = tmp.nor().angle() + 90f;
            }
            else
            {
                tmp = new Vector2(c.x - cur.x, c.y - cur.y);
                angle = tmp.nor().angle() + 270f;
            }

            sb.setColor(EUIColors.lerp(c1, c2, divisor));
            sb.draw(texture, cur.x - origin, cur.y - origin, origin, origin, texture.getWidth(), texture.getHeight(), scale, scale, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
            scale += scaleGrowth;
        }
    }

    public static BufferedImage scalrScale(Texture image, float xScale, float yScale) {
        return scalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage scalrScale(Texture image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        if (!image.getTextureData().isPrepared()) {
            image.getTextureData().prepare();
        }
        return scalrScale(image.getTextureData().consumePixmap(), xScale, yScale);
    }

    public static BufferedImage scalrScale(Pixmap image, float xScale, float yScale) {
        return scalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage scalrScale(Pixmap image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        try {
            PixmapIO.PNG writer = new PixmapIO.PNG((int)((float)(image.getWidth() * image.getHeight()) * 1.5F));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            writer.setFlipY(false);
            writer.write(stream, image);
            writer.dispose();
            return scalrScale(ImageIO.read(new ByteArrayInputStream(stream.toByteArray())), xScale, yScale, scalingMethod, ops);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage scalrScale(BufferedImage image, float xScale, float yScale) {
        return scalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage scalrScale(BufferedImage image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        if (image == null) {
            return null;
        }
        return Scalr.resize(image, scalingMethod, (int) (image.getWidth() * xScale), (int) (image.getHeight() * yScale), ops);
    }

    public static Pixmap scalrScaleAsPixmap(Texture image, float xScale, float yScale) {
        BufferedImage bi = scalrScale(image, xScale, yScale);
        return bi != null ? getPixmapFromBufferedImage(bi) : null;
    }

    public static Pixmap scalrScaleAsPixmap(Pixmap image, float xScale, float yScale) {
        BufferedImage bi = scalrScale(image, xScale, yScale);
        return bi != null ? getPixmapFromBufferedImage(bi) : null;
    }

    public static Pixmap scalrScaleAsPixmap(BufferedImage image, float xScale, float yScale) {
        return getPixmapFromBufferedImage(image);
    }

    public static void setBlending(SpriteBatch sb, BlendingMode blendingMode)
    {
        sb.setBlendFunction(blendingMode.srcFunc, blendingMode.dstFunc);
    }

    public static ColoredTexture toEYBTexture(ColoredTexture texture)
    {
        return new ColoredTexture(texture.texture, texture.color);
    }
}
