package pinacolada.skills.skills.base.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PDelay;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.delay.DelayUse;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PDelay_EndOfTurnLast extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelay_EndOfTurnLast.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelay_EndOfTurnLast()
    {
        super(DATA);
    }

    public PDelay_EndOfTurnLast(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelay_EndOfTurnLast(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnEndLast(amount, info, childAction);
    }
}