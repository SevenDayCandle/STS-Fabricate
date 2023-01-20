package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PCond_IfHasAffinity extends PCond<PField_CardCategory>
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
        return TEXT.conditions.ifTargetHas(TEXT.subjects.x, TEXT.subjects.x);
    }

    @Override
    public String getSubText()
    {
        return hasParentType(PTrigger.class) ? fields.getFullCardAndString() :
                TEXT.conditions.ifTargetHas(TEXT.subjects.thisObj, PField.getAffinityOrString(fields.affinities));
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
            return fields.getFullCardFilter().invoke(info.card);
        }
        return false;
    }
}
