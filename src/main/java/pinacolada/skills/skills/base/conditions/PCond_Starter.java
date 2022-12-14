package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_Starter extends PCond
{

    public static final PSkillData DATA = register(PCond_Starter.class, PCLEffectType.General, 1, 1)
            .selfTarget();

    public PCond_Starter()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Starter(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Starter(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_Starter(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return alt ^ info.isStarter;
    }

    @Override
    public String getSubText()
    {
        return alt ? TEXT.conditions.not(PGR.core.tooltips.starter.title) : PGR.core.tooltips.starter.title;
    }
}
