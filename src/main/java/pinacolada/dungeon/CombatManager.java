package pinacolada.dungeon;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Calipers;
import com.megacrit.cardcrawl.relics.PenNib;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.EUIBase;
import pinacolada.ui.combat.GridCardSelectScreenHelper;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.HasteAction;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.cardmods.SkillModifier;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.combat.DodgeEffect;
import pinacolada.interfaces.listeners.OnRelicObtainedListener;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.providers.CooldownProvider;
import pinacolada.interfaces.subscribers.*;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.orbs.PCLOrb;
import pinacolada.powers.PCLClickableUse;
import pinacolada.powers.PCLPower;
import pinacolada.powers.TemporaryPower;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.delay.DelayUse;
import pinacolada.ui.combat.ControllableCardPile;
import pinacolada.ui.combat.DrawPileCardPreview;
import pinacolada.ui.combat.PCLPlayerSystem;
import pinacolada.ui.combat.SummonPool;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class CombatManager {
    private static final ArrayList<AbstractCard> cardsDiscardedThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractCard> cardsDiscardedThisTurn = new ArrayList<>();
    private static final ArrayList<AbstractCard> cardsExhaustedThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractCard> cardsExhaustedThisTurn = new ArrayList<>();
    private static final ArrayList<AbstractCard> hasteInfinitesThisTurn = new ArrayList<>();
    private static final ArrayList<AbstractOrb> orbsEvokedThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractOrb> orbsEvokedThisTurn = new ArrayList<>();
    private static final ArrayList<UUID> unplayableCards = new ArrayList<>();
    private static final HashMap<Class<? extends PCLCombatSubscriber>, ConcurrentLinkedQueue<? extends PCLCombatSubscriber>> EVENTS = new HashMap<>();
    private static final HashMap<String, Float> EFFECT_BONUSES = new HashMap<>();
    private static final HashMap<String, Float> PLAYER_EFFECT_BONUSES = new HashMap<>();
    private static final Map<Integer, ArrayList<AbstractCard>> cardsPlayedThisCombat = new HashMap<>();
    private static final Map<String, Integer> limitedData = new HashMap<>();
    private static final Map<String, Integer> semiLimitedData = new HashMap<>();
    private static final Map<String, Object> combatData = new HashMap<>();
    private static final Map<String, Object> turnData = new HashMap<>();
    public static final CardGroup PURGED_CARDS = new CardGroup(PCLEnum.CardGroupType.PURGED_CARDS);
    public static final CardTargetingManager targeting = new CardTargetingManager();
    public static final ControllableCardPile controlPile = new ControllableCardPile();
    public static final PCLPlayerSystem playerSystem = new PCLPlayerSystem();
    public static final SummonPool summons = new SummonPool();
    private static GameActionManager.Phase currentPhase;
    private static HashMap<AbstractCreature, Integer> estimatedDamages;
    private static boolean shouldRefreshHand;
    private static int cardsDrawnThisTurn = 0;
    private static int turnCount = 0;
    public static AbstractRoom room;
    public static UUID battleID;
    public static boolean haveScried;
    public static boolean isPlayerTurn;
    public static int blockRetained;
    public static int dodgeChance;
    public static int energySuspended;
    public static int maxHPSinceLastTurn;

    public static void addBonus(String powerID, float multiplier, boolean forPlayer) {
        multiplier = CombatManager.onGainTriggerablePowerBonus(powerID, multiplier, forPlayer);

        HashMap<String, Float> bonusMap = forPlayer ? PLAYER_EFFECT_BONUSES : EFFECT_BONUSES;
        bonusMap.merge(powerID, multiplier, Float::sum);

        if (GameUtilities.inBattle()) {

            for (AbstractCreature cr : GameUtilities.getAllCharacters(true)) {
                if (cr.powers != null) {
                    for (AbstractPower po : cr.powers) {
                        if (powerID.equals(po.ID)) {
                            po.updateDescription();
                            po.flashWithoutSound();
                        }
                    }
                }
            }

        }

    }

    public static void addEffectBonus(String powerID, float multiplier) {
        addBonus(powerID, multiplier, false);
    }

    public static void addPlayerEffectBonus(String powerID, float multiplier) {
        addBonus(powerID, multiplier, true);
    }

    public static void atEndOfTurn(boolean isPlayer) {

        for (OnEndOfTurnLastSubscriber s : getSubscriberGroup(OnEndOfTurnLastSubscriber.class)) {
            s.onEndOfTurnLast(isPlayer);
        }

        summons.onEndOfTurnLast();
        CombatManager.playerSystem.onEndOfTurn();
        cardsDiscardedThisTurn.clear();
        hasteInfinitesThisTurn.clear();
        turnData.clear();
        cardsExhaustedThisTurn.clear();
        cardsDrawnThisTurn = 0;
        unplayableCards.clear();
        orbsEvokedThisTurn.clear();
        turnCount += 1;
        haveScried = false;

        playerSystem.setLastCardPlayed(null);
    }

    public static void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        isPlayerTurn = false;

        for (OnEndOfTurnFirstSubscriber s : getSubscriberGroup(OnEndOfTurnFirstSubscriber.class)) {
            s.onEndOfTurnFirst(isPlayer);
        }

        summons.onEndOfTurnFirst();
    }

    public static void atPlayerTurnStart() {
        isPlayerTurn = true;
        maxHPSinceLastTurn = GameActionManager.playerHpLastTurn;
        CombatManager.playerSystem.onStartOfTurn();
        dodgeChance = 0;

        subscriberDo(OnStartOfTurnSubscriber.class, OnStartOfTurnSubscriber::onStartOfTurn);

        summons.onStartOfTurn();

        if (blockRetained > 0 && !AbstractDungeon.player.hasPower(BarricadePower.POWER_ID) && !AbstractDungeon.player.hasPower(BlurPower.POWER_ID)) {
            blockRetained = 0;
        }

        if (energySuspended != 0) {
            player.energy.use(energySuspended);
            energySuspended = 0;
        }
    }

    public static void atPlayerTurnStartPostDraw() {
        for (OnStartOfTurnPostDrawSubscriber s : getSubscriberGroup(OnStartOfTurnPostDrawSubscriber.class)) {
            s.onStartOfTurnPostDraw();
        }

        summons.onStartOfTurnPostDraw();

        if (blockRetained > 0) {
            int temp = Math.max(0, AbstractDungeon.player.currentBlock - blockRetained);
            if (temp > 0) {
                if (AbstractDungeon.player.hasRelic(Calipers.ID)) {
                    temp = Math.min(Calipers.BLOCK_LOSS, temp);
                }

                AbstractDungeon.player.loseBlock(temp, true);
            }

            blockRetained = 0;
        }
    }

    public static boolean canActivateLimited(String id) {
        return !hasActivatedLimited(id);
    }

    public static boolean canActivateLimited(String id, int cap) {
        return !hasActivatedLimited(id, cap);
    }

    public static boolean canActivateSemiLimited(String id) {
        return !hasActivatedSemiLimited(id);
    }

    public static boolean canActivateSemiLimited(String id, int cap) {
        return !hasActivatedSemiLimited(id, cap);
    }

    public static boolean canApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower powerToApply, AbstractGameAction action) {
        return target == null || subscriberCanDeny(OnTryApplyPowerSubscriber.class, s -> s.tryApplyPower(powerToApply, target, source, action));
    }

    public static boolean canPlayCard(AbstractCard card, AbstractPlayer p, AbstractMonster m, boolean canPlay) {
        if (GameUtilities.isUnplayableThisTurn(card)) {
            return false;
        }

        for (OnTryUsingCardSubscriber s : getSubscriberGroup(OnTryUsingCardSubscriber.class)) {
            canPlay = s.canUse(card, p, m, canPlay);
        }

        return canPlay;
    }

    public static boolean canReducePower(AbstractCreature source, AbstractCreature target, String powerID, AbstractGameAction action) {
        AbstractPower power = powerID != null ? target.getPower(powerID) : null;
        return power == null || canReducePower(source, target, power, action);
    }

    public static boolean canReducePower(AbstractCreature source, AbstractCreature target, AbstractPower powerToApply, AbstractGameAction action) {
        return target == null || subscriberCanDeny(OnTryReducePowerSubscriber.class, s -> s.tryReducePower(powerToApply, target, source, action));
    }

    public static List<AbstractCard> cardsDiscardedThisCombat() {
        return cardsDiscardedThisCombat;
    }

    public static List<AbstractCard> cardsDiscardedThisTurn() {
        return cardsDiscardedThisTurn;
    }

    public static int cardsDrawnThisTurn() {
        return cardsDrawnThisTurn;
    }

    public static List<AbstractCard> cardsExhaustedThisCombat() {
        return cardsExhaustedThisCombat;
    }

    public static List<AbstractCard> cardsExhaustedThisTurn() {
        return cardsExhaustedThisTurn;
    }

    public static List<AbstractCard> cardsPlayedThisCombat(int turn) {
        return cardsPlayedThisCombat.computeIfAbsent(turn, k -> new ArrayList<>());
    }

    private static <T extends PCLCombatSubscriber> void castAndSubscribe(Class<T> subtype, PCLCombatSubscriber subscriber) {
        subscribe(subtype, (T) subscriber);
    }

    private static <T extends PCLCombatSubscriber> void castAndUnsubscribe(Class<T> subtype, PCLCombatSubscriber subscriber) {
        unsubscribe(subtype, (T) subscriber);
    }

    private static void clearStats() {
        refreshPlayer();
        EUIUtils.logInfoIfDebug(CombatManager.class, "Clearing Stats");
        for (ConcurrentLinkedQueue<?> event : EVENTS.values()) {
            event.clear();
        }

        dodgeChance = 0;
        EFFECT_BONUSES.clear();
        PLAYER_EFFECT_BONUSES.clear();
        PGR.combatScreen.formulaDisplay.initialize();
        DrawPileCardPreview.reset();
        controlPile.clear();
        GridCardSelectScreenHelper.clear(true);
        playerSystem.initialize();
        playerSystem.setLastCardPlayed(null);
        summons.initialize();
        maxHPSinceLastTurn = AbstractDungeon.player == null ? 0 : AbstractDungeon.player.currentHealth;
        blockRetained = 0;
        battleID = null;
        estimatedDamages = null;

        haveScried = false;
        shouldRefreshHand = false;
        turnCount = 0;
        cardsDrawnThisTurn = 0;
        orbsEvokedThisCombat.clear();
        orbsEvokedThisTurn.clear();
        cardsDiscardedThisCombat.clear();
        cardsDiscardedThisTurn.clear();
        cardsPlayedThisCombat.clear();
        cardsExhaustedThisCombat.clear();
        cardsExhaustedThisTurn.clear();
        hasteInfinitesThisTurn.clear();
        unplayableCards.clear();
        currentPhase = null;
        combatData.clear();
        limitedData.clear();
        semiLimitedData.clear();
        turnData.clear();

        PURGED_CARDS.clear();
    }

    public static Set<Map.Entry<String, Float>> getAllEffectBonuses() {
        return EFFECT_BONUSES.entrySet();
    }

    public static Set<Map.Entry<String, Float>> getAllPlayerEffectBonuses() {
        return PLAYER_EFFECT_BONUSES.entrySet();
    }

    public static <T> T getCombatData(String key, T defaultData) {
        if (combatData.containsKey(key)) {
            return (T) combatData.get(key);
        }
        else if (defaultData != null) {
            return setCombatData(key, defaultData);
        }

        return defaultData;
    }

    public static float getBonus(String powerID, boolean forPlayer) {
        return forPlayer ? getPlayerEffectBonus(powerID) : getEffectBonus(powerID);
    }

    public static float getEffectBonus(String powerID) {
        return EFFECT_BONUSES.getOrDefault(powerID, 0f);
    }

    public static float getEffectBonusForPower(AbstractPower po) {
        return (GameUtilities.isPlayer(po.owner)) ? (CombatManager.getPlayerEffectBonus(po.ID)) : (CombatManager.getEffectBonus(po.ID));
    }

    private static List<Class<? extends PCLCombatSubscriber>> getInterfaces(PCLCombatSubscriber subscriber) {
        return EUIUtils.mapAsNonnull(subscriber.getClass().getInterfaces(), i -> PCLCombatSubscriber.class.isAssignableFrom(i) ? (Class<? extends PCLCombatSubscriber>) i : null);
    }

    public static float getPlayerEffectBonus(String powerID) {
        return PLAYER_EFFECT_BONUSES.getOrDefault(powerID, 0f);
    }

    private static <T extends PCLCombatSubscriber> ConcurrentLinkedQueue<T> getSubscriberGroup(Class<T> subscriberClass) {
        return (ConcurrentLinkedQueue<T>) EVENTS.get(subscriberClass);
    }

    public static <T> T getTurnData(String key, T defaultData) {
        if (turnData.containsKey(key)) {
            return (T) turnData.get(key);
        }
        else if (defaultData != null) {
            return setCombatData(key, defaultData);
        }

        return defaultData;
    }

    public static boolean hasActivatedLimited(String id) {
        return limitedData.containsKey(id);
    }

    public static boolean hasActivatedLimited(String id, int cap) {
        return limitedData.containsKey(id) && limitedData.get(id) >= cap;
    }

    public static boolean hasActivatedSemiLimited(String id) {
        return semiLimitedData.containsKey(id);
    }

    public static boolean hasActivatedSemiLimited(String id, int cap) {
        return semiLimitedData.containsKey(id) && semiLimitedData.get(id) >= cap;
    }

    public static boolean hasEnoughEnergyForCard(AbstractCard card) {
        boolean canPass = PCLCardTag.Suspensive.has(card) && card.costForTurn + energySuspended - player.energy.energy <= player.energy.energyMaster;
        return subscriberInout(OnTrySpendEnergySubscriber.class, canPass, (s, d) -> s.canSpendEnergy(card, d));
    }

    public static List<AbstractCard> hasteInfinitesThisTurn() {
        return hasteInfinitesThisTurn;
    }

    public static void initializeEvents() {
        for (Class<?> eventClass : GameUtilities.getClassesWithAnnotation(CombatSubscriber.class)) {
            try {
                registerSubscribeGroup((Class<? extends PCLCombatSubscriber>) eventClass);
            }
            catch (Exception e) {
                EUIUtils.logError(CombatManager.class, "Failed to load subscriber class " + eventClass);
            }
        }
    }

    public static void onAfterCardPlayed(AbstractCard card) {
        subscriberDo(OnCardPlayedSubscriber.class, s -> s.onCardPlayed(card));

        cardsPlayedThisCombat(turnCount).add(card);

        if (AbstractDungeon.player.limbo.contains(card)) {
            PCLActions.top.add(new UnlimboAction(card));
        }
    }

    public static void onAfterDeath() {
        subscriberDo(OnPlayerDeathSubscriber.class, OnPlayerDeathSubscriber::onAfterDeath);

        clearStats();
    }

    public static void onAfterDraw(AbstractCard card) {
        cardsDrawnThisTurn += 1;
        subscriberDo(OnCardDrawnSubscriber.class, s -> s.onCardDrawn(card));

        if (PCLCardTag.Haste.has(card)) {
            PCLActions.top.add(new HasteAction(card));
        }
    }

    public static void onAllyDeath(PCLCard card, PCLCardAlly ally) {
        card.triggerWhenKilled(ally);
        subscriberDo(OnAllyDeathSubscriber.class, s -> s.onAllyDeath(card, ally));
    }

    public static void onAllySummon(PCLCard card, PCLCardAlly ally) {
        card.triggerWhenSummoned(ally);
        subscriberDo(OnAllySummonSubscriber.class, s -> s.onAllySummon(card, ally));
    }

    public static void onAllyTrigger(PCLCard card, PCLCardAlly ally) {
        card.triggerWhenTriggered(ally);
        subscriberDo(OnAllyTriggerSubscriber.class, s -> s.onAllyTrigger(card, ally));
    }

    public static void onAllyWithdraw(PCLCard card, PCLCardAlly ally) {
        if (card != null) {
            card.triggerWhenWithdrawn(ally);
        }
        subscriberDo(OnAllyWithdrawSubscriber.class, s -> s.onAllyWithdraw(card, ally));
    }

    public static void onApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power) {
        subscriberDo(OnApplyPowerSubscriber.class, s -> s.onApplyPower(power, target, source));
    }

    public static void onAttack(DamageInfo info, int damageAmount, AbstractCreature receiver) {
        subscriberDo(OnAttackSubscriber.class, s -> s.onAttack(info, damageAmount, receiver));
    }

    public static void onBattleEnd() {
        subscriberDo(OnBattleEndSubscriber.class, OnBattleEndSubscriber::onBattleEnd);

        summons.onBattleEnd();

        clearStats();
    }

    public static void onBattleStart() {
        refresh();

        subscriberDo(OnBattleStartSubscriber.class, OnBattleStartSubscriber::onBattleStart);

        final ArrayList<AbstractCard> cards = new ArrayList<>(AbstractDungeon.player.drawPile.group);
        cards.addAll(AbstractDungeon.player.hand.group);
        cards.addAll(AbstractDungeon.player.discardPile.group);
        cards.addAll(AbstractDungeon.player.exhaustPile.group);

        for (AbstractCard c : cards) {
            onCardCreated(c, true);
        }
    }

    public static void onBlockBroken(AbstractCreature creature) {
        subscriberDo(OnBlockBrokenSubscriber.class, s -> s.onBlockBroken(creature));
    }

    public static int onBlockGained(AbstractCreature creature, int block) {
        return subscriberInout(OnBlockGainedSubscriber.class, block, (s, b) -> s.onBlockGained(creature, b));
    }

    public static void onCardCreated(AbstractCard card, boolean startOfBattle) {
        final EditorCard c = EUIUtils.safeCast(card, EditorCard.class);
        if (c != null) {
            c.triggerWhenCreated(startOfBattle);
        }

        playerSystem.onCardCreated(card, startOfBattle);
        subscriberDo(OnCardCreatedSubscriber.class, s -> s.onCardCreated(card, startOfBattle));
    }

    public static void onCardDiscarded(AbstractCard card) {
        cardsDiscardedThisCombat.add(card);
        cardsDiscardedThisTurn.add(card);

        for (SkillModifier wrapper : SkillModifier.getAll(card)) {
            wrapper.onDiscard(card);
        }
        for (OnCardDiscardedSubscriber s : getSubscriberGroup(OnCardDiscardedSubscriber.class)) {
            s.onCardDiscarded(card);
        }
    }

    public static void onCardMoved(AbstractCard card, CardGroup source, CardGroup destination) {
        PCLActions.last.callback(() -> {
            controlPile.refreshCards();
            DrawPileCardPreview.refreshAll();
        });
    }

    public static void onCardPurged(AbstractCard card) {
        if (!PURGED_CARDS.contains(card)) {
            PURGED_CARDS.group.add(card);
        }

        if (card instanceof EditorCard) {
            ((EditorCard) card).triggerOnPurge();
        }

        for (SkillModifier wrapper : SkillModifier.getAll(card)) {
            wrapper.onPurged(card);
        }

        for (OnCardPurgedSubscriber s : getSubscriberGroup(OnCardPurgedSubscriber.class)) {
            s.onPurge(card);
        }
    }

    public static void onCardReset(AbstractCard card) {
        subscriberDo(OnCardResetSubscriber.class, s -> s.onCardReset(card));
    }

    public static void onCardReshuffled(AbstractCard card, CardGroup sourcePile) {
        if (card instanceof EditorCard) {
            ((EditorCard) card).triggerOnReshuffle(sourcePile);
        }

        for (SkillModifier wrapper : SkillModifier.getAll(card)) {
            wrapper.onReshuffled(card, sourcePile);
        }

        subscriberDo(OnCardReshuffledSubscriber.class, s -> s.onCardReshuffled(card, sourcePile));
    }

    public static void onCardRetain(AbstractCard card) {
        subscriberDo(OnCardRetainSubscriber.class, s -> s.onRetain(card));
    }

    public static void onCardScry(AbstractCard card) {
        if (card instanceof PCLCard) {
            ((PCLCard) card).triggerOnScryThatDoesntLoopOnEnd();
        }
        subscriberDo(OnCardScrySubscriber.class, s -> s.onScry(card));
    }

    public static void onCardUpgrade(AbstractCard card) {
        if (card instanceof PCLCard) {
            ((PCLCard) card).triggerOnUpgrade();
        }

        for (SkillModifier wrapper : SkillModifier.getAll(card)) {
            wrapper.onUpgraded(card);
        }

        subscriberDo(OnCardUpgradeSubscriber.class, s -> s.onUpgrade(card));
    }

    public static void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
        subscriberDo(OnStanceChangedSubscriber.class, s -> s.onStanceChanged(oldStance, newStance));
    }

    public static void onChannel(AbstractOrb orb) {
        if (orb != null && !(orb instanceof EmptyOrbSlot)) {
            if (orb instanceof PCLOrb) {
                ((PCLOrb) orb).onChannel();
            }

            subscriberDo(OnOrbChannelSubscriber.class, s -> s.onChannelOrb(orb));
        }
    }

    public static boolean onClickableUsed(PCLClickableUse condition, AbstractMonster target, int uses) {
        return subscriberCanDeny(OnClickableUsedSubscriber.class, s -> s.onClickablePowerUsed(condition, target, uses));
    }

    public static boolean onCooldownTriggered(AbstractCreature source, AbstractCreature m, CooldownProvider cooldown) {
        return subscriberCanDeny(OnCooldownTriggeredSubscriber.class, s -> s.onCooldownTriggered(cooldown, source, m));
    }

    public static void onDamageAction(AbstractGameAction action, AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect) {
        subscriberDo(OnDamageActionSubscriber.class, s -> s.onDamageAction(action, target, info, effect));
    }

    public static float onDamageOverride(AbstractCreature target, DamageInfo.DamageType type, float damage, AbstractCard card) {
        return subscriberInout(OnDamageOverrideSubscriber.class, damage, (s, d) -> s.onDamageOverride(target, type, d, card));
    }

    public static void onDeath() {
        clearStats();
    }

    public static int onEnergyRecharge(int previousEnergy, int currentEnergy) {
        return subscriberInout(OnEnergyRechargeSubscriber.class, currentEnergy, (s, d) -> s.onEnergyRecharge(previousEnergy, currentEnergy));
    }

    public static void onEvokeOrb(AbstractOrb orb) {
        if (orb != null && !(orb instanceof EmptyOrbSlot)) {
            subscriberDo(OnOrbEvokeSubscriber.class, s -> s.onEvokeOrb(orb));
            orbsEvokedThisCombat.add(orb);
            orbsEvokedThisTurn.add(orb);
        }
    }

    public static void onExhaust(AbstractCard card) {
        card.clearPowers();

        subscriberDo(OnCardExhaustedSubscriber.class, s -> s.onCardExhausted(card));

        cardsExhaustedThisCombat.add(card);
        cardsExhaustedThisTurn.add(card);
    }

    public static int onGainTempHP(int amount) {
        return subscriberInout(OnGainTempHPSubscriber.class, amount, OnGainTempHPSubscriber::onGainTempHP);
    }

    public static float onGainTriggerablePowerBonus(String powerID, float amount, boolean forPlayer) {
        return subscriberInout(OnGainPowerBonusSubscriber.class, amount, (s, d) -> s.onGainPowerBonus(powerID, d, forPlayer));
    }

    public static void onGameStart() {
        clearStats();

        PGR.dungeon.reset();
    }

    public static void onHealthBarUpdated(AbstractCreature creature) {
        if (creature == AbstractDungeon.player && creature.currentHealth > maxHPSinceLastTurn) {
            maxHPSinceLastTurn = creature.currentHealth;
        }

        subscriberDo(OnHealthBarUpdatedSubscriber.class, s -> s.onHealthBarUpdated(creature));
        refreshHandLayout();
    }

    public static int onIncomingDamageFirst(AbstractCreature target, DamageInfo info, int damage) {
        return subscriberInout(OnReceiveDamageFirstSubscriber.class, damage, (s, d) -> s.onReceiveDamageFirst(target, info, d));
    }

    public static int onIncomingDamageLast(AbstractCreature target, DamageInfo info, int damage) {
        damage = subscriberInout(OnReceiveDamageLastSubscriber.class, damage, (s, d) -> s.onReceiveDamageLast(target, info, d));

        if (target == AbstractDungeon.player && info.type == DamageInfo.DamageType.NORMAL && GameUtilities.chance(dodgeChance)) {
            AbstractDungeon.player.tint.color.a = 0;
            PCLActions.bottom.playSFX(PCLSFX.NULLIFY_SFX, 1.6f, 1.6f);
            PCLActions.top.wait(0.15f);
            PCLEffects.Queue.add(new DodgeEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, PGR.core.strings.combat_dodged));
            return 0;
        }

        return damage;
    }

    public static void onIncreaseAffinityLevel(PCLAffinity affinity) {
        subscriberDo(OnIntensifySubscriber.class, s -> s.onIntensify(affinity));
    }

    public static float onModifyBlockFirst(float amount, AbstractCard card) {
        return subscriberInout(OnModifyBlockFirstSubscriber.class, amount, (s, d) -> s.onModifyBlockFirst(d, card));
    }

    public static float onModifyBlockLast(float amount, AbstractCard card) {
        return subscriberInout(OnModifyBlockLastSubscriber.class, amount, (s, d) -> s.onModifyBlockLast(d, card));
    }

    public static float onModifyDamageGiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        return subscriberInout(OnModifyDamageGiveFirstSubscriber.class, amount, (s, d) -> s.onModifyDamageGiveFirst(d, type, source, target, card));
    }

    public static float onModifyDamageGiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        return subscriberInout(OnModifyDamageGiveLastSubscriber.class, amount, (s, d) -> s.onModifyDamageGiveLast(d, type, source, target, card));
    }

    public static float onModifyDamageReceiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        return subscriberInout(OnModifyDamageReceiveFirstSubscriber.class, amount, (s, d) -> s.onModifyDamageReceiveFirst(d, type, source, target, card));
    }

    public static float onModifyDamageReceiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        return subscriberInout(OnModifyDamageReceiveLastSubscriber.class, amount, (s, d) -> s.onModifyDamageReceiveLast(d, type, source, target, card));
    }

    public static float onModifySkillBonus(float amount, AbstractCard card) {
        return subscriberInout(OnModifySkillBonusSubscriber.class, amount, (s, d) -> s.onModifySkillBonus(d, card));
    }

    public static void onMonsterDeath(AbstractMonster monster, boolean triggerRelics) {
        subscriberDo(OnMonsterDeathSubscriber.class, s -> s.onMonsterDeath(monster, triggerRelics));
    }

    public static boolean onMonsterMove(AbstractMonster target) {
        return subscriberCanDeny(OnMonsterMoveSubscriber.class, s -> s.onMonsterMove(target));
    }

    public static void onOrbApplyFocus(AbstractOrb orb) {
        subscriberDo(OnOrbApplyFocusSubscriber.class, s -> s.onApplyFocus(orb));
    }

    public static float onOrbApplyLockOn(AbstractCreature target, float dmg) {
        return subscriberInout(OnOrbApplyLockOnSubscriber.class, dmg, (s, d) -> s.onOrbApplyLockOn(target, d));
    }

    public static void onOrbPassiveEffect(AbstractOrb orb) {
        subscriberDo(OnOrbPassiveEffectSubscriber.class, s -> s.onOrbPassiveEffect(orb));
    }

    public static void onPlayCardPostActions(AbstractCard card, AbstractMonster m) {
        CombatManager.playerSystem.setLastCardPlayed(card);
        if (PCLCardTag.Recast.has(card)) {
            PCLCardTag.Recast.tryProgress(card);
            DelayUse.turnStartLast(1, playerSystem.generateInfo(card, AbstractDungeon.player, m), (i) -> PCLActions.bottom.playCopy(card, EUIUtils.safeCast(i.target, AbstractMonster.class))).start();
        }
    }

    public static int onPlayerLoseHP(AbstractPlayer p, DamageInfo info, int damageAmount) {
        damageAmount = subscriberInout(OnLoseHPSubscriber.class, damageAmount, (s, d) -> s.onLoseHP(p, info, d));
        if (damageAmount > 0 && info.type != DamageInfo.DamageType.HP_LOSS) {
            damageAmount = summons.tryDamage(info, damageAmount);
        }
        return damageAmount;
    }

    public static void onRelicObtained(AbstractRelic relic) {
        refreshPlayer();

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c instanceof OnRelicObtainedListener) {
                ((OnRelicObtainedListener) c).onRelicObtained(relic);
            }
        }

        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof OnRelicObtainedListener) {
                ((OnRelicObtainedListener) r).onRelicObtained(relic);
            }
        }
    }

    public static void onScryAction(AbstractGameAction action) {
        haveScried = true;
        subscriberDo(OnScryActionSubscriber.class, s -> s.onScryAction(action));
    }

    public static void onShuffle(boolean triggerRelics) {
        subscriberDo(OnShuffleSubscriber.class, s -> s.onShuffle(triggerRelics));
    }

    public static boolean onSpecificPowerActivated(AbstractPower power, AbstractCreature source, boolean originalValue) {
        return subscriberInout(OnSpecificPowerActivatedSubscriber.class, originalValue, (s, d) -> s.onPowerActivated(power, source, d));
    }

    public static void onStartOver() {
        clearStats();

        PGR.dungeon.reset();
    }

    public static void onStartup() {
        refresh();
        clearStats();
    }

    public static void onTagChanged(AbstractCard card, PCLCardTag tag, int value) {
        subscriberDo(OnTagChangedSubscriber.class, s -> s.onTagChanged(card, tag, value));
    }

    public static AbstractCardModifier onTryAddModifier(AbstractCard card, AbstractCardModifier abstractCardModifier) {
        return playerSystem.onCardModified(card, abstractCardModifier);
    }

    public static int onTrySpendEnergy(AbstractCard card, AbstractPlayer p, int cost) {
        // Hardcoded base game logic
        if (p.hasPower(CorruptionPower.POWER_ID) && card.type == AbstractCard.CardType.SKILL) {
            cost = 0;
        }

        // Suspensive
        if (cost > p.energy.energy) {
            energySuspended += cost - p.energy.energy;
        }

        return subscriberInout(OnTrySpendEnergySubscriber.class, cost, (s, d) -> s.onTrySpendEnergy(card, d));
    }

    public static int onTryUseXCost(int original, AbstractCard card) {
        return subscriberInout(OnTryUseXCostSubscriber.class, original, (s, d) -> s.onTryUseXCost(d, card));
    }

    public static boolean onUsingCard(AbstractCard card, AbstractPlayer p, AbstractMonster m) {
        if (card == null) {
            throw new RuntimeException("Card played is null");
        }

        PCLCard pclCard = EUIUtils.safeCast(card, PCLCard.class);
        if (pclCard != null) {
            pclCard.unfadeOut();
            pclCard.lighten(true);

            AbstractCreature target = CustomTargeting.getCardTarget(pclCard);
            pclCard.calculateCardDamage(EUIUtils.safeCast(target, AbstractMonster.class));
            final PCLUseInfo info = playerSystem.generateInfo(pclCard, p, target);

            if (pclCard.type == PCLEnum.CardType.SUMMON) {
                summons.summon(pclCard, EUIUtils.safeCast(info.target, PCLCardAlly.class));
            }
            else {
                pclCard.onUse(info);
            }

            playerSystem.onCardPlayed(pclCard, info, false);
            return true;
        }
        else {
            return false;
        }
    }

    public static void onVictory() {
        PGR.dungeon.updateHighestScore(playerSystem.getActiveMeter().getHighestScore());
        clearStats();
    }

    public static List<AbstractOrb> orbsEvokedThisCombat() {
        return orbsEvokedThisCombat;
    }

    public static List<AbstractOrb> orbsEvokedThisTurn() {
        return orbsEvokedThisTurn;
    }

    public static void queueRefreshHandLayout() {
        shouldRefreshHand = true;
    }

    public static void refresh() {
        refreshPlayer();

        room = GameUtilities.getCurrentRoom();

        if (room == null || AbstractDungeon.player == null) {
            battleID = null;
        }
        else if (room.isBattleOver || AbstractDungeon.player.isDead) {
            if (room.phase != AbstractRoom.RoomPhase.COMBAT || room.monsters == null || room.monsters.areMonstersBasicallyDead()) {
                battleID = null;
            }
        }
        else if (battleID == null && room.phase == AbstractRoom.RoomPhase.COMBAT) {
            battleID = UUID.randomUUID();
        }

        PGR.combatScreen.initialize();
    }

    public static void refreshHandLayout() {
        if (GameUtilities.getCurrentRoom() != null) {
            player.hand.refreshHandLayout();
            player.hand.applyPowers();
            player.hand.glowCheck();
        }
    }

    public static AbstractPlayer refreshPlayer() {
        PCLCard.rng = PCLPower.rng = PCLRelic.rng = AbstractDungeon.cardRandomRng;
        return PCLCard.player = PCLPower.player = PCLRelic.player = AbstractDungeon.player;
    }

    public static <T extends PCLCombatSubscriber> ConcurrentLinkedQueue<T> registerSubscribeGroup(Class<T> eventClass) {
        ConcurrentLinkedQueue<T> event = new ConcurrentLinkedQueue<>();
        EVENTS.put(eventClass, event);
        return event;
    }

    public static void removeDamagePowers(AbstractCreature creature) {
        // Remove temp vigor first to avoid it glitching out the existing vigor
        TemporaryPower tmpVigor = TemporaryPower.getFromCreature(creature, VigorPower.POWER_ID);
        if (tmpVigor != null) {
            PCLActions.bottom.removePower(creature, tmpVigor);
        }

        for (AbstractPower po : creature.powers) {
            if (VigorPower.POWER_ID.equals(po.ID)) {
                PCLActions.bottom.removePower(creature, po);
            }
            else if (PenNibPower.POWER_ID.equals(po.ID)) {
                PCLActions.bottom.applyPower(creature, creature, po, -1);
                if (creature == player) {
                    final AbstractRelic relic = player.getRelic(PenNib.ID);
                    if (relic != null) {
                        relic.counter = 0;
                        relic.flash();
                        relic.stopPulse();
                    }
                }
            }
            else if (po instanceof PCLPower) {
                ((PCLPower) po).onRemoveDamagePowers();
            }
        }
    }

    public static void render(SpriteBatch sb) {
        summons.render(sb);
        controlPile.render(sb);
        playerSystem.render(sb);
        if (PGR.config.showEstimatedDamage.get() && estimatedDamages != null) {
            FontHelper.damageNumberFont.getData().setScale(0.5f);
            for (AbstractCreature c : estimatedDamages.keySet()) {
                int damage = estimatedDamages.get(c);
                float startX = c.hb.cX + c.healthHb.width / 1.6f + EUIBase.scale(17);
                float startY = c.hb.y - EUIBase.scale(23);
                Texture texture = damage >= c.currentHealth ? PCLCoreImages.Core.dead.texture() : PCLCoreImages.CardIcons.hp.texture();
                PCLRenderHelpers.drawCentered(sb, Color.WHITE, texture, startX, startY, texture.getWidth(), texture.getHeight(), 0.55f, 0f);
                FontHelper.renderFontLeftTopAligned(sb, FontHelper.damageNumberFont, String.valueOf(-1 * damage), startX + EUIBase.scale(22), startY + EUIBase.scale(10), Color.SALMON);
            }
            PCLRenderHelpers.resetFont(FontHelper.damageNumberFont);
        }
    }

    public static <T> T setCombatData(String key, T data) {
        combatData.put(key, data);
        return data;
    }

    public static <T> T setTurnData(String key, T data) {
        turnData.put(key, data);
        return data;
    }

    public static void subscribe(PCLCombatSubscriber subscriber) {
        for (Class<? extends PCLCombatSubscriber> c : getInterfaces(subscriber)) {
            castAndSubscribe(c, subscriber);
        }
    }

    public static <T extends PCLCombatSubscriber> void subscribe(Class<T> subtype, T subscriber) {
        getSubscriberGroup(subtype).add(subscriber);
    }

    public static <T extends PCLCombatSubscriber> boolean subscriberCanDeny(Class<T> subscriberClass, FuncT1<Boolean, T> doFor) {
        boolean passes = true;
        for (T subscriber : getSubscriberGroup(subscriberClass)) {
            passes = passes & doFor.invoke(subscriber);
        }
        return passes;
    }

    public static <T extends PCLCombatSubscriber> boolean subscriberCanPass(Class<T> subscriberClass, FuncT1<Boolean, T> doFor) {
        boolean passes = false;
        for (T subscriber : getSubscriberGroup(subscriberClass)) {
            passes = passes | doFor.invoke(subscriber);
        }
        return passes;
    }

    public static <T extends PCLCombatSubscriber> void subscriberDo(Class<T> subscriberClass, ActionT1<T> doFor) {
        for (T subscriber : getSubscriberGroup(subscriberClass)) {
            doFor.invoke(subscriber);
        }
    }

    public static <T extends PCLCombatSubscriber, U> U subscriberInout(Class<T> subscriberClass, U inout, FuncT2<U, T, U> doFor) {
        for (T subscriber : getSubscriberGroup(subscriberClass)) {
            inout = doFor.invoke(subscriber, inout);
        }
        return inout;
    }

    public static <T extends PCLCombatSubscriber> int subscriberSum(Class<T> subscriberClass, FuncT1<Integer, T> doFor) {
        int sum = 0;
        for (T subscriber : getSubscriberGroup(subscriberClass)) {
            sum = sum + doFor.invoke(subscriber);
        }
        return sum;
    }

    public static boolean tryActivateLimited(String id) {
        return limitedData.put(id, 1) == null;
    }

    public static boolean tryActivateLimited(String id, int cap) {
        return limitedData.merge(id, 1, Integer::sum) <= cap;
    }

    public static boolean tryActivateSemiLimited(String id) {
        return semiLimitedData.put(id, 1) == null;
    }

    public static boolean tryActivateSemiLimited(String id, int cap) {
        return semiLimitedData.merge(id, 1, Integer::sum) <= cap;
    }

    public static int turnCount(boolean fromZero) {
        return fromZero ? turnCount : (turnCount + 1);
    }

    public static List<UUID> unplayableCards() {
        return unplayableCards;
    }

    public static void unsubscribe(PCLCombatSubscriber subscriber) {
        for (Class<? extends PCLCombatSubscriber> c : getInterfaces(subscriber)) {
            castAndUnsubscribe(c, subscriber);
        }
    }

    public static <T extends PCLCombatSubscriber> void unsubscribe(Class<T> subtype, T subscriber) {
        getSubscriberGroup(subtype).remove(subscriber);
    }

    public static void update(PCLCard hoveredCard, PCLCard originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard) {
        summons.update();
        controlPile.update();
        playerSystem.update(hoveredCard, originalCard, target, originalTarget, draggingCard);
        if (currentPhase != AbstractDungeon.actionManager.phase) {
            currentPhase = AbstractDungeon.actionManager.phase;
            subscriberDo(OnPhaseChangedSubscriber.class, s -> s.onPhaseChanged(currentPhase));
            controlPile.refreshCards();
            summons.applyPowers();
            if (PGR.config.showEstimatedDamage.get()) {
                updateEstimatedDamage();
            }
            if (shouldRefreshHand) {
                shouldRefreshHand = false;
                refreshHandLayout();
            }
        }
    }

    public static void updateEstimatedDamage() {
        ArrayList<PCLIntentInfo> intents = GameUtilities.getIntents();

        int bufferCount = GameUtilities.getBlockedHits(player);
        int expectedDamage = 0;

        for (PCLIntentInfo intent : intents) {
            if (intent.isIntentVisible()) {
                int hits = intent.getDamageMulti();
                while (bufferCount > 0 && hits > 0) {
                    hits -= 1;
                    bufferCount -= 1;
                }
                expectedDamage += intent.getDamage(false) * hits;
            }
        }

        expectedDamage = GameUtilities.getHealthBarAmount(player, expectedDamage, true, true);

        estimatedDamages = summons.estimateDamage(expectedDamage);
    }
}
