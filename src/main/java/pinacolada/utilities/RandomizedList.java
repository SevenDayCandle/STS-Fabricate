package pinacolada.utilities;

import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RandomizedList<T> extends ArrayList<T>
{
    public RandomizedList()
    {
        super();
    }

    public RandomizedList(Collection<? extends T> collection)
    {
        super(collection);
    }

    @SafeVarargs
    public RandomizedList(T... array)
    {
        super();
        addAll(array);
    }

    @SafeVarargs
    public final boolean addAll(T... arr)
    {
        return Collections.addAll(this, arr);
    }

    public T retrieve(Random rng, boolean remove)
    {
        T item = GameUtilities.getRandomElement(this, rng);
        if (remove)
        {
            remove(item);
        }

        return item;
    }

    public T retrieve(Random rng)
    {
        return retrieve(rng, true);
    }

    public T retrieveUnseeded(boolean remove)
    {
        T item = EUIUtils.random(this);
        if (remove)
        {
            remove(item);
        }

        return item;
    }
}
