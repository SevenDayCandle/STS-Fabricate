package pinacolada.powers;

import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.interfaces.listeners.OnTryReducePowerListener;
import pinacolada.resources.PGR;

public class StabilizingPower extends PCLPower implements InvisiblePower, OnTryReducePowerListener
{
    public static final String POWER_ID = PGR.core.createID(StabilizingPower.class.getSimpleName());
    protected AbstractPower target;

    public StabilizingPower(AbstractCreature owner, AbstractPower target)
    {
        super(owner, POWER_ID);
        this.target = target;
    }

    @Override
    public boolean tryReducePower(AbstractPower var1, AbstractCreature var2, AbstractCreature var3, AbstractGameAction var4)
    {
        return var1 != target;
    }

    public void atStartOfTurnPostDraw()
    {
        super.atStartOfTurnPostDraw();
        removePower();
    }
}
