package pinacolada.skills.skills;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.DelayUse;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PDelay extends PSkill
{
    public static final int DELAY_PRIORITY = 2;
    public static final PSkillData DATA = register(PDelay.class, General, DELAY_PRIORITY, 0, DEFAULT_MAX);
    protected DelayUse.Timing timing = DelayUse.Timing.StartOfTurnLast;

    public static PDelay turnEnd()
    {
        return turnEnd(0);
    }

    public static PDelay turnEndLast()
    {
        return turnEndLast(0);
    }

    public static PDelay turnEnd(int amount)
    {
        return new PDelay(amount, DelayUse.Timing.EndOfTurnFirst);
    }

    public static PDelay turnEndLast(int amount)
    {
        return new PDelay(amount, DelayUse.Timing.EndOfTurnLast);
    }

    public static PDelay turnStart(int amount)
    {
        return new PDelay(amount, DelayUse.Timing.StartOfTurnFirst);
    }

    public static PDelay turnStartLast(int amount)
    {
        return new PDelay(amount, DelayUse.Timing.StartOfTurnLast);
    }

    public PDelay()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PDelay(PSkillSaveData content)
    {
        super(content);
    }

    public PDelay(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PDelay(int amount, DelayUse.Timing timing)
    {
        this(amount);
        setTiming(timing);
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
            new DelayUse(amount, timing, info, (i) -> this.childEffect.use(i)).start();
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (this.childEffect != null)
        {
            new DelayUse(amount, timing, info, (i) -> this.childEffect.use(i, index)).start();
        }
    }

    @Override
    public String getSubText()
    {
        return (amount <= 0 && (timing == DelayUse.Timing.EndOfTurnFirst || timing == DelayUse.Timing.EndOfTurnLast) ? TEXT.conditions.atEndOfTurn() :
                amount <= 1 ? TEXT.conditions.nextTurn() : TEXT.conditions.inTurns(amount));
    }

    public PDelay setTiming(DelayUse.Timing timing)
    {
        this.timing = timing;
        return this;
    }
}
