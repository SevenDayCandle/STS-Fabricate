package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLAugmentListItem extends EUIHoverable {

    public final ActionT1<PCLAugment> panel;
    public final PCLAugmentRenderable augment;
    public final EUILabel ownerText;
    public final EUILabel title;

    public PCLAugmentListItem(ActionT1<PCLAugment> panel, PCLAugment augment) {
        this(panel, augment, 3.5f);
    }

    public PCLAugmentListItem(ActionT1<PCLAugment> panel, PCLAugment augment, float xOffsetPercentage) {
        super(new EUIHitbox(0, 0, AbstractRelic.PAD_X, AbstractRelic.PAD_X));
        this.augment = new PCLAugmentRenderable(augment, augment.getTooltip(), hb);
        this.panel = panel;
        title = new EUILabel(EUIFontHelper.cardTitleFontLarge, new RelativeHitbox(hb, scale(360), scale(360), hb.width * xOffsetPercentage, hb.height * 0.7f))
                .setFontScale(0.7f)
                .setLabel(augment.getName())
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.01f);
        ownerText = new
                EUILabel(EUIFontHelper.cardTitleFontSmall, RelativeHitbox.fromPercentages(hb, 1, 1, xOffsetPercentage, 0f))
                .setAlignment(0.5f, 0.5f)
                .setFontScale(0.8f)
                .setColor(Settings.BLUE_TEXT_COLOR);
        if (augment.card != null) {
            ownerText.setLabel(augment.card.name);
        }
    }

    public String getAmountString(float amount) {
        return PCLRenderHelpers.decimalFormat(amount) + "%";
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        ownerText.renderImpl(sb);
        title.renderImpl(sb);
        augment.render(sb);
    }

    @Override
    public void updateImpl() {
        augment.update();
        if (augment.hb.hovered)  {
            EUITooltip.queueTooltips(augment.getTips());
            if (EUIInputManager.leftClick.isJustPressed()) {
                augment.hb.unhover();
                panel.invoke(augment.item);
            }
        }
        ownerText.updateImpl();
        title.updateImpl();
    }
}
