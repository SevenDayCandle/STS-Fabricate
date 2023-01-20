package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;

@VisibleSkill
public class PCond_CheckLevel extends PCond<PField_Affinity>
{
    public static final PSkillData<PField_Affinity> DATA = register(PCond_CheckLevel.class, PField_Affinity.class)
            .pclOnly()
            .selfTarget();

    public PCond_CheckLevel(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_CheckLevel(int amount, PCLAffinity... stance)
    {
        super(DATA, PCLCardTarget.None, amount);
        fields.setAffinity(stance);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (fields.affinities.isEmpty())
        {
            return fields.random ^ CombatManager.playerSystem.getLevel(PCLAffinity.General) >= amount;
        }
        else
        {
            for (PCLAffinity affinity : fields.affinities)
            {
                if (CombatManager.playerSystem.getLevel(affinity) < amount)
                {
                    return fields.random;
                }
            }
        }
        return !fields.random;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.levelItem(TEXT.subjects.x, PGR.core.tooltips.affinityGeneral.title);
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.levelItem(amount, fields.getAffinityChoiceString());
    }
}
