package pinacolada.skills.skills.base.modifiers;

import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PActiveMod;

public abstract class PMod_ChangeGroup extends PActiveMod<PField_CardGeneric> {

    public PMod_ChangeGroup(PSkillData<PField_CardGeneric> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_ChangeGroup(PSkillData<PField_CardGeneric> data) {
        super(data, PCLCardTarget.None, 1);
    }

    public PMod_ChangeGroup(PSkillData<PField_CardGeneric> data, PCLCardGroupHelper... groups) {
        super(data, PCLCardTarget.None, 1);
        fields.setCardGroup(groups);
    }

    public String getConditionText() {
        return getConditionSampleText();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_bonusIf(TEXT.subjects_from(TEXT.subjects_x), getConditionSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.cond_bonusIf(fields.getGroupString(), getConditionText());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return TEXT.cond_xConditional(childEffect != null ? capital(childEffect.getText(perspective, requestor, false), addPeriod) : "", getSubText(perspective, requestor)) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, () -> childEffect.use(info, PCLActions.bottom));
        }
    }

    // TODO refactor
    protected void useImpl(PCLUseInfo info, ActionT0 callback) {
        if (meetsCondition(info)) {
            if (this.childEffect instanceof PMultiBase) {
                for (PSkill<?> ce : ((PMultiBase<?>) this.childEffect).getSubEffects()) {
                    if (ce.fields instanceof PField_CardGeneric) {
                        ((PField_CardGeneric) ce.fields).setTemporaryGroups(fields.groupTypes);
                    }
                }
                callback.invoke();
                for (PSkill<?> ce : ((PMultiBase<?>) this.childEffect).getSubEffects()) {
                    if (ce.fields instanceof PField_CardGeneric) {
                        ((PField_CardGeneric) ce.fields).resetTemporaryGroups();
                    }
                }
            }
            else if (this.childEffect != null && this.childEffect.fields instanceof PField_CardGeneric) {
                ((PField_CardGeneric) this.childEffect.fields).setTemporaryGroups(fields.groupTypes);
                callback.invoke();
                ((PField_CardGeneric) this.childEffect.fields).resetTemporaryGroups();
            }
        }
    }

    public abstract String getConditionSampleText();

    public abstract boolean meetsCondition(PCLUseInfo info);
}
