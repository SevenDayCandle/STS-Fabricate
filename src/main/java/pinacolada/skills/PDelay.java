package pinacolada.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.delay.DelayTiming;
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

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return getTiming().getTitle();
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return (amount <= 0 ? getTiming().getDesc() :
                (amount <= 1 ? TEXT.cond_nextTurn() : TEXT.cond_inTurns(amount)) + COMMA_SEPARATOR + getTiming().getDesc());
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return getCapitalSubText(perspective, requestor, addPeriod) + (childEffect != null ? ", " + childEffect.getText(perspective, requestor, addPeriod) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public boolean hasChildWarning() {
        return childEffect == null;
    }

    // Modifiers pass through
    @Override
    public int refreshChildAmount(PCLUseInfo info, int amount, boolean isUsing) {
        return parent != null ? parent.refreshChildAmount(info, amount, isUsing) : amount;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (this.childEffect != null) {
            getDelayUse(info, (i) -> this.childEffect.use(info, order), this.childEffect.getName(), this.childEffect.getPowerText(null)).start();
        }
    }

    // Modifiers pass through
    @Override
    public String wrapTextAmountChild(String input) {
        return parent != null ? parent.wrapTextAmountChild(input) : super.wrapTextAmountChild(input);
    }

    public abstract DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction, String title, String description);

    public abstract DelayTiming getTiming();
}
