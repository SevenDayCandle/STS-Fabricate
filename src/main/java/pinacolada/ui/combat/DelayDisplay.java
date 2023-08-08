package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.delay.DelayUse;

import java.util.Collection;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class DelayDisplay extends EUIImage {
    protected Collection<EUITooltip> tips;

    public DelayDisplay() {
        super(PCLCoreImages.Core.timer.texture());
    }

    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, String.valueOf(DelayUse.minTurns()), hb.cX + hb.width * 0.15f, hb.cY - scale(7), Settings.GREEN_TEXT_COLOR);
        //FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, String.valueOf(DelayUse.delayCount()), hb.cX + hb.width * 0.25f, hb.cY - scale(29), Settings.BLUE_TEXT_COLOR);
    }

    public void updateImpl() {
        setPosition(player.hb.x - scale(80), player.hb.y);
        super.updateImpl();
        if (hb.hovered) {
            if (tips == null) {
                tips = DelayUse.getTooltips();
            }
            EUITooltip.queueTooltips(tips);
        }
        else {
            tips = null;
        }
    }
}
