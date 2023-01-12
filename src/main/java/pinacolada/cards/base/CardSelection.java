package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT3;
import pinacolada.utilities.ListSelection;

import java.util.List;

public class CardSelection
{
    public static final ListSelection<AbstractCard> Top = ListSelection.last(0);
    public static final ListSelection<AbstractCard> Bottom = ListSelection.first(0);
    public static final ListSelection<AbstractCard> Random = ListSelection.random(null);
    public static final ListSelection<AbstractCard> Default = Top;

    public static ListSelection<AbstractCard> top(int shift)
    {
        return ListSelection.last(shift);
    }

    public static ListSelection<AbstractCard> bottom(int shift)
    {
        return ListSelection.first(shift);
    }

    public static ListSelection<AbstractCard> random(com.megacrit.cardcrawl.random.Random rng)
    {
        return ListSelection.random(rng);
    }

    public static ListSelection<AbstractCard> special(ActionT3<List<AbstractCard>, AbstractCard, Integer> add,
                                                      FuncT3<AbstractCard, List<AbstractCard>, Integer, Boolean> get)
    {
        return ListSelection.special(add, get);
    }
}
