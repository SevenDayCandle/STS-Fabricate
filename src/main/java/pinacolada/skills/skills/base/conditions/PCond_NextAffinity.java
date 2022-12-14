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

public class PCond_NextAffinity extends PCond
{
    public static final PSkillData DATA = register(PCond_NextAffinity.class, PCLEffectType.Affinity)
            .pclOnly()
            .selfTarget();

    public PCond_NextAffinity(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_NextAffinity()
    {
        super(DATA, PCLCardTarget.None, 0, new PCLAffinity[]{});
    }

    public PCond_NextAffinity(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 0, affinities);
    }

    public PCond_NextAffinity(int amount, PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities);
    }

    public PCond_NextAffinity(int amount, List<PCLAffinity> affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities.toArray(new PCLAffinity[]{}));
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return affinities.contains(CombatManager.playerSystem.getActiveMeter().get(Math.max(0, amount)));
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.currentAffinity.title;
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.objIs(
                        amount <= 0 ? PGR.core.tooltips.currentAffinity : PGR.core.tooltips.lastAffinity,
                affinities.isEmpty() ? TEXT.subjects.anyX(PGR.core.tooltips.affinityGeneral) : (getAffinityPowerOrString()));
    }
}
