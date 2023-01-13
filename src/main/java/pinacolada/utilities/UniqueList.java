package pinacolada.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class UniqueList<T> extends ArrayList<T>
{
    private final HashMap<Object, Integer> indexes = new HashMap<>();

    public UniqueList()
    {
        super();
    }

    public UniqueList(Collection<T> items)
    {
        this.addAll(items);
    }

    public boolean add(T item)
    {
        int prevSize = size();
        int result = addAndGetIndex(item);
        return result >= prevSize;
    }

    // Returns the index at which the item exists in the map
    public int addAndGetIndex(T item)
    {
        Integer found = getIndex(item);
        if (found != null)
        {
            return found;
        }

        int size = size();
        super.add(item);
        indexes.put(item, size);
        return size;
    }

    public void clear()
    {
        super.clear();
        indexes.clear();
    }

    public T get(int index)
    {
        return index >= 0 && size() > index ? super.get(index) : null;
    }

    public Integer getIndex(Object item)
    {
        return indexes.get(item);
    }

    public T getOrDefault(int index, T defaultValue)
    {
        T found = get(index);
        return found != null ? found : defaultValue;
    }

    public T remove(int index)
    {
        if (size() > index)
        {
            T item = super.remove(index);
            indexes.remove(item);
            return item;
        }

        return null;
    }

    public boolean remove(Object item)
    {
        Integer cIndex = getIndex(item);
        if (cIndex != null)
        {
            indexes.remove(item);
            super.remove(cIndex.intValue());
            return true;
        }

        return false;
    }
}
