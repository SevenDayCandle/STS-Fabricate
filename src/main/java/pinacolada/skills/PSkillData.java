package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.fields.PField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PSkillData<T extends PField>
{
    public final String ID;
    public final Class<? extends PSkill<T>> effectClass;
    public final Class<T> fieldType;
    public final Set<AbstractCard.CardColor> colors;
    public final ArrayList<PCLCardGroupHelper> groups = new ArrayList<>();
    public final ArrayList<PCLCardSelection> origins = new ArrayList<>();
    public final ArrayList<PCLCardTarget> targets = new ArrayList<>();
    public int minAmount;
    public int maxAmount;
    public int minExtra = PSkill.DEFAULT_EXTRA_MIN;
    public int maxExtra = PSkill.DEFAULT_EXTRA_MIN;
    public boolean excludeColors;

    public PSkillData(String id, Class<? extends PSkill<T>> effectClass, Class<T> effectType)
    {
        this(id, effectClass, effectType, PSkill.DEFAULT_MAX, PSkill.DEFAULT_PRIORITY);
    }

    public PSkillData(String id, Class<? extends PSkill<T>> effectClass, Class<T> effectType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors)
    {
        this.ID = id;
        this.effectClass = effectClass;
        this.fieldType = effectType;
        this.colors = new HashSet<AbstractCard.CardColor>(Arrays.asList(cardColors));
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

    public final T instantiateField()
    {
        try
        {
            return this.fieldType.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Only register colors registered with the PGR system. Ignores colorless because this needs to be used by other characters too
    // TODO enable if PGR.config.showIrrelevantProperties.get() is enabled
    public PSkillData<T> pclOnly()
    {
        for (PCLResources<?,?,?,?> r : PGR.getRegisteredResources())
        {
            colors.add(r.cardColor);
        }
        return this;
    }

    public PSkillData<T> selfTarget()
    {
        targets.add(PCLCardTarget.Self);
        return this;
    }

    public PSkillData<T> setAmounts(int min, int max)
    {
        this.minAmount = min;
        this.maxAmount = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setColors(AbstractCard.CardColor... colors)
    {
        this.colors.addAll(Arrays.asList(colors));
        return this;
    }

    public PSkillData<T> setExcludeColors(boolean val)
    {
        excludeColors = val;
        return this;
    }

    public PSkillData<T> setExtra(int min, int max)
    {
        this.minExtra = min;
        this.maxExtra = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setGroups(PCLCardGroupHelper... groups)
    {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setOrigins(PCLCardSelection... groups)
    {
        this.origins.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setTargets(PCLCardTarget... targets)
    {
        this.targets.addAll(Arrays.asList(targets));
        return this;
    }
}
