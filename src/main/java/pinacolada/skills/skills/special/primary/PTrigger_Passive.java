package pinacolada.skills.skills.special.primary;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PTrigger;

// TODO make this visible again when this is fixed
public class PTrigger_Passive extends PTrigger
{
    public static final PSkillData<PField_Not> DATA = register(PTrigger_Passive.class, PField_Not.class, -1, DEFAULT_MAX);

    public PTrigger_Passive()
    {
        super(DATA);
    }

    public PTrigger_Passive(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrigger_Passive(int maxUses)
    {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    public PTrigger_Passive(PCLCardTarget target, int maxUses)
    {
        super(DATA, target, maxUses);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_passive();
    }
}
