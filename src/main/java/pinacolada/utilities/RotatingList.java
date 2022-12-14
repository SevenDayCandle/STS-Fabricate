package pinacolada.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class RotatingList<T> implements Iterable<T>
{
    private final ArrayList<T> items;
    private int index;

    public RotatingList()
    {
        items = new ArrayList<>();
    }

    public RotatingList(Collection<? extends T> collection)
    {
        items = new ArrayList<>(collection);
    }

    public RotatingList(T[] array)
    {
        items = new ArrayList<>(array.length);
        Collections.addAll(items, array);
    }

    public void add(T item)
    {
        items.add(item);
    }

    public void addAll(ArrayList<T> list)
    {
        items.addAll(list);
    }

    public void clear()
    {
        resetIndex();
        items.clear();
    }

    public boolean contains(T item)
    {
        return items.contains(item);
    }

    public int count()
    {
        return items.size();
    }

    public int getIndex()
    {
        return index;
    }

    /** Call ResetIndex() if you reduce the list size. */
    public ArrayList<T> getInnerList()
    {
        return items;
    }

    public T setIndex(int index)
    {
        this.index = index < 0 ? 0 : index < items.size() ? index : items.size() - 1;
        return current();
    }

    public T current()
    {
        return current(false);
    }

    public T current(boolean moveIndex)
    {
        T item = items.isEmpty() ? null : items.get(index);
        if (moveIndex)
        {
            next(true);
        }

        return item;
    }

    public T next(boolean moveIndex)
    {
        int newIndex = index + 1;
        if (newIndex >= items.size())
        {
            newIndex = 0;
        }

        if (moveIndex)
        {
            index = newIndex;
        }

        return items.get(newIndex);
    }

    public T previous(boolean moveIndex)
    {
        int newIndex = index - 1;
        if (newIndex < 0)
        {
            newIndex = items.size() - 1;
        }

        if (moveIndex)
        {
            index = newIndex;
        }

        return items.get(newIndex);
    }

    public void resetIndex()
    {
        index = 0;
    }

    @Override
    public Iterator<T> iterator()
    {
        return items.iterator();
    }
}
