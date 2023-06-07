package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PShift;

@VisibleSkill
public class PShift_OnRemove extends PShift {
    public static final PSkillData<PField_Empty> DATA = register(PShift_OnRemove.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PShift_OnRemove() {
        super(DATA);
    }

    public PShift_OnRemove(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubText() {
        return TEXT.cond_onGeneric(PGR.core.tooltips.remove.title) + ": ";
    }

    @Override
    public void triggerOnRemoval() {
        useOutsideOfBattle();
    }
}
