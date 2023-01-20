package pinacolada.skills.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.delay.DelayUse;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.delay.PDelayEndOfTurnFirst;
import pinacolada.skills.skills.base.delay.PDelayEndOfTurnLast;
import pinacolada.skills.skills.base.delay.PDelayStartOfTurn;
import pinacolada.skills.skills.base.delay.PDelayStartOfTurnPostDraw;

public abstract class PDelay extends PSkill<PField_Empty>
{
    public static PDelay turnEnd()
    {
        return turnEnd(0);
    }

    public static PDelay turnEndLast()
    {
        return turnEndLast(0);
    }

    public static PDelayEndOfTurnFirst turnEnd(int amount)
    {
        return new PDelayEndOfTurnFirst(amount);
    }

    public static PDelayEndOfTurnLast turnEndLast(int amount)
    {
        return new PDelayEndOfTurnLast(amount);
    }

    public static PDelayStartOfTurn turnStart(int amount)
    {
        return new PDelayStartOfTurn(amount);
    }

    public static PDelayStartOfTurnPostDraw turnStartLast(int amount)
    {
        return new PDelayStartOfTurnPostDraw(amount);
    }

    public PDelay(PSkillData<PField_Empty> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PDelay(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PDelay(PSkillData<PField_Empty> data, int amount)
    {
        super(data, PCLCardTarget.None, amount);
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return capital(getSubText(), false) + (childEffect != null ? ", " + childEffect.getText(addPeriod) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public PDelay setTemporaryAmount(int amount)
    {
        if (this.childEffect != null)
        {
            this.childEffect.setTemporaryAmount(amount);
        }
        return this;
    }

    @Override
    public PDelay setTemporaryExtra(int amount)
    {
        if (this.childEffect != null)
        {
            this.childEffect.setTemporaryExtra(amount);
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (this.childEffect != null)
        {
            getDelayUse(info, (i) -> this.childEffect.use(i)).start();
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (this.childEffect != null)
        {
            getDelayUse(info, (i) -> this.childEffect.use(i, index)).start();
        }
    }

    @Override
    public String getSubText()
    {
        return (amount <= 0 ? TEXT.conditions.atEndOfTurn() :
                amount <= 1 ? TEXT.conditions.nextTurn() : TEXT.conditions.inTurns(amount));
    }

    public abstract DelayUse getDelayUse(PCLUseInfo info, ActionT1<PCLUseInfo> childAction);
}
