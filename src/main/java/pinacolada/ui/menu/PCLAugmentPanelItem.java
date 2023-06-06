package pinacolada.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITutorial;
import extendedui.ui.controls.EUITutorialPage;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.PCLTopPanelItem;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugmentData;
import pinacolada.resources.PCLHotkeys;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.cardView.PCLAugmentList;

import java.util.HashMap;

public class PCLAugmentPanelItem extends PCLTopPanelItem {
    public static final String ID = createFullID(PCLAugmentPanelItem.class);
    protected Color currentColor = Color.WHITE;
    protected float lerpAmount = 1;

    public PCLAugmentPanelItem() {
        super(PCLCoreImages.Menu.augmentPanel, ID);
        this.setTooltip(new EUITooltip(PGR.core.strings.misc_viewAugments, EUIUtils.format(PGR.core.strings.misc_viewAugmentsDescription, "")));
    }

    public void flash() {
        currentColor = Settings.GREEN_TEXT_COLOR;
        lerpAmount = 0;
    }

    protected HashMap<PCLAugmentData, Integer> getAugmentData() {
        HashMap<PCLAugmentData, Integer> map = new HashMap<>();
        for (String key : PGR.dungeon.augments.keySet()) {
            map.put(PCLAugmentData.get(key), PGR.dungeon.augments.get(key));
        }
        return map;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        FontHelper.renderFontCentered(sb, EUIFontHelper.cardTitleFontNormal, String.valueOf(PGR.dungeon.getAugmentTotal()), this.x + (this.hb_w * 0.75f), this.y + 16f * Settings.scale, currentColor);
    }

    public void update() {
        super.update();
        if (this.tooltip != null && this.getHitbox().hovered) {
            this.tooltip.setText(
                    PGR.core.strings.misc_viewAugments + " (" + PCLHotkeys.viewAugmentScreen.getKeyString() + ")",
                    EUIUtils.format(PGR.core.strings.misc_viewAugmentsDescription, PGR.dungeon.getAugmentTotal() == 0 ? PGR.core.strings.misc_viewAugmentsNone : "")
            );
            EUITooltip.queueTooltip(this.tooltip);
        }
        if (PCLHotkeys.viewAugmentScreen.isJustPressed() && AbstractDungeon.screen != PCLAugmentScreen.AUGMENT_SCREEN) {
            onClick();
        }
        if (lerpAmount < 1) {
            currentColor = EUIColors.lerp(Settings.GREEN_TEXT_COLOR, Color.WHITE, lerpAmount);
            lerpAmount += Gdx.graphics.getDeltaTime();
        }
    }

    protected void onClick() {
        super.onClick();
        if (PGR.dungeon.getAugmentTotal() > 0) {
            PGR.augmentScreen.open(this::getAugmentData, PCLAugmentList.DEFAULT, true);
        }
    }

    protected void onRightClick() {
        super.onRightClick();
        this.getHitbox().unhover();
        EUITutorial tutorial = new EUITutorial(new EUIHitbox((float) Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 450.0F, 1350.0F, 900.0F), EUIRM.images.greySquare.texture(),
                new EUITutorialPage(PGR.core.strings.misc_viewAugments, PGR.core.strings.tutorial_augmentTutorial1), new EUITutorialPage(PGR.core.strings.misc_viewAugments, PGR.core.strings.tutorial_augmentTutorial2));
        EUI.ftueScreen.openScreen(tutorial);
    }
}
