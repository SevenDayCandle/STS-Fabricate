package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import pinacolada.actions.PCLActions;

// Variant of Curl Up that is cloneable and that can be applied on non-louses without crashing
public class PCLCurlUpPower extends CurlUpPower implements CloneablePowerInterface
{
    public static final String POWER_ID = CurlUpPower.POWER_ID;
    protected boolean triggered = false;

    public PCLCurlUpPower(AbstractCreature owner, int amount)
    {
        super(owner, amount);
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PCLCurlUpPower(owner, amount);
    }

    public int onAttacked(DamageInfo info, int damageAmount)
    {
        if (!this.triggered && damageAmount < this.owner.currentHealth && damageAmount > 0 && info.owner != null && info.type == DamageInfo.DamageType.NORMAL)
        {
            this.flash();
            triggered = true;
            PCLActions.bottom.gainBlock(this.owner, this.amount);
            PCLActions.bottom.removePower(owner, owner, this);
        }

        return damageAmount;
    }
}