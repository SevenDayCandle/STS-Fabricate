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
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return getCapitalSubText(perspective, addPeriod) + (childEffect != null ? (": " + childEffect.getText(perspective, addPeriod)) : "");
    }

    // Should not activate effects when played normally in battle
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.cond_whenSingle(getDelegateText());
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PMultiBase ||
                skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof OutOfCombatMove;
    }

    public abstract String getDelegateText();
}
