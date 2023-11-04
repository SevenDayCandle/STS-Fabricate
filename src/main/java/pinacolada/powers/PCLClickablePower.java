package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLSFX;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLClickablePower extends PCLPower implements ClickableProvider {
    protected static final float BORDER_SIZE = 48f;
    public PCLClickableUse triggerCondition;
    public boolean clickable;

    public PCLClickablePower(PCLPowerData data, AbstractCreature owner, AbstractCreature source, int amount) {
        super(data, owner, source, amount);
    }

    @Override
    public void atStartOfTurn() {
        super.atStartOfTurn();

        if (triggerCondition != null) {
            triggerCondition.refresh(true, true);
        }
    }

    public PCLClickableUse createTrigger(ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        triggerCondition = new PCLClickableUse(this, onUse);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        triggerCondition = new PCLClickableUse(this, onUse, uses, refreshEachTurn, stackAutomatically);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, PCLTriggerUsePool pool) {
        triggerCondition = new PCLClickableUse(this, onUse, pool);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?> move) {
        triggerCondition = new PCLClickableUse(this, move);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?> move, int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        triggerCondition = new PCLClickableUse(this, move, uses, refreshEachTurn, stackAutomatically);
        return triggerCondition;
    }

    public PCLClickableUse createTrigger(PSkill<?> move, PCLTriggerUsePool pool) {
        triggerCondition = new PCLClickableUse(this, move, pool);
        return triggerCondition;
    }

    @Override
    protected Color getBorderColor(Color c) {
        return (enabled && triggerCondition != null && triggerCondition.interactable()) ? c : disabledColor;
    }

    @Override
    public PCLClickableUse getClickable() {
        return triggerCondition;
    }

    @Override
    protected void onSamePowerApplied(AbstractPower power) {
        if (triggerCondition != null && triggerCondition.pool.stackAutomatically) {
            triggerCondition.addUses(power instanceof PCLClickablePower && ((PCLClickablePower) power).triggerCondition != null ? ((PCLClickablePower) power).triggerCondition.getCurrentUses() : 1);
        }
    }

    @Override
    protected void renderIconsImpl(SpriteBatch sb, float x, float y, Color borderColor, Color imageColor) {
        float scale = 1;
        if (triggerCondition != null) {
            PCLRenderHelpers.drawCentered(sb, borderColor, PCLCoreImages.Menu.squaredbuttonEmptycenter.texture(), x, y, BORDER_SIZE, BORDER_SIZE, 1f, 0);
            scale = 0.75f;
        }

        if (this.region128 != null) {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.region128, x, y, ICON_SIZE, ICON_SIZE, scale, 0);
        }
        else {
            PCLRenderHelpers.drawCentered(sb, imageColor, this.img, x, y, ICON_SIZE, ICON_SIZE, scale, 0);
        }

        if (triggerCondition != null && enabled && hb.hovered && clickable) {
            PCLRenderHelpers.drawCentered(sb, EUIColors.white(0.3f), EUIRM.images.squaredButton.texture(), x, y, BORDER_SIZE, BORDER_SIZE, 1f, 0);
        }

    }

    @Override
    public void updateHitbox() {
        super.updateHitbox();
        if (triggerCondition != null) {
            triggerCondition.refresh(false, hb.justHovered);
        }
    }

    @Override
    public void updateHoverLogic() {
        clickable = triggerCondition != null && triggerCondition.interactable();
        if (clickable) {
            if (hb.justHovered) {
                PCLSFX.play(PCLSFX.UI_HOVER);
            }

            if (InputHelper.justClickedLeft) {
                hb.clickStarted = true;
                PCLSFX.play(PCLSFX.UI_CLICK_1);
            }
            else if (hb.clicked) {
                hb.clicked = false;
                triggerCondition.targetToUse(1);
            }
        }
    }

}
