package pinacolada.misc;

import java.util.LinkedHashSet;

public class GameEvent<T>
{
    private final LinkedHashSet<T> subscribersCopy = new LinkedHashSet<>();
    private final LinkedHashSet<T> subscribers = new LinkedHashSet<>();
    private final LinkedHashSet<T> oneTimeSubscribers = new LinkedHashSet<>();

    public int count()
    {
        return subscribers.size() + oneTimeSubscribers.size();
    }

    public LinkedHashSet<T> getSubscribers()
    {
        subscribersCopy.clear();
        subscribersCopy.addAll(subscribers);
        subscribersCopy.addAll(oneTimeSubscribers);

        oneTimeSubscribers.clear();

        return subscribersCopy;
    }

    public void clear()
    {
        oneTimeSubscribers.clear();
        subscribersCopy.clear();
        subscribers.clear();
    }

    public boolean toggleSubscription(T subscriber, boolean subscribe)
    {
        return subscribe ? subscribe(subscriber) : unsubscribe(subscriber);
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
