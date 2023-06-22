package pinacolada.skills.skills;

import pinacolada.actions.PCLActions;
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

    public static PShift_OnObtain obtain() {
        return new PShift_OnObtain();
    }

    public static PShift_OnRemove remove() {
        return new PShift_OnRemove();
    }

    public PShift(PSkillData<PField_Empty> data) {
        super(data);
    }

    public PShift(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    @Override
    public String getText(boolean addPeriod) {
        return getCapitalSubText(addPeriod) + (childEffect != null ? (": " + childEffect.getText(addPeriod)) : "");
    }

    @Override
    public String getSubText() {
        return TEXT.cond_whenSingle(getDelegateText());
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PMultiBase ||
                skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof OutOfCombatMove;
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
