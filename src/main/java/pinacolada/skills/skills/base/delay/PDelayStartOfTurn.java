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
public class PDelayStartOfTurn extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelayStartOfTurn.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelayStartOfTurn()
    {
        super(DATA);
    }

    public PDelayStartOfTurn(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelayStartOfTurn(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnStart(amount, info, childAction);
    }
}
