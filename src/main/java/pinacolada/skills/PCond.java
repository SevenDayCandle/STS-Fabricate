package pinacolada.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.base.conditions.*;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.skills.skills.special.conditions.PCond_Match;
import pinacolada.skills.skills.special.primary.PTrigger_Passive;
import pinacolada.utilities.GameUtilities;

public abstract class PCond<T extends PField> extends PSkill<T>
{
    public static final int CONDITION_PRIORITY = 1;
    public static final int SPECIAL_CONDITION_PRIORITY = 0;
    protected boolean conditionMetCache = false;

    public PCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond(PSkillData<T> data)
    {
        super(data);
    }

    public PCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public static PCond_CheckBlock block(int amount)
    {
        return new PCond_CheckBlock(PCLCardTarget.Self, amount);
    }

    public static PCond_CheckBlock block(PCLCardTarget target, int amount)
    {
        return new PCond_CheckBlock(target, amount);
    }

    public static PCond_CheckEnergy checkEnergy(int amount)
    {
        return new PCond_CheckEnergy(amount);
    }

    public static PCond_CheckLevel checkLevel(int amount, PCLAffinity... affinities)
    {
        return new PCond_CheckLevel(amount, affinities);
    }

    public static PCond_CheckOrb checkOrb(int amount, PCLOrbHelper... orbs)
    {
        return new PCond_CheckOrb(amount, orbs);
    }

    public static PCond_CheckPower checkPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(target, amount, powers);
    }

    public static PCond_CheckPower checkPowerAoe(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.AllEnemy, amount, powers);
    }

    public static PCond_CheckPower checkPowerSelf(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.Self, amount, powers);
    }

    public static PCond_CheckPower checkPowerSingle(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.Single, amount, powers);
    }

    public static PCond_Cooldown cooldown(int amount)
    {
        return new PCond_Cooldown(amount);
    }

    public static PCond_CycleTo cycle(int amount)
    {
        return new PCond_CycleTo(amount);
    }

    public static PCond_CycleTo cycleRandom(int amount)
    {
        return (PCond_CycleTo) new PCond_CycleTo(amount).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_DiscardTo discard(int amount)
    {
        return new PCond_DiscardTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discard(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_DiscardTo(amount, h);
    }

    public static PCond_DiscardBranch discardBranch(int amount, PCLCardGroupHelper... groups)
    {
        return new PCond_DiscardBranch(amount, groups);
    }

    public static PCond_DiscardTo discardRandom(int amount)
    {
        return discardRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discardRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_DiscardTo) new PCond_DiscardTo(amount, h).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_DrawBranch drawBranch(int amount)
    {
        return new PCond_DrawBranch(amount);
    }

    public static PCond_EvokeTo evokeTo(int amount, PCLOrbHelper... h)
    {
        return new PCond_EvokeTo(amount, h);
    }

    public static PCond_ExhaustTo exhaust(int amount)
    {
        return new PCond_ExhaustTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ExhaustTo exhaust(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_ExhaustTo(amount, h);
    }

    public static PCond_ExhaustBranch exhaustBranch(int amount)
    {
        return new PCond_ExhaustBranch(amount);
    }

    public static PCond_ExhaustTo exhaustRandom(int amount)
    {
        return exhaustRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ExhaustTo exhaustRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_ExhaustTo) new PCond_ExhaustTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_Fatal fatal()
    {
        return new PCond_Fatal();
    }

    public static PCond_HaveDiscarded haveDiscarded()
    {
        return new PCond_HaveDiscarded();
    }

    public static PCond_HaveDiscarded haveDiscarded(int amount)
    {
        return new PCond_HaveDiscarded(amount);
    }

    public static PCond_HaveExhausted haveExhausted()
    {
        return new PCond_HaveExhausted();
    }

    public static PCond_HaveExhausted haveExhausted(int amount)
    {
        return new PCond_HaveExhausted(amount);
    }

    public static PCond_HavePlayed havePlayed()
    {
        return new PCond_HavePlayed();
    }

    public static PCond_HavePlayed havePlayed(PCLAffinity... types)
    {
        return (PCond_HavePlayed) new PCond_HavePlayed().edit(f -> f.setAffinity(types));
    }

    public static PCond_HavePlayed havePlayed(AbstractCard.CardType... types)
    {
        return (PCond_HavePlayed) new PCond_HavePlayed().edit(f -> f.setType(types));
    }

    public static PCond_HavePlayed havePlayed(int amount)
    {
        return new PCond_HavePlayed(amount);
    }

    public static PCond_HighestAffinityBranch highestAffinityBranch(PCLAffinity... groups)
    {
        return new PCond_HighestAffinityBranch(groups);
    }

    public static PCond_HPPercent hpPercent(int amount)
    {
        return new PCond_HPPercent(PCLCardTarget.Self, amount);
    }

    public static PCond_HPPercent hpPercent(PCLCardTarget target, int amount)
    {
        return new PCond_HPPercent(target, amount);
    }

    public static PMultiCond ifElse(PSkill<?> ef1, PSkill<?> ef2, PCond<?>... conds)
    {
        return (PMultiCond) new PMultiCond().setEffects(conds).setChild(ef1, ef2).setAmount(1);
    }

    public static PCond_IsAttacking isAttacking(PCLCardTarget target)
    {
        return new PCond_IsAttacking(target);
    }

    public static PCond_Match match()
    {
        return new PCond_Match();
    }

    public static PCond_OnAllyTrigger onAllyTrigger()
    {
        return new PCond_OnAllyTrigger();
    }

    public static PCond_OnCreate onCreate()
    {
        return new PCond_OnCreate();
    }

    public static PCond_OnDiscard onDiscard()
    {
        return new PCond_OnDiscard();
    }

    public static PCond_OnAllyDeath onDeath()
    {
        return new PCond_OnAllyDeath();
    }

    public static PCond_OnDraw onDraw()
    {
        return new PCond_OnDraw();
    }

    public static PCond_OnDraw onDraw(PCLAffinity... aff)
    {
        return (PCond_OnDraw) new PCond_OnDraw().edit(f -> f.setAffinity(aff));
    }

    public static PCond_OnExhaust onExhaust()
    {
        return new PCond_OnExhaust();
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed()
    {
        return new PCond_OnOtherCardPlayed();
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed(AbstractCard.CardType... cardType)
    {
        return (PCond_OnOtherCardPlayed) new PCond_OnOtherCardPlayed().edit(f -> f.setType(cardType));
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed(PCLAffinity... aff)
    {
        return (PCond_OnOtherCardPlayed) new PCond_OnOtherCardPlayed().edit(f -> f.setAffinity(aff));
    }

    public static PCond_OnPurge onPurge()
    {
        return new PCond_OnPurge();
    }

    public static PCond_OnReshuffle onReshuffle()
    {
        return new PCond_OnReshuffle();
    }

    public static PCond_OnSummon onSummon()
    {
        return new PCond_OnSummon();
    }

    public static PCond_AtTurnEnd onTurnEnd()
    {
        return new PCond_AtTurnEnd();
    }

    public static PCond_AtTurnStart onTurnStart()
    {
        return new PCond_AtTurnStart();
    }

    public static PCond_OnWithdraw onWithdraw()
    {
        return new PCond_OnWithdraw();
    }

    public static PCond_PayEnergy payEnergy(int amount)
    {
        return new PCond_PayEnergy(amount);
    }

    public static PCond_PileHas pileHas(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_PileHas(amount, h);
    }

    public static PCond_PurgeTo purge(int amount)
    {
        return new PCond_PurgeTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_PurgeTo purge(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_PurgeTo(amount, h);
    }

    public static PCond_PurgeTo purgeRandom(int amount)
    {
        return purgeRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_PurgeTo purgeRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_PurgeTo) new PCond_PurgeTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_ReshuffleTo reshuffle(int amount)
    {
        return new PCond_ReshuffleTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ReshuffleTo reshuffle(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_ReshuffleTo(amount, h);
    }

    public static PCond_ReshuffleBranch reshuffleBranch(int amount)
    {
        return new PCond_ReshuffleBranch(amount);
    }

    public static PCond_ReshuffleTo reshuffleRandom(int amount)
    {
        return reshuffleRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ReshuffleTo reshuffleRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_ReshuffleTo) new PCond_ReshuffleTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_ScoutBranch scoutBranch(int amount)
    {
        return new PCond_ScoutBranch(amount);
    }

    public static PCond_ScryBranch scryBranch(int amount)
    {
        return new PCond_ScryBranch(amount);
    }

    public static PCond_Starter starter()
    {
        return new PCond_Starter();
    }

    public static PCond_TakeDamageTo takeDamage()
    {
        return new PCond_TakeDamageTo();
    }

    public static PCond_TakeDamageTo takeDamage(int amount)
    {
        return new PCond_TakeDamageTo(amount);
    }

    public abstract boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource);

    @Override
    public PCond<T> setAmountFromCard()
    {
        super.setAmountFromCard();
        return this;
    }

    @Override
    public PCond<T> setSource(PointerProvider card)
    {
        super.setSource(card);
        return this;
    }

    @Override
    public PCond<T> setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        super.setSource(card, valueSource);
        return this;
    }

    @Override
    public PCond<T> setSource(PointerProvider card, PCLCardValueSource valueSource, PCLCardValueSource extraSource)
    {
        super.setSource(card, valueSource, extraSource);
        return this;
    }

    public PCond<T> setChild(PSkill<?> effect)
    {
        super.setChild(effect);
        return this;
    }

    public PCond<T> setChild(PSkill<?>... effects)
    {
        super.setChild(effects);
        return this;
    }

    @Override
    public boolean canMatch(AbstractCard card)
    {
        return this.childEffect != null && checkCondition(card != null ? CombatManager.playerSystem.generateInfo(card, getSourceCreature(), null) : null, false, null) && this.childEffect.canMatch(card);
    }

    @Override
    public boolean canPlay(PCLUseInfo info)
    {
        return this.childEffect == null || !checkCondition(info, false, null) || this.childEffect.canPlay(info);
    }

    @Override
    public float modifyBlock(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyBlock(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamage(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyDamage(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyHeal(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyMagicNumber(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyMagicNumber(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyHitCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(info, false, null))
        {
            return this.childEffect.modifyRightCount(info, amount);
        }
        return amount;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        conditionMetCache = checkCondition(info, false, null);
        super.refresh(info, conditionMetCache & conditionMet);
    }

    @Override
    public Color getConditionColor()
    {
        return GameUtilities.inBattle() && conditionMetCache ? Settings.GREEN_TEXT_COLOR : null;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getConditionRawString() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : "");
    }

    /*
        Returns true if this is the skill that activates on a when trigger
        i.e. this is either the first condition underneath a when trigger, or if this is part of a multicond that meets the first clause
    */
    public final boolean isWhenClause()
    {
        return (parent != null && parent.hasParentType(PTrigger_When.class) && (!(parent instanceof PCond) || (parent instanceof PMultiCond && ((PMultiCond) parent).isWhenClause())));
    }

    /*
    Returns true if this is the skill that activates on a passive trigger
    i.e. this is either the first condition underneath a passive trigger, or if this is part of a multicond that meets the first clause
    */
    public final boolean isPassiveClause()
    {
        return (parent != null && parent.hasParentType(PTrigger_Passive.class) && (!(parent instanceof PCond) || (parent instanceof PMultiCond && ((PMultiCond) parent).isPassiveClause())));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (checkCondition(info, true, null) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (checkCondition(info, true, null) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (checkCondition(info, isUsing, null) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    protected void useFromTrigger(PCLUseInfo info)
    {
        if (tryPassParent(this, info))
        {
            if (childEffect != null)
            {
                childEffect.use(info);
            }
            // When a delegate (e.g. on draw) is triggered from an and multicond, it should only execute the effect if the other conditions would pass
            else if (parent instanceof PMultiCond && parent.childEffect != null)
            {
                ((PMultiCond) parent).useCond(this, info, 0, () -> parent.childEffect.use(info), () -> {});
            }
        }
    }
}
