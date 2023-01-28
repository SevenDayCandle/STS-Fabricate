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
public class PDelay_EndOfTurnFirst extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelay_EndOfTurnFirst.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelay_EndOfTurnFirst()
    {
        super(DATA);
    }

    public PDelay_EndOfTurnFirst(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelay_EndOfTurnFirst(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnEnd(amount, info, childAction);
    }
}
