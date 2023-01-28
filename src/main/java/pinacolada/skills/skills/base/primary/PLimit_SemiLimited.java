package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PLimit;

@VisibleSkill
public class PLimit_SemiLimited extends PLimit
{

    public static final PSkillData<PField_Empty> DATA = register(PLimit_SemiLimited.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PLimit_SemiLimited()
    {
        super(DATA);
    }

    public PLimit_SemiLimited(PSkillSaveData content)
    {
        super(DATA, content);
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
