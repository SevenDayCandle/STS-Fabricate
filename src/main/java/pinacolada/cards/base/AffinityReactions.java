package pinacolada.cards.base;

import java.util.HashMap;

public class AffinityReactions
{
    public final HashMap<PCLAffinity, HashMap<PCLAffinity, Integer>> combustions = new HashMap<>();
    public final HashMap<PCLAffinity, HashMap<PCLAffinity, Integer>> redoxes = new HashMap<>();

    public AffinityReactions()
    {
    }

    public void addCombust(PCLAffinity dest, PCLAffinity reactor, int amount)
    {
        HashMap<PCLAffinity, Integer> amp = combustions.getOrDefault(dest, new HashMap<>());
        amp.merge(reactor, amount, Integer::sum);
        combustions.putIfAbsent(dest, amp);
    }

    public void addRedox(PCLAffinity dest, PCLAffinity reactor, int amount)
    {
        HashMap<PCLAffinity, Integer> intensification = redoxes.getOrDefault(dest, new HashMap<>());
        intensification.merge(reactor, amount, Integer::sum);
        redoxes.putIfAbsent(dest, intensification);
    }

    public boolean hasCombust()
    {
        return !combustions.isEmpty();
    }

    public boolean hasCombust(PCLAffinity aff)
    {
        return combustions.containsKey(aff);
    }

    public boolean hasRedox()
    {
        return !redoxes.isEmpty();
    }

    public boolean hasRedox(PCLAffinity aff)
    {
        return redoxes.containsKey(aff);
    }

    public boolean isEmpty()
    {
        return !hasCombust() && !hasRedox();
    }
}
