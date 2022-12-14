package pinacolada.actions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardTarget;

import java.util.ArrayList;
import java.util.List;

public class PCLActionAutoTarget<T> extends PCLActionWithCallback<T>
{
    protected final List<AbstractCreature> targets = new ArrayList<>();
    protected PCLCardTarget targetHelper;
    protected int limit = 1;

    public PCLActionAutoTarget(ActionType type)
    {
        super(type);
    }

    public PCLActionAutoTarget(ActionType type, float duration)
    {
        super(type, duration);
    }

    protected List<AbstractCreature> findTargets(boolean reverse)
    {
        targets.clear();

        for (AbstractCreature c : targetHelper.getTargets(source, target, limit))
        {
            if (reverse)
            {
                targets.add(0, c);
            }
            else
            {
                targets.add(c);
            }
        }

        if (targets.size() > 0)
        {
            target = targets.get(0);
        }

        return targets;
    }

    protected void initialize(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, int amount, int limit)
    {
        this.targetHelper = targetHelper;
        this.limit = limit;

        initialize(source, target, amount);
    }
}
