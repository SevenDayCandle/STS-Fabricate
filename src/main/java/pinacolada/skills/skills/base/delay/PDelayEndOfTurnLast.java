package pinacolada.skills.skills.base.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.delay.DelayUse;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDelay;

@VisibleSkill
public class PDelayEndOfTurnLast extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelayEndOfTurnLast.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelayEndOfTurnLast()
    {
        super(DATA);
    }

    public PDelayEndOfTurnLast(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelayEndOfTurnLast(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnEndLast(amount, info, childAction);
    }
}
