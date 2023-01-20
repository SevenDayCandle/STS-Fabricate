package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PCond_Starter extends PCond<PField_Not>
{

    public static final PSkillData<PField_Not> DATA = register(PCond_Starter.class, PField_Not.class, 1, 1)
            .selfTarget();

    public PCond_Starter()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Starter(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fields.not ^ info.isStarter;
    }

    @Override
    public String getSubText()
    {
        return fields.not ? TEXT.conditions.not(PGR.core.tooltips.starter.title) : PGR.core.tooltips.starter.title;
    }
}
