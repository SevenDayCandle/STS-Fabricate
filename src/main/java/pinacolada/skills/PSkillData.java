package pinacolada.skills;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.fields.PField;
import pinacolada.ui.editor.PCLCustomPowerEffectPage;
import pinacolada.ui.editor.blight.PCLCustomBlightEditScreen;
import pinacolada.ui.editor.card.PCLCustomCardEditScreen;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;
import pinacolada.ui.editor.power.PCLCustomPowerEditScreen;
import pinacolada.ui.editor.relic.PCLCustomRelicEditScreen;

import java.util.*;

import static pinacolada.skills.PSkill.DEFAULT_MAX;

public class PSkillData<T extends PField> {
    public final String ID;
    public final Class<? extends PSkill<T>> effectClass;
    public final Class<T> fieldType;
    public final Set<AbstractCard.CardColor> colors;
    public ArrayList<PCLCardGroupHelper> groups;
    public ArrayList<PCLCardSelection> destinations;
    public ArrayList<PCLCardSelection> origins;
    public ArrayList<PCLCardTarget> targets;
    public ArrayList<SourceType> sourceTypes;
    public int minAmount;
    public int maxAmount;
    public int minExtra = PSkill.DEFAULT_EXTRA_MIN;
    public int maxExtra = PSkill.DEFAULT_EXTRA_MIN;
    public int minExtra2;
    public int maxExtra2;

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
        return colors.isEmpty() || (colors.contains(co));
    }

    public PSkillData<T> noTarget() {
        this.targets = new ArrayList<>();
        targets.add(PCLCardTarget.None);
        return this;
    }

    // Only register colors registered with the PGR system. Ignores colorless because this needs to be used by other characters too
    public PSkillData<T> pclOnly() {
        for (PCLResources<?, ?, ?, ?> r : PGR.getRegisteredResources()) {
            colors.add(r.cardColor);
        }
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

    public PSkillData<T> setDestinations(PCLCardSelection... groups) {
        this.destinations = new ArrayList<>();
        this.destinations.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setExtra(int min, int max) {
        this.minExtra = min;
        this.maxExtra = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setExtra2(int min, int max) {
        this.minExtra2 = min;
        this.maxExtra2 = Math.max(min, max);
        return this;
    }

    public PSkillData<T> setGroups(Collection<PCLCardGroupHelper> groups) {
        this.groups = new ArrayList<>();
        this.groups.addAll(groups);
        return this;
    }

    public PSkillData<T> setGroups(PCLCardGroupHelper... groups) {
        this.groups = new ArrayList<>();
        this.groups.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setOrigins(PCLCardSelection... groups) {
        this.origins = new ArrayList<>();
        this.origins.addAll(Arrays.asList(groups));
        return this;
    }

    public PSkillData<T> setSourceTypes(SourceType... targets) {
        this.sourceTypes = new ArrayList<>();
        this.sourceTypes.addAll(Arrays.asList(targets));
        return this;
    }

    public PSkillData<T> setTargets(PCLCardTarget... targets) {
        this.targets = new ArrayList<>();
        this.targets.addAll(Arrays.asList(targets));
        return this;
    }

    public enum SourceType {
        Card,
        Collectible,
        Power;

        public boolean isSourceAllowed(PCLCustomEffectNode node) {
            switch (this) {
                case Card:
                    return node.editor.screen instanceof PCLCustomCardEditScreen;
                case Collectible:
                    return !(node.editor.screen instanceof PCLCustomCardEditScreen);
                case Power:
                    return node.editor instanceof PCLCustomPowerEffectPage || node.editor.screen instanceof PCLCustomRelicEditScreen || node.editor.screen instanceof PCLCustomBlightEditScreen || node.editor.screen instanceof PCLCustomPowerEditScreen;
            }
            return true;
        }

        public boolean isSourceAllowed(PSkill<?> skill) {
            switch (this) {
                case Card:
                    return skill.source instanceof AbstractCard;
                case Collectible:
                    return !(skill.source instanceof AbstractCard);
                case Power:
                    return skill.source instanceof AbstractRelic || skill.source instanceof AbstractBlight || skill.source instanceof AbstractPower;
            }
            return true;
        }
    }
}
