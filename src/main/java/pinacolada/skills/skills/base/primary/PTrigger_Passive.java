package pinacolada.skills.skills.base.primary;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.interfaces.markers.PSkillAttribute;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_Not;

public class PTrigger_Passive extends PTrigger implements PSkillAttribute
{

    public static final PSkillData<PField_Not> DATA = register(PTrigger_Passive.class, PField_Not.class, -1, DEFAULT_MAX);

    public PTrigger_Passive()
    {
        super(DATA);
    }

    public PTrigger_Passive(PSkillSaveData content)
    {
        super(content);
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
        return TEXT.conditions.modifyCards();
    }
}
