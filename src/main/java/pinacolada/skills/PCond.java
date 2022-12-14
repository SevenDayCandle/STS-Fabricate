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
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT0;
import pinacolada.cards.base.*;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.base.conditions.*;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class PCond extends PSkill
{
    public static final int CONDITION_PRIORITY = 1;
    public static final int SPECIAL_CONDITION_PRIORITY = 0;
    protected boolean conditionMetCache = false;

    public PCond(PSkillSaveData content)
    {
        super(content);
    }

    public PCond(PSkillData data)
    {
        super(data);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(data, target, amount, powers);
    }

    public PCond(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PCond(PSkillData data, PCLStanceHelper... stance)
    {
        super(data, stance);
    }

    public static PCond block(int amount)
    {
        return new PCond_CheckBlock(PCLCardTarget.Self, amount);
    }

    public static PCond block(PCLCardTarget target, int amount)
    {
        return new PCond_CheckBlock(target, amount);
    }

    public static PCond checkEnergy(int amount)
    {
        return new PCond_CheckEnergy(amount);
    }

    public static PCond checkLevel(int amount, PCLAffinity... affinities)
    {
        return new PCond_CheckLevel(amount, affinities);
    }

    public static PCond checkOrb(int amount, PCLOrbHelper... orbs)
    {
        return new PCond_CheckOrb(amount, orbs);
    }

    public static PCond checkPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(target, amount, powers);
    }

    public static PCond checkPowerAoe(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.AllEnemy, amount, powers);
    }

    public static PCond checkPowerSelf(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.Self, amount, powers);
    }

    public static PCond checkPowerSingle(int amount, PCLPowerHelper... powers)
    {
        return new PCond_CheckPower(PCLCardTarget.Single, amount, powers);
    }

    public static PCond cooldown(int amount)
    {
        return new PCond_Cooldown(amount);
    }

    public static PCond cycle(int amount)
    {
        return new PCond_CycleTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond cycle(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_CycleTo(amount, h);
    }

    public static PCond cycleRandom(int amount)
    {
        return (PCond) new PCond_CycleTo(amount, PCLCardGroupHelper.Hand).setAlt(true);
    }

    public static PCond cycleRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond) new PCond_CycleTo(amount, h).setAlt(true);
    }

    public static PCond discard(int amount)
    {
        return new PCond_DiscardTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond discard(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_DiscardTo(amount, h);
    }

    public static PCond discardRandom(int amount)
    {
        return (PCond) new PCond_DiscardTo(amount, PCLCardGroupHelper.Hand).setAlt(true);
    }

    public static PCond discardRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond) new PCond_DiscardTo(amount, h).setAlt(true);
    }

    public static PCond evoke(int amount, PCLOrbHelper... h)
    {
        return new PCond_EvokeOrb(amount, h);
    }

    public static PCond exhaust(int amount)
    {
        return new PCond_ExhaustTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond exhaust(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_ExhaustTo(amount, h);
    }

    public static PCond exhaustRandom(int amount)
    {
        return (PCond) new PCond_ExhaustTo(amount, PCLCardGroupHelper.Hand).setAlt(true);
    }

    public static PCond exhaustRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond) new PCond_ExhaustTo(amount, h).setAlt(true);
    }

    public static PCond fatal()
    {
        return new PCond_Fatal();
    }

    public static ArrayList<PCond> getEligibleConditions(AbstractCard.CardColor co, Integer priority)
    {
        return EUIUtils.mapAsNonnull(getEligibleEffects(co, priority), ef -> EUIUtils.safeCast(ef, PCond.class));
    }

    public static PCond haveDiscarded()
    {
        return new PCond_HaveDiscarded();
    }

    public static PCond haveDiscarded(int amount)
    {
        return new PCond_HaveDiscarded(amount);
    }

    public static PCond haveExhausted()
    {
        return new PCond_HaveExhausted();
    }

    public static PCond haveExhausted(int amount)
    {
        return new PCond_HaveExhausted(amount);
    }

    public static PCond havePlayed()
    {
        return new PCond_HavePlayed();
    }

    public static PCond havePlayed(PCLAffinity... types)
    {
        return (PCond) new PCond_HavePlayed().setAffinity(types);
    }

    public static PCond havePlayed(AbstractCard.CardType... types)
    {
        return (PCond) new PCond_HavePlayed().setCardTypes(types);
    }

    public static PCond havePlayed(int amount)
    {
        return new PCond_HavePlayed(amount);
    }

    public static PCond hp(int amount)
    {
        return new PCond_HP(PCLCardTarget.Self, amount);
    }

    public static PCond hp(PCLCardTarget target, int amount)
    {
        return new PCond_HP(target, amount);
    }

    public static PCond ifElse(PSkill ef1, PSkill ef2, PCond... conds)
    {
        return (PCond) new PMultiCond().setEffects(conds).setChild(ef1, ef2).setAmount(1);
    }

    public static PCond isAttacking(PCLCardTarget target)
    {
        return new PCond_IsAttacking(target);
    }

    public static PCond limited()
    {
        return new PCond_Limited();
    }

    public static PCond match()
    {
        return new PCond_Match();
    }

    public static PCond onCreate()
    {
        return new PCond_OnCreate();
    }

    public static PCond onDiscard()
    {
        return new PCond_OnDiscard();
    }

    public static PCond onDraw()
    {
        return new PCond_OnDraw();
    }

    public static PCond onExhaust()
    {
        return new PCond_OnExhaust();
    }

    public static PCond onOtherCardPlayed()
    {
        return new PCond_OnOtherCardPlayed();
    }

    public static PCond onOtherCardPlayed(AbstractCard.CardType... cardType)
    {
        return (PCond) new PCond_OnOtherCardPlayed().setCardTypes(cardType);
    }

    public static PCond onOtherCardPlayed(PCLAffinity... aff)
    {
        return (PCond) new PCond_OnOtherCardPlayed().setAffinity(aff);
    }

    public static PCond onPurge()
    {
        return new PCond_OnPurge();
    }

    public static PCond onReshuffle()
    {
        return new PCond_OnReshuffle();
    }

    public static PCond onTurnEnd()
    {
        return new PCond_AtTurnEnd();
    }

    public static PCond onTurnStart()
    {
        return new PCond_AtTurnStart();
    }

    public static PCond payEnergy(int amount)
    {
        return new PCond_PayEnergy(amount);
    }

    public static PCond pileHas(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_PileHas(amount, h);
    }

    public static PCond purge(int amount)
    {
        return new PCond_PurgeTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond purge(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_PurgeTo(amount, h);
    }

    public static PCond purgeRandom(int amount)
    {
        return (PCond) new PCond_PurgeTo(amount, PCLCardGroupHelper.Hand).setAlt(true);
    }

    public static PCond purgeRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond) new PCond_PurgeTo(amount, h).setAlt(true);
    }

    public static PCond reshuffle(int amount)
    {
        return new PCond_ReshuffleTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond reshuffle(int amount, PCLCardGroupHelper... h)
    {
        return new PCond_ReshuffleTo(amount, h);
    }

    public static PCond reshuffleRandom(int amount)
    {
        return (PCond) new PCond_ReshuffleTo(amount, PCLCardGroupHelper.Hand).setAlt(true);
    }

    public static PCond reshuffleRandom(int amount, PCLCardGroupHelper... h)
    {
        return (PCond) new PCond_ReshuffleTo(amount, h).setAlt(true);
    }

    public static PCond semiLimited()
    {
        return new PCond_SemiLimited();
    }

    public static PCond starter()
    {
        return new PCond_Starter();
    }

    public abstract boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger);

    @Override
    public PCond setAmountFromCard()
    {
        super.setAmountFromCard();
        return this;
    }

    @Override
    public PCond setSource(PointerProvider card)
    {
        super.setSource(card);
        return this;
    }

    @Override
    public PCond setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        super.setSource(card, valueSource);
        return this;
    }

    @Override
    public PCond setSource(PointerProvider card, PCLCardValueSource valueSource, PCLCardValueSource extraSource)
    {
        super.setSource(card, valueSource, extraSource);
        return this;
    }

    public PCond setChild(PSkill effect)
    {
        super.setChild(effect);
        return this;
    }

    public PCond setChild(PSkill... effects)
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
    public boolean triggerOnPCLPowerUsed(PCLPower c)
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
}
