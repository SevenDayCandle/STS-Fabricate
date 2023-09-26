package pinacolada.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.monsters.PCLIntentType;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.PBranchCond;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.base.conditions.*;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class PCond<T extends PField> extends PSkill<T> {
    protected boolean conditionMetCache = false;

    public PCond(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCond(PSkillData<T> data) {
        super(data);
    }

    public PCond(PSkillData<T> data, T fields) {
        super(data, fields);
    }

    public PCond(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public static PCond_CheckBlock block(int amount) {
        return new PCond_CheckBlock(PCLCardTarget.Self, amount);
    }

    public static PCond_CheckBlock block(PCLCardTarget target, int amount) {
        return new PCond_CheckBlock(target, amount);
    }

    public static PCond_BlockBreak blockBreak(PCLCardTarget target) {return new PCond_BlockBreak(target);}

    public static PCond_CheckCreatureSummon checkAlly(PCLCardTarget target) {
        return new PCond_CheckCreatureSummon(target);
    }

    public static PCond_CheckCreature checkCreature(PCLCardTarget target, int amount) {
        return new PCond_CheckCreature(target, amount);
    }

    public static PCond_CheckDistinctPower checkDistinctPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        return new PCond_CheckDistinctPower(target, amount, powers);
    }

    public static PCond_CheckDistinctPower checkDistinctPowerAoe(int amount, PCLPowerData... powers) {
        return new PCond_CheckDistinctPower(PCLCardTarget.AllEnemy, amount, powers);
    }

    public static PCond_CheckDistinctPower checkDistinctPowerSelf(int amount, PCLPowerData... powers) {
        return new PCond_CheckDistinctPower(PCLCardTarget.Self, amount, powers);
    }

    public static PCond_CheckDistinctPower checkDistinctPowerSingle(int amount, PCLPowerData... powers) {
        return new PCond_CheckDistinctPower(PCLCardTarget.Single, amount, powers);
    }

    public static PCond_CheckEnergy checkEnergy(int amount) {
        return new PCond_CheckEnergy(amount);
    }

    public static PCond_CheckOrb checkOrb(int amount, PCLOrbHelper... orbs) {
        return new PCond_CheckOrb(amount, orbs);
    }

    public static PCond_CheckPower checkPower(PCLCardTarget target, int amount, PCLPowerData... powers) {
        return new PCond_CheckPower(target, amount, powers);
    }

    public static PCond_CheckPower checkPowerAoe(int amount, PCLPowerData... powers) {
        return new PCond_CheckPower(PCLCardTarget.AllEnemy, amount, powers);
    }

    public static PCond_CheckPower checkPowerSelf(int amount, PCLPowerData... powers) {
        return new PCond_CheckPower(PCLCardTarget.Self, amount, powers);
    }

    public static PCond_CheckPower checkPowerSingle(int amount, PCLPowerData... powers) {
        return new PCond_CheckPower(PCLCardTarget.Single, amount, powers);
    }

    public static PCond_Cooldown cooldown(int amount) {
        return new PCond_Cooldown(amount);
    }

    public static PCond_CycleTo cycle(int amount) {
        return new PCond_CycleTo(amount);
    }

    public static PCond_CycleTo cycleRandom(int amount) {
        return (PCond_CycleTo) new PCond_CycleTo(amount).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_CheckDamage damage(PCLCardTarget target, int amount) {
        return new PCond_CheckDamage(target, amount);
    }

    public static PCond_DiscardTo discard(int amount) {
        return new PCond_DiscardTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discard(int amount, PCLCardGroupHelper... h) {
        return new PCond_DiscardTo(amount, h);
    }

    public static PCond_DiscardTo discardRandom(int amount) {
        return discardRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_DiscardTo discardRandom(int amount, PCLCardGroupHelper... h) {
        return (PCond_DiscardTo) new PCond_DiscardTo(amount, h).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_EvokeTo evokeTo(int amount, PCLOrbHelper... h) {
        return new PCond_EvokeTo(amount, h);
    }

    public static PCond_ExhaustTo exhaust(int amount) {
        return new PCond_ExhaustTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ExhaustTo exhaust(int amount, PCLCardGroupHelper... h) {
        return new PCond_ExhaustTo(amount, h);
    }

    public static PCond_ExhaustTo exhaustRandom(int amount) {
        return exhaustRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ExhaustTo exhaustRandom(int amount, PCLCardGroupHelper... h) {
        return (PCond_ExhaustTo) new PCond_ExhaustTo(amount, h).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_Fatal fatal() {
        return (PCond_Fatal) new PCond_Fatal().edit(f -> f.setRandom(true));
    }

    public static PCond_Fatal fatal(PCLCardTarget target) {
        return (PCond_Fatal) new PCond_Fatal(target).edit(f -> f.setRandom(true));
    }

    public static PCond_Fatal fatalMinion() {
        return new PCond_Fatal();
    }

    public static PCond_Fatal fatalMinion(PCLCardTarget target) {
        return new PCond_Fatal(target);
    }

    public static PCond_HaveDiscarded haveDiscarded() {
        return new PCond_HaveDiscarded();
    }

    public static PCond_HaveDiscarded haveDiscarded(int amount) {
        return new PCond_HaveDiscarded(amount);
    }

    public static PCond_HaveExhausted haveExhausted() {
        return new PCond_HaveExhausted();
    }

    public static PCond_HaveExhausted haveExhausted(int amount) {
        return new PCond_HaveExhausted(amount);
    }

    public static PCond_HaveLostHP haveLostHP() {
        return new PCond_HaveLostHP();
    }

    public static PCond_HaveLostHP haveLostHP(int amount) {
        return new PCond_HaveLostHP(amount);
    }

    public static PCond_HavePlayed havePlayed() {
        return new PCond_HavePlayed();
    }

    public static PCond_HavePlayed havePlayed(PCLAffinity... types) {
        return (PCond_HavePlayed) new PCond_HavePlayed().edit(f -> f.setAffinity(types));
    }

    public static PCond_HavePlayed havePlayed(AbstractCard.CardType... types) {
        return (PCond_HavePlayed) new PCond_HavePlayed().edit(f -> f.setType(types));
    }

    public static PCond_HavePlayed havePlayed(int amount) {
        return new PCond_HavePlayed(amount);
    }

    public static PCond_HaveTakenDamage haveTakenDamage() {
        return new PCond_HaveTakenDamage();
    }

    public static PCond_HaveTakenDamage haveTakenDamage(PCLCardTarget target, int amount) {
        return new PCond_HaveTakenDamage(target, amount);
    }

    public static PCond_HPPercent hpPercent(int amount) {
        return new PCond_HPPercent(PCLCardTarget.Self, amount);
    }

    public static PCond_HPPercent hpPercent(PCLCardTarget target, int amount) {
        return new PCond_HPPercent(target, amount);
    }

    public static PCond_IfHasProperty ifProperty(AbstractCard.CardRarity... property) {
        return new PCond_IfHasProperty(property);
    }

    public static PCond_IfHasProperty ifProperty(AbstractCard.CardType... property) {
        return new PCond_IfHasProperty(property);
    }

    public static PCond_IfHasProperty ifProperty(PCLAffinity... property) {
        return new PCond_IfHasProperty(property);
    }

    public static PCond_IfHasProperty ifProperty(PCLCardTag... property) {
        return new PCond_IfHasProperty(property);
    }

    public static PCond_Intent intent(PCLCardTarget target, PCLIntentType... types) {
        return new PCond_Intent(target, types);
    }

    public static PCond_Intent isAttacking(PCLCardTarget target) {
        return new PCond_Intent(target, PCLIntentType.Attack);
    }

    public static PCond_Intent isBuffing(PCLCardTarget target) {
        return new PCond_Intent(target, PCLIntentType.Buff);
    }

    public static PCond_Intent isDebuffing(PCLCardTarget target) {
        return new PCond_Intent(target, PCLIntentType.Debuff);
    }

    public static PCond_Intent isDefending(PCLCardTarget target) {
        return new PCond_Intent(target, PCLIntentType.Defend);
    }

    public static PCond_OnAllyTrigger onAllyTrigger() {
        return new PCond_OnAllyTrigger();
    }

    public static PCond_OnCreate onCreate() {
        return new PCond_OnCreate();
    }

    public static PCond_OnAllyDeath onDeath() {
        return new PCond_OnAllyDeath();
    }

    public static PCond_OnDiscard onDiscard() {
        return new PCond_OnDiscard();
    }

    public static PCond_OnDraw onDraw() {
        return new PCond_OnDraw();
    }

    public static PCond_OnDraw onDraw(PCLAffinity... aff) {
        return (PCond_OnDraw) new PCond_OnDraw().edit(f -> f.setAffinity(aff));
    }

    public static PCond_OnDraw onDraw(AbstractCard.CardType... aff) {
        return (PCond_OnDraw) new PCond_OnDraw().edit(f -> f.setType(aff));
    }

    public static PCond_OnExhaust onExhaust() {
        return new PCond_OnExhaust();
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed() {
        return new PCond_OnOtherCardPlayed();
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed(AbstractCard.CardType... cardType) {
        return (PCond_OnOtherCardPlayed) new PCond_OnOtherCardPlayed().edit(f -> f.setType(cardType));
    }

    public static PCond_OnOtherCardPlayed onOtherCardPlayed(PCLAffinity... aff) {
        return (PCond_OnOtherCardPlayed) new PCond_OnOtherCardPlayed().edit(f -> f.setAffinity(aff));
    }

    public static PCond_OnPurge onPurge() {
        return new PCond_OnPurge();
    }

    public static PCond_OnReshuffle onReshuffle() {
        return new PCond_OnReshuffle();
    }

    public static PCond_OnRetain onRetain() {
        return new PCond_OnRetain();
    }

    public static PCond_OnSummon onSummon() {
        return new PCond_OnSummon();
    }

    public static PCond_AtTurnEnd onTurnEnd() {
        return new PCond_AtTurnEnd();
    }

    public static PCond_AtTurnStart onTurnStart() {
        return new PCond_AtTurnStart();
    }

    public static PCond_OnWithdraw onWithdraw() {
        return new PCond_OnWithdraw();
    }

    public static PCond_PayBlock payBlock(int amount) {
        return new PCond_PayBlock(amount);
    }

    public static PCond_PayEnergy payEnergy(int amount) {
        return new PCond_PayEnergy(amount);
    }

    public static PCond_PayPower payPower(int amount, PCLPowerData... h) {
        return new PCond_PayPower(amount, h);
    }

    public static PCond_PayPower payPower(PCLCardTarget target, int amount, PCLPowerData... h) {
        return new PCond_PayPower(target, amount, h);
    }

    public static PCond_PileHas pileHas(int amount, PCLCardGroupHelper... h) {
        return new PCond_PileHas(amount, h);
    }

    public static PCond_PurgeTo purge(int amount) {
        return new PCond_PurgeTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_PurgeTo purge(int amount, PCLCardGroupHelper... h) {
        return new PCond_PurgeTo(amount, h);
    }

    public static PCond_PurgeTo purgeRandom(int amount) {
        return purgeRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_PurgeTo purgeRandom(int amount, PCLCardGroupHelper... h) {
        return (PCond_PurgeTo) new PCond_PurgeTo(amount, h).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_ReshuffleTo reshuffle(int amount) {
        return new PCond_ReshuffleTo(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ReshuffleTo reshuffle(int amount, PCLCardGroupHelper... h) {
        return new PCond_ReshuffleTo(amount, h);
    }

    public static PCond_ReshuffleTo reshuffleRandom(int amount) {
        return reshuffleRandom(amount, PCLCardGroupHelper.Hand);
    }

    public static PCond_ReshuffleTo reshuffleRandom(int amount, PCLCardGroupHelper... h) {
        return (PCond_ReshuffleTo) new PCond_ReshuffleTo(amount, h).edit(PField_CardGeneric::setRandom);
    }

    public static PCond_Shuffle shuffle() {
        return new PCond_Shuffle();
    }

    public static PCond_Starter starter() {
        return new PCond_Starter();
    }

    public static PCond_TakeDamageTo takeDamageTo() {
        return new PCond_TakeDamageTo();
    }

    public static PCond_TakeDamageTo takeDamageTo(int amount) {
        return new PCond_TakeDamageTo(amount);
    }

    public static PCond_UnblockedDamage unblockedDamage() {
        return new PCond_UnblockedDamage();
    }

    public static PCond_UnblockedDamage unblockedDamage(PCLCardTarget target) {
        return new PCond_UnblockedDamage(target);
    }

    @Override
    public boolean canPlay(PCLUseInfo info, PSkill<?> triggerSource) {
        return this.childEffect == null || !checkCondition(info, false, null) || this.childEffect.canPlay(info, triggerSource);
    }

    public boolean checkConditionOutsideOfBattle() {
        return checkCondition(generateInfo(null), false, null);
    }

    @Override
    public PCond<T> edit(ActionT1<T> editFunc) {
        editFunc.invoke(fields);
        return this;
    }

    @Override
    public Color getConditionColor() {
        return GameUtilities.inBattle() && conditionMetCache ? Settings.GREEN_TEXT_COLOR : null;
    }

    @Override
    public int getQualifierRange() {
        return 1;
    }

    @Override
    public String getQualifierText(int i) {
        return "";
    }

    @Override
    public ArrayList<Integer> getQualifiers(PCLUseInfo info, boolean conditionPassed) {
        return EUIUtils.arrayList(checkCondition(info, true, null) ? 0 : 1);
    }

    public String getTargetHasString(PCLCardTarget target, String desc) {
        // For the case of self on the player, use ordinal 0 to get "have" in the description
        return TEXT.cond_ifTargetHas(getTargetSubjectString(target), getTargetOrdinal(target), desc);
    }

    public String getTargetHasStringPerspective(PCLCardTarget target, String desc) {
        return getTargetHasString(getTargetForPerspective(target), desc);
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String condString = isWhenClause() ? getCapitalSubText(perspective, addPeriod) : getConditionRawString(perspective, addPeriod);
        if (childEffect != null) {
            if (childEffect instanceof PCond && !isWhenClause()) {
                return PCLCoreStrings.joinWithAnd(condString, childEffect.getText(perspective, false)) + PCLCoreStrings.period(addPeriod);
            }
            return condString + COMMA_SEPARATOR + childEffect.getText(perspective, false) + PCLCoreStrings.period(addPeriod);
        }
        return condString + PCLCoreStrings.period(addPeriod);
    }

    /*
    Returns true if this is the skill that activates on a when trigger
    i.e. this is either the first condition underneath a when trigger, or if this is part of a branching condition that meets the first clause
*/
    public final PTrigger_When getWhenClause() {
        PTrigger_When when = EUIUtils.safeCast(parent, PTrigger_When.class);
        if (when != null) {
            return when;
        }
        if (parent instanceof PMultiBase<?> && parent instanceof PCond) {
            when = EUIUtils.safeCast(parent.parent, PTrigger_When.class);
        }
        return when;
    }

    public final String getWheneverAreString(Object impl, PCLCardTarget perspective) {
        PCLCardTarget properTarget = getTargetForPerspective(perspective);
        return TEXT.cond_xIsY(getTargetSubjectString(properTarget), getTargetOrdinal(properTarget), impl);
    }

    public final String getWheneverString(Object impl, PCLCardTarget perspective) {
        PCLCardTarget properTarget = getTargetForPerspective(perspective);
        return EUIRM.strings.nounVerb(getTargetSubjectString(properTarget), impl);
    }

    public final String getWheneverYouString(Object impl) {
        String subjectString = isFromCreature() ? TEXT.subjects_thisCard() : TEXT.subjects_you;
        return EUIRM.strings.nounVerb(subjectString, impl);
    }

    @Override
    public boolean hasChildWarning() {
        return childEffect == null && !((this.parent instanceof PBranchCond && this.parent.childEffect == this) || (this.parent instanceof PMultiCond && this.parent.childEffect != this));
    }

    /* Check if this skill should display as a branch (i.e. defer to branch for listing out items in conditions) */
    public final boolean isBranch() {
        return (parent instanceof PBranchCond && ((PBranchCond) parent).getSubEffects().size() > 2);
    }

    /*
        Returns true if this is the skill that activates on a passive trigger
        i.e. this is either the first condition underneath a passive trigger, or if this is part of a branching condition that meets the first clause
    */
    public final boolean isPassiveClause() {
        return (parent != null && parent.hasParentType(PTrigger_Passive.class) && (!(parent instanceof PCond) || (parent instanceof PMultiBase && ((PCond<?>) parent).isPassiveClause())));
    }

    public boolean isUnderWhen(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return callingSkill instanceof PTrigger_When && !(parentSkill instanceof PCond);
    }

    /*
        Returns true if this is the skill that activates on a when trigger
        i.e. this is either the first condition underneath a when trigger, or if this is part of a branching condition that meets the first clause
    */
    public final boolean isWhenClause() {
        return getWhenClause() != null;
    }

    @Override
    public float modifyBlockFirst(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyBlockFirst(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyBlockLast(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyBlockLast(info, amount);
        }
        return amount;
    }

    @Override
    public int modifyCost(PCLUseInfo info, int amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyCost(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyDamageGiveFirst(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageGiveLast(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyDamageGiveLast(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyDamageReceiveFirst(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyDamageReceiveFirst(info, amount, type);
        }
        return amount;
    }

    @Override
    public float modifyDamageReceiveLast(PCLUseInfo info, float amount, DamageInfo.DamageType type) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyDamageReceiveLast(info, amount, type);
        }
        return amount;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyHeal(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyHitCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyOrbIncoming(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyOrbIncoming(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyOrbOutgoing(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyOrbOutgoing(info, amount);
        }
        return amount;
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifyRightCount(info, amount);
        }
        return amount;
    }

    @Override
    public float modifySkillBonus(PCLUseInfo info, float amount) {
        if (this.childEffect != null && checkCondition(info, false, null)) {
            return this.childEffect.modifySkillBonus(info, amount);
        }
        return amount;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet) {
        conditionMetCache = checkCondition(info, false, null);
        super.refresh(info, conditionMetCache & conditionMet);
    }

    @Override
    public PCond<T> setAmountFromCard() {
        super.setAmountFromCard();
        return this;
    }

    public PCond<T> setChild(PSkill<?> effect) {
        super.setChild(effect);
        return this;
    }

    public PCond<T> setChild(PSkill<?>... effects) {
        super.setChild(effects);
        return this;
    }

    @Override
    public PCond<T> setSource(PointerProvider card) {
        super.setSource(card);
        return this;
    }

    @Override
    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        return checkCondition(info, true, source) && super.tryPassParent(source, info);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (checkCondition(info, true, null) && childEffect != null) {
            childEffect.use(info, order);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (checkCondition(info, shouldPay, null) && childEffect != null) {
            childEffect.use(info, order);
        }
    }

    protected void useFromTrigger(PCLUseInfo info) {
        useFromTrigger(info, PCLActions.bottom);
    }

    protected void useFromTrigger(PCLUseInfo info, PCLActions order) {
        // Use the super pass parent to bypass the cond check for the triggering cond
        if (super.tryPassParent(this, info)) {
            if (childEffect != null) {
                childEffect.use(info, order);
            }
            // When a delegate (e.g. on draw) is triggered from an and multicond, it should only execute the effect if the other conditions would pass
            else if (parent instanceof PMultiCond) {
                ((PMultiCond) parent).useDirectly(info, order);
            }
            // When a delegate is triggered from a branch, the resulting effect should be chosen based on the info gleaned from the branch
            else if (parent instanceof PBranchCond) {
                parent.use(info, order);
            }
        }
    }

    public void useOutsideOfBattle() {
        if (checkConditionOutsideOfBattle() && this.childEffect != null) {
            this.childEffect.useOutsideOfBattle();
        }
    }

    public abstract boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource);
}
