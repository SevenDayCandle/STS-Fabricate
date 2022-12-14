package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class PCLCardRewardInfo extends EUIBase
{
    public final EUIToggle upgradeToggle;
    public final EUIToggle zoomToggle;
    public final EUIToggle simplifyCardUIToggle;

    public PCLCardRewardInfo()
    {
        upgradeToggle = new EUIToggle(new EUIHitbox(scale(256), scale(48f)))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(screenW(0.9f), screenH(0.65f))
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);

        zoomToggle = new EUIToggle(new EUIHitbox(scale(256), scale(48f)))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(screenW(0.9f), upgradeToggle.hb.y - upgradeToggle.hb.height)
                .setText(PGR.core.strings.misc.dynamicPortraits)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setOnToggle(this::toggleCardZoom);

        simplifyCardUIToggle = new EUIToggle(new EUIHitbox(scale(256), scale(48f)))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(screenW(0.9f), zoomToggle.hb.y - zoomToggle.hb.height)
                .setText(PGR.core.strings.misc.simplifyCardUI)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.475f)
                .setOnToggle(this::toggleSimplifyCardUI);

        zoomToggle.setToggle(PGR.core.config.cropCardImages.get());
        simplifyCardUIToggle.setToggle(PGR.core.config.simplifyCardUI.get());
    }

    public void close()
    {
        isActive = false;
        upgradeToggle.toggle(false);
    }

    public void open()
    {
        isActive = GameUtilities.isPCLPlayerClass();
        upgradeToggle.toggle(false);
    }

    private void toggleCardZoom(boolean value)
    {
        PGR.core.config.cropCardImages.set(value, true);
        zoomToggle.setToggle(PGR.core.config.cropCardImages.get());
    }

    private void toggleSimplifyCardUI(boolean value)
    {
        PGR.core.config.simplifyCardUI.set(value, true);
        simplifyCardUIToggle.setToggle(PGR.core.config.simplifyCardUI.get());
    }

    private void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    @Override
    public void updateImpl()
    {
        upgradeToggle.updateImpl();
        zoomToggle.updateImpl();
        simplifyCardUIToggle.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        upgradeToggle.renderImpl(sb);
        zoomToggle.renderImpl(sb);
        simplifyCardUIToggle.renderImpl(sb);
    }
}
