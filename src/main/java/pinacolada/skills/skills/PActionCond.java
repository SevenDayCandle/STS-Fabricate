package pinacolada.skills.skills;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PActionCond<T extends PField> extends PCond<T>
{
    public PActionCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PActionCond(PSkillData<T> data)
    {
        super(data);
    }

    public PActionCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    // Use check is handled in use action
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return false;
    }
}
