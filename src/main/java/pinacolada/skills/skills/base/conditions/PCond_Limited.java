package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_Limited extends PCond_Info
{
    public static final PSkillData DATA = register(PCond_Limited.class, PCLEffectType.General, SPECIAL_CONDITION_PRIORITY, 1, 1)
            .selfTarget();

    public PCond_Limited()
    {
        super(DATA);
    }

    public PCond_Limited(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Limited(PSkill effect)
    {
        super(DATA, effect);
    }

    public PCond_Limited(PSkill... effect)
    {
        super(DATA, effect);
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
