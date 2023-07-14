package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.powers.ApplyOrReducePowerAction;
import pinacolada.actions.utility.SequentialAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_PayPower extends PActiveCond<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PCond_PayPower.class, PField_Power.class)
            .selfTarget();

    public PCond_PayPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayPower() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayPower(int amount, PCLPowerHelper... powers) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        for (PCLPowerHelper power : fields.powers) {
            if (GameUtilities.getPowerAmount(power.ID) < amount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_pay(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText() {
        return capital(TEXT.act_pay(getAmountRawString(), fields.powers.isEmpty()
                ? plural(PGR.core.tooltips.debuff) :
                fields.getPowerAndString()), true);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        AbstractCreature sourceCreature = getSourceCreature();
        return order.callback(new SequentialAction(EUIUtils.map(fields.powers, power -> new ApplyOrReducePowerAction(sourceCreature, sourceCreature, power, -amount))), () -> {
            if (conditionMetCache) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
