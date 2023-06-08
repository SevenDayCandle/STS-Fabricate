package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrigger_Passive extends PTrigger {
    public static final PSkillData<PField_Not> DATA = register(PTrigger_Passive.class, PField_Not.class, -1, DEFAULT_MAX)
            .selfTarget();

    public PTrigger_Passive() {
        super(DATA);
    }

    public PTrigger_Passive(PSkillSaveData content) {
        super(DATA, content);
    }

    public PTrigger_Passive(int maxUses) {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    public PTrigger_Passive(PCLCardTarget target, int maxUses) {
        super(DATA, target, maxUses);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_passive();
    }

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PMultiBase ||
                skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof PTrait;
    }
}
