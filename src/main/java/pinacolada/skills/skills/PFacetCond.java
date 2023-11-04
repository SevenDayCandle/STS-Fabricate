package pinacolada.skills.skills;

import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Conditions that check properties of a particular entity, for use in making "passive" boosts that affect only entities passing a certain filter
// e.g. making a check that increases orb strength for Lightning Orbs only
public abstract class PFacetCond<T extends PField> extends PCond<T> {
    public PFacetCond(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PFacetCond(PSkillData<T> data) {
        super(data);
    }

    public PFacetCond(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PFacetCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    // Child effects use this skill's subtext directly in their description when used in powers
    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        if ((isWhenClause() || isPassiveClause()) && childEffect != null) {
            return childEffect.getText(perspective, requestor, addPeriod);
        }
        return super.getText(perspective, requestor, addPeriod);
    }

    // TODO in the use condition, have the effect pass for "each" of the items that pass
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        super.use(info, order);
    }
}
