package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.fields.PCLCardAffinity;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static pinacolada.augments.PCLAugment.WEIGHT_MODIFIER;

public class PCLAugmentWeights
{
    protected final HashMap<PCLAugmentCategory, Integer> weights = new HashMap<>();
    protected int total;
    protected int rareModifier;

    public PCLAugmentWeights(PCLAugmentCategory... affinities)
    {
        this(WEIGHT_MODIFIER + 1, affinities);
    }

    public PCLAugmentWeights(int amount, PCLAugmentCategory... affinities)
    {
        for (PCLAugmentCategory aff : affinities)
        {
            weights.put(aff, amount);
            total += amount;
        }
    }

    public PCLAugmentWeights(AbstractCard c)
    {
        for (PCLAugmentCategory category : PCLAugmentCategory.values())
        {
            if (category.isTypeValid(c.type))
            {
                weights.put(category, 1);
                total += 1;
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
            case Yellow:
            case Purple:
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

    public float getPercentage(PCLAugmentCategory affinity)
    {
        return getWeight(affinity) * 100f / (float) Math.max(1, total);
    }

    public int getRareModifier()
    {
        return rareModifier;
    }

    public int getWeight(PCLAugmentCategory affinity)
    {
        return weights.getOrDefault(affinity, 0);
    }

    public List<PCLAugmentCategory> sortedKeys()
    {
        return weights.keySet().stream().sorted((a, b) -> Float.compare(weights.getOrDefault(b, 0), weights.getOrDefault(a, 0))).collect(Collectors.toList());
    }
}
