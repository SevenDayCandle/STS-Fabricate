package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Conds where the use check must happen in the use action
public abstract class PActiveCond<T extends PField> extends PCond<T>
{
    public PActiveCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PActiveCond(PSkillData<T> data)
    {
        super(data);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    // Use check is handled in use action
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return false;
    }
}
