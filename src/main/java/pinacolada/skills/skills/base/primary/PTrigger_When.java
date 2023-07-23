package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.subscribers.PCLCombatSubscriber;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrigger_When extends PTrigger {
    public static final PSkillData<PField_CardGeneric> DATA = register(PTrigger_When.class, PField_CardGeneric.class, -1, DEFAULT_MAX)
            .selfTarget();

    public PTrigger_When() {
        super(DATA);
    }

    public PTrigger_When(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return capital(TEXT.cond_whenSingle(TEXT.subjects_x), true);
    }

    // Restrict conditions that do not subscribe to anything and are not under a condition that does so
    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return !(skill instanceof PCond) || skill instanceof PMultiBase || skill instanceof PCLCombatSubscriber || skill.parent instanceof PCond;
    }
}
