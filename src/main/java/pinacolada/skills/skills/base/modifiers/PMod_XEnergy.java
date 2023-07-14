package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.utilities.GameUtilities;

// While paying X energy is active, the effect can be determined before you actually pay the power
@VisibleSkill
public class PMod_XEnergy extends PPassiveMod<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMod_XEnergy.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();

    public PMod_XEnergy(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_XEnergy() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMod_XEnergy(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.energy.title);
    }

    @Override
    public String wrapAmountChild(PSkill<?> source, String input) {
        // Only apply X modifier text if the source is the skill that is actually being modified by this modifier
        if (isSkillAffected(source)) {
            // If the value is not parseable, don't remove the numbers
            int value = EUIUtils.parseInt(input, 2);
            if (value == 1) {
                input = GameUtilities.EMPTY_STRING;
            }
            input = this.amount > 0 ? input + TEXT.subjects_x + "+" + this.amount : input + TEXT.subjects_x;
        }
        return parent != null ? parent.wrapAmountChild(source, input) : (input);
    }

    @Override
    public String getSubText() {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public String getText(boolean addPeriod) {
        return childEffect != null ? childEffect.getText(addPeriod) : GameUtilities.EMPTY_STRING;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> {
            if (this.childEffect != null) {
                updateChildAmount(info);
                GameUtilities.useXCostEnergy(sourceCard);
                this.childEffect.use(info, order);
            }
        });
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        order.callback(() -> {
            if (this.childEffect != null) {
                updateChildAmount(info);
                GameUtilities.useXCostEnergy(sourceCard);
                this.childEffect.use(info, order, shouldPay);
            }
        });
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info) {
        return be.baseAmount * GameUtilities.getXCostEnergy(sourceCard, false) + this.amount;
    }
}
