package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

public abstract class PPassiveCond<T extends PField> extends PCond<T>
{
    public PPassiveCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PPassiveCond(PSkillData<T> data)
    {
        super(data);
    }

    public PPassiveCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PPassiveCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }
}
