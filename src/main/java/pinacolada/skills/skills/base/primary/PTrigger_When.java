package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrigger_When extends PTrigger {
    public static final PSkillData<PField_Not> DATA = register(PTrigger_When.class, PField_Not.class, -1, DEFAULT_MAX)
            .selfTarget();

    public PTrigger_When() {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return capital(TEXT.cond_whenSingle(TEXT.subjects_x), true);
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(skill instanceof PCond) || skill instanceof PCLCombatSubscriber;
    }
}
