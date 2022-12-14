package pinacolada.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class UniqueList<T>
{
    private final HashMap<T, Integer> indexes = new HashMap<>();
    private final ArrayList<T> items = new ArrayList<>();

    public UniqueList()
    {

    }

    public UniqueList(Collection<T> items)
    {
        for (T item : items)
        {
            add(item);
        }
    }

    // Returns the index at which the item exists in the map
    public int add(T item)
    {
        Integer found = getIndex(item);
        if (found != null)
        {
            return found;
        }

        int size = items.size();
        items.add(item);
        indexes.put(item, size);
        return size;
    }

    public void clear()
    {
        indexes.clear();
        items.clear();
    }

    public T get(int index)
    {
        return index >= 0 && items.size() > index ? items.get(index) : null;
    }

    public Integer getIndex(T item)
    {
        return indexes.get(item);
    }

    public T getOrDefault(int index, T defaultValue)
    {
        T found = get(index);
        return found != null ? found : defaultValue;
    }

    public boolean remove(int index)
    {
        if (items.size() > index)
        {
            T item = items.get(index);
            items.remove(index);
            indexes.remove(item);
            return true;
        }

        return false;
    }

    public boolean remove(T item)
    {
        Integer cIndex = getIndex(item);
        if (cIndex != null)
        {
            indexes.remove(item);
            items.remove(cIndex.intValue());
            return true;
        }

        return false;
    }

    public int size()
    {
        return items.size();
    }
}
