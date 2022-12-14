package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardAffinities;
import pinacolada.cards.base.PCLCardAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static pinacolada.augments.PCLAugment.WEIGHT_MODIFIER;

public class PCLAugmentWeights
{
    protected final HashMap<PCLAffinity, Integer> weights = new HashMap<>();
    protected int total;
    protected int rareModifier;

    public PCLAugmentWeights(PCLAffinity... affinities)
    {
        this(WEIGHT_MODIFIER + 1, affinities);
    }

    public PCLAugmentWeights(int amount, PCLAffinity... affinities)
    {
        for (PCLAffinity aff : affinities)
        {
            weights.put(aff, amount);
            total += amount;
        }
    }

    public PCLAugmentWeights(AbstractCard c)
    {
        PCLCardAffinities cAff = GameUtilities.getPCLCardAffinities(c);
        if (cAff != null)
        {
            for (PCLCardAffinity aff : cAff.getCardAffinities(true))
            {
                int value = getAffinityBaseLevel(aff);
                weights.put(aff.type, value);
                total += value;
            }
        }

        switch (c.rarity)
        {
            case UNCOMMON:
                rareModifier = 1;
                break;
            case RARE:
            case CURSE:
                rareModifier = 2;
                break;
            case SPECIAL:
                rareModifier = 3;
                break;
        }
    }

    protected int getAffinityBaseLevel(PCLCardAffinity aff)
    {
        switch (aff.type)
        {
            case Red:
            case Green:
            case Blue:
            case Orange:
                return aff.level * 4;
            case Light:
            case Dark:
                return aff.level * 2;
        }
        return aff.level;
    }

    public HashMap<PCLAugmentData, Integer> getPerAugmentWeights()
    {
        HashMap<PCLAugmentData, Integer> dataMap = new HashMap<>();
        for (PCLAugmentData data : PCLAugment.getValues())
        {
            PCLAugment augment = data.create();
            int weight = PCLAugment.getWeight(this, data);
            if (weight > 0)
            {
                dataMap.put(data, weight);
            }
        }
        return dataMap;
    }

    public float getPercentage(PCLAffinity affinity)
    {
        return getWeight(affinity) * 100f / (float) Math.max(1, total);
    }

    public int getRareModifier()
    {
        return rareModifier;
    }

    public int getWeight(PCLAffinity affinity)
    {
        return weights.getOrDefault(affinity, 0);
    }

    public List<PCLAffinity> sortedKeys()
    {
        return weights.keySet().stream().sorted((a, b) -> Float.compare(weights.getOrDefault(b, 0), weights.getOrDefault(a, 0))).collect(Collectors.toList());
    }
}
