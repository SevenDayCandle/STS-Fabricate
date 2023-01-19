package pinacolada.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

// Copied and modified from STS-AnimatorMod
public class RotatingList<T> extends ArrayList<T>
{
    private int index;

    public RotatingList()
    {
        super();
    }

    public RotatingList(Collection<? extends T> collection)
    {
        super(collection);
    }

    public RotatingList(T[] array)
    {
        super();
        Collections.addAll(this, array);
    }

    public void clear()
    {
        resetIndex();
        super.clear();
    }

    public int getIndex()
    {
        return index;
    }

    public T setIndex(int index)
    {
        this.index = index < 0 ? 0 : index < size() ? index : size() - 1;
        return current();
    }

    public T current()
    {
        return current(false);
    }

    public T current(boolean moveIndex)
    {
        T item = isEmpty() ? null : get(index);
        if (moveIndex)
        {
            next(true);
        }

        return item;
    }

    public T next(boolean moveIndex)
    {
        int newIndex = index + 1;
        if (newIndex >= size())
        {
            newIndex = 0;
        }

        if (moveIndex)
        {
            index = newIndex;
        }

        return super.get(newIndex);
    }

    public T previous(boolean moveIndex)
    {
        int newIndex = index - 1;
        if (newIndex < 0)
        {
            newIndex = size() - 1;
        }

        if (moveIndex)
        {
            index = newIndex;
        }

        return super.get(newIndex);
    }

    public void resetIndex()
    {
        index = 0;
    }
}
