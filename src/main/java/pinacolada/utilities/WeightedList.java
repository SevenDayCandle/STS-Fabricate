package pinacolada.utilities;

import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;

import java.util.ArrayList;
import java.util.List;

public class WeightedList<T> extends ArrayList<WeightedList<T>.Item>
{
    public class Item
    {
        public final int weight;
        public final T object;

        private Item(T object, int weight)
        {
            this.weight = weight;
            this.object = object;
        }
    }

    private int totalWeight;

    public WeightedList()
    {
        super();
        totalWeight = 0;
    }

    public WeightedList(WeightedList<T> copy)
    {
        this();

        for (Item item : copy)
        {
            add(item.object, item.weight);
        }
    }

    public List<T> getInnerList()
    {
        final ArrayList<T> result = new ArrayList<>();
        for (Item item : this)
        {
            result.add(item.object);
        }

        return result;
    }

    public void clear()
    {
        super.clear();
        totalWeight = 0;
    }

    public void add(T object, int weight)
    {
        totalWeight += weight;
        super.add(new Item(object, weight));
    }

    public T retrieve(Random rng)
    {
        return retrieve(rng, true);
    }

    public T retrieve(Random rng, boolean remove)
    {
        return retrieve(rng.random(totalWeight), remove);
    }

    public Item retrieveWithWeight(Random rng)
    {
        return retrieveWithWeight(rng, true);
    }

    public Item retrieveWithWeight(Random rng, boolean remove)
    {
        return retrieveWithWeight(rng.random(totalWeight), remove);
    }

    public T retrieveUnseeded(boolean remove)
    {
        return retrieve(EUIUtils.RNG.nextInt(totalWeight + 1), remove);
    }

    public Item retrieveUnseededWithWeight(boolean remove)
    {
        return retrieveWithWeight(EUIUtils.RNG.nextInt(totalWeight + 1), remove);
    }

    private T retrieve(int roll, boolean remove)
    {
        Item i = retrieveWithWeight(roll, remove);
        return i != null ? i.object : null;
    }

    private Item retrieveWithWeight(int roll, boolean remove)
    {
        Item selected = null;
        int currentWeight = 0;
        for (Item item : this)
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

        return selected;
    }

    private void remove(Item item)
    {
        totalWeight -= item.weight;
        super.remove(item);
    }
}
