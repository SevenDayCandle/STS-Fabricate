package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
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

import java.util.ArrayList;

@VisibleSkill
public class PCond_PayPower extends PActiveCond<PField_Power> {
    public static final PSkillData<PField_Power> DATA = register(PCond_PayPower.class, PField_Power.class);

    public PCond_PayPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_PayPower() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_PayPower(int amount, PCLPowerHelper... powers) {
        this(PCLCardTarget.None, amount, powers);
    }

    public PCond_PayPower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, t -> fields.debuff ? EUIUtils.any(fields.powers, po -> checkPowers(po, t)) : EUIUtils.all(fields.powers, po -> checkPowers(po, t)));
    }

    private boolean checkPowers(PCLPowerHelper po, AbstractCreature t) {
        return fields.doesValueMatchThreshold(GameUtilities.getPowerAmount(t, po.ID));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_pay(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String joinedString = fields.powers.isEmpty() ? TEXT.subjects_randomX(plural(fields.debuff ? PGR.core.tooltips.debuff : PGR.core.tooltips.buff)) : fields.getPowerAndString();
        return capital(target == PCLCardTarget.Self ? TEXT.act_pay(getAmountRawString(), joinedString) : TEXT.act_removeFrom(EUIRM.strings.numNoun(getAmountRawString(), joinedString), getTargetStringPerspective(perspective)), true);
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        AbstractCreature sourceCreature = getSourceCreature();
        ArrayList<AbstractGameAction> actions = EUIUtils.flattenList(EUIUtils.map(getTargetList(info), t ->
                EUIUtils.map(fields.powers, power -> new ApplyOrReducePowerAction(sourceCreature, t, power, -amount))));

        return order.callback(new SequentialAction(actions), () -> {
            if (conditionMetCache) {
                onComplete.invoke(info);
            }
            else {
                onFail.invoke(info);
            }
        });
    }
}
