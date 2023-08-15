package pinacolada.skills.skills;

import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
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

    // Qualifiers are dependent on the outcome of the action
    @Override
    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    @Override
    public ArrayList<Integer> getQualifiers(PCLUseInfo info, boolean conditionPassed) {
        return EUIUtils.arrayList(conditionPassed ? 0 : 1);
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return getCapitalSubText(perspective, addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(perspective, addPeriod)) : "");
    }
}
