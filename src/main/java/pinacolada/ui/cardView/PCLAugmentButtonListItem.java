package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLAugmentButtonListItem extends PCLAugmentListItem {

    public final EUIImage background;

    public PCLAugmentButtonListItem(ActionT1<PCLAugment> panel, ActionT1<PCLAugment> rClick, PCLAugment augment) {
        super(panel, rClick, augment, 4f);
        background = new EUIImage(ImageMaster.REWARD_SCREEN_ITEM, RelativeHitbox.fromPercentages(hb, 5, 1.12f, 2.2f, 0.5f))
                .setBackgroundTexture(ImageMaster.REWARD_SCREEN_ITEM, new Color(0.2f, 0.4f, 0.4f, 1f), 1.05f)
                .setColor(new Color(0.45f, 0.6f, 0.6f, 1f));
    }

    public String getAmountString(float amount) {
        return PCLRenderHelpers.decimalFormat(amount) + "x";
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        background.renderImpl(sb);
        super.renderImpl(sb);
    }

    @Override
    public void updateImpl() {
        background.updateImpl();
        augment.update();
        if (background.hb.hovered || augment.hb.hovered) {
            EUITooltip.queueTooltips(augment.getTips());
            if (EUIInputManager.leftClick.isJustPressed()) {
                background.hb.unhover();
                augment.hb.unhover();
                onClick.invoke(augment.item);
            }
            else if (EUIInputManager.rightClick.isJustPressed()) {
                background.hb.unhover();
                augment.hb.unhover();
                onRightClick.invoke(augment.item);
            }
        }
        ownerText.updateImpl();
        title.updateImpl();
    }
}
