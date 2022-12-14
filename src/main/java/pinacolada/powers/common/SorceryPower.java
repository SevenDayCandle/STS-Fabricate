package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.interfaces.subscribers.OnChannelOrbSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

public class SorceryPower extends PCLPower implements OnChannelOrbSubscriber
{
    public static final String POWER_ID = createFullID(SorceryPower.class);

    public SorceryPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public void onChannelOrb(AbstractOrb orb)
    {
        GameUtilities.modifyOrbBaseFocus(orb, amount, true, false);
        removePower();
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        CombatManager.onChannelOrb.subscribe(this);
    }

    @Override
    public void onRemove()
    {
        super.onRemove();

        CombatManager.onChannelOrb.unsubscribe(this);
    }

    public float modifyOrbAmount(float initial)
    {
        return initial + amount;
    }
}
