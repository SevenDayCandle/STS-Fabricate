package pinacolada.misc;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SubscriberGroup<T>
{
    private final LinkedHashSet<T> subscribers = new LinkedHashSet<>();
    private final LinkedHashSet<T> oneTimeSubscribers = new LinkedHashSet<>();

    public int size()
    {
        return subscribers.size() + oneTimeSubscribers.size();
    }

    public ArrayList<T> getSubscribers()
    {
        ArrayList<T> list = new ArrayList<>(subscribers);
        list.addAll(oneTimeSubscribers);
        oneTimeSubscribers.clear();
        return list;
    }

    public void clear()
    {
        oneTimeSubscribers.clear();
        subscribers.clear();
    }

    public boolean unsubscribe(T subscriber)
    {
        return subscribers.remove(subscriber);
    }

    public boolean subscribe(T subscriber)
    {
        return subscribers.add(subscriber);
    }

    public boolean subscribeOnce(T subscriber)
    {
        return oneTimeSubscribers.add(subscriber);
    }
}
