package pinacolada.skills.skills.base.primary;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PTrigger_Interactable extends PTrigger
{

    public static final PSkillData<PField_Not> DATA = register(PTrigger_Interactable.class, PField_Not.class, -1, DEFAULT_MAX);

    public PTrigger_Interactable()
    {
        this(1);
    }

    public PTrigger_Interactable(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrigger_Interactable(int maxUses)
    {
        super(DATA, PCLCardTarget.None, maxUses);
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.interactable.title;
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.interactable.title + ": " + (fields.not ? TEXT.conditions.timesPerCombat(amount) + ", " : amount > 1 ? TEXT.conditions.timesPerTurn(amount) + ", " : "");
    }
}
