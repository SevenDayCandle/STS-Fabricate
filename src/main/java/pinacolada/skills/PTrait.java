package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PFacetCond;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.traits.*;
import pinacolada.skills.skills.base.traits.PTrait_Affinity;
import pinacolada.skills.skills.special.traits.PTrait_CardTarget;
import pinacolada.skills.skills.special.traits.PTrait_HP;

public abstract class PTrait<T extends PField> extends PSkill<T> {
    protected boolean conditionMetCache;
    protected AbstractCard appliedCard;

    public PTrait(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PTrait(PSkillData<T> data) {
        super(data);
    }

    public PTrait(PSkillData<T> data, int amount) {
        super(data, PCLCardTarget.Self, amount);
    }

    public static PTrait_Affinity affinity(PCLAffinity... tags) {
        return new PTrait_Affinity(tags);
    }

    public static PTrait_Affinity affinity(int amount, PCLAffinity... tags) {
        return new PTrait_Affinity(amount, tags);
    }

    public static PTrait_Affinity affinityNot(PCLAffinity... tags) {
        return new PTrait_Affinity(-1, tags);
    }

    public static PTrait_AttackType attackType(PCLAttackType type) {
        return new PTrait_AttackType(type);
    }

    public static PTrait_Block block(int amount) {
        return new PTrait_Block(amount);
    }

    public static PTrait_BlockCount blockCount(int amount) {
        return new PTrait_BlockCount(amount);
    }

    public static PTrait_BlockMultiplier blockMultiplier(int amount) {
        return new PTrait_BlockMultiplier(amount);
    }

    public static PTrait_CardTarget cardTarget(PCLCardTarget type) {
        return new PTrait_CardTarget(type);
    }

    public static PTrait_Cost cost(int amount) {
        return new PTrait_Cost(amount);
    }

    public static PTrait_Damage damage(int amount) {
        return new PTrait_Damage(amount);
    }

    public static PTrait_DamageMultiplier damageMultiplier(int amount) {
        return new PTrait_DamageMultiplier(amount);
    }

    public static PTrait_HitCount hitCount(int amount) {
        return new PTrait_HitCount(amount);
    }

    public static PTrait_HP hp(int amount) {
        return new PTrait_HP(amount);
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType) {
        return PSkill.register(type, effectType, -DEFAULT_MAX, DEFAULT_MAX)
                .noTarget();
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType, AbstractCard.CardColor... cardColors) {
        return PSkill.register(type, effectType, -DEFAULT_MAX, DEFAULT_MAX, cardColors)
                .noTarget();
    }

    public static PTrait_Tag tags(PCLCardTag... tags) {
        return new PTrait_Tag(tags);
    }

    public static PTrait_Tag tags(int amount, PCLCardTag... tags) {
        return new PTrait_Tag(amount, tags);
    }

    public static PTrait_Tag tagsExact(PCLCardTag... tags) {
        return (PTrait_Tag) new PTrait_Tag(tags).edit(f -> f.setNot(true));
    }

    public static PTrait_Tag tagsExact(int amount, PCLCardTag... tags) {
        return (PTrait_Tag) new PTrait_Tag(amount, tags).edit(f -> f.setNot(true));
    }

    public static PTrait_Tag tagsNot(PCLCardTag... tags) {
        return (PTrait_Tag) new PTrait_Tag(tags).edit(f -> f.setRandom(true));
    }

    public static PTrait_TakeDamage takeDamage(int amount) {
        return new PTrait_TakeDamage(amount);
    }

    public static PTrait_TakeDamageMultiplier takeDamageMultiplier(int amount) {
        return new PTrait_TakeDamageMultiplier(amount);
    }

    public static PTrait_Unplayable unplayable() {
        return new PTrait_Unplayable();
    }

    public void applyToCard(AbstractCard c, boolean conditionMet) {
    }

    protected String getDamageString(PCLCardTarget perspective) {
        if (perspective != PCLCardTarget.Self || !EUIConfiguration.enableDescriptionIcons.get()) {
            return TEXT.subjects_damage;
        }
        EUIKeywordTooltip tooltip = getAttackTooltip();
        if (tooltip == PGR.core.tooltips.normalDamage && !PGR.config.abbreviateEffects.get()) {
            return TEXT.subjects_damage;
        }
        return tooltip.getTitleOrIcon();
    }

    protected String getParentCardString(PCLCardTarget perspective, Object requestor) {
        PSkill<?> par = parent;
        while (par != null) {
            if (par instanceof PFacetCond) {
                return par.getSubText(perspective, requestor);
            }
            par = par.parent;
        }
        return PCLCoreStrings.pluralForce(TEXT.subjects_cardN);
    }

    public String getSampleAmount() {
        return "+X";
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_hasAmount(getSampleAmount(), getSubSampleText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (hasParentType(PTrigger_Passive.class)) {
            return TEXT.act_zHas(getParentCardString(perspective, requestor), getSubDescText(perspective));
        }
        if (isVerbose()) {
            return TEXT.act_has(getSubDescText(perspective));
        }
        return getSubDescText(perspective);
    }

    public boolean isPassiveOnly() {
        return true;
    }

    @Override
    public PTrait<T> makeCopy() {
        PTrait<T> copy = (PTrait<T>) super.makeCopy();
        copy.conditionMetCache = conditionMetCache;
        return copy;
    }

    @Override
    public PTrait<T> onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        appliedCard = card;
        return this;
    }

    @Override
    public PTrait<T> onRemoveFromCard(AbstractCard card) {
        if (conditionMetCache) {
            applyToCard(card, false);
        }
        appliedCard = null;
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet, boolean isUsing) {
        super.refresh(info, conditionMet, isUsing);
        if (info != null && conditionMet != conditionMetCache) {
            conditionMetCache = conditionMet;
            applyToCard(info.card, conditionMet);
        }
    }

    @Override
    public PTrait<T> setAmount(int amount) {
        if (conditionMetCache && appliedCard != null) {
            applyToCard(appliedCard, false);
            conditionMetCache = false;
        }
        super.setAmount(amount);
        return this;
    }

    @Override
    public PTrait<T> setTemporaryAmount(int amount) {
        if (conditionMetCache && appliedCard != null) {
            applyToCard(appliedCard, false);
            conditionMetCache = false;
        }
        super.setTemporaryAmount(amount);
        return this;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    @Override
    public String wrapAmount(int input) {
        return input >= 0 ? "+" + input : String.valueOf(input);
    }

    abstract public String getSubDescText(PCLCardTarget perspective);

    abstract public String getSubSampleText();

}
