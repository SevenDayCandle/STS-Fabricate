package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PCond_Limited extends PCond_Info
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_Limited.class, PField_Empty.class, SPECIAL_CONDITION_PRIORITY, 1, 1)
            .selfTarget();

    public PCond_Limited()
    {
        super(DATA);
    }

    public PCond_Limited(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean canActivate(PCLUseInfo info)
    {
        return info.canActivateLimited;
    }

    @Override
    public boolean tryActivate(PCLUseInfo info)
    {
        return info.tryActivateLimited();
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.limited.title;
    }
}
