package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PCond_SemiLimited extends PCond_Info
{

    public static final PSkillData<PField_Empty> DATA = register(PCond_SemiLimited.class, PField_Empty.class, SPECIAL_CONDITION_PRIORITY, 1, 1)
            .selfTarget();

    public PCond_SemiLimited()
    {
        super(DATA);
    }

    public PCond_SemiLimited(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean canActivate(PCLUseInfo info)
    {
        return info.canActivateSemiLimited;
    }

    @Override
    public boolean tryActivate(PCLUseInfo info)
    {
        return info.tryActivateSemiLimited();
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.semiLimited.title;
    }
}
