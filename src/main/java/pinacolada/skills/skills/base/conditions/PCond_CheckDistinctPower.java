package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckDistinctPower extends PPassiveCond<PField_Power> implements OnApplyPowerSubscriber {
    public static final PSkillData<PField_Power> DATA = register(PCond_CheckDistinctPower.class, PField_Power.class);

    public PCond_CheckDistinctPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckDistinctPower() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckDistinctPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        super(DATA, target, amount);
        fields.setPower(powers);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return ((fields.powers.isEmpty() ?
                evaluateTargets(info, t -> fields.doesValueMatchThreshold(EUIUtils.count(t.powers, po -> po.type == targetType))) :
                evaluateTargets(info, t -> fields.doesValueMatchThreshold(EUIUtils.count(t.powers, po -> EUIUtils.any(fields.powers, f -> checkPower(f, po.ID)))))));
    }

    private boolean checkPower(String helper, String powerID) {
        PCLPowerData data = PCLPowerData.getStaticDataOrCustom(helper);
        if (data != null) {
            return data.ifAny(d -> d.equals(powerID));
        }
        return powerID.equals(helper);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_distinct(TEXT.cedit_powers));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return getWheneverYouString(TEXT.act_applyXToTarget(fields.getThresholdRawString(fields.getPowerSubjectString()), getTargetSubjectStringPerspective(perspective)));
        }
        return getTargetHasStringPerspective(perspective, fields.getThresholdRawString(TEXT.subjects_distinct(fields.getPowerSubjectString())));
    }

    // When the owner APPLIES the specified power to the specified target, triggers the effect on that target
    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature t, AbstractCreature source) {
        AbstractCreature owner = getOwnerCreature();
        if (source == owner) {
            PCLUseInfo info = generateInfo(owner, t);
            boolean eval = evaluateTargets(info, c -> c == t);
            // For single target powers, the power target needs to match the owner of this skill
            if (fields.powers.isEmpty() ? power.type == (fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF)
                    : (fields.getPowerFilter().invoke(power) && eval)) {
                useFromTrigger(info.setData(power));
            }
        }
    }
}
