package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLAugmentListItem extends EUIHoverable {

    public final ActionT1<PCLAugment> panel;
    public final PCLAugmentRenderable augment;
    public final EUILabel amountText;
    public final EUILabel title;
    public float amount;

    public PCLAugmentListItem(ActionT1<PCLAugment> panel, PCLAugmentData augment, float amount) {
        this(panel, augment, amount, 7f, 3.5f);
    }

    public PCLAugmentListItem(ActionT1<PCLAugment> panel, PCLAugmentData augment, float amount, float amountOffset, float titleOffset) {
        super(new EUIHitbox(0, 0, AbstractRelic.PAD_X, AbstractRelic.PAD_X));
        this.augment = new PCLAugmentRenderable(augment, augment.getTooltip(), hb);
        this.panel = panel;
        title = new EUILabel(FontHelper.cardTitleFont, new RelativeHitbox(hb, scale(360), scale(360), hb.width * titleOffset, hb.height * 0.7f))
                .setFontScale(0.85f)
                .setLabel(augment.strings.NAME)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.01f);
        amountText = new
                EUILabel(FontHelper.cardTitleFont, RelativeHitbox.fromPercentages(hb, 1, 1, amountOffset, 0.7f))
                .setAlignment(0.5f, 0.5f)
                .setLabel(getAmountString(amount))
                .setFontScale(0.75f);
    }

    public String getAmountString(float amount) {
        return PCLRenderHelpers.decimalFormat(amount) + "%";
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        amountText.renderImpl(sb);
        title.renderImpl(sb);
        augment.render(sb);
    }

    @Override
    public void updateImpl() {
        augment.update();
        if (augment.hb.hovered && EUIInputManager.leftClick.isJustPressed()) {
            augment.hb.unhover();
            panel.invoke(augment.item.create());
        }
        amountText.updateImpl();
        title.updateImpl();
    }
}
