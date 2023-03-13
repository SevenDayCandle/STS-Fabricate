package pinacolada.cards.base.fields;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUI;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCardAffinity implements Comparable<PCLCardAffinity>
{
    public final PCLAffinity type;
    public int level;

    public PCLCardAffinity(PCLAffinity affinity)
    {
        this.type = affinity;
    }

    public PCLCardAffinity(PCLAffinity affinity, int level)
    {
        this.type = affinity;
        this.level = level;
    }

    public void render(SpriteBatch sb, Color color, float cX, float cY, float size)
    {
        Texture background = type.getBackground(level);
        if (background != null)
        {
            PCLRenderHelpers.drawCentered(sb, Color.LIGHT_GRAY, background, cX, cY, size, size, 1, 0);
        }

        PCLRenderHelpers.drawCentered(sb, color, type.getIcon(), cX, cY, size, size, 1, 0);

        Texture border = type.getBorder(level);
        if (border != null)
        {
            PCLRenderHelpers.drawCentered(sb, color, border, cX, cY, size, size, 1, 0);
        }

        if (type == PCLAffinity.Star)
        {
            PCLRenderHelpers.drawCentered(sb, color, PCLCoreImages.starFg.texture(), cX, cY, size, size, 1, 0);
        }
    }

    public void renderOnCard(SpriteBatch sb, PCLCard card, float x, float y, float size, boolean highlight, boolean allowAlternateBorder)
    {
        float borderScale = 1f;
        final Color color = Color.WHITE.cpy();
        Color backgroundColor = color.cpy();
        Color borderColor = color;
        if (highlight)
        {
            borderColor = Settings.GREEN_RELIC_COLOR.cpy();
            borderColor.a = color.a;
            borderScale += EUI.timeSin(0.015f, 2.5f);
        }

        Texture background = type.getBackground(allowAlternateBorder ? level : Math.min(1, level));
        if (background != null)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, background, new Vector2(x, y), size, size, Color.LIGHT_GRAY, 1f, 1f, 0);
        }

        PCLRenderHelpers.drawOnCardAuto(sb, card, type.getIcon(), new Vector2(x, y), size, size, color, 1f, 1f, 0f);

        Texture border = type.getBorder(allowAlternateBorder ? level : Math.min(1, level));
        if (border != null)
        {
            PCLRenderHelpers.drawOnCardAuto(sb, card, border, new Vector2(x, y), size, size, borderColor, 1f, borderScale, 0f);
        }

        if (type == PCLAffinity.Star)
        {
            Texture star = PCLCoreImages.starFg.texture();
            PCLRenderHelpers.drawOnCardAuto(sb, card, star, new Vector2(x, y), size, size, color, 1f, 1f, 0);
        }
    }

    public int calculateRank()
    {
        return this.level * 1000 - this.type.id;
    }

    @Override
    public int compareTo(PCLCardAffinity other)
    {
        return other.calculateRank() - calculateRank();
    }

    @Override
    public String toString()
    {
        return type + ": " + level;
    }
}