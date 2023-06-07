package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PShift;

@VisibleSkill
public class PShift_OnObtain extends PShift {
    public static final PSkillData<PField_Empty> DATA = register(PShift_OnObtain.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PShift_OnObtain() {
        super(DATA);
    }

    public PShift_OnObtain(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getDelegateText() {
        return PGR.core.tooltips.obtain.title;
    }

    @Override
    public void triggerOnObtain() {
        useOutsideOfBattle();
    }
}
