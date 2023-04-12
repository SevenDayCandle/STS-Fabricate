package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Passive cond whose text should not be highlighted
public abstract class PPassiveNonCheckCond<T extends PField> extends PPassiveCond<T>
{
    public PPassiveNonCheckCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PPassiveNonCheckCond(PSkillData<T> data)
    {
        super(data);
    }

    public PPassiveNonCheckCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PPassiveNonCheckCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getCapitalSubText(addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : "");
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        return triggerSource != null;
    }
}
