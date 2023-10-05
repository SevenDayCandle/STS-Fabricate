package pinacolada.actions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.actions.utility.ShakeScreenAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.cardManip.CardFlashVfx;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import pinacolada.actions.cards.*;
import pinacolada.actions.creature.*;
import pinacolada.actions.orbs.ChannelOrb;
import pinacolada.actions.orbs.EvokeOrb;
import pinacolada.actions.orbs.RemoveOrb;
import pinacolada.actions.orbs.TriggerOrbPassiveAbility;
import pinacolada.actions.piles.*;
import pinacolada.actions.player.SpendEnergy;
import pinacolada.actions.powers.AddPowerEffectBonus;
import pinacolada.actions.powers.ApplyOrReducePowerAction;
import pinacolada.actions.powers.SpreadPower;
import pinacolada.actions.special.*;
import pinacolada.actions.utility.*;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.interfaces.subscribers.OnPhaseChangedSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PSkill;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;


// Copied and modified from STS-AnimatorMod
@SuppressWarnings("UnusedReturnValue")
public final class PCLActions {

    public static PCLActions nextCombat = new PCLActions(ActionOrder.NextCombat);
    public static PCLActions turnStart = new PCLActions(ActionOrder.TurnStart);
    public static PCLActions instant = new PCLActions(ActionOrder.Instant);
    public static PCLActions top = new PCLActions(ActionOrder.Top);
    public static PCLActions bottom = new PCLActions(ActionOrder.Bottom);
    public static PCLActions delayed = new PCLActions(ActionOrder.Delayed);
    public static PCLActions delayedTop = new PCLActions(ActionOrder.DelayedTop);
    public static PCLActions last = new PCLActions(ActionOrder.Last);
    private final ActionOrder actionOrder;

    private PCLActions(ActionOrder actionOrder) {
        this.actionOrder = actionOrder;
    }

    public static void clearActions() {
        AbstractDungeon.actionManager.actions.clear();
    }

    public static DelayAllActions delayCurrentActions() {
        return top.add(new DelayAllActions(true));
    }

    public static ArrayList<AbstractGameAction> getActions() {
        return AbstractDungeon.actionManager.actions;
    }

    public <T extends AbstractGameAction> T add(T action) {
        if (action instanceof PCLAction) {
            ((PCLAction<?>) action).setOriginalOrder(actionOrder);
        }

        switch (actionOrder) {
            case Top: {
                AbstractDungeon.actionManager.addToTop(action);
                break;
            }

            case Bottom: {
                AbstractDungeon.actionManager.addToBottom(action);
                break;
            }

            case TurnStart: {
                AbstractDungeon.actionManager.addToTurnStart(action);
                break;
            }

            case NextCombat: {
                AbstractDungeon.actionManager.addToNextCombat(action);
                break;
            }

            case Instant: {
                AbstractGameAction current = AbstractDungeon.actionManager.currentAction;
                if (current != null) {
                    AbstractDungeon.actionManager.addToTop(current);
                }

                AbstractDungeon.actionManager.currentAction = action;
                AbstractDungeon.actionManager.phase = GameActionManager.Phase.EXECUTING_ACTIONS;

                break;
            }

            case Delayed:
            case DelayedTop: {
                bottom.callback(action, bottom::add);
                break;
            }

            case Last: {
                ExecuteLast.add(action);
                break;
            }
        }

        return action;
    }

    public AddPowerEffectBonus addPowerEffectBonus(String powerID, int amount, boolean forPlayer) {
        return add(new AddPowerEffectBonus(powerID, amount, forPlayer));
    }

    public AddPowerEffectBonus addPowerEffectEnemyBonus(String powerID, int amount) {
        return addPowerEffectBonus(powerID, amount, false);
    }

    public AddPowerEffectBonus addPowerEffectPlayerBonus(String powerID, int amount) {
        return addPowerEffectBonus(powerID, amount, true);
    }

    public ApplyOrReducePowerAction applyPower(AbstractPower power) {
        return applyPower(power.owner, power.owner, power);
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature target, PCLPowerData power) {
        return add(new ApplyOrReducePowerAction(target, target, power, 1));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature target, PCLPowerData power, int amount) {
        return add(new ApplyOrReducePowerAction(target, target, power, amount));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractCreature target, PCLPowerData power) {
        return add(new ApplyOrReducePowerAction(source, target, power, 1));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractCreature target, PCLPowerData power, int amount) {
        return add(new ApplyOrReducePowerAction(source, target, power, amount));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractCreature target, PCLPowerData power, int amount, boolean temporary) {
        return add(new ApplyOrReducePowerAction(source, target, power, amount, temporary));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractPower power) {
        return applyPower(source, power.owner, power);
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractCreature target, AbstractPower power) {
        return add(new ApplyOrReducePowerAction(source, target, power));
    }

    public ApplyOrReducePowerAction applyPower(AbstractCreature source, AbstractCreature target, AbstractPower power, int amount) {
        return add(new ApplyOrReducePowerAction(source, target, power, amount));
    }

    public PlayVFX borderFlash(Color color) {
        return playVFX(new BorderFlashEffect(color, true));
    }

    public PlayVFX borderLongFlash(Color color) {
        return playVFX(new BorderLongFlashEffect(color, true));
    }

    public <T extends AbstractGameAction, U> CallbackAction<T> callback(T action, U state, ActionT2<U, T> onCompletion) {
        return add(new CallbackAction<T>(action, state, onCompletion));
    }

    public <T extends AbstractGameAction> CallbackAction<T> callback(T action, ActionT1<T> onCompletion) {
        return add(new CallbackAction<T>(action, onCompletion));
    }

    public <T extends AbstractGameAction> CallbackAction<T> callback(T action, ActionT0 onCompletion) {
        return add(new CallbackAction<T>(action, onCompletion));
    }

    public CallbackAction<WaitAction> callback(ActionT0 onCompletion) {
        return callback(new WaitAction(0.05f), onCompletion);
    }

    public CallbackAction<WaitAction> callback(ActionT1<WaitAction> onCompletion) {
        return callback(new WaitAction(0.05f), onCompletion);
    }

    public <U> CallbackAction<WaitAction> callback(U state, ActionT2<U, WaitAction> onCompletion) {
        return callback(new WaitAction(0.05f), state, onCompletion);
    }

    public ChangeStanceAction changeStance(AbstractStance stance) {
        return add(new ChangeStanceAction(stance));
    }

    public ChangeStanceAction changeStance(PCLStanceHelper stance) {
        return add(new ChangeStanceAction(stance.ID));
    }

    public ChangeStanceAction changeStance(String stanceName) {
        return add(new ChangeStanceAction(stanceName));
    }

    public ChannelOrb channelOrb(AbstractOrb orb) {
        return add(new ChannelOrb(orb));
    }

    public ChannelOrb channelOrbs(PCLOrbHelper orbHelper, int amount) {
        return add(new ChannelOrb(orbHelper, amount));
    }

    public ChannelOrb channelRandomOrbs(int amount) {
        return add(new ChannelOrb(PCLOrbHelper.randomHelper(true), amount));
    }

    public ChannelOrb channelRandomOrbs(int amount, boolean weighted) {
        return add(new ChannelOrb(PCLOrbHelper.randomHelper(weighted), amount));
    }

    public CycleCards cycle(String sourceName, int amount) {
        return add(new CycleCards(sourceName, amount));
    }

    public DealDamage dealCardDamage(PCLCard card, AbstractCreature source, AbstractCreature target, PCLAttackVFX effect) {
        return dealCardDamage(card, source, target, effect.key);
    }

    public DealDamage dealCardDamage(PCLCard card, AbstractCreature source, AbstractCreature target, AbstractGameAction.AttackEffect effect) {
        return dealCardDamage(card, card.hitCount, source, target, effect, card.pclTarget.targetsRandom());
    }

    public DealDamage dealCardDamage(PCLCard card, int times, AbstractCreature source, AbstractCreature target, AbstractGameAction.AttackEffect effect, boolean randomize) {
        return add(new DealDamage(card, source, target, effect, times))
                .canRedirect(randomize)
                .shouldRandomize(randomize)
                .setPiercing(card.attackType.bypassThorns, card.attackType.bypassBlock);
    }

    public DealDamageToAll dealCardDamageToAll(PCLCard card, AbstractCreature source, PCLAttackVFX effect) {
        return dealCardDamageToAll(card, source, effect.key);
    }

    public DealDamageToAll dealCardDamageToAll(PCLCard card, AbstractCreature source, AbstractGameAction.AttackEffect effect) {
        return dealCardDamageToAll(card, card.hitCount, source, effect);
    }

    public DealDamageToAll dealCardDamageToAll(PCLCard card, int times, AbstractCreature source, AbstractGameAction.AttackEffect effect) {
        return add(new DealDamageToAll(card, source, card.multiDamageCreatures, card.multiDamage, card.damageTypeForTurn, effect, times))
                .setPiercing(card.attackType.bypassThorns, card.attackType.bypassBlock);
    }

    public DealDamage dealDamage(AbstractCreature source, AbstractCreature target, int baseDamage, DamageInfo.DamageType damageType, PCLAttackVFX effect) {
        return add(new DealDamage(target, new DamageInfo(source, baseDamage, damageType), effect.key));
    }

    public DealDamage dealDamage(AbstractCreature source, AbstractCreature target, int baseDamage, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect) {
        return add(new DealDamage(target, new DamageInfo(source, baseDamage, damageType), effect));
    }

    public DealDamage dealDamage(AbstractCreature target, DamageInfo damageInfo, PCLAttackVFX effect) {
        return add(new DealDamage(target, damageInfo, effect.key));
    }

    public DealDamage dealDamage(AbstractCreature target, DamageInfo damageInfo, AbstractGameAction.AttackEffect effect) {
        return add(new DealDamage(target, damageInfo, effect));
    }

    public DealDamageToAll dealDamageToAll(AbstractCreature source, ArrayList<AbstractCreature> targets, int[] damageMatrix, DamageInfo.DamageType damageType, PCLAttackVFX effect) {
        return add(new DealDamageToAll(source, targets, damageMatrix, damageType, effect.key));
    }

    public DealDamageToAll dealDamageToAll(AbstractCreature source, ArrayList<AbstractCreature> targets, int[] damageMatrix, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect) {
        return add(new DealDamageToAll(source, targets, damageMatrix, damageType, effect));
    }

    public DealDamage dealDamageToRandomEnemy(int baseDamage, DamageInfo.DamageType damageType, PCLAttackVFX effect) {
        return dealDamage(player, null, baseDamage, damageType, effect).canRedirect(true).shouldRandomize(true);
    }

    public DealDamage dealDamageToRandomEnemy(int baseDamage, DamageInfo.DamageType damageType, AbstractGameAction.AttackEffect effect) {
        return dealDamage(player, null, baseDamage, damageType, effect).canRedirect(true).shouldRandomize(true);
    }

    public MoveCard discard(AbstractCard card, CardGroup group) {
        return moveCard(card, group, player.discardPile);
    }

    public DiscardFromPile discardFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new DiscardFromPile(sourceName, amount, groups));
    }

    public DrawCards draw(int amount) {
        return add(new DrawCards(amount));
    }

    public MoveCard draw(AbstractCard card) {
        final float cardX = CardGroup.DRAW_PILE_X * 1.5f;
        final float cardY = CardGroup.DRAW_PILE_Y * 2f;

        return moveCard(card, player.drawPile, player.hand)
                .setCardPosition(cardX, cardY)
                .showEffect(true, false);
    }

    public EvokeOrb evokeOrb(int times) {
        return add(new EvokeOrb(times));
    }

    public EvokeOrb evokeOrb(int times, AbstractOrb orb) {
        return add(new EvokeOrb(times, orb));
    }

    public EvokeOrb evokeOrb(int times, int limit) {
        return add(new EvokeOrb(times, limit, false));
    }

    public EvokeOrb evokeOrb(int times, int limit, boolean random) {
        return add(new EvokeOrb(times, limit, random));
    }

    public RemoveOrb evokeOrb(AbstractOrb orb) {
        return add(new RemoveOrb(orb));
    }

    public MoveCard exhaust(AbstractCard card) {
        return moveCard(card, player.exhaustPile);
    }

    public MoveCard exhaust(AbstractCard card, CardGroup group) {
        return moveCard(card, group, player.exhaustPile);
    }

    public ExhaustFromPile exhaustFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new ExhaustFromPile(sourceName, amount, groups));
    }

    public FetchFromPile fetchFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new FetchFromPile(sourceName, amount, groups));
    }

    public PlayVFX flash(AbstractCard card) {
        return playVFX(new CardFlashVfx(card, Color.ORANGE.cpy()));
    }

    public GainBlockAction gainBlock(int amount) {
        return gainBlock(player, amount);
    }

    public GainBlockAction gainBlock(AbstractCreature target, int amount) {
        return add(new GainBlockAction(target, target, amount));
    }

    public GainEnergyAction gainEnergy(int amount) {
        return add(new GainEnergyAction(amount));
    }

    public GainGoldAction gainGold(int amount) {
        return add(new GainGoldAction(amount));
    }

    public IncreaseMaxOrbAction gainOrbSlots(int slots) {
        return add(new IncreaseMaxOrbAction(slots));
    }

    public AddOrRemoveSummonSlotAction gainSummonSlots(int slots) {
        return add(new AddOrRemoveSummonSlotAction(slots));
    }

    public GainTemporaryHP gainTemporaryHP(int amount) {
        return add(new GainTemporaryHP(player, player, amount));
    }

    public GainTemporaryHP gainTemporaryHP(AbstractCreature source, AbstractCreature target, int amount) {
        return add(new GainTemporaryHP(source, target, amount));
    }

    public HealAction heal(AbstractCreature source, AbstractCreature target, int amount) {
        return add(new HealAction(target, source, amount));
    }

    public HealAction heal(int amount) {
        return add(new HealAction(player, player, amount));
    }

    public LoseBlockAction loseBlock(int amount) {
        return loseBlock(player, amount);
    }

    public LoseBlockAction loseBlock(AbstractCreature target, int amount) {
        return add(new LoseBlockAction(target, target, amount));
    }

    public LoseHP loseHP(AbstractCreature source, AbstractCreature target, int amount, PCLAttackVFX effect) {
        return add(new LoseHP(target, source, amount, effect.key));
    }

    public LoseHP loseHP(AbstractCreature source, AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect) {
        return add(new LoseHP(target, source, amount, effect));
    }

    public LoseHP loseHP(int amount, AbstractGameAction.AttackEffect effect) {
        return add(new LoseHP(player, player, amount, effect));
    }

    public GenerateCard makeCard(AbstractCard card, CardGroup group) {
        return add(new GenerateCard(card, group));
    }

    public GenerateCard makeCardInDiscardPile(AbstractCard card) {
        return makeCard(card, player.discardPile);
    }

    public GenerateCard makeCardInDrawPile(AbstractCard card) {
        return makeCard(card, player.drawPile);
    }

    public GenerateCard makeCardInHand(AbstractCard card) {
        return makeCard(card, player.hand);
    }

    public ModifyAffinityLevel modifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinity, int amount, boolean relative, boolean reset) {
        return add(new ModifyAffinityLevel(card, affinity, amount, relative, reset));
    }

    public ModifyAffinityLevel modifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinity, int amount, boolean relative) {
        return add(new ModifyAffinityLevel(card, affinity, amount, relative));
    }

    public ModifyAllCopies modifyAllCopies(String cardID, ActionT1<AbstractCard> onCompletion) {
        return add(new ModifyAllCopies(cardID, onCompletion));
    }

    public ModifyAllCopies modifyAllCopies(String cardID) {
        return add(new ModifyAllCopies(cardID));
    }

    public <S> ModifyAllInstances modifyAllInstances(UUID uuid, S state, ActionT2<S, AbstractCard> onCompletion) {
        return add(new ModifyAllInstances(uuid, state, onCompletion));
    }

    public ModifyAllInstances modifyAllInstances(UUID uuid, ActionT1<AbstractCard> onCompletion) {
        return add(new ModifyAllInstances(uuid, onCompletion));
    }

    public ModifyAllInstances modifyAllInstances(UUID uuid) {
        return add(new ModifyAllInstances(uuid));
    }

    public ModifyBlock modifyBlock(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        return add(new ModifyBlock(card, costChange, permanent, relative, untilPlayed));
    }

    public ModifyCardHP modifyCardHp(AbstractCard card, int costChange, boolean permanent, boolean relative) {
        return add(new ModifyCardHP(card, costChange, permanent, relative));
    }

    public ModifyCost modifyCost(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        return add(new ModifyCost(card, costChange, permanent, relative, untilPlayed));
    }

    public ModifyDamage modifyDamage(AbstractCard card, int costChange, boolean permanent, boolean relative, boolean untilPlayed) {
        return add(new ModifyDamage(card, costChange, permanent, relative, untilPlayed));
    }

    public ModifyMagicNumber modifyMagicNumber(AbstractCard card, int costChange, boolean permanent, boolean relative) {
        return add(new ModifyMagicNumber(card, costChange, permanent, relative));
    }

    public ModifyTag modifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative) {
        return add(new ModifyTag(card, tag, value, relative));
    }

    public ModifyTag modifyTag(AbstractCard card, PCLCardTag tag, int value) {
        return add(new ModifyTag(card, tag, value));
    }

    public MoveCard moveCard(AbstractCard card, CardGroup destination) {
        return add(new MoveCard(card, destination));
    }

    public MoveCard moveCard(AbstractCard card, CardGroup source, CardGroup destination) {
        return add(new MoveCard(card, destination, source));
    }

    public MoveCards moveCards(CardGroup source, CardGroup destination) {
        return add(new MoveCards(destination, source));
    }

    public MoveCards moveCards(CardGroup source, CardGroup destination, int amount) {
        return add(new MoveCards(destination, source, amount));
    }

    public PCLObtainPotionAction obtainPotion(AbstractPotion potion) {
        return add(new PCLObtainPotionAction(potion));
    }

    public ObtainRelicAction obtainRelic(AbstractRelic relic) {
        return add(new ObtainRelicAction(relic));
    }

    public PlayCard playCard(AbstractCard card, CardGroup sourcePile, AbstractCreature target) {
        return add(new PlayCard(card, target, false, actionOrder != PCLActions.ActionOrder.Top)).setSourcePile(sourcePile);
    }

    public PlayCard playCard(AbstractCard card, AbstractCreature target) {
        return add(new PlayCard(card, target, false, actionOrder != PCLActions.ActionOrder.Top));
    }

    public PlayCard playCopy(AbstractCard card, AbstractCreature target) {
        return add(new PlayCard(card, target, true, actionOrder != PCLActions.ActionOrder.Top))
                .setCurrentPosition(card.current_x, card.current_y)
                .spendEnergy(false)
                .setPurge(true);
    }

    public PlayFromPile playFromPile(String sourceName, int amount, AbstractCreature target, CardGroup... groups) {
        return add(new PlayFromPile(sourceName, target, amount, groups));
    }

    public PlaySFX playSFX(String key) {
        return playSFX(key, 1, 1, 1);
    }

    public PlaySFX playSFX(String key, float pitchMin, float pitchMax) {
        return add(new PlaySFX(key, pitchMin, pitchMax, 1));
    }

    public PlaySFX playSFX(String key, float pitchMin, float pitchMax, float volume) {
        return add(new PlaySFX(key, pitchMin, pitchMax, volume));
    }

    public PlayVFX playVFX(AbstractGameEffect effect) {
        return add(new PlayVFX(effect, 0));
    }

    public PlayVFX playVFX(AbstractGameEffect effect, float wait) {
        return add(new PlayVFX(effect, wait));
    }

    public PlayVFX playVFX(AbstractGameEffect effect, float wait, boolean isPercentage) {
        return add(new PlayVFX(effect, isPercentage ? effect.duration * wait : wait));
    }

    public ProgressCooldown progressCooldown(AbstractCreature source, AbstractCard card, int change) {
        return add(new ProgressCooldown(source, card, change));
    }

    public MoveCard purge(AbstractCard card) {
        return moveCard(card, CombatManager.PURGED_CARDS);
    }

    public MoveCard purge(AbstractCard card, CardGroup group) {
        return moveCard(card, group, CombatManager.PURGED_CARDS);
    }

    public PurgeFromPile purgeFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new PurgeFromPile(sourceName, amount, groups));
    }

    public RemoveOrb removeOrb(int times) {
        return add(new RemoveOrb(times));
    }

    public RemoveOrb removeOrb(int times, boolean random) {
        return add(new RemoveOrb(times, random));
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractPower power) {
        return add(new RemoveSpecificPowerAction(power.owner, source, power));
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractCreature target, AbstractPower power) {
        return add(new RemoveSpecificPowerAction(target, source, power));
    }

    public RemoveSpecificPowerAction removePower(AbstractCreature source, AbstractCreature target, String powerID) {
        return add(new RemoveSpecificPowerAction(target, source, powerID));
    }

    public ReplaceCard replaceCard(UUID uuid, AbstractCard replacement) {
        return add(new ReplaceCard(uuid, replacement));
    }

    public ReshuffleDiscardPile reshuffleDiscardPile(boolean onlyIfEmpty) {
        return add(new ReshuffleDiscardPile(onlyIfEmpty));
    }

    public ReshuffleFromPile reshuffleFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new ReshuffleFromPile(sourceName, amount, groups));
    }

    public ScoutCards scout(String sourceName, int amount) {
        return add(new ScoutCards(sourceName, amount));
    }

    public ScryCards scry(String sourceName, int amount) {
        return add(new ScryCards(sourceName, amount));
    }

    public SelectCreature selectCreature(PCLCardTarget target, String source) {
        return add(new SelectCreature(target, source));
    }

    public SelectCreature selectCreature(AbstractCard card) {
        return add(new SelectCreature(card));
    }

    public SelectFromPile selectFromPile(String sourceName, int amount, CardGroup... groups) {
        return add(new SelectFromPile(sourceName, amount, groups));
    }

    public SelectFromPile selectFromPile(String sourceName, int amount, Collection<AbstractCard> cards) {
        return add(new SelectFromPile(sourceName, amount, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED)));
    }

    public SequentialAction sequential(AbstractGameAction... action) {
        return add(new SequentialAction(action));
    }

    public SequentialAction sequential(List<? extends AbstractGameAction> action) {
        return add(new SequentialAction(action));
    }

    public ShakeScreenAction shakeScreen(float actionDuration, ScreenShake.ShakeDur shakeDuration, ScreenShake.ShakeIntensity intensity) {
        return add(new ShakeScreenAction(actionDuration, shakeDuration, intensity));
    }

    public ShowAndObtainCardAction showAndObtain(AbstractCard card) {
        return showAndObtain(card, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f);
    }

    public ShowAndObtainCardAction showAndObtain(AbstractCard card, float x, float y) {
        return add(new ShowAndObtainCardAction(card, x, y));
    }

    public SpendEnergy spendEnergy(AbstractCard card) {
        return add(new SpendEnergy(card.freeToPlay() ? 0 : card.costForTurn, false));
    }

    public SpendEnergy spendEnergy(int amount, boolean canSpendLess) {
        return add(new SpendEnergy(amount, canSpendLess));
    }

    public SpreadPower spreadPower(AbstractCreature source, AbstractCreature target, PCLPowerData power, int amount) {
        return add(new SpreadPower(source, target, power.ID, amount));
    }

    public SpreadPower spreadPower(AbstractCreature source, AbstractCreature target, String power, int amount) {
        return add(new SpreadPower(source, target, power, amount));
    }

    public SummonAllyAction summonAlly(PCLCard card, PCLCardAlly slot) {
        return add(new SummonAllyAction(card, slot));
    }

    public PlayVFX superFlash(AbstractCard card) {
        return playVFX(new CardFlashVfx(card, Color.ORANGE.cpy(), true));
    }

    public DealDamage takeDamage(int amount, AbstractGameAction.AttackEffect effect) {
        return takeDamage(player, amount, effect);
    }

    public DealDamage takeDamage(AbstractCreature target, int amount, AbstractGameAction.AttackEffect effect) {
        return dealDamage(null, target, amount, DamageInfo.DamageType.THORNS, effect);
    }

    public TalkAction talk(AbstractCreature source, String text) {
        return add(new TalkAction(source, text));
    }

    public TalkAction talk(AbstractCreature source, String text, float duration, float bubbleDuration) {
        return add(new TalkAction(source, text, duration, bubbleDuration));
    }

    public TriggerAllyAction triggerAlly(PCLCardAlly ally) {
        return add(new TriggerAllyAction(ally));
    }

    public TriggerAllyAction triggerAlly(PCLCardAlly ally, int amount) {
        return add(new TriggerAllyAction(ally, amount));
    }

    public TriggerAllyAction triggerAlly(PCLCardAlly ally, boolean manual) {
        return add(new TriggerAllyAction(ally).setManual(manual));
    }

    public TriggerAllyAction triggerAlly(PCLCardAlly ally, int amount, boolean manual) {
        return add(new TriggerAllyAction(ally, amount).setManual(manual));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(int times) {
        return add(new TriggerOrbPassiveAbility(times));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(int times, int limit, boolean random) {
        return add(new TriggerOrbPassiveAbility(times, limit, random, null));
    }

    public TriggerOrbPassiveAbility triggerOrbPassive(AbstractOrb orb, int times) {
        return add(new TriggerOrbPassiveAbility(orb, times));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinity(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PCLAffinity> affinities) {
        return add(TryChooseChoice.chooseAffinity(name, choices, source, target, affinities));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinitySkill(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useAffinitySkill(name, choices, source, target, skills));
    }

    public TryChooseChoice<PCLAffinity> tryChooseAffinitySkill(String name, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useAffinitySkill(name, choices, cost, source, target, skills));
    }

    public TryChooseChoice<PSkill<?>> tryChooseSkill(PCLCardData sourceData, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useSkill(sourceData, choices, source, target, skills));
    }

    public TryChooseChoice<PSkill<?>> tryChooseSkill(PCLCardData sourceData, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useSkill(sourceData, choices, cost, source, target, skills));
    }

    public TryChooseChoice<PSkill<?>> tryChooseTargetSkill(PCLCardData sourceData, int choices, AbstractCreature source, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useSkillWithTargeting(sourceData, choices, source, skills));
    }

    public TryChooseChoice<PSkill<?>> tryChooseTargetSkill(PCLCardData sourceData, int choices, int cost, AbstractCreature source, Collection<PSkill<?>> skills) {
        return add(TryChooseChoice.useSkillWithTargeting(sourceData, choices, cost, source, skills));
    }

    public UpgradeFromPile upgradeFromPile(String sourceName, int amount, CardGroup... group) {
        return add(new UpgradeFromPile(sourceName, amount, group));
    }

    public UsePotionAction usePotion(AbstractPotion potion, AbstractCreature target) {
        return usePotion(potion, target, 1);
    }

    public UsePotionAction usePotion(AbstractPotion potion, AbstractCreature target, int amount) {
        return add(new UsePotionAction(potion, target, amount));
    }

    public WaitAction wait(float duration) {
        return add(new WaitAction(duration));
    }

    public WaitRealtimeAction waitRealtime(float duration) {
        return add(new WaitRealtimeAction(duration));
    }

    public WithdrawAllyAction withdrawAlly(PCLCardAlly ally) {
        return add(new WithdrawAllyAction(ally));
    }

    public WithdrawAllyAction withdrawAlly(Collection<PCLCardAlly> ally) {
        return add(new WithdrawAllyAction(ally));
    }

    public enum ActionOrder {
        TurnStart,
        NextCombat,
        Instant,
        Top,
        Bottom,
        Delayed,
        DelayedTop,
        Last
    }

    public static class ExecuteLast implements OnPhaseChangedSubscriber {
        public final AbstractGameAction action;

        private ExecuteLast(AbstractGameAction action) {
            this.action = action;
        }

        public static void add(AbstractGameAction action) {
            CombatManager.subscribe(OnPhaseChangedSubscriber.class, new ExecuteLast(action));
        }

        @Override
        public void onPhaseChanged(GameActionManager.Phase phase) {
            if (phase == GameActionManager.Phase.WAITING_ON_USER) {
                // This may end up being executed during the middle of Quick Restart clearing and restarting the room
                if (GameUtilities.getCurrentRoom() != null) {
                    PCLActions.bottom.add(action);
                }
                CombatManager.unsubscribe(OnPhaseChangedSubscriber.class, this);
            }
        }
    }
}