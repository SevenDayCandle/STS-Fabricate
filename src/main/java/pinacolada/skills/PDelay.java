package pinacolada.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.delay.DelayUse;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.delay.PDelay_EndOfTurnFirst;
import pinacolada.skills.skills.base.delay.PDelay_EndOfTurnLast;
import pinacolada.skills.skills.base.delay.PDelay_StartOfTurn;
import pinacolada.skills.skills.base.delay.PDelay_StartOfTurnPostDraw;

public abstract class PDelay extends PSkill<PField_Empty> {
    public PDelay(PSkillData<PField_Empty> data) {
        super(data, PCLCardTarget.None, 0);
    }

    public PDelay(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    public PDelay(PSkillData<PField_Empty> data, int amount) {
        super(data, PCLCardTarget.None, amount);
    }

    public static PDelay turnEnd() {
        return turnEnd(0);
    }

    public static PDelay_EndOfTurnFirst turnEnd(int amount) {
        return new PDelay_EndOfTurnFirst(amount);
    }

    public static PDelay turnEndLast() {
        return turnEndLast(0);
    }

    public static PDelay_EndOfTurnLast turnEndLast(int amount) {
        return new PDelay_EndOfTurnLast(amount);
    }

    public static PDelay_StartOfTurn turnStart(int amount) {
        return new PDelay_StartOfTurn(amount);
    }

    public static PDelay_StartOfTurnPostDraw turnStartLast(int amount) {
        return new PDelay_StartOfTurnPostDraw(amount);
    }

    public abstract DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction);

    @Override
    public String getSubText() {
        return (amount <= 0 ? TEXT.cond_atEndOfTurn() :
                amount <= 1 ? TEXT.cond_nextTurn() : TEXT.cond_inTurns(amount));
    }

    @Override
    public String getText(boolean addPeriod) {
        return getCapitalSubText(addPeriod) + (childEffect != null ? ", " + childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public PDelay setTemporaryAmount(int amount) {
        if (this.childEffect != null) {
            this.childEffect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PDelay setTemporaryExtra(int amount) {
        if (this.childEffect != null) {
            this.childEffect.setTemporaryExtra(amount);
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info) {
        if (this.childEffect != null) {
            getDelayUse(info, (i) -> this.childEffect.use(i)).start();
        }
    }

    public void use(PCLUseInfo info, int index) {
        if (this.childEffect != null) {
            getDelayUse(info, (i) -> this.childEffect.use(i, index)).start();
        }
    }
}
