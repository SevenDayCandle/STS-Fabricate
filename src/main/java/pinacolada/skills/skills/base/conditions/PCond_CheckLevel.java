package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PCond_CheckLevel extends PCond
{
    public static final PSkillData DATA = register(PCond_CheckLevel.class, PCLEffectType.Affinity)
            .pclOnly()
            .selfTarget();

    public PCond_CheckLevel(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_CheckLevel()
    {
        super(DATA, PCLCardTarget.None, 1, new PCLAffinity[]{});
    }

    public PCond_CheckLevel(int amount, PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities);
    }

    public PCond_CheckLevel(int amount, List<PCLAffinity> affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities.toArray(new PCLAffinity[]{}));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (affinities.isEmpty())
        {
            return alt ^ CombatManager.playerSystem.getLevel(PCLAffinity.General) >= amount;
        }
        else
        {
            for (PCLAffinity affinity : affinities)
            {
                if (CombatManager.playerSystem.getLevel(affinity) < amount)
                {
                    return alt;
                }
            }
        }
        return !alt;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.levelItem("X", PGR.core.tooltips.affinityGeneral.title);
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.levelItem(amount, alt ? getAffinityLevelOrString() : getAffinityLevelAndString());
    }
}
