package pinacolada.utilities;

import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;

import java.util.ArrayList;
import java.util.List;

public class WeightedList<T>
{
    private class Item
    {
        final int weight;
        final T object;

        private Item(T object, int weight)
        {
            this.weight = weight;
            this.object = object;
        }
    }

    private final List<Item> items;
    private int totalWeight;

    public WeightedList()
    {
        totalWeight = 0;
        items = new ArrayList<>();
    }

    public WeightedList(WeightedList<T> copy)
    {
        this();

        for (Item item : copy.items)
        {
            add(item.object, item.weight);
        }
    }

    public List<T> getInnerList()
    {
        final ArrayList<T> result = new ArrayList<>();
        for (Item item : items)
        {
            result.add(item.object);
        }

        return result;
    }

    public int size()
    {
        return items.size();
    }

    public void clear()
    {
        items.clear();
        totalWeight = 0;
    }

    public void add(T object, int weight)
    {
        totalWeight += weight;
        items.add(new Item(object, weight));
    }

    public T retrieve(Random rng)
    {
        return retrieve(rng, true);
    }

    public T retrieve(Random rng, boolean remove)
    {
        return retrieve(rng.random(totalWeight), remove);
    }

    public T retrieveUnseeded(boolean remove)
    {
        return retrieve(EUIUtils.RNG.nextInt(totalWeight + 1), remove);
    }

    private T retrieve(int roll, boolean remove)
    {
        Item selected = null;
        int currentWeight = 0;
        for (Item item : items)
        {
            if ((currentWeight + item.weight) >= roll)
            {
                selected = item;
                break;
            }

            currentWeight += item.weight;
        }

        if (selected == null)
        {
            return null;
        }

        if (remove)
        {
            remove(selected);
        }

        return selected.object;
    }

    private void remove(Item item)
    {
        totalWeight -= item.weight;
        items.remove(item);
    }
}
