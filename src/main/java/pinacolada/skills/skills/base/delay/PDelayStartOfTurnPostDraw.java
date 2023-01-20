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
public class PDelayStartOfTurnPostDraw extends PDelay
{
    public static final PSkillData<PField_Empty> DATA = register(PDelayStartOfTurnPostDraw.class, PField_Empty.class, 0, DEFAULT_MAX);

    public PDelayStartOfTurnPostDraw()
    {
        super(DATA);
    }

    public PDelayStartOfTurnPostDraw(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PDelayStartOfTurnPostDraw(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction)
    {
        return DelayUse.turnStartLast(amount, info, childAction);
    }
}
