package pinacolada.skills.skills;

import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.primary.PShift_OnObtain;
import pinacolada.skills.skills.base.primary.PShift_OnRemove;
import pinacolada.ui.editor.PCLCustomEffectPage;

public abstract class PShift extends PPrimary<PField_Empty> {

    public PShift(PSkillData<PField_Empty> data) {
        super(data);
    }

    public PShift(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    public static PShift_OnObtain obtain() {
        return new PShift_OnObtain();
    }

    public static PShift_OnRemove remove() {
        return new PShift_OnRemove();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.cond_when(getDelegateText());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return getCapitalSubText(perspective, requestor, addPeriod) + (childEffect != null ? (": " + childEffect.getText(perspective, requestor, addPeriod)) : "");
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill, PCLCustomEffectPage editor) {
        return skill instanceof PMultiBase ||
                skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof OutOfCombatMove;
    }

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }

    // Should not activate effects when played normally in battle
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
    }

    public abstract String getDelegateText();
}
