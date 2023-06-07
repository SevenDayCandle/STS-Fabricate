package pinacolada.skills.skills;

import pinacolada.annotations.VisibleSkill;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public abstract class PShift extends PPrimary<PField_Empty> {

    public PShift(PSkillData<PField_Empty> data) {
        super(data);
    }

    public PShift(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }
}
