package pinacolada.powers.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActions;
import pinacolada.interfaces.listeners.OnTryApplyPowerListener;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

public class SilencedPower extends PCLPower implements OnTryApplyPowerListener
{
    public static final String POWER_ID = createFullID(SilencedPower.class);
    public int secondaryAmount;

    public SilencedPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);
        this.priority = 99;
        initialize(amount, PowerType.DEBUFF, true);
        updateDescription();
    }

    @Override
    public boolean tryApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source, AbstractGameAction action)
    {
        return !GameUtilities.isCommonBuff(power) || (power.owner != owner && target != owner);
    }

    @Override
    public void atEndOfRound()
    {
        super.atEndOfRound();

        PCLActions.bottom.reducePower(this, 1);
    }
}