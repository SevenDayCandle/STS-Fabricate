package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

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

    @Override
    public String getText(boolean addPeriod) {
        return getCapitalSubText(addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : "");
    }

    // Actual use check is handled in use action. This passes to allow the use effect to run
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return true;
    }
}
