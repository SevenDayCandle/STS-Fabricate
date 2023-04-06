package pinacolada.skills.skills.special.conditions;

import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PTrigger;

@Deprecated
public class PCond_IfHasAffinity extends PPassiveCond<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_IfHasAffinity.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_IfHasAffinity(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_IfHasAffinity()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_IfHasAffinity(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 0);
        fields.setAffinity(affinities);
    }

    @Override
    public String getSampleText()
    {
        return "";
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? fields.getFullCardAndString() :
                getTargetHasString(PField.getAffinityOrString(fields.affinities));
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return hasParentType(PTrigger.class) ? TEXT.act_objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        if (info != null)
        {
            return fields.getFullCardFilter().invoke(info.card);
        }
        return false;
    }
}
