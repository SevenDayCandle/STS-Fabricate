package pinacolada.skills.skills.base.conditions;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PSkillAttribute;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;

import java.util.List;

public class PCond_IfHasAffinity extends PCond implements PSkillAttribute
{
    public static final PSkillData DATA = register(PCond_IfHasAffinity.class, PCLEffectType.CardGroupFull)
            .selfTarget();

    public PCond_IfHasAffinity(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_IfHasAffinity()
    {
        super(DATA, PCLCardTarget.None, 0, new PCLAffinity[]{});
    }

    public PCond_IfHasAffinity(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 0, affinities);
    }

    public PCond_IfHasAffinity(List<PCLAffinity> affinities)
    {
        super(DATA, PCLCardTarget.None, 0, affinities.toArray(new PCLAffinity[]{}));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifTargetHas("X", "Y");
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? getFullCardAndString(EFFECT_CHAR) :
                TEXT.conditions.ifTargetHas(TEXT.subjects.thisObj, getAffinityOrString());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return hasParentType(PTrigger.class) ? TEXT.actions.objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info != null)
        {
            return getFullCardFilter().invoke(info.card);
        }
        return false;
    }
}
