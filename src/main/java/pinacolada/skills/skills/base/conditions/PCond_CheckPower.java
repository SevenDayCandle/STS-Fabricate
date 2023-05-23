package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PCond_CheckPower extends PPassiveCond<PField_Power> implements OnApplyPowerSubscriber {
    public static final PSkillData<PField_Power> DATA = register(PCond_CheckPower.class, PField_Power.class);

    public PCond_CheckPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckPower() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, PCLPowerHelper... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    public PCond_CheckPower(PCLCardTarget target, int amount, List<PCLPowerHelper> powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return ((fields.powers.isEmpty() ?
                evaluateTargets(info, t -> amount == 0 ? (t.powers == null || !EUIUtils.any(t.powers, po -> po.type == targetType)) : t.powers != null && EUIUtils.any(t.powers, po -> po.type == targetType && po.amount >= amount)) :
                evaluateTargets(info, t -> fields.debuff ? EUIUtils.any(fields.powers, po -> checkPowers(po, t)) : EUIUtils.all(fields.powers, po -> checkPowers(po, t)))));
    }

    // fields.debuff is treated as a "not" condition if the amount is above 0
    private boolean checkPowers(PCLPowerHelper po, AbstractCreature t) {
        return amount == 0 ? GameUtilities.getPowerAmount(t, po.ID) == amount :
                fields.debuff == (GameUtilities.getPowerAmount(t, po.ID) < amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(TEXT.act_gain(TEXT.cedit_powers)) : EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.cedit_powers);
    }

    @Override
    public String getSubText() {
        String baseString = fields.getThresholdString(fields.getPowerSubjectString());
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_gain(baseString));
        }

        return getTargetHasString(baseString);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature t, AbstractCreature source) {
        // For single target powers, the power target needs to match the owner of this skill
        if (fields.powers.isEmpty() ? power.type == (fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF)
                : (fields.getPowerFilter().invoke(power) && (target.targetsSingle() ? t == getOwnerCreature() : target.getTargets(source, t).contains(t)))) {
            useFromTrigger(makeInfo(t).setData(power));
        }
    }
}
