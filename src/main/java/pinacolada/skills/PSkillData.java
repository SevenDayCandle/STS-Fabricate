package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.fields.PField;

import java.util.*;

import static pinacolada.skills.PSkill.DEFAULT_MAX;

public class PSkillData<T extends PField> {
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

    public PSkillData(String id, Class<? extends PSkill<T>> effectClass, Class<T> effectType) {
        this(id, effectClass, effectType, 0, DEFAULT_MAX);
    }

    public PSkillData(String id, Class<? extends PSkill<T>> effectClass, Class<T> effectType, int minAmount, int maxAmount, AbstractCard.CardColor... cardColors) {
        this.ID = id;
        this.effectClass = effectClass;
        this.fieldType = effectType;
        this.colors = new HashSet<>(Arrays.asList(cardColors));
        this.minAmount = minAmount;
        this.maxAmount = Math.max(minAmount, maxAmount);
    }

    public final boolean amountViewable() {
        return minAmount < maxAmount;
    }

    public final T instantiateField() {
        try {
            return this.fieldType.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final boolean isColorCompatible(AbstractCard.CardColor co) {
        return colors.isEmpty() || (excludeColors ^ colors.contains(co));
    }

    // Only register colors registered with the PGR system. Ignores colorless because this needs to be used by other characters too
    public PSkillData<T> pclOnly() {
        for (PCLResources<?, ?, ?, ?> r : PGR.getRegisteredResources()) {
            colors.add(r.cardColor);
        }
        return this;
    }

    public PSkillData<T> selfTarget() {
        targets.add(PCLCardTarget.Self);
        return this;
    }

    public PSkillData<T> setAmounts(int min, int max) {
        this.minAmount = min;
        this.maxAmount = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setColors(AbstractCard.CardColor... colors) {
        this.colors.addAll(Arrays.asList(colors));
        return this;
    }

    public PSkillData<T> setExcludeColors(boolean val) {
        excludeColors = val;
        return this;
    }

    public PSkillData<T> setExtra(int min, int max) {
        this.minExtra = min;
        this.maxExtra = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setGroups(Collection<PCLCardGroupHelper> groups) {
        this.groups.addAll(groups);
        return this;
    }

    public PSkillData<T> setGroups(PCLCardGroupHelper... groups) {
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setOrigins(PCLCardSelection... groups) {
        this.origins.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setTargets(PCLCardTarget... targets) {
        this.targets.addAll(Arrays.asList(targets));
        return this;
    }
}
