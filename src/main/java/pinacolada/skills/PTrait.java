package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.interfaces.markers.PSkillAttribute;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.skills.base.traits.*;
import pinacolada.skills.skills.special.traits.PTrait_Affinity;
import pinacolada.skills.skills.special.traits.PTrait_AttackType;
import pinacolada.skills.skills.special.traits.PTrait_CardTarget;
import pinacolada.stances.PCLStanceHelper;

public abstract class PTrait extends PSkill implements PSkillAttribute
{
    protected boolean conditionMetCache;

    public PTrait(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait(PSkillData data)
    {
        super(data);
    }

    public PTrait(PSkillData data, int amount)
    {
        super(data, PCLCardTarget.Self, amount);
    }

    public PTrait(PSkillData data, int amount, PSkill effect)
    {
        super(data, PCLCardTarget.Self, amount);
    }

    public PTrait(PSkillData data, int amount, PSkill... effect)
    {
        super(data, PCLCardTarget.Self, amount);
    }

    public PTrait(PSkillData data, int amount, PCLCardGroupHelper... groups)
    {
        super(data, PCLCardTarget.Self, amount, groups);
    }

    public PTrait(PSkillData data, int amount, PCLAffinity... affinities)
    {
        super(data, PCLCardTarget.Self, amount, affinities);
    }

    public PTrait(PSkillData data, int amount, PCLPowerHelper... powers)
    {
        super(data, PCLCardTarget.Self, amount, powers);
    }

    public PTrait(PSkillData data, int amount, PCLOrbHelper... orbs)
    {
        super(data, PCLCardTarget.Self, amount, orbs);
    }

    public PTrait(PSkillData data, int amount, PCLCardTag... tags)
    {
        super(data, amount, tags);
    }

    public PTrait(PSkillData data, PCLStanceHelper... stance)
    {
        super(data, stance);
    }

    public PTrait(PSkillData data, PCLCardTag... tags)
    {
        super(data, tags);
    }

    public static PTrait hasAffinity(PCLAffinity... tags)
    {
        return new PTrait_Affinity(tags);
    }

    public static PTrait hasAffinity(int amount, PCLAffinity... tags)
    {
        return new PTrait_Affinity(amount, tags);
    }

    public static PTrait hasAffinityNot(PCLAffinity... tags)
    {
        return new PTrait_Affinity(-1, tags);
    }

    public static PTrait hasAttackType(PCLAttackType type)
    {
        return new PTrait_AttackType(type);
    }

    public static PTrait hasBlock(int amount)
    {
        return new PTrait_Block(amount);
    }

    public static PTrait hasBlockCount(int amount)
    {
        return new PTrait_BlockCount(amount);
    }

    public static PTrait hasBlockMultiplier(int amount)
    {
        return new PTrait_BlockMultiplier(amount);
    }

    public static PTrait hasCardTarget(PCLCardTarget type)
    {
        return new PTrait_CardTarget(type);
    }

    public static PTrait hasCost(int amount)
    {
        return new PTrait_Cost(amount);
    }

    public static PTrait hasDamage(int amount)
    {
        return new PTrait_Damage(amount);
    }

    public static PTrait hasDamageMultiplier(int amount)
    {
        return new PTrait_DamageMultiplier(amount);
    }

    public static PTrait hasHits(int amount)
    {
        return new PTrait_HitCount(amount);
    }

    public static PTrait hasTags(PCLCardTag... tags)
    {
        return new PTrait_Tag(tags);
    }

    public static PTrait hasTags(int amount, PCLCardTag... tags)
    {
        return new PTrait_Tag(tags);
    }

    public static PTrait hasTagsNot(PCLCardTag... tags)
    {
        return (PTrait) new PTrait_Tag(tags).setAlt(true);
    }

    public static PTrait hasTempHP(int amount)
    {
        return new PTrait_TempHP(amount);
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), -DEFAULT_MAX, DEFAULT_MAX)
                .selfTarget();
    }

    public static PSkillData register(Class<? extends PSkill> type, PCLEffectType effectType, AbstractCard.CardColor... cardColors)
    {
        return PSkill.register(type, effectType, getDefaultPriority(type), -DEFAULT_MAX, DEFAULT_MAX, cardColors)
                .selfTarget();
    }

    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
    }

    public String getSampleAmount()
    {
        return "+X";
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.hasAmount(getSampleAmount(), getSubSampleText());
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.numNoun(getAmountRawString(), getSubDescText());
    }

    @Override
    public PTrait makeCopy()
    {
        PTrait copy = (PTrait) super.makeCopy();
        copy.conditionMetCache = conditionMetCache;
        return copy;
    }

    @Override
    public PSkill onRemoveFromCard(AbstractCard card)
    {
        if (conditionMetCache)
        {
            applyToCard(card, false);
        }
        return super.onRemoveFromCard(card);
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        if (c != null && conditionMet != conditionMetCache)
        {
            conditionMetCache = conditionMet;
            applyToCard(c, conditionMet);
        }
    }

    @Override
    public void use(PCLUseInfo info)
    {

    }

    @Override
    public String wrapAmount(int input)
    {
        return input > 0 ? "+" + input : String.valueOf(input);
    }

    abstract public String getSubDescText();

    abstract public String getSubSampleText();

}
