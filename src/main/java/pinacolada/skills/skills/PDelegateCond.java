package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Conditions that trigger on specific game events
public abstract class PDelegateCond<T extends PField> extends PCond<T> {
    public PDelegateCond(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PDelegateCond(PSkillData<T> data) {
        super(data);
    }

    public PDelegateCond(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PDelegateCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return triggerSource == this;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        if (childEffect != null) {
            if (childEffect instanceof PCond && !isWhenClause()) {
                return PCLCoreStrings.joinWithAnd(getCapitalSubText(perspective, requestor, addPeriod), childEffect.getText(perspective, requestor, false)) + PCLCoreStrings.period(addPeriod);
            }
            return getCapitalSubText(perspective, requestor, addPeriod) + COMMA_SEPARATOR + childEffect.getText(perspective, requestor, false) + PCLCoreStrings.period(addPeriod);
        }
        return getCapitalSubText(perspective, requestor, addPeriod) + PCLCoreStrings.period(addPeriod);
    }
}
