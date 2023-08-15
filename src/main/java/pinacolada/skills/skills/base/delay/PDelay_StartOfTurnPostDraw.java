package pinacolada.skills.skills.base.delay;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PDelay;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.skills.delay.DelayUse;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PDelay_StartOfTurnPostDraw extends PDelay {
    public static final PSkillData<PField_Empty> DATA = register(PDelay_StartOfTurnPostDraw.class, PField_Empty.class, 0, DEFAULT_MAX)
            .selfTarget();

    public PDelay_StartOfTurnPostDraw() {
        super(DATA);
    }

    public PDelay_StartOfTurnPostDraw(PSkillSaveData content) {
        super(DATA, content);
    }

    public PDelay_StartOfTurnPostDraw(int amount) {
        super(DATA, amount);
    }

    @Override
    public DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction, String title, String description) {
        return DelayUse.turnStartLast(amount, info, childAction, title, description);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return (amount <= 0 ? getTiming().getDesc() :
                (amount <= 1 ? TEXT.cond_nextTurn() : TEXT.cond_inTurns(amount)));
    }

    @Override
    public DelayTiming getTiming() {
        return DelayTiming.StartOfTurnLast;
    }
}
