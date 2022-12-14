package pinacolada.actions.orbs;

import com.evacipated.cardcrawl.mod.stslib.actions.defect.EvokeSpecificOrbAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT0;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class InduceOrb extends PCLActionWithCallback<ArrayList<AbstractOrb>>
{
    protected final ArrayList<AbstractOrb> inducedOrbs = new ArrayList<>();
    protected final FuncT0<AbstractOrb> orbConstructor;
    protected final int initialOrbSlotCount;
    protected boolean shouldTriggerOnEvoke;
    protected AbstractOrb orb;

    public InduceOrb(AbstractOrb orb, boolean shouldTriggerOnEvoke)
    {
        this(orb, null, 1, shouldTriggerOnEvoke);
    }

    public InduceOrb(FuncT0<AbstractOrb> orbConstructor, int amount, boolean shouldTriggerOnEvoke)
    {
        this(null, orbConstructor, amount, shouldTriggerOnEvoke);
    }

    public InduceOrb(AbstractOrb orb, FuncT0<AbstractOrb> orbConstructor, int amount, boolean shouldTriggerOnEvoke)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.orb = orb;
        this.orbConstructor = orbConstructor;
        this.initialOrbSlotCount = player.orbs.size();
        this.shouldTriggerOnEvoke = shouldTriggerOnEvoke;

        initialize(amount);
    }

    public InduceOrb setShouldTriggerOnEvoke(boolean shouldTriggerOnEvoke)
    {
        this.shouldTriggerOnEvoke = shouldTriggerOnEvoke;

        return this;
    }

    protected void triggerEffectAndEvoke(AbstractOrb orb, int times)
    {
        if (GameUtilities.isValidOrb(orb))
        {
            for (int i = 0; i < times; i++)
            {
                orb.applyFocus();
                orb.onEvoke();
                inducedOrbs.add(orb);
            }
        }
    }

    protected void triggerEffectOnly(AbstractOrb orb, int times)
    {
        if (GameUtilities.isValidOrb(orb))
        {
            for (int i = 0; i < times; i++)
            {
                orb.applyFocus();
                GameActions.top.callback(new EvokeSpecificOrbAction(orb))
                        .addCallback(() -> {
                            while (player.orbs.size() > this.initialOrbSlotCount)
                            {
                                player.orbs.remove(player.orbs.size() - 1);
                            }
                        });
                inducedOrbs.add(orb);
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (amount > 0)
        {
            if (orbConstructor != null)
            {
                orb = orbConstructor.invoke();
            }

            if (shouldTriggerOnEvoke)
            {
                triggerEffectOnly(orb, amount);
            }
            else
            {
                triggerEffectAndEvoke(orb, amount);
            }
        }

        complete(inducedOrbs);
    }
}
