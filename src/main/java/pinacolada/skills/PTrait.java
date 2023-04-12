package pinacolada.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.base.traits.*;
import pinacolada.skills.skills.special.traits.PTrait_Affinity;
import pinacolada.skills.skills.special.traits.PTrait_CardTarget;
import pinacolada.skills.skills.special.traits.PTrait_HP;
import pinacolada.skills.skills.special.traits.PTrait_Priority;

public abstract class PTrait<T extends PField> extends PMove<T>
{
    protected boolean conditionMetCache;

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType)
    {
        return PSkill.register(type, effectType, -DEFAULT_MAX, DEFAULT_MAX)
                .selfTarget();
    }

    public static <T extends PField> PSkillData<T> register(Class<? extends PSkill<T>> type, Class<T> effectType, AbstractCard.CardColor... cardColors)
    {
        return PSkill.register(type, effectType, -DEFAULT_MAX, DEFAULT_MAX, cardColors)
                .selfTarget();
    }

    public static PTrait_Affinity affinity(PCLAffinity... tags)
    {
        return new PTrait_Affinity(tags);
    }

    public static PTrait_Affinity affinity(int amount, PCLAffinity... tags)
    {
        return new PTrait_Affinity(amount, tags);
    }

    public static PTrait_Affinity affinityNot(PCLAffinity... tags)
    {
        return new PTrait_Affinity(-1, tags);
    }

    public static PTrait_AttackType attackType(PCLAttackType type)
    {
        return new PTrait_AttackType(type);
    }

    public static PTrait_Block block(int amount)
    {
        return new PTrait_Block(amount);
    }

    public static PTrait_BlockCount blockCount(int amount)
    {
        return new PTrait_BlockCount(amount);
    }

    public static PTrait_BlockMultiplier blockMultiplier(int amount)
    {
        return new PTrait_BlockMultiplier(amount);
    }

    public static PTrait_CardTarget cardTarget(PCLCardTarget type)
    {
        return new PTrait_CardTarget(type);
    }

    public static PTrait_Cost cost(int amount)
    {
        return new PTrait_Cost(amount);
    }

    public static PTrait_Damage damage(int amount)
    {
        return new PTrait_Damage(amount);
    }

    public static PTrait_DamageMultiplier damageMultiplier(int amount)
    {
        return new PTrait_DamageMultiplier(amount);
    }

    public static PTrait_HitCount hitCount(int amount)
    {
        return new PTrait_HitCount(amount);
    }

    public static PTrait_HP hp(int amount)
    {
        return new PTrait_HP(amount);
    }

    public static PTrait_Priority priority(int amount)
    {
        return new PTrait_Priority(amount);
    }

    public static PTrait_Tag tags(PCLCardTag... tags)
    {
        return new PTrait_Tag(tags);
    }

    public static PTrait_Tag tags(int amount, PCLCardTag... tags)
    {
        return new PTrait_Tag(tags);
    }

    public static PTrait_Tag tagsNot(PCLCardTag... tags)
    {
        return (PTrait_Tag) new PTrait_Tag(tags).edit(f -> f.setRandom(true));
    }

    public PTrait(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PTrait(PSkillData<T> data)
    {
        super(data);
    }

    public PTrait(PSkillData<T> data, int amount)
    {
        super(data, PCLCardTarget.Self, amount);
    }

    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
    }

    public String getSampleAmount()
    {
        return "+X";
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_hasAmount(getSampleAmount(), getSubSampleText());
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.numNoun(getAmountRawString(), getSubDescText());
    }

    @Override
    public PTrait<T> makeCopy()
    {
        PTrait<T> copy = (PTrait<T>) super.makeCopy();
        copy.conditionMetCache = conditionMetCache;
        return copy;
    }

    @Override
    public PTrait<T> onRemoveFromCard(AbstractCard card)
    {
        if (conditionMetCache)
        {
            applyToCard(card, false);
        }
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        super.refresh(info, conditionMet);
        if (info != null && conditionMet != conditionMetCache)
        {
            conditionMetCache = conditionMet;
            applyToCard(info.card, conditionMet);
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
