package pinacolada.actions.orbs;

import com.evacipated.cardcrawl.mod.stslib.actions.defect.EvokeSpecificOrbAction;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

public class EvokeOrb extends PCLActionWithCallback<ArrayList<AbstractOrb>>
{
    protected final ArrayList<AbstractOrb> orbs = new ArrayList<>();
    protected GenericCondition<AbstractOrb> filter;
    protected AbstractOrb orb;
    protected boolean isRandom;
    protected int limit = 1;
    protected int additionalFocus;

    public EvokeOrb(int times)
    {
        this(times, 1, false);
    }

    public EvokeOrb(int times, int limit, boolean isRandom)
    {
        super(ActionType.WAIT);

        this.isRandom = isRandom;
        this.limit = limit;
        this.orb = null;

        initialize(times);
    }

    public EvokeOrb(int times, AbstractOrb orb)
    {
        super(ActionType.WAIT);

        this.orb = orb;

        initialize(times);
    }

    public <S> EvokeOrb addFocus(int focus)
    {
        this.additionalFocus = focus;

        return this;
    }

    protected boolean checkOrb(AbstractOrb orb)
    {
        return GameUtilities.isValidOrb(orb) && (filter == null || filter.check(orb));
    }

    protected void doEvoke(AbstractOrb orb, int times)
    {
        for (int j = 0; j < (amount - 1); j++)
        {
            orb.passiveAmount += additionalFocus;
            orb.evokeAmount += additionalFocus;
            orb.onEvoke();
            orbs.add(orb);
        }
        if (amount > 0)
        {
            PCLActions.top.add(new EvokeSpecificOrbAction(orb));
            orbs.add(orb);
        }
    }

    @Override
    protected void firstUpdate()
    {
        if (player.orbs == null || player.orbs.isEmpty())
        {
            complete(orbs);
            return;
        }

        if (orb != null)
        {
            doEvoke(orb, amount);
        }
        else if (isRandom)
        {
            final RandomizedList<AbstractOrb> randomOrbs = new RandomizedList<>();
            for (AbstractOrb temp : player.orbs)
            {
                if (checkOrb(temp))
                {
                    randomOrbs.add(temp);
                }
            }

            for (int i = 0; i < limit; i++)
            {
                doEvoke(randomOrbs.retrieve(rng, false), amount);
            }
        }
        else
        {
            int i = 0;
            int orbs = 0;
            while (orbs < limit && i < player.orbs.size())
            {
                final AbstractOrb orb = player.orbs.get(i++);
                if (checkOrb(orb))
                {
                    doEvoke(orb, amount);
                    orbs += 1;
                }
            }
        }

        complete(orbs);
    }

    public EvokeOrb setFilter(FuncT1<Boolean, AbstractOrb> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public <S> EvokeOrb setFilter(S state, FuncT2<Boolean, S, AbstractOrb> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }
}
