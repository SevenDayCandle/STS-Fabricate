package pinacolada.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.PCLTopPanelItem;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.FakeFtue;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.ui.cardView.PCLAugmentList;

import java.util.HashMap;

public class PCLAugmentPanelItem extends PCLTopPanelItem
{
    public static final String ID = createFullID(PCLAugmentPanelItem.class);
    protected Color currentColor = Color.WHITE;
    protected float lerpAmount = 1;

    public PCLAugmentPanelItem()
    {
        super(PGR.core.images.augmentPanel, ID);
        this.setTooltip(new EUITooltip(PGR.core.strings.misc.viewAugments, EUIUtils.format(PGR.core.strings.misc.viewAugmentsDescription, "")));
    }

    protected HashMap<PCLAugmentData, Integer> getAugmentData()
    {
        HashMap<PCLAugmentData, Integer> map = new HashMap<>();
        for (String key : PGR.core.dungeon.augments.keySet())
        {
            map.put(PCLAugment.get(key), PGR.core.dungeon.augments.get(key));
        }
        return map;
    }

    protected void onClick()
    {
        super.onClick();
        if (PGR.core.dungeon.getAugmentTotal() > 0)
        {
            PGR.core.augmentScreen.open(this::getAugmentData, PCLAugmentList.DEFAULT,true);
        }
    }

    protected void onRightClick()
    {
        super.onRightClick();
        this.getHitbox().unhover();
        EUITutorial tutorial = new EUITutorial(new EUIHitbox((float) Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 450.0F, 1350.0F, 900.0F), EUIRM.images.panelLarge.texture(),
                PGR.core.strings.misc.viewAugments, EUIUtils.list(PGR.core.strings.tutorial.augmentTutorial1, PGR.core.strings.tutorial.augmentTutorial2));
        AbstractDungeon.ftue = new FakeFtue(tutorial);
    }

    public void flash()
    {
        currentColor = Settings.GREEN_TEXT_COLOR;
        lerpAmount = 0;
    }

    public void update()
    {
        super.update();
        if (this.tooltip != null && this.getHitbox().hovered)
        {
            this.tooltip.setText(
                    PGR.core.strings.misc.viewAugments + " (" + PCLHotkeys.viewAugmentScreen.getKeyString() + ")",
                    EUIUtils.format(PGR.core.strings.misc.viewAugmentsDescription, PGR.core.dungeon.getAugmentTotal() == 0 ? PGR.core.strings.misc.viewAugmentsNone : "")
            );
            EUITooltip.queueTooltip(this.tooltip);
        }
        if (PCLHotkeys.viewAugmentScreen.isJustPressed() && EUI.currentScreen != EUI.cardsScreen)
        {
            onClick();
        }
        if (lerpAmount < 1)
        {
            currentColor = EUIColors.lerp(Settings.GREEN_TEXT_COLOR, Color.WHITE, lerpAmount);
            lerpAmount += Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        super.render(sb);
        EUIRenderHelpers.writeCentered(sb, EUIFontHelper.cardtitlefontNormal, String.valueOf(PGR.core.dungeon.getAugmentTotal()), this.x + (this.hb_w * 0.75f), this.y + 16f * Settings.scale, currentColor);
    }
}
