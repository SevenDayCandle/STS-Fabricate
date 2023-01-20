package pinacolada.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.interfaces.delegates.FuncT0;
import pinacolada.cards.base.*;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.base.conditions.*;
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
        return new PCond_CycleTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_CycleTo cycle(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_CycleTo(amount, h);
    }

    public static PCond_CycleTo cycleRandom(int amount)
    {
        return cycleRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_CycleTo cycleRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_CycleTo) new PCond_CycleTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_DiscardTo discard(int amount)
    {
        return new PCond_DiscardTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discard(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_DiscardTo(amount, h);
    }

    public static PCond_DiscardTo discardRandom(int amount)
    {
        return discardRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discardRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_DiscardTo) new PCond_DiscardTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_EvokeOrb evoke(int amount, PCLOrbHelper... h)
    {
        return new PCond_EvokeOrb(amount, h);
    }

    public static PCond_ExhaustTo exhaust(int amount)
    {
        return new PCond_ExhaustTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ExhaustTo exhaust(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_ExhaustTo(amount, h);
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

    public static PCond_HP hp(int amount)
    {
        return new PCond_HP(PCLCardTarget.Self, amount);
    }

    public static PCond_HP hp(PCLCardTarget target, int amount)
    {
        return new PCond_HP(target, amount);
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

    public static PCond_ReshuffleTo reshuffleRandom(int amount)
    {
        return reshuffleRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ReshuffleTo reshuffleRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond_ReshuffleTo) new PCond_ReshuffleTo(amount, h).edit(r -> r.setRandom());
    }

    public static PCond_Starter starter()
    {
        return new PCond_Starter();
    }

    public abstract boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger);

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
        return this.childEffect != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), null) : null, false, false) && this.childEffect.canMatch(card);
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return this.childEffect == null || !checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : null, false, false) || this.childEffect.canPlay(card, m);
    }

    @Override
    public float modifyBlock(AbstractCard card, AbstractMonster m, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : makeInfo(m), false, false))
        {
            return this.childEffect.modifyBlock(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamage(AbstractCard card, AbstractMonster m, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : makeInfo(m), false, false))
        {
            return this.childEffect.modifyDamage(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyMagicNumber(AbstractCard card, AbstractMonster m, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : makeInfo(m), false, false))
        {
            return this.childEffect.modifyMagicNumber(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLCard card, AbstractMonster m, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : makeInfo(m), false, false))
        {
            return this.childEffect.modifyHitCount(card, m, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLCard card, AbstractMonster m, float amount)
    {
        if (this.childEffect != null && sourceCard != null && checkCondition(card != null ? new PCLUseInfo(card, getSourceCreature(), m) : makeInfo(m), false, false))
        {
            return this.childEffect.modifyRightCount(card, m, amount);
        }
        return amount;
    }

    @Override
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        conditionMetCache = checkCondition(makeInfo(m), false, false);
        super.refresh(m, c, conditionMetCache & conditionMet);
    }

    @Override
    public int triggerOnAttack(DamageInfo info, int damageAmount, AbstractCreature target)
    {
        return this.childEffect != null && checkCondition(makeInfo(target), false, true) ? this.childEffect.triggerOnAttack(info, damageAmount, target) : damageAmount;
    }

    @Override
    public int triggerOnAttacked(DamageInfo info, int damageAmount)
    {
        return this.childEffect != null && checkCondition(makeInfo(null), false, true) ? this.childEffect.triggerOnAttacked(info, damageAmount) : damageAmount;
    }

    @Override
    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return triggerOn(() -> this.childEffect.triggerOnReshuffle(c, sourcePile));
    }

    @Override
    public boolean triggerOnApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower c)
    {
        return triggerOn(() -> this.childEffect.triggerOnApplyPower(source, target, c), makeInfo(target).setData(c));
    }

    @Override
    public boolean triggerOnAllyDeath(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyDeath(c, ally));
    }

    @Override
    public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllySummon(c, ally));
    }

    @Override
    public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyTrigger(c, ally));
    }

    @Override
    public boolean triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally)
    {
        return triggerOn(() -> this.childEffect.triggerOnAllyWithdraw(c, ally));
    }

    @Override
    public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        return triggerOn(() -> this.childEffect.triggerOnCreate(c, startOfBattle));
    }

    @Override
    public boolean triggerOnDiscard(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnDiscard(c));
    }

    @Override
    public boolean triggerOnDraw(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnDraw(c));
    }

    @Override
    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        return triggerOn(() -> this.childEffect.triggerOnEndOfTurn(isUsing));
    }

    @Override
    public boolean triggerOnExhaust(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnExhaust(c));
    }

    @Override
    public boolean triggerOnIntensify(PCLAffinity c)
    {
        return triggerOn(() -> this.childEffect.triggerOnIntensify(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnMatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn(() -> this.childEffect.triggerOnMatch(c, info), info);
    }

    @Override
    public boolean triggerOnMismatch(AbstractCard c, PCLUseInfo info)
    {
        return triggerOn(() -> this.childEffect.triggerOnMismatch(c, info), info);
    }

    @Override
    public boolean triggerOnOrbChannel(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbChannel(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbFocus(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbFocus(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbEvoke(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbEvoke(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOrbTrigger(AbstractOrb c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOrbTrigger(c), makeInfo(null).setData(c));
    }

    @Override
    public boolean triggerOnOtherCardPlayed(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnOtherCardPlayed(c));
    }

    @Override
    public boolean triggerOnPCLPowerUsed(PCLClickableUse c)
    {
        return triggerOn(() -> this.childEffect.triggerOnPCLPowerUsed(c));
    }

    @Override
    public boolean triggerOnPurge(AbstractCard c)
    {
        return triggerOn(() -> this.childEffect.triggerOnPurge(c));
    }

    @Override
    public boolean triggerOnScry()
    {
        return triggerOn(() -> this.childEffect.triggerOnScry());
    }

    @Override
    public boolean triggerOnShuffle(boolean r)
    {
        return triggerOn(() -> this.childEffect.triggerOnShuffle(r));
    }

    @Override
    public boolean triggerOnStartOfTurn()
    {
        return triggerOn(() -> this.childEffect.triggerOnStartOfTurn());
    }

    @Override
    public boolean triggerOnStartup()
    {
        return triggerOn(() -> this.childEffect.triggerOnStartup());
    }

    @Override
    public Color getConditionColor()
    {
        return GameUtilities.inBattle() && conditionMetCache ? Settings.GREEN_TEXT_COLOR : null;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getConditionRawString() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (checkCondition(info, true, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (checkCondition(info, true, false) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (checkCondition(info, isUsing, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction)
    {
        return triggerOn(childAction, makeInfo(null));
    }

    // Only trigger the condition use effects if the child actually triggers for the specified action
    protected boolean triggerOn(FuncT0<Boolean> childAction, PCLUseInfo info)
    {
        return this.childEffect != null && sourceCard != null
                && checkCondition(info, false, true) && childAction.invoke()
                && checkCondition(info, true, false);
    }

    protected void useFromTrigger(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            childEffect.use(info);
        }
        else if (parent instanceof PMultiCond && parent.childEffect != null)
        {
            parent.childEffect.use(info);
        }
    }
}
