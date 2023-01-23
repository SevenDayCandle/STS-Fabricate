package pinacolada.augments;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;
import java.util.HashSet;

public class PCLAugmentReqs
{
    public HashSet<AbstractCard.CardColor> colors = new HashSet<>();
    public HashSet<AbstractCard.CardRarity> rarities = new HashSet<>();
    public HashSet<AbstractCard.CardType> types = new HashSet<>();
    public HashSet<PCLAffinity> affinities = new HashSet<>();
    public HashSet<PCLAffinity> affinitiesNot = new HashSet<>();
    public HashSet<PCLAttackType> attackTypesNot = new HashSet<>();
    public HashSet<PCLCardTag> tags = new HashSet<>();
    public HashSet<PCLCardTag> tagsNot = new HashSet<>();
    public HashSet<PCLCardTarget> cardTargets = new HashSet<>();
    public int blockMax = Integer.MAX_VALUE;
    public int costMin = Integer.MIN_VALUE;
    public int costMax = Integer.MAX_VALUE;
    public int damageMax = Integer.MAX_VALUE;
    public int hitsMax = Integer.MAX_VALUE;
    public int rightMax = Integer.MAX_VALUE;
    public int maxEffects = Integer.MAX_VALUE;

    public boolean check(PCLCard c)
    {
        return c != null && (
                (colors.isEmpty() || colors.contains(c.color))
                        && (rarities.isEmpty() || rarities.contains(c.rarity))
                        && (types.isEmpty() || types.contains(c.type))
                        && (cardTargets.isEmpty() || cardTargets.contains(c.pclTarget))
                        && (affinities.isEmpty() || GameUtilities.hasAnyAffinity(c, affinities))
                        && (affinitiesNot.isEmpty() || !GameUtilities.hasAnyAffinity(c, affinitiesNot))
                        && (tags.isEmpty() || EUIUtils.all(tags, t -> t.has(c)))
                        && (tagsNot.isEmpty() || EUIUtils.all(tagsNot, t -> !t.has(c)))
                        && c.baseBlock <= blockMax
                        && c.cost >= costMin && c.cost <= costMax
                        && c.baseDamage <= damageMax
                        && c.baseHitCount <= hitsMax
                        && c.baseRightCount <= rightMax
                        && c.getEffects().size() <= maxEffects);
    }

    public String getString()
    {
        String base = EUIUtils.joinTrueStrings(", ",
                colors.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(colors, EUIGameUtils::getColorName)),
                affinities.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(affinities, a -> a.getTooltip().getTitleOrIcon())),
                affinitiesNot.isEmpty() ? null : PGR.core.strings.conditions.not(PCLCoreStrings.joinWithOr(EUIUtils.map(affinitiesNot, a -> a.getTooltip().getTitleOrIcon()))),
                cardTargets.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(cardTargets, PCLCardTarget::getTitle)),
                tags.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(tags, a -> a.getTooltip().getTitleOrIcon())),
                tagsNot.isEmpty() ? null : PGR.core.strings.conditions.not(PCLCoreStrings.joinWithOr(EUIUtils.map(tagsNot, a -> a.getTooltip().getTitleOrIcon()))),
                rarities.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(rarities, EUIGameUtils::textForRarity)),
                types.isEmpty() ? null : PCLCoreStrings.joinWithOr(EUIUtils.map(types, type -> EUIGameUtils.textForType(type))),
                damageMax != Integer.MAX_VALUE ? PGR.core.strings.subjects.damage + " <= " + damageMax : null,
                blockMax != Integer.MAX_VALUE ? PGR.core.tooltips.block + " <= " + blockMax : null,
                costMax != Integer.MAX_VALUE ? PGR.core.strings.subjects.cost + " <= " + costMax : null,
                costMin != Integer.MIN_VALUE ? PGR.core.strings.subjects.cost + " >= " + costMin : null,
                hitsMax != Integer.MAX_VALUE ? PGR.core.strings.subjects.hits + " <= " + hitsMax : null,
                rightMax != Integer.MAX_VALUE ? PGR.core.strings.subjects.count(PGR.core.tooltips.block) + " <= " + rightMax : null,
                maxEffects != Integer.MAX_VALUE ? PGR.core.strings.cardEditor.effects + " <= " + maxEffects : null
        );
        return base.isEmpty() ? PGR.core.strings.combat.na : base;
    }

    public PCLAugmentReqs setAffinities(PCLAffinity... values)
    {
        this.affinities.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setAffinitiesNot(PCLAffinity... values)
    {
        this.affinitiesNot.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setMaxBlock(int value)
    {
        this.blockMax = value;
        return this;
    }

    public PCLAugmentReqs setMaxCost(int value)
    {
        this.costMax = value;
        return this;
    }

    public PCLAugmentReqs setMaxDamage(int value)
    {
        this.damageMax = value;
        return this;
    }

    public PCLAugmentReqs setMaxHits(int value)
    {
        this.hitsMax = value;
        return this;
    }

    public PCLAugmentReqs setMaxRight(int value)
    {
        this.rightMax = value;
        return this;
    }

    public PCLAugmentReqs setRarities(AbstractCard.CardRarity... values)
    {
        this.rarities.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setTags(PCLCardTag... values)
    {
        this.tags.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setTagsNot(PCLCardTag... values)
    {
        this.tagsNot.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setTargets(PCLCardTarget... values)
    {
        this.cardTargets.addAll(Arrays.asList(values));
        return this;
    }

    public PCLAugmentReqs setTypes(AbstractCard.CardType... values)
    {
        this.types.addAll(Arrays.asList(values));
        return this;
    }
}
