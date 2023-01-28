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
public class PDelay_StartOfTurn extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelay_StartOfTurn.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelay_StartOfTurn()
    {
        super(DATA);
    }

    public PDelay_StartOfTurn(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelay_StartOfTurn(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnStart(amount, info, childAction);
    }
}