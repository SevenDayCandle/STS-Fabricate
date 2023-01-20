package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Comparator;

// Copied and modified from STS-AnimatorMod
public class ModifyPowers extends PCLActionWithCallback<ArrayList<AbstractPower>>
{
    protected final ArrayList<AbstractPower> result = new ArrayList<>();
    protected Comparator<AbstractPower> comparator;
    protected ListSelection<AbstractPower> selection;
    protected GenericCondition<AbstractPower> filter;
    protected boolean relative;
    protected int count;

    public ModifyPowers(AbstractCreature target, AbstractCreature source, int amount, boolean relative)
    {
        super(ActionType.POWER);

        this.relative = relative;
        this.count = 1;

        initialize(target, source, amount);
    }

    protected boolean canStack(AbstractPower power)
    {
        return !power.canGoNegative || power.amount != -1;
    }

    @Override
    protected void firstUpdate()
    {
        final ArrayList<AbstractPower> powers = new ArrayList<>();
        for (AbstractPower p : target.powers)
        {
            if ((canStack(p) || (!relative && amount == 0)) && (filter == null || filter.check(p)))
            {
                powers.add(p);
            }
        }

        if (comparator != null)
        {
            powers.sort(comparator);
        }

        if (selection == null)
        {
            selection = ListSelection.last(0);
        }

        selection.forEach(powers, count, this::modify);

        complete(result);
    }

    protected void modify(AbstractPower power)
    {
        if (!canStack(power))
        {
            if (!relative && amount == 0)
            {
                PCLActions.bottom.removePower(source, power);
                result.add(power);
            }

            return;
        }

        final int stacks = relative ? amount : (amount - power.amount);
        if (stacks >= 0)
        {
            PCLActions.bottom.increasePower(power, stacks);
        }
        else
        {
            PCLActions.bottom.reducePower(power, -stacks);
        }

        result.add(power);
    }

    public <S> ModifyPowers setFilter(S state, FuncT2<Boolean, S, AbstractPower> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public ModifyPowers setFilter(FuncT1<Boolean, AbstractPower> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public ModifyPowers setSelection(ListSelection<AbstractPower> selection, int count)
    {
        this.selection = selection;
        this.count = count;

        return this;
    }

    public ModifyPowers sort(Comparator<AbstractPower> comparator)
    {
        this.comparator = comparator;

        return this;
    }
}