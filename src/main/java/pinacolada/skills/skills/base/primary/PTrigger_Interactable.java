package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.powers.PCLClickableUse;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PTrigger;

@VisibleSkill
public class PTrigger_Interactable extends PTrigger {

    public static final PSkillData<PField_Not> DATA = register(PTrigger_Interactable.class, PField_Not.class, -1, DEFAULT_MAX)
            .selfTarget();

    public PTrigger_Interactable() {
        this(1);
    }

    public PTrigger_Interactable(int maxUses) {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    public PTrigger_Interactable(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public PCLClickableUse getClickable(ClickableProvider provider) {
        return new PCLClickableUse(provider, getChild(), amount <= 0 ? -1 : amount, !fields.not, true);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return PGR.core.tooltips.interactable.title;
    }

    // No-Op, should not subscribe children
    @Override
    public void subscribeChildren() {
    }

    @Override
    public String getSubText() {
        return PGR.core.tooltips.interactable.title + ": " + (fields.not ? TEXT.cond_timesPerCombat(amount) + ", " : amount > 1 ? TEXT.cond_timesPerTurn(amount) + ", " : "");
    }
}
