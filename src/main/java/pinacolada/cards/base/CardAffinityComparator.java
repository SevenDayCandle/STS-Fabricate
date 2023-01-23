package pinacolada.cards.base;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;

import java.util.Comparator;

public class CardAffinityComparator implements Comparator<AbstractCard>
{
    private final PCLAffinity affinity;
    private final boolean descending;

    public CardAffinityComparator(PCLAffinity affinity)
    {
        this(affinity, false);
    }

    public CardAffinityComparator(PCLAffinity affinity, boolean descending)
    {
        this.affinity = affinity;
        this.descending = descending;
    }

    public int calculateRank(AbstractCard card)
    {
        PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
        if (c == null || c.affinities == null)
        {
            return 0;
        }

        return (c.affinities.hasStar() ? 100 : 1000) * c.affinities.getLevel(affinity);
    }

    public int compare(AbstractCard c1, AbstractCard c2)
    {
        int a = calculateRank(c1);
        int b = calculateRank(c2);
        return descending ? (b - a) : (a - b);
    }
}