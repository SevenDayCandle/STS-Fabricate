package pinacolada.skills.skills;

import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

import java.util.ArrayList;

// Active cond whose text should not be highlighted
public abstract class PActiveNonCheckCond<T extends PField> extends PActiveCond<T> {
    public PActiveNonCheckCond(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PActiveNonCheckCond(PSkillData<T> data) {
        super(data);
    }

    public PActiveNonCheckCond(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PActiveNonCheckCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    // Actual use check is handled in use action. This passes to allow the use effect to run
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return true;
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String condString = isWhenClause() ? getCapitalSubText(perspective, requestor, addPeriod) : getConditionRawString(perspective, requestor, addPeriod);
        if (childEffect != null) {
            if (childEffect instanceof PCond && !isWhenClause()) {
                return PCLCoreStrings.joinWithAnd(condString, childEffect.getText(perspective, requestor, false)) + PCLCoreStrings.period(addPeriod);
            }
            return condString + COMMA_SEPARATOR + childEffect.getText(perspective, requestor, false) + PCLCoreStrings.period(addPeriod);
        }
        return condString + PCLCoreStrings.period(addPeriod);
    }
}
