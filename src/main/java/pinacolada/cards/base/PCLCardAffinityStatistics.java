package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardAffinities;

import java.util.*;

public class PCLCardAffinityStatistics implements Iterable<PCLCardAffinityStatistics.Group>
{
    protected final ArrayList<PCLCardAffinities> cardsAffinities = new ArrayList<>();
    protected final ArrayList<Group> groups = new ArrayList<>();
    protected int cards;

    public PCLCardAffinityStatistics()
    {

    }

    public PCLCardAffinityStatistics(Collection<AbstractCard> cards, boolean useStar)
    {
        addCards(cards);
        refreshStatistics(useStar);
    }

    public static PCLCardAffinities getAffinitiesFromCard(AbstractCard card)
    {
        return card instanceof PCLCard ? ((PCLCard) card).affinities : null;
    }

    public void addAugment(PCLAugmentData data, int count)
    {
        final PCLCardAffinities a = new PCLCardAffinities(null);
        a.set(data.affinity, count);
        cardsAffinities.add(a);
        cards += 1;
    }

    public void addAugments(HashMap<PCLAugmentData, Integer> augments)
    {
        for (Map.Entry<PCLAugmentData, Integer> params : augments.entrySet())
        {
            addAugment(params.getKey(), params.getValue());
        }
    }

    public void addCard(AbstractCard card)
    {
        final PCLCardAffinities a = getAffinitiesFromCard(card);
        if (a != null)
        {
            cardsAffinities.add(a);
        }

        cards += 1;
    }

    public void addCards(CardGroup group)
    {
        addCards(group.group);
    }

    public void addCards(Collection<AbstractCard> cards)
    {
        for (AbstractCard c : cards)
        {
            addCard(c);
        }
    }

    public int cardsCount()
    {
        return cards;
    }

    public ArrayList<PCLCardAffinities> getAffinities()
    {
        return cardsAffinities;
    }

    public Group getGroup(int index)
    {
        return groups.get(index);
    }

    public Group getGroup(PCLAffinity affinity)
    {
        for (Group g : groups)
        {
            if (g.affinity == affinity)
            {
                return g;
            }
        }

        Group g = new Group(this, affinity);
        groups.add(g);
        return g;
    }

    public ArrayList<Group> refreshStatistics(boolean useStar)
    {
        for (Group g : groups)
        {
            g.reset();
        }

        for (PCLCardAffinities a : cardsAffinities)
        {
            for (PCLAffinity t : PCLAffinity.all())
            {
                int level = a.getLevel(t, useStar);
                getGroup(t).add(level);
            }
        }

        groups.sort(Comparator.comparingInt(a -> -a.getTotal()));// descending
        return groups;
    }

    public void reset()
    {
        cards = 0;
        cardsAffinities.clear();

        for (Group g : groups)
        {
            g.reset();
        }
    }

    @Override
    public Iterator<Group> iterator()
    {
        return groups.iterator();
    }

    public static class Group
    {
        public PCLAffinity affinity;
        public PCLCardAffinityStatistics statistics;
        public int size;
        public int totalLv1;
        public int totalLv2;

        public Group(PCLCardAffinityStatistics statistics, PCLAffinity affinity)
        {
            this.affinity = affinity;
            this.statistics = statistics;
        }

        public void add(int level)
        {
            size += 1;
            if (level == 1)
            {
                totalLv1 += 1;
            }
            else if (level > 1)
            {
                totalLv2 += 1;
            }
        }

        public ArrayList<AbstractCard> getCards()
        {
            final ArrayList<AbstractCard> cards = new ArrayList<>();
            for (PCLCardAffinities a : statistics.getAffinities())
            {
                if (a.getLevel(affinity) > 0)
                {
                    cards.add(a.card);
                }
            }

            return cards;
        }

        public float getPercentage(int level)
        {
            return getTotal(level) / (float) size;
        }

        public String getPercentageString(int level)
        {
            return Math.round(getPercentage(level) * 100) + "%";
        }

        public int getTotal()
        {
            return totalLv1 + totalLv2;
        }

        public int getTotal(int level)
        {
            return level == 1 ? totalLv1 : level > 1 ? totalLv2 : getTotal();
        }

        public void reset()
        {
            size = totalLv1 = totalLv2 = 0;
        }
    }
}