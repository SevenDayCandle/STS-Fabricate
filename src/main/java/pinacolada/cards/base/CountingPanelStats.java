package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.interfaces.markers.CountingPanelItem;
import pinacolada.ui.cardReward.CountingPanelCounter;
import pinacolada.utilities.GameUtilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountingPanelStats<T extends CountingPanelItem, J, K> implements Iterable<Map.Entry<T, Integer>>
{
    private final HashMap<T, Integer> groups = new HashMap<>();
    private final FuncT1<Iterable<J>, K> vendorFunc;
    private final FuncT1<T, J> keyFunc;
    private final FuncT1<Integer, J> countingFunc;
    private final FuncT2<Integer, Iterable<J>, K> amountFunc;
    private int totalK;

    public static CountingPanelStats<PCLAffinity, PCLAffinity, AbstractCard> affinityStats(Iterable<AbstractCard> cards)
    {
        return basic(
                card -> GameUtilities.getPCLCardAffinities(card).getAffinities(false, true),
                cards);
    }

    public static CountingPanelStats<PCLAugmentCategory, Map.Entry<PCLAugmentData, Integer>, Map.Entry<PCLAugmentData, Integer>> augmentStats(HashMap<PCLAugmentData, Integer> augments)
    {
        return new CountingPanelStats<PCLAugmentCategory, Map.Entry<PCLAugmentData, Integer>, Map.Entry<PCLAugmentData, Integer>>(
                Collections::singleton,
                entry -> entry.getKey().category,
                Map.Entry::getValue,
                (entries, entry) -> entry.getValue(),
                augments.entrySet());
    }

    public static <T extends CountingPanelItem, K> CountingPanelStats<T, T, K> basic(FuncT1<Iterable<T>, K> vendorFunc)
    {
        return new CountingPanelStats<>(vendorFunc,
                (a) -> {return a;},
                (a) -> {return 1;},
                (a, b) -> {return 1;}
        );
    }

    public static <T extends CountingPanelItem, K> CountingPanelStats<T, T, K> basic(FuncT1<Iterable<T>, K> vendorFunc, Iterable<K> items)
    {
        return new CountingPanelStats<>(vendorFunc,
                (a) -> {return a;},
                (a) -> {return 1;},
                (a, b) -> {return 1;},
                items
        );
    }

    public CountingPanelStats(FuncT1<Iterable<J>, K> vendorFunc, FuncT1<T, J> keyFunc, FuncT1<Integer, J> countingFunc, FuncT2<Integer, Iterable<J>, K> amountFunc)
    {
        this.vendorFunc = vendorFunc;
        this.keyFunc = keyFunc;
        this.countingFunc = countingFunc;
        this.amountFunc = amountFunc;
    }

    public CountingPanelStats(FuncT1<Iterable<J>, K> vendorFunc, FuncT1<T, J> keyFunc, FuncT1<Integer, J> countingFunc, FuncT2<Integer, Iterable<J>, K> amountFunc, Iterable<K> items)
    {
        this(vendorFunc, keyFunc, countingFunc, amountFunc);
        addItems(items);
    }

    public void addItems(Iterable<K> items)
    {
        for (K item : items)
        {
            addItem(item);
        }
    }

    public void addItem(K item)
    {
        totalK += 1;
        Iterable<J> results = vendorFunc.invoke(item);
        totalK += amountFunc.invoke(results, item);
        for (J res : results)
        {
            groups.merge(keyFunc.invoke(res), countingFunc.invoke(res), Integer::sum);
        }
    }

    public int size()
    {
        return totalK;
    }

    public void reset()
    {
        totalK = 0;
        groups.clear();
    }

    public int getAmount(T key)
    {
        return groups.getOrDefault(key, 0);
    }

    public float getPercentage(T key)
    {
        return totalK <= 0 ? 0 : getAmount(key) / (float) totalK;
    }

    public String getPercentageString(T key)
    {
        return Math.round(getPercentage(key) * 100) + "%";
    }

    public ArrayList<Map.Entry<T, Integer>> getSortedItems()
    {
        return sortedStream().collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<CountingPanelCounter<T>> generateCounters(Hitbox baseHb, ActionT1<CountingPanelCounter<T>> onClick)
    {
        ArrayList<CountingPanelCounter<T>> base = sortedStream().map(entry -> new CountingPanelCounter<>(this, baseHb, entry.getKey(), onClick)).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < base.size(); i++)
        {
            base.get(i).setIndex(i);
        }
        return base;
    }

    @Override
    public Iterator<Map.Entry<T, Integer>> iterator()
    {
        return sortedStream().iterator();
    }

    protected Stream<Map.Entry<T, Integer>> sortedStream()
    {
        // Descending value
        return groups.entrySet().stream().sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()));
    }
}