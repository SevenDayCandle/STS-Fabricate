package pinacolada.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.fields.PField;

public abstract class PPrimary<T extends PField> extends PSkill<T>
{
    public PPrimary(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PPrimary(PSkillData<T> data)
    {
        super(data);
    }

    public PPrimary(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PPrimary(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public boolean isCondAllowed(PCond<?> skill) {return true;}

    public boolean isModAllowed(PMod<?> skill) {return true;}

    public boolean isMoveAllowed(PMove<?> skill) {return true;}
}
