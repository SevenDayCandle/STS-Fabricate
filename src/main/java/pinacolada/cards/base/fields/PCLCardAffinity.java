package pinacolada.cards.base.fields;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCardAffinity implements Comparable<PCLCardAffinity> {
    public final PCLAffinity type;
    public Color renderColor = Color.WHITE.cpy();
    public int level;

    public PCLCardAffinity(PCLAffinity affinity) {
        this.type = affinity;
    }

    public PCLCardAffinity(PCLAffinity affinity, int level) {
        this.type = affinity;
        this.level = level;
    }

    public int calculateRank() {
        return this.level * 1000 - this.type.ID;
    }

    @Override
    public int compareTo(PCLCardAffinity other) {
        return other.calculateRank() - calculateRank();
    }

    public void render(SpriteBatch sb, Color color, float cX, float cY, float size) {
        Texture background = type.getBackground(level);
        if (background != null) {
            PCLRenderHelpers.drawCentered(sb, Color.LIGHT_GRAY, background, cX, cY, size, size, 1, 0);
        }

        PCLRenderHelpers.drawCentered(sb, color, type.getIcon(), cX, cY, size, size, 1, 0);

        Texture border = type.getBorder(level);
        if (border != null) {
            PCLRenderHelpers.drawCentered(sb, color, border, cX, cY, size, size, 1, 0);
        }

        if (type == PCLAffinity.Star) {
            PCLRenderHelpers.drawCentered(sb, color, PCLCoreImages.CardAffinity.starFg.texture(), cX, cY, size, size, 1, 0);
        }
    }

    public void renderOnCard(SpriteBatch sb, AbstractCard card, float x, float y, float size, boolean allowAlternateBorder) {
        float borderScale = 1f;
        renderColor.a = card.transparency;

        Texture background = type.getBackground(allowAlternateBorder ? level : Math.min(1, level));
        if (background != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, card, background, x, y, size, size, renderColor, card.transparency, 1f, 0);
        }

        PCLRenderHelpers.drawOnCardAuto(sb, card, type.getIcon(), x, y, size, size, renderColor, card.transparency, 1f, 0f);

        Texture border = type.getBorder(allowAlternateBorder ? level : Math.min(1, level));
        if (border != null) {
            PCLRenderHelpers.drawOnCardAuto(sb, card, border, x, y, size, size, renderColor, card.transparency, 1f, 0f);
        }

        if (type == PCLAffinity.Star) {
            Texture star = PCLCoreImages.CardAffinity.starFg.texture();
            PCLRenderHelpers.drawOnCardAuto(sb, card, star, x, y, size, size, renderColor, card.transparency, 1f, 0);
        }
    }

    @Override
    public String toString() {
        return type + ": " + level;
    }
}