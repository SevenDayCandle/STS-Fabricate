package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PCLEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PSkillData
{
    public final String ID;
    public final Class<? extends PSkill> effectClass;
    public final PSkill.PCLEffectType effectType;
    public final Set<AbstractCard.CardColor> colors;
    public final ArrayList<PCLCardGroupHelper> groups = new ArrayList<>();
    public final ArrayList<PCLCardTarget> targets = new ArrayList<>();
    public final int priority;
    public int minAmount;
    public int maxAmount;
    public int minExtra = -1;
    public int maxExtra = -1;
    public String altText;
    public String alt2Text;
    public boolean excludeColors;

    public PSkillData(String id, Class<? extends PSkill> effectClass, PSkill.PCLEffectType effectType)
    {
        this(id, effectClass, effectType, 1, PSkill.DEFAULT_MAX, PSkill.DEFAULT_PRIORITY);
    }

    public PSkillData(String id, Class<? extends PSkill> effectClass, PSkill.PCLEffectType effectType, int minAmount, int maxAmount)
    {
        this(id, effectClass, effectType, minAmount, maxAmount, PSkill.DEFAULT_PRIORITY);
    }

    public PSkillData(String id, Class<? extends PSkill> effectClass, PSkill.PCLEffectType effectType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        this(id, effectClass, effectType, PSkill.DEFAULT_PRIORITY, minAmount, maxAmount, cardColors);
    }

    public PSkillData(String id, Class<? extends PSkill> effectClass, PSkill.PCLEffectType effectType, int priority, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        this.ID = id;
        this.effectClass = effectClass;
        this.effectType = effectType;
        this.colors = new HashSet<AbstractCard.CardColor>(Arrays.asList(cardColors));
        this.priority = priority;
        this.minAmount = minAmount;
        this.maxAmount = Math.max(minAmount, maxAmount);
    }

    public final boolean amountViewable()
    {
        return minAmount < maxAmount;
    }

    public final boolean isColorCompatible(AbstractCard.CardColor co)
    {
        return colors.isEmpty() || (excludeColors ^ colors.contains(co));
    }

    public final boolean matchesPriority(Integer pr)
    {
        return pr == null || priority == pr;
    }

    public PSkillData pclOnly()
    {
        colors.add(PCLEnum.Cards.THE_ETERNAL);
        colors.add(PCLEnum.Cards.THE_CONJURER);
        colors.add(PCLEnum.Cards.THE_DECIDER);
        return this;
    }

    public PSkillData selfTarget()
    {
        targets.add(PCLCardTarget.Self);
        return this;
    }

    public PSkillData setAltText(String val)
    {
        altText = val;
        return this;
    }

    public PSkillData setAlt2Text(String val)
    {
        alt2Text = val;
        return this;
    }

    public PSkillData setAmounts(int min, int max)
    {
        this.minAmount = min;
        this.maxAmount = Math.max(min, max);
        return this;
    }

    public PSkillData setColors(AbstractCard.CardColor... colors)
    {
        this.colors.addAll(Arrays.asList(colors));
        return this;
    }

    public PSkillData setExcludeColors(boolean val)
    {
        excludeColors = val;
        return this;
    }

    public PSkillData setExtra(int min, int max)
    {
        this.minExtra = min;
        this.maxExtra = Math.max(min, max);
        return this;
    }

    public PSkillData setGroups(PCLCardGroupHelper... groups)
    {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData setTargets(PCLCardTarget... targets)
    {
        this.targets.addAll(Arrays.asList(targets));
        return this;
    }
}
