package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.interfaces.subscribers.OnPCLClickablePowerUsed;
import pinacolada.misc.CombatStats;
import pinacolada.powers.PCLPower;

public class InnovationPower extends PCLPower implements OnPCLClickablePowerUsed
{
    public static final String POWER_ID = createFullID(InnovationPower.class);

    public InnovationPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public boolean onClickablePowerUsed(PCLPower power, AbstractMonster target)
    {
        reducePower(1);
        this.flashWithoutSound();
        return false;
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        CombatStats.onPCLClickablePowerUsed.subscribe(this);
    }

    @Override
    public void onRemove()
    {
        super.onRemove();

        CombatStats.onPCLClickablePowerUsed.unsubscribe(this);
    }
}
