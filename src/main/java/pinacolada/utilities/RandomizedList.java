package pinacolada.utilities;

import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomizedList<T>
{
    private final ArrayList<T> items;

    public RandomizedList()
    {
        items = new ArrayList<>();
    }

    public RandomizedList(Collection<? extends T> collection)
    {
        items = new ArrayList<>(collection);
    }

    @SafeVarargs
    public RandomizedList(T... array)
    {
        items = new ArrayList<>(array.length);
        addAll(array);
    }

    public void add(T item)
    {
        items.add(item);
    }

    @SafeVarargs
    public final void addAll(T... arr)
    {
        Collections.addAll(items, arr);
    }

    public void addAll(List<T> list)
    {
        items.addAll(list);
    }

    public void clear()
    {
        items.clear();
    }

    public int size()
    {
        return items.size();
    }

    public boolean remove(T item)
    {
        return items.remove(item);
    }

    public T retrieve(Random rng, boolean remove)
    {
        T item = GameUtilities.getRandomElement(items, rng);
        if (remove)
        {
            items.remove(item);
        }

        return item;
    }

    public T retrieve(Random rng)
    {
        return retrieve(rng, true);
    }

    public T retrieveUnseeded(boolean remove)
    {
        T item = EUIUtils.random(items);
        if (remove)
        {
            items.remove(item);
        }

        return item;
    }

    public ArrayList<T> getInnerList()
    {
        return items;
    }
}
