package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.resources.pcl.PCLTrophies;

public class PCLTrophiesRenderer extends EUIBase
{
    protected static final PCLCoreStrings.Trophies trophyStrings = PGR.core.strings.trophies;

    protected final EUIImage trophy1Image;
    protected final EUIImage trophy2Image;
    protected final EUIImage trophy3Image;

    protected final EUIHitbox trophy1Hb;
    protected final EUIHitbox trophy2Hb;
    protected final EUIHitbox trophy3Hb;
    protected final EUITooltip tooltip;

    protected PCLLoadout loadout;
    protected PCLTrophies trophies;

    public PCLTrophiesRenderer()
    {
        final float size = scale(48);
        trophy1Hb = new EUIHitbox(screenW(0.28958f), screenH(0.564815f), size, size);
        trophy2Hb = new EUIHitbox(screenW(0.32083f), screenH(0.564815f), size, size);
        trophy3Hb = new EUIHitbox(screenW(0.35208f), screenH(0.564815f), size, size);
        tooltip = new EUITooltip("", "");

        trophy1Image = new EUIImage(PGR.core.images.lockedTrophy.texture(), trophy1Hb);
        trophy2Image = new EUIImage(PGR.core.images.lockedTrophy.texture(), trophy2Hb);
        trophy3Image = new EUIImage(PGR.core.images.lockedTrophy.texture(), trophy3Hb);

//
//        float baseX = 200f * Settings.scale;
//        float baseY = (float) Settings.HEIGHT / 2f;
//
//        trophy1Hb.move(baseX + 380f * Settings.scale, baseY + 94f * Settings.scale);
//        trophy2Hb.move(baseX + 440f * Settings.scale, baseY + 94f * Settings.scale);
//        trophy3Hb.move(baseX + 500f * Settings.scale, baseY + 94f * Settings.scale);
    }

    public void refresh(PCLLoadout loadout)
    {
        this.loadout = loadout;
        this.trophies = loadout.getTrophies();
    }

    private void renderTrophy(EUIImage image, int trophyLevel, SpriteBatch sb)
    {
        image.renderImpl(sb);

        if (trophyLevel > 0)
        {
            FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, trophyLevel + "/20",
                    image.hb.cX, image.hb.y - (4 * Settings.scale), Settings.GOLD_COLOR);
        }
    }

    public void updateImpl()
    {
        if (trophies == null)
        {
            return;
        }

        updateTrophy(trophy1Image, PGR.core.images.bronzeTrophy.texture(), trophies.trophy1);
        updateTrophy(trophy2Image, PGR.core.images.silverTrophy.texture(), trophies.trophy2);
        updateTrophy(trophy3Image, PGR.core.images.goldTrophy.texture(), trophies.trophy3);

        if (trophy1Image.hb.hovered)
        {
            EUITooltip.queueTooltip(tooltip.setText(trophyStrings.trophy, loadout.getTrophyMessage(1)));
        }
        else if (trophy2Image.hb.hovered)
        {
            EUITooltip.queueTooltip(tooltip.setText(trophyStrings.glyph, loadout.getTrophyMessage(2)));
        }
        else if (trophy3Image.hb.hovered)
        {
            EUITooltip.queueTooltip(tooltip.setText(trophyStrings.gold, loadout.getTrophyMessage(3)));
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        if (trophies == null)
        {
            return;
        }

        FontHelper.tipHeaderFont.getData().setScale(0.6f);
        renderTrophy(trophy1Image, trophies.trophy1, sb);
        renderTrophy(trophy2Image, trophies.trophy2, sb);
        renderTrophy(trophy3Image, trophies.trophy3, sb);
        FontHelper.tipHeaderFont.getData().setScale(1);
    }

    private void updateTrophy(EUIImage image, Texture texture, int trophyLevel)
    {
        Texture slotTexture;
        if (trophyLevel <= 0)
        {
            slotTexture = PGR.core.images.bronzeTrophySlot.texture();
        }
        else
        {
            slotTexture = PGR.core.images.goldTrophySlot.texture();
        }

        Texture trophyTexture;
        if (trophyLevel < 0)
        {
            trophyTexture = PGR.core.images.lockedTrophy.texture();
        }
        else
        {
            trophyTexture = texture;
        }

        image.setBackgroundTexture(slotTexture).setTexture(trophyTexture).updateImpl();
    }
}