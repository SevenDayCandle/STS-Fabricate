package pinacolada.skills.skills;

import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField;

public abstract class PDamageTrait<T extends PField> extends PTrait<T>
{
    public PDamageTrait(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PDamageTrait(PSkillData<T> data)
    {
        super(data);
    }

    public PDamageTrait(PSkillData<T> data, int amount)
    {
        super(data, amount);
    }
}
