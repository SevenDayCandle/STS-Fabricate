package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.utilities.GameUtilities;

public class PCLCardRewardInfo extends EUIBase {
    public final EUIToggle upgradeToggle;

    public PCLCardRewardInfo() {
        upgradeToggle = new EUIToggle(new EUIHitbox(scale(256), scale(48f)))
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
                .setPosition(screenW(0.9f), screenH(0.65f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);
    }

    public void close() {
        isActive = false;
        upgradeToggle.toggle(false);
    }

    public void open() {
        isActive = GameUtilities.isPCLPlayerClass();
        upgradeToggle.toggle(false);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        upgradeToggle.renderImpl(sb);
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    @Override
    public void updateImpl() {
        upgradeToggle.updateImpl();
    }
}
