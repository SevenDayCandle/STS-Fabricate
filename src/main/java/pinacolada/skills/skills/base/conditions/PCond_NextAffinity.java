package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;

public class PCond_NextAffinity extends PCond<PField_Affinity>
{
    public static final PSkillData<PField_Affinity> DATA = register(PCond_NextAffinity.class, PField_Affinity.class)
            .pclOnly()
            .selfTarget();

    public PCond_NextAffinity(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_NextAffinity()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_NextAffinity(PCLAffinity... affinities)
    {
        this(0, affinities);
    }

    public PCond_NextAffinity(int amount, PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setAffinity(affinities);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fields.affinities.contains(CombatManager.playerSystem.getActiveMeter().get(Math.max(0, amount)));
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
                fields.affinities.isEmpty() ? TEXT.subjects.anyX(PGR.core.tooltips.affinityGeneral) : (fields.getAffinityPowerOrString()));
    }
}
