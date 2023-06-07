package pinacolada.skills.skills;

import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.*;
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
        return getCapitalSubText(addPeriod) + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : "");
    }

    @Override
    public String getSubText() {
        return TEXT.cond_onGeneric(getDelegateText());
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof OutOfCombatMove;
    }

    public abstract String getDelegateText();
}
