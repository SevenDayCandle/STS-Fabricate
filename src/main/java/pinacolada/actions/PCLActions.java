package pinacolada.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.CardFlashVfx;
import extendedui.interfaces.delegates.*;
import pinacolada.actions.affinity.AddAffinityLevel;
import pinacolada.actions.affinity.RerollAffinity;
import pinacolada.actions.affinity.TryChooseChoice;
import pinacolada.actions.basic.*;
import pinacolada.actions.cardManipulation.*;
import pinacolada.actions.creature.SummonAllyAction;
import pinacolada.actions.creature.TriggerAllyAction;
import pinacolada.actions.creature.WithdrawAllyAction;
import pinacolada.actions.damage.DealDamage;
import pinacolada.actions.damage.DealDamageToAll;
import pinacolada.actions.damage.LoseHP;
import pinacolada.actions.orbs.ChannelOrb;
import pinacolada.actions.orbs.EvokeOrb;
import pinacolada.actions.orbs.InduceOrb;
import pinacolada.actions.orbs.TriggerOrbPassiveAbility;
import pinacolada.actions.pileSelection.*;
import pinacolada.actions.player.ChangeStance;
import pinacolada.actions.player.GainGold;
import pinacolada.actions.player.SpendEnergy;
import pinacolada.actions.powers.*;
import pinacolada.actions.special.*;
import pinacolada.actions.utility.CallbackAction;
import pinacolada.actions.utility.SequentialAction;
import pinacolada.actions.utility.WaitRealtimeAction;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.pcl.tokens.AffinityToken;
import pinacolada.interfaces.subscribers.OnPhaseChangedSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.powers.common.DelayedDamagePower;
import pinacolada.powers.common.DrawLessPower;
import pinacolada.powers.common.EnergizedPower;
import pinacolada.skills.PSkill;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;


@SuppressWarnings("UnusedReturnValue")
public final class PCLActions
{

    public enum ActionOrder
    {
        TurnStart,
        NextCombat,

        Instant,
        Top,
        Bottom,
        Delayed,
        DelayedTop,
        Last
    }

    @Deprecated
    public static PCLActions nextCombat = new PCLActions(ActionOrder.NextCombat);
    @Deprecated
    public static PCLActions turnStart = new PCLActions(ActionOrder.TurnStart);
    public static PCLActions instant = new PCLActions(ActionOrder.Instant);
    public static PCLActions top = new PCLActions(ActionOrder.Top);
    public static PCLActions bottom = new PCLActions(ActionOrder.Bottom);
    public static PCLActions delayed = new PCLActions(ActionOrder.Delayed);
    public static PCLActions delayedTop = new PCLActions(ActionOrder.DelayedTop);
    public static PCLActions last = new PCLActions(ActionOrder.Last);

    protected final ActionOrder actionOrder;

    protected PCLActions(ActionOrder actionOrder)
    {
        this.actionOrder = actionOrder;
    }

    public static void clearActions()
    {
        AbstractDungeon.actionManager.actions.clear();
    }

    public static DelayAllActions delayCurrentActions()
    {
        return top.add(new DelayAllActions(true));
    }

    public static ArrayList<AbstractGameAction> getActions()
    {
        return AbstractDungeon.actionManager.actions;
    }

    public <T extends AbstractGameAction> T add(T action)
    {
        if (action instanceof PCLAction)
        {
            ((PCLAction)action).setOriginalOrder(actionOrder);
        }

        switch (actionOrder)
        {
            case Top:
            {
                AbstractDungeon.actionManager.addToTop(action);
                break;
            }

            case Bottom:
            {
                AbstractDungeon.actionManager.addToBottom(action);
                break;
            }

            case TurnStart:
            {
                AbstractDungeon.actionManager.addToTurnStart(action);
                break;
            }

            case NextCombat:
            {
                AbstractDungeon.actionManager.addToNextCombat(action);
                break;
            }

            case Instant:
            {
                AbstractGameAction current = AbstractDungeon.actionManager.currentAction;
                if (current != null)
                {
                    AbstractDungeon.actionManager.addToTop(current);
                }

                AbstractDungeon.actionManager.currentAction = action;
                AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

                break;
            }

            case Delayed:
            {
                bottom.callback(action, bottom::add);
                break;
            }

            case DelayedTop:
            {
                bottom.callback(action, bottom::add);
                break;
            }

            case Last:
            {
                ExecuteLast.add(action);
                break;
            }
        }

        return action;
    }

    public AddAffinityLevel addAffinityLevel(PCLAffinity affinity, int amount)
    {
        return add(new AddAffinityLevel(affinity, amount));
    }

    public AddPowerEffectBonus addPowerEffectBonus(String powerID, CombatManager.Type effectType, int amount)
    {
        return add(new AddPowerEffectBonus(powerID, effectType, amount));
    }

    public AddPowerEffectBonus addPowerEffectBonus(AbstractPower power, CombatManager.Type effectType, int amount)
    {
        return add(new AddPowerEffectBonus(power, effectType, amount));
    }

    public AddPowerEffectBonus addPowerEffectEnemyBonus(String powerID, int amount)
    {
        return add(new AddPowerEffectBonus(powerID, CombatManager.Type.Effect, amount));
    }

    public AddPowerEffectBonus addPowerEffectPassiveDamageBonus(String powerID, int amount)
    {
        return add(new AddPowerEffectBonus(powerID, CombatManager.Type.PassiveDamage, amount));
    }

    public AddPowerEffectBonus addPowerEffectPlayerBonus(String powerID, int amount)
    {
        return add(new AddPowerEffectBonus(powerID, CombatManager.Type.PlayerEffect, amount));
    }

    public ApplyPower applyPower(AbstractPower power)
    {
        return applyPower(power.owner, power.owner, power);
    }

    public ApplyPowerAuto applyPower(PCLCardTarget targetHelper, PCLPowerHelper power, int amount)
    {
        return applyPower(player, targetHelper, power, amount);
    }

    public ApplyPowerAuto applyPower(AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper power)
    {
        return add(new ApplyPowerAuto(target, target, targetHelper, power, 1));
    }

    public ApplyPowerAuto applyPower(AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper power, int amount)
    {
        return add(new ApplyPowerAuto(target, target, targetHelper, power, amount));
    }

    public ApplyPowerAuto applyPower(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper power)
    {
        return add(new ApplyPowerAuto(source, target, targetHelper, power, 1));
    }

    public ApplyPowerAuto applyPower(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper power, int amount)
    {
        return add(new ApplyPowerAuto(source, target, targetHelper, power, amount));
    }

    public ApplyPowerAuto applyPower(AbstractCreature source, AbstractCreature target, PCLCardTarget targetHelper, PCLPowerHelper power, int amount, boolean temporary)
    {
        return add(new ApplyPowerAuto(source, target, targetHelper, power, amount).setTemporary(temporary));
    }

    public ApplyPower applyPower(AbstractCreature source, AbstractPower power)
    {
        return applyPower(source, power.owner, power);
    }

    public ApplyPower applyPower(AbstractCreature source, AbstractCreature target, AbstractPower power)
    {
        return add(new ApplyPower(source, target, power));
    }

    public ApplyPower applyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount)
    {
        return add(new ApplyPower(source, target, power, amount));
    }

    public PlayVFX borderFlash(Color color)
    {
        return playVFX(new BorderFlashEffect(color, true));
    }

    public PlayVFX borderLongFlash(Color color)
    {
        return playVFX(new BorderLongFlashEffect(color, true));
    }

    public <T> CallbackAction callback(AbstractGameAction action, T state, ActionT2<T, AbstractGameAction> onCompletion)
    {
        return add(new CallbackAction(action, state, onCompletion));
    }

    public CallbackAction callback(AbstractGameAction action, ActionT1<AbstractGameAction> onCompletion)
    {
        return add(new CallbackAction(action, onCompletion));
    }

    public CallbackAction callback(AbstractGameAction action, ActionT0 onCompletion)
    {
        return add(new CallbackAction(action, onCompletion));
    }

    public CallbackAction callback(AbstractGameAction action)
    {
        return add(new CallbackAction(action));
    }

    public CallbackAction callback(ActionT0 onCompletion)
    {
        return callback(new WaitAction(0.05f), onCompletion);
    }

    public CallbackAction callback(ActionT1<AbstractGameAction> onCompletion)
    {
        return callback(new WaitAction(0.05f), onCompletion);
    }

    public <T> CallbackAction callback(T state, ActionT2<T, AbstractGameAction> onCompletion)
    {
        return callback(new WaitAction(0.05f), state, onCompletion);
    }

    public ChangeStanceAction changeStance(AbstractStance stance)
    {
        return add(new ChangeStanceAction(stance));
    }

    public ChangeStance changeStance(PCLStanceHelper stance)
    {
        return add(new ChangeStance(stance));
    }

    public ChangeStance changeStance(String stanceName)
    {
        return add(new ChangeStance(stanceName));
    }

    public ChannelOrb channelOrb(AbstractOrb orb)
    {
        return add(new ChannelOrb(orb));
    }

    public ChannelOrb channelOrbs(PCLOrbHelper orbHelper, int amount)
    {
        return add(new ChannelOrb(orbHelper, amount));
    }

    public ChannelOrb channelRandomOrbs(int amount)
    {
        return add(new ChannelOrb(PCLOrbHelper.randomHelper(true), amount));
    }

    public ChannelOrb channelRandomOrbs(int amount, boolean weighted)
    {
        return add(new ChannelOrb(PCLOrbHelper.randomHelper(weighted), amount));
    }

    public CycleCards cycle(String sourceName, int amount)
    {
        return (CycleCards) add(new CycleCards(sourceName, amount, false));
    }

    public ArrayList<DealDamage> dealCardDamage(PCLCard card, AbstractCreature target, AbstractGameAction.AttackEffect effect)
    {
        ArrayList<DealDamage> actions = new ArrayList<>();
        for (int i = 0; i < card.hitCount; i++)
        {
            actions.add(add(new DealDamage(card, target, effect))
                    .setPiercing(card.attackType.bypassThorns, card.attackType.bypassBlock));
        }

        return actions;
    }

    public ArrayList<DealDamageToAll> dealCardDamageToAll(PCLCard card, AbstractGameAction.AttackEffect effect)
    {
        ArrayList<DealDamageToAll> actions = new ArrayList<>();
        for (int i = 0; i < card.hitCount; i++)
        {
            actions.add(add(new DealDamageToAll(player, card.multiDamage, card.damageTypeForTurn, effect, false))
                    .setPiercing(card.attackType.bypassThorns, card.attackType.bypassBlock));
        }

        return actions;
    }

    public ArrayList<DealDamage> dealCardDamageToRandomEnemy(PCLCard card, AbstractGameAction.AttackEffect effect)
    {
        return dealCardDamage(card, null, effect);
    }

    public DealDamage dealDamage(AbstractCreature source, AbstractCreature target, int baseDamage, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect)
    {
        return add(new DealDamage(target, new DamageInfo(source, baseDamage, damageType), effect));
    }

    public DealDamage dealDamage(AbstractCreature target, DamageInfo damageInfo, AbstractGameAction.AttackEffect effect)
    {
        return add(new DealDamage(target, damageInfo, effect));
    }

    public ApplyPower dealDamageAtEndOfTurn(AbstractCreature source, AbstractCreature target, int amount)
    {
        return applyPower(source, new DelayedDamagePower(target, amount));
    }

    public ApplyPower dealDamageAtEndOfTurn(AbstractCreature source, AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect)
    {
        return applyPower(source, new DelayedDamagePower(target, amount, effect));
    }

    public DealDamageToAll dealDamageToAll(int[] damageMatrix, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect)
    {
        return add(new DealDamageToAll(player, damageMatrix, damageType, effect, false));
    }

    public DealDamage dealDamageToRandomEnemy(int baseDamage, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect)
    {
        return dealDamage(player, null, baseDamage, damageType, effect);
    }

    public MoveCard discard(AbstractCard card, CardGroup group)
    {
        return moveCard(card, group, player.discardPile);
    }

    public DiscardFromPile discardFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new DiscardFromPile(sourceName, amount, groups));
    }

    public DrawCards draw(int amount)
    {
        return add(new DrawCards(amount));
    }

    public MoveCard draw(AbstractCard card)
    {
        final float cardX = CardGroup.DRAW_PILE_X * 1.5f;
        final float cardY = CardGroup.DRAW_PILE_Y * 2f;

        return moveCard(card, player.drawPile, player.hand)
                .setCardPosition(cardX, cardY)
                .showEffect(true, false);
    }

    public ApplyPower drawLessNextTurn(int amount)
    {
        return applyPower(new DrawLessPower(player, amount));
    }

    public ApplyPower drawNextTurn(int amount)
    {
        return applyPower(new DrawCardNextTurnPower(player, amount));
    }

    public EvokeOrb evokeOrb(int times)
    {
        return add(new EvokeOrb(times));
    }

    public EvokeOrb evokeOrb(int times, AbstractOrb orb)
    {
        return add(new EvokeOrb(times, orb));
    }

    public EvokeOrb evokeOrb(int times, int limit)
    {
        return add(new EvokeOrb(times, limit, false));
    }

    public EvokeOrb evokeOrb(int times, int limit, boolean random)
    {
        return add(new EvokeOrb(times, limit, random));
    }

    public MoveCard exhaust(AbstractCard card)
    {
        return moveCard(card, player.exhaustPile);
    }

    public MoveCard exhaust(AbstractCard card, CardGroup group)
    {
        return moveCard(card, group, player.exhaustPile);
    }

    public ExhaustFromPile exhaustFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new ExhaustFromPile(sourceName, amount, groups));
    }

    public FetchFromPile fetchFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new FetchFromPile(sourceName, amount, groups));
    }

    public PlayVFX flash(AbstractCard card)
    {
        return playVFX(new CardFlashVfx(card, Color.ORANGE.cpy()));
    }

    public ApplyPowerAuto gain(PCLPowerHelper po, int amount)
    {
        return gain(po, amount, false);
    }

    public ApplyPowerAuto gain(PCLPowerHelper po, int amount, boolean temporary)
    {
        return applyPower(AbstractDungeon.player, AbstractDungeon.player, PCLCardTarget.Self, po, amount, temporary);
    }

    public GainBlock gainBlock(int amount)
    {
        return gainBlock(player, amount);
    }

    public GainBlock gainBlock(AbstractCreature target, int amount)
    {
        return add(new GainBlock(target, target, amount));
    }

    public GainEnergyAction gainEnergy(int amount)
    {
        return add(new GainEnergyAction(amount));
    }

    public ApplyPower gainEnergyNextTurn(int amount)
    {
        return applyPower(new EnergizedPower(player, amount));
    }

    public GainGold gainGold(int amount)
    {
        return add(new GainGold(amount, true));
    }

    public IncreaseMaxOrbAction gainOrbSlots(int slots)
    {
        return add(new IncreaseMaxOrbAction(slots));
    }

    public ApplyPower gainStrength(int amount)
    {
        return applyPower(new StrengthPower(player, amount));
    }

    public GainTemporaryHP gainTemporaryHP(int amount)
    {
        return add(new GainTemporaryHP(player, player, amount));
    }

    public HealCreature heal(AbstractCreature source, AbstractCreature target, int amount)
    {
        return add(new HealCreature(target, source, amount));
    }

    public HealCreature heal(int amount)
    {
        return add(new HealCreature(player, player, amount));
    }

    public IncreasePower increasePower(AbstractPower power, int amount)
    {
        return add(new IncreasePower(power.owner, power.owner, power, amount));
    }

    public InduceOrb induceOrb(AbstractOrb orb, boolean shouldTriggerEvokeEffect)
    {
        return add(new InduceOrb(orb, shouldTriggerEvokeEffect));
    }

    public InduceOrb induceOrbs(FuncT0<AbstractOrb> orbConstructor, int amount, boolean shouldTriggerEvokeEffect)
    {
        return add(new InduceOrb(orbConstructor, amount, shouldTriggerEvokeEffect));
    }

    public LoseBlock loseBlock(int amount)
    {
        return loseBlock(player, amount);
    }

    public LoseBlock loseBlock(AbstractCreature target, int amount)
    {
        return add(new LoseBlock(target, target, amount));
    }

    public LoseHP loseHP(AbstractCreature source, AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect)
    {
        return add(new LoseHP(target, source, amount, effect));
    }

    public LoseHP loseHP(int amount, AbstractGameAction.AttackEffect effect)
    {
        return add(new LoseHP(player, player, amount, effect));
    }

    public GenerateCard makeCard(AbstractCard card, CardGroup group)
    {
        return add(new GenerateCard(card, group));
    }

    public GenerateCard makeCardInDiscardPile(AbstractCard card)
    {
        return makeCard(card, player.discardPile);
    }

    public GenerateCard makeCardInDrawPile(AbstractCard card)
    {
        return makeCard(card, player.drawPile);
    }

    public GenerateCard makeCardInHand(AbstractCard card)
    {
        return makeCard(card, player.hand);
    }

    public ModifyAffinityLevel modifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinity, int amount, boolean relative, boolean reset)
    {
        return add(new ModifyAffinityLevel(card, affinity, amount, relative, reset));
    }

    public ModifyAffinityLevel modifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinity, int amount, boolean relative)
    {
        return add(new ModifyAffinityLevel(card, affinity, amount, relative));
    }

    public ModifyAffinityLevel modifyAffinityLevel(CardGroup group, int cards, List<PCLAffinity> affinity, int amount, boolean relative, boolean reset)
    {
        return add(new ModifyAffinityLevel(group, cards, affinity, amount, relative, reset));
    }

    public ModifyAffinityLevel modifyAffinityLevel(CardGroup group, int cards, List<PCLAffinity> affinity, int amount, boolean relative)
    {
        return add(new ModifyAffinityLevel(group, cards, affinity, amount, relative));
    }

    public <S> ModifyAllCopies modifyAllCopies(String cardID, S state, ActionT2<S, AbstractCard> onCompletion)
    {
        return add(new ModifyAllCopies(cardID, state, onCompletion));
    }

    public ModifyAllCopies modifyAllCopies(String cardID, ActionT1<AbstractCard> onCompletion)
    {
        return add(new ModifyAllCopies(cardID, onCompletion));
    }

    public ModifyAllCopies modifyAllCopies(String cardID)
    {
        return add(new ModifyAllCopies(cardID));
    }

    public <S> ModifyAllInstances modifyAllInstances(UUID uuid, S state, ActionT2<S, AbstractCard> onCompletion)
    {
        return add(new ModifyAllInstances(uuid, state, onCompletion));
    }

    public ModifyAllInstances modifyAllInstances(UUID uuid, ActionT1<AbstractCard> onCompletion)
    {
        return add(new ModifyAllInstances(uuid, onCompletion));
    }

    public ModifyAllInstances modifyAllInstances(UUID uuid)
    {
        return add(new ModifyAllInstances(uuid));
    }

    public ModifyBlock modifyBlock(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyBlock(card, costChange, permanent, relative));
    }

    public ModifyBlock modifyBlock(CardGroup cardGroup, int amount, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyBlock(cardGroup, amount, costChange, permanent, relative));
    }

    public ModifyCost modifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyCost(card, costChange, permanent, relative));
    }

    public ModifyCost modifyCost(CardGroup cardGroup, int amount, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyCost(cardGroup, amount, costChange, permanent, relative));
    }

    public ModifyDamage modifyDamage(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyDamage(card, costChange, permanent, relative));
    }

    public ModifyDamage modifyDamage(CardGroup cardGroup, int amount, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyDamage(cardGroup, amount, costChange, permanent, relative));
    }

    public ModifyTag modifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative)
    {
        return add(new ModifyTag(card, tag, value, relative));
    }

    public ModifyTag modifyTag(AbstractCard card, PCLCardTag tag, int value)
    {
        return add(new ModifyTag(card, tag, value));
    }

    public ModifyTag modifyTag(CardGroup group, int cards, PCLCardTag tag, int value, boolean relative)
    {
        return add(new ModifyTag(group, cards, tag, value, relative));
    }

    public ModifyTag modifyTag(CardGroup group, int cards, PCLCardTag tag, int value)
    {
        return add(new ModifyTag(group, cards, tag, value));
    }

    public ModifyTempHP modifyTempHP(AbstractCard card, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyTempHP(card, costChange, permanent, relative));
    }

    public ModifyTempHP modifyTempHP(CardGroup cardGroup, int amount, int costChange, boolean permanent, boolean relative)
    {
        return add(new ModifyTempHP(cardGroup, amount, costChange, permanent, relative));
    }

    public MoveCard moveCard(AbstractCard card, CardGroup destination)
    {
        return add(new MoveCard(card, destination));
    }

    public MoveCard moveCard(AbstractCard card, CardGroup source, CardGroup destination)
    {
        return add(new MoveCard(card, destination, source));
    }

    public MoveCards moveCards(CardGroup source, CardGroup destination)
    {
        return add(new MoveCards(destination, source));
    }

    public MoveCards moveCards(CardGroup source, CardGroup destination, int amount)
    {
        return add(new MoveCards(destination, source, amount));
    }

    public GenerateCard obtainAffinityToken(PCLAffinity affinity, boolean upgraded)
    {
        return makeCardInHand(AffinityToken.getCard(affinity)).setUpgrade(upgraded, false);
    }

    public PlayCard playCard(CardGroup sourcePile, AbstractCreature target, FuncT1<AbstractCard, CardGroup> findCard)
    {
        return add(new PlayCard(findCard, sourcePile, target));
    }

    public PlayCard playCard(AbstractCard card, CardGroup sourcePile, AbstractCreature target)
    {
        return add(new PlayCard(card, target, false, actionOrder != PCLActions.ActionOrder.Top)).setSourcePile(sourcePile);
    }

    public PlayCard playCard(AbstractCard card, AbstractCreature target)
    {
        return add(new PlayCard(card, target, false, actionOrder != PCLActions.ActionOrder.Top));
    }

    public PlayCard playCopy(AbstractCard card, AbstractCreature target)
    {
        return add(new PlayCard(card, target, true, actionOrder != PCLActions.ActionOrder.Top))
                .setCurrentPosition(card.current_x, card.current_y)
                .spendEnergy(false)
                .setPurge(true);
    }

    public PlayFromPile playFromPile(String sourceName, int amount, AbstractMonster target, CardGroup... groups)
    {
        return add(new PlayFromPile(sourceName, target, amount, groups));
    }

    public ProgressCooldown progressCooldown(AbstractCard card, int change)
    {
        return add(new ProgressCooldown(card, change));
    }

    public PurgeFromPile purgeFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new PurgeFromPile(sourceName, amount, groups));
    }

    public HealCreature recoverHP(int amount)
    {
        return add(new HealCreature(player, player, amount)).recover(true);
    }

    public ModifyPowers reduceCommonDebuffs(AbstractCreature target, int amount)
    {
        return add(new ModifyPowers(target, target, -amount, true))
                .setFilter(GameUtilities::isCommonDebuff);
    }

    public ModifyPowers reduceDebuffs(AbstractCreature target, int amount)
    {
        return add(new ModifyPowers(target, target, -amount, true))
                .setFilter(GameUtilities::isDebuff);
    }

    public ReducePower reducePower(AbstractCreature source, String powerID, int amount)
    {
        return add(new ReducePower(source, source, powerID, amount));
    }

    public ReducePower reducePower(AbstractCreature target, AbstractCreature source, String powerID, int amount)
    {
        return add(new ReducePower(target, source, powerID, amount));
    }

    public ReducePower reducePower(AbstractPower power, int amount)
    {
        return add(new ReducePower(power.owner, power.owner, power, amount));
    }

    public ModifyPowers removeCommonDebuffs(AbstractCreature target, ListSelection<AbstractPower> selection, int count)
    {
        return add(new ModifyPowers(target, target, 0, false))
                .setFilter(GameUtilities::isCommonDebuff)
                .setSelection(selection, count);
    }

    public ModifyPowers removeDebuffs(AbstractCreature target, ListSelection<AbstractPower> selection, int count)
    {
        return add(new ModifyPowers(target, target, 0, false))
                .setFilter(GameUtilities::isDebuff)
                .setSelection(selection, count);
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractPower power)
    {
        return add(new RemoveSpecificPowerAction(power.owner, source, power));
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractCreature target, AbstractPower power)
    {
        return add(new RemoveSpecificPowerAction(target, source, power));
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractCreature target, String powerID)
    {
        return add(new RemoveSpecificPowerAction(target, source, powerID));
    }

    public ReplaceCard replaceCard(UUID uuid, AbstractCard replacement)
    {
        return add(new ReplaceCard(uuid, replacement));
    }

    public RerollAffinity rerollAffinity()
    {
        return add(new RerollAffinity(0));
    }

    public RerollAffinity rerollAffinity(int target)
    {
        return add(new RerollAffinity(target));
    }

    public RerollAffinity rerollAffinity(int target, PCLAffinity... affinities)
    {
        return add(new RerollAffinity(target)).setAffinityChoices(affinities);
    }

    public ReshuffleDiscardPile reshuffleDiscardPile(boolean onlyIfEmpty)
    {
        return add(new ReshuffleDiscardPile(onlyIfEmpty));
    }

    public ReshuffleFromPile reshuffleFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new ReshuffleFromPile(sourceName, amount, groups));
    }

    public PlaySFX playSFX(String key)
    {
        return playSFX(key, 1, 1, 1);
    }

    public PlaySFX playSFX(String key, float pitchMin, float pitchMax)
    {
        return add(new PlaySFX(key, pitchMin, pitchMax, 1));
    }

    public PlaySFX playSFX(String key, float pitchMin, float pitchMax, float volume)
    {
        return add(new PlaySFX(key, pitchMin, pitchMax, volume));
    }

    public ScoutCards scout(String sourceName, int amount)
    {
        return (ScoutCards) add(new ScoutCards(sourceName, amount));
    }

    public ScryWhichActuallyTriggersDiscard scry(int amount)
    {
        return add(new ScryWhichActuallyTriggersDiscard(amount));
    }

    public SelectCreature selectCreature(PCLCardTarget target, String source)
    {
        return add(new SelectCreature(target, source));
    }

    public SelectCreature selectCreature(AbstractCard card)
    {
        return add(new SelectCreature(card));
    }

    public SelectFromPile selectFromPile(String sourceName, int amount, CardGroup... groups)
    {
        return add(new SelectFromPile(sourceName, amount, groups));
    }

    public SequentialAction sequential(AbstractGameAction action, AbstractGameAction action2)
    {
        return add(new SequentialAction(action, action2));
    }

    public ShakeScreenAction shakeScreen(float actionDuration, ScreenShake.ShakeDur shakeDuration, ScreenShake.ShakeIntensity intensity)
    {
        return add(new ShakeScreenAction(actionDuration, shakeDuration, intensity));
    }

    public SpendEnergy spendEnergy(AbstractCard card)
    {
        return add(new SpendEnergy(card.freeToPlay() ? 0 : card.costForTurn, false));
    }

    public SpendEnergy spendEnergy(int amount, boolean canSpendLess)
    {
        return add(new SpendEnergy(amount, canSpendLess));
    }

    public SpreadPower spreadPower(AbstractCreature source, AbstractCreature target, PCLPowerHelper power, int amount)
    {
        return add(new SpreadPower(source, target, power, amount));
    }

    public SpreadPower spreadPower(AbstractCreature source, AbstractCreature target, String power, int amount)
    {
        return add(new SpreadPower(source, target, power, amount));
    }

    public StabilizePower stabilizePower(AbstractCreature source, AbstractCreature target, PCLPowerHelper power, int amount)
    {
        return add(new StabilizePower(source, target, power, amount));
    }

    public StabilizePower stabilizePower(AbstractCreature source, AbstractCreature target, String power, int amount)
    {
        return add(new StabilizePower(source, target, power, amount));
    }

    public ApplyAffinityPower stackAffinityPower(PCLAffinity affinity, int amount)
    {
        return add(new ApplyAffinityPower(player, affinity, amount, false));
    }

    public ApplyAffinityPower stackAffinityPower(PCLAffinity affinity, int amount, boolean temporary)
    {
        return add(new ApplyAffinityPower(player, affinity, amount, temporary));
    }

    public SummonAllyAction summonAlly(PCLCard card, PCLCardAlly slot)
    {
        return add(new SummonAllyAction(card, slot));
    }

    public PlayVFX superFlash(AbstractCard card)
    {
        return playVFX(new CardFlashVfx(card, Color.ORANGE.cpy(), true));
    }

    public DealDamage takeDamage(int amount, AbstractGameAction.AttackEffect effect)
    {
        return takeDamage(player, amount, effect);
    }

    public DealDamage takeDamage(AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect)
    {
        return dealDamage(null, target, amount, DamageInfo.DamageType.THORNS, effect);
    }

    public TalkAction talk(AbstractCreature source, String text)
    {
        return add(new TalkAction(source, text));
    }

    public TalkAction talk(AbstractCreature source, String text, float duration, float bubbleDuration)
    {
        return add(new TalkAction(source, text, duration, bubbleDuration));
    }

    public TriggerAllyAction triggerAlly(PCLCardAlly ally)
    {
        return add(new TriggerAllyAction(ally));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(int times)
    {
        return add(new TriggerOrbPassiveAbility(times));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(int times, int limit, boolean random)
    {
        return add(new TriggerOrbPassiveAbility(times, limit, random, null));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(AbstractOrb orb, int times)
    {
        return add(new TriggerOrbPassiveAbility(orb, times));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinity(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PCLAffinity> affinities)
    {
        return add(TryChooseChoice.chooseAffinity(name, choices, source, target, affinities));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinitySkill(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useAffinitySkill(name, choices, source, target, skills));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinitySkill(String name, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useAffinitySkill(name, choices, cost, source, target, skills));
    }

    public TryChooseChoice<PSkill> tryChooseSkill(PCLCardData sourceData, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useSkill(sourceData, choices, source, target, skills));
    }

    public TryChooseChoice<PSkill> tryChooseSkill(PCLCardData sourceData, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useSkill(sourceData, choices, cost, source, target, skills));
    }

    public TryChooseChoice<PSkill> tryChooseTargetSkill(PCLCardData sourceData, int choices, AbstractCreature source, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useSkillWithTargeting(sourceData, choices, source, skills));
    }

    public TryChooseChoice<PSkill> tryChooseTargetSkill(PCLCardData sourceData, int choices, int cost, AbstractCreature source, Collection<PSkill> skills)
    {
        return add(TryChooseChoice.useSkillWithTargeting(sourceData, choices, cost, source, skills));
    }

    public UpgradeFromPile upgradeFromPile(String sourceName, int amount, CardGroup... group)
    {
        return add(new UpgradeFromPile(sourceName, amount, group));
    }

    public UsePotionAction usePotion(AbstractPotion potion, AbstractCreature target)
    {
        return usePotion(potion, target, 1);
    }

    public UsePotionAction usePotion(AbstractPotion potion, AbstractCreature target, int amount)
    {
        return add(new UsePotionAction(potion, target, amount));
    }

    public WithdrawAllyAction withdrawAlly(PCLCardAlly ally)
    {
        return add(new WithdrawAllyAction(ally));
    }

    public WithdrawAllyAction withdrawAlly(Collection<PCLCardAlly> ally)
    {
        return add(new WithdrawAllyAction(ally));
    }

    public PlayVFX playVFX(AbstractGameEffect effect)
    {
        return add(new PlayVFX(effect, 0));
    }

    public PlayVFX playVFX(AbstractGameEffect effect, float wait)
    {
        return add(new PlayVFX(effect, wait));
    }

    public PlayVFX playVFX(AbstractGameEffect effect, float wait, boolean isPercentage)
    {
        return add(new PlayVFX(effect, isPercentage ? effect.duration * wait : wait));
    }

    public WaitAction wait(float duration)
    {
        return add(new WaitAction(duration));
    }

    public WaitRealtimeAction waitRealtime(float duration)
    {
        return add(new WaitRealtimeAction(duration));
    }

    protected static class ExecuteLast implements OnPhaseChangedSubscriber
    {
        private final AbstractGameAction action;

        private ExecuteLast(AbstractGameAction action)
        {
            this.action = action;
        }

        public static void add(AbstractGameAction action)
        {
            CombatManager.onPhaseChanged.subscribe(new ExecuteLast(action));
        }

        @Override
        public void onPhaseChanged(GameActionManager.Phase phase)
        {
            if (phase == GameActionManager.Phase.WAITING_ON_USER)
            {
                PCLActions.bottom.add(action);
                CombatManager.onPhaseChanged.unsubscribe(this);
            }
        }
    }
}