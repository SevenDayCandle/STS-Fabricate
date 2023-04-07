package pinacolada.skills.skills;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Conditions that check properties of a particular entity, for use in making "passive" boosts that affect only entities passing a certain filter
// e.g. making a check that increases orb strength for Lightning Orbs only
public abstract class PFacetCond<T extends PField> extends PCond<T>
{
    public PFacetCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PFacetCond(PSkillData<T> data)
    {
        super(data);
    }

    public PFacetCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PFacetCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return isWhenClause() ? TEXT.act_objectHas(getSubText(), childEffect != null ? childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod)) : super.getText(addPeriod);
    }
}
