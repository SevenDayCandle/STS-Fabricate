package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardAffinities;
import pinacolada.cards.base.fields.PCLCardAffinity;
import pinacolada.cards.base.fields.PCLCardDataAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.*;

public class PCLCardAffinityStatistics implements Iterable<PCLCardAffinityStatistics.Group>
{
    protected final ArrayList<Group> groups = new ArrayList<>();
    private int count;

    public PCLCardAffinityStatistics()
    {

    }

    public PCLCardAffinityStatistics(Collection<PCLCardData> count)
    {
        addCardDatas(count);
        sortGroups();
    }

    private void addInternal(PCLAffinity affinity, int count)
    {
        getGroup(affinity).add(count);
    }

    public void addAugment(PCLAugmentData data, int count)
    {
        addInternal(data.affinity, count);
        this.count += 1;
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
        final PCLCardAffinities a = GameUtilities.getPCLCardAffinities(card);
        if (a != null)
        {
            for (PCLCardAffinity affinity : a.getCardAffinities())
            {
                addInternal(affinity.type, affinity.level);
            }
        }

        this.count += 1;
    }

    public void addCardData(PCLCardData data)
    {
        for (PCLCardDataAffinity affinity : data.affinities.getCardAffinities())
        {
            addInternal(affinity.type, affinity.get(0));
        }

        this.count += 1;
    }

    public PCLCardAffinityStatistics addCards(CardGroup group)
    {
        addCards(group.group);
        return this;
    }

    public PCLCardAffinityStatistics addCards(Collection<AbstractCard> cards)
    {
        for (AbstractCard c : cards)
        {
            addCard(c);
        }
        return this;
    }

    public PCLCardAffinityStatistics addCardDatas(Collection<PCLCardData> cards)
    {
        for (PCLCardData c : cards)
        {
            addCardData(c);
        }
        return this;
    }

    public int size()
    {
        return count;
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

    public ArrayList<Group> sortGroups()
    {
        groups.sort(Comparator.comparingInt(a -> -a.getTotal()));
        return groups;
    }

    public void reset()
    {
        count = 0;
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