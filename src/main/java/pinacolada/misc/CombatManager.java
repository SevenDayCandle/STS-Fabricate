package pinacolada.misc;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.EUIUtils;
import extendedui.patches.game.CardGlowBorderPatches;
import extendedui.ui.GridCardSelectScreenHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.PCLHasteAction;
import pinacolada.cards.base.AffinityReactions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.cards.base.modifiers.SkillModifier;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.combat.DodgeEffect;
import pinacolada.interfaces.listeners.OnCardResetListener;
import pinacolada.interfaces.markers.CooldownProvider;
import pinacolada.interfaces.subscribers.*;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.orbs.PCLOrb;
import pinacolada.powers.PCLPower;
import pinacolada.powers.common.ImpairedPower;
import pinacolada.powers.common.PCLLockOnPower;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.skills.DelayUse;
import pinacolada.ui.combat.PCLPlayerSystem;
import pinacolada.ui.combat.SummonPool;
import pinacolada.ui.common.ControllableCardPile;
import pinacolada.utilities.GameUtilities;

import java.util.*;

public class CombatManager
{
    private static final ArrayList<AbstractCard> cardsDiscardedThisCombat= new ArrayList<>();
    private static final ArrayList<AbstractCard> cardsDiscardedThisTurn = new ArrayList<>();
    private static final ArrayList<AbstractCard> hasteInfinitesThisTurn = new ArrayList<>();
    private static final ArrayList<AbstractCard> matchesThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractCard> matchesThisTurn = new ArrayList<>();
    private static final Map<String, Object> combatData = new HashMap<>();
    private static final Map<String, Object> turnData = new HashMap<>();
    private static final ArrayList<AbstractOrb> orbsEvokedThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractOrb> orbsEvokedThisTurn = new ArrayList<>();
    private static final Map<Integer, ArrayList<AbstractCard>> cardsPlayedThisCombat = new HashMap<>();
    private static final ArrayList<AbstractCard> cardsExhaustedThisCombat = new ArrayList<>();
    private static final ArrayList<AbstractCard> cardsExhaustedThisTurn = new ArrayList<>();
    private static final ArrayList<UUID> unplayableCards = new ArrayList<>();
    private static final ArrayList<AbstractGameAction> cachedActions = new ArrayList<>();
    private static GameActionManager.Phase currentPhase;
    private static int cardsDrawnThisTurn = 0;
    private static int turnCount = 0;
    protected static final HashMap<String, Float> AMPLIFIER_BONUSES = new HashMap<>();
    protected static final HashMap<String, Float> EFFECT_BONUSES = new HashMap<>();
    protected static final HashMap<String, Float> PASSIVE_DAMAGE_BONUSES = new HashMap<>();
    protected static final HashMap<String, Float> PLAYER_EFFECT_BONUSES = new HashMap<>();
    protected static final ArrayList<GameEvent<?>> EVENTS = new ArrayList<>();
    public static final ControllableCardPile controlPile = new ControllableCardPile();
    public static final CardGroup PURGED_CARDS = new CardGroup(PGR.Enums.CardGroupType.PURGED_CARDS);
    public static int blockRetained;
    public static int maxHPSinceLastTurn;
    public static boolean isPlayerTurn;
    public static AbstractRoom room;
    public static UUID battleID;
    public static final GameEvent<OnAfterCardDrawnSubscriber> onAfterCardDrawn = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAfterCardExhaustedSubscriber> onAfterCardExhausted = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAfterCardPlayedSubscriber> onAfterCardPlayed = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAfterDeathSubscriber> onAfterDeath = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAfterlifeSubscriber> onAfterlife = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAllySummonSubscriber> onAllySummon = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAllyTriggerSubscriber> onAllyTrigger = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAllyWithdrawSubscriber> onAllyWithdraw = registerEvent(new GameEvent<>());
    public static final GameEvent<OnApplyPowerSubscriber> onApplyPower = registerEvent(new GameEvent<>());
    public static final GameEvent<OnAttackSubscriber> onAttack = registerEvent(new GameEvent<>());
    public static final GameEvent<OnBattleEndSubscriber> onBattleEnd = registerEvent(new GameEvent<>());
    public static final GameEvent<OnBattleStartSubscriber> onBattleStart = registerEvent(new GameEvent<>());
    public static final GameEvent<OnBeforeLoseBlockSubscriber> onBeforeLoseBlock = registerEvent(new GameEvent<>());
    public static final GameEvent<OnBlockBrokenSubscriber> onBlockBroken = registerEvent(new GameEvent<>());
    public static final GameEvent<OnBlockGainedSubscriber> onBlockGained = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardCreatedSubscriber> onCardCreated = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardDiscardedSubscriber> onCardDiscarded = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardMovedSubscriber> onCardMoved = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardPurgedSubscriber> onCardPurged = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardResetSubscriber> onCardReset = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCardReshuffledSubscriber> onCardReshuffled = registerEvent(new GameEvent<>());
    public static final GameEvent<OnChannelOrbSubscriber> onChannelOrb = registerEvent(new GameEvent<>());
    public static final GameEvent<OnCooldownTriggeredSubscriber> onCooldownTriggered = registerEvent(new GameEvent<>());
    public static final GameEvent<OnDamageActionSubscriber> onDamageAction = registerEvent(new GameEvent<>());
    public static final GameEvent<OnDamageOverrideSubscriber> onDamageOverride = registerEvent(new GameEvent<>());
    public static final GameEvent<OnElementReactSubscriber> onElementReact = registerEvent(new GameEvent<>());
    public static final GameEvent<OnEndOfTurnFirstSubscriber> onEndOfTurnFirst = registerEvent(new GameEvent<>());
    public static final GameEvent<OnEndOfTurnLastSubscriber> onEndOfTurnLast = registerEvent(new GameEvent<>());
    public static final GameEvent<OnEnergyRechargeSubscriber> onEnergyRecharge = registerEvent(new GameEvent<>());
    public static final GameEvent<OnEvokeOrbSubscriber> onEvokeOrb = registerEvent(new GameEvent<>());
    public static final GameEvent<OnGainPowerBonusSubscriber> onGainTriggerablePowerBonus = registerEvent(new GameEvent<>());
    public static final GameEvent<OnGainTempHPSubscriber> onGainTempHP = registerEvent(new GameEvent<>());
    public static final GameEvent<OnHealthBarUpdatedSubscriber> onHealthBarUpdated = registerEvent(new GameEvent<>());
    public static final GameEvent<OnIntensifySubscriber> onIntensify = registerEvent(new GameEvent<>());
    public static final GameEvent<OnLoseHPSubscriber> onLoseHP = registerEvent(new GameEvent<>());
    public static final GameEvent<OnMatchBonusSubscriber> onMatchBonus = registerEvent(new GameEvent<>());
    public static final GameEvent<OnMatchCheckSubscriber> onMatchCheck = registerEvent(new GameEvent<>());
    public static final GameEvent<OnMatchSubscriber> onMatch = registerEvent(new GameEvent<>());
    public static final GameEvent<OnModifyDamageFirstSubscriber> onModifyDamageFirst = registerEvent(new GameEvent<>());
    public static final GameEvent<OnModifyDamageLastSubscriber> onModifyDamageLast = registerEvent(new GameEvent<>());
    public static final GameEvent<OnModifyDebuffSubscriber> onModifyDebuff = registerEvent(new GameEvent<>());
    public static final GameEvent<OnModifyMagicNumberSubscriber> onModifyMagicNumber = registerEvent(new GameEvent<>());
    public static final GameEvent<OnMonsterDeathSubscriber> onMonsterDeath = registerEvent(new GameEvent<>());
    public static final GameEvent<OnMonsterMoveSubscriber> onMonsterMove = registerEvent(new GameEvent<>());
    public static final GameEvent<OnNotMatchSubscriber> onNotMatch = registerEvent(new GameEvent<>());
    public static final GameEvent<OnOrbApplyFocusSubscriber> onOrbApplyFocus = registerEvent(new GameEvent<>());
    public static final GameEvent<OnOrbApplyLockOnSubscriber> onOrbApplyLockOn = registerEvent(new GameEvent<>());
    public static final GameEvent<OnOrbPassiveEffectSubscriber> onOrbPassiveEffect = registerEvent(new GameEvent<>());
    public static final GameEvent<OnPCLClickablePowerUsed> onPCLClickablePowerUsed = registerEvent(new GameEvent<>());
    public static final GameEvent<OnPhaseChangedSubscriber> onPhaseChanged = registerEvent(new GameEvent<>());
    public static final GameEvent<OnShuffleSubscriber> onShuffle = registerEvent(new GameEvent<>());
    public static final GameEvent<OnStanceChangedSubscriber> onStanceChanged = registerEvent(new GameEvent<>());
    public static final GameEvent<OnStartOfTurnPostDrawSubscriber> onStartOfTurnPostDraw = registerEvent(new GameEvent<>());
    public static final GameEvent<OnStartOfTurnSubscriber> onStartOfTurn = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTagChangedSubscriber> onTagChanged = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryChannelOrbSubscriber> onTryChannelOrb = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryElementReactSubscriber> onTryElementReact = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryGainResolveSubscriber> onTryGainResolve = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTrySpendEnergySubscriber> onTrySpendEnergy = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryUseResolveSubscriber> onTryUseResolve = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryUseXCostSubscriber> onTryUseXCost = registerEvent(new GameEvent<>());
    public static final GameEvent<OnTryUsingCardSubscriber> onTryUsingCard = registerEvent(new GameEvent<>());
    public static final PCLPlayerSystem playerSystem = new PCLPlayerSystem();
    public static final SummonPool summons = new SummonPool();
    public static int dodgeChance;

    public static boolean canActivateLimited(String id) { return !hasActivatedLimited(id); }
    public static boolean hasActivatedLimited(String id) { return combatData.containsKey(id); }
    public static boolean tryActivateLimited(String id) { return combatData.put(id, 1) == null; }
    public static boolean canActivateSemiLimited(String id) { return !hasActivatedSemiLimited(id); }
    public static boolean hasActivatedSemiLimited(String id) { return turnData.containsKey(id); }
    public static boolean tryActivateSemiLimited(String id) { return turnData.put(id, 1) == null; }
    //
    public static boolean canActivateLimited(String id, int cap) { return !hasActivatedLimited(id, cap); }
    public static boolean hasActivatedLimited(String id, int cap) { return combatData.containsKey(id) && (int)combatData.get(id) >= cap; }
    public static boolean tryActivateLimited(String id, int cap) { return EUIUtils.incrementMapElement(combatData, id) <= cap; }
    public static boolean canActivateSemiLimited(String id, int cap) { return !hasActivatedSemiLimited(id, cap); }
    public static boolean hasActivatedSemiLimited(String id, int cap) { return turnData.containsKey(id) && (int)turnData.get(id) >= cap; }
    public static boolean tryActivateSemiLimited(String id, int cap) { return EUIUtils.incrementMapElement(turnData, id) <= cap; }


    public static void addAmplifierBonus(String powerID, int multiplier)
    {
        addBonus(powerID, Type.Amplifier, multiplier);
    }

    public static void addBonus(String powerID, Type effectType, float multiplier)
    {
        multiplier = CombatManager.onGainTriggerablePowerBonus(powerID, effectType, multiplier);
        getEffectBonusMapForType(effectType).merge(powerID, multiplier, Float::sum);

        if (GameUtilities.inBattle())
        {

            for (AbstractCreature cr : GameUtilities.getAllCharacters(true))
            {
                if (cr.powers != null)
                {
                    for (AbstractPower po : cr.powers)
                    {
                        if (powerID.equals(po.ID))
                        {
                            po.updateDescription();
                            po.flashWithoutSound();
                        }
                    }
                }
            }

        }

    }

    public static void addEffectBonus(String powerID, float multiplier)
    {
        addBonus(powerID, Type.Effect, multiplier);
    }

    public static void addPassiveDamageBonus(String powerID, float multiplier)
    {
        addBonus(powerID, Type.PassiveDamage, multiplier);
    }

    public static void addPlayerEffectBonus(String powerID, float multiplier)
    {
        addBonus(powerID, Type.PlayerEffect, multiplier);
    }

    public static void atEndOfTurn()
    {
        CombatManager.playerSystem.onEndOfTurn();
        cardsDiscardedThisTurn.clear();
        matchesThisTurn.clear();
        hasteInfinitesThisTurn.clear();
    }

    public static void atStartOfTurn()
    {
        CombatManager.playerSystem.onStartOfTurn();
        dodgeChance = 0;
    }

    public static List<AbstractCard> cardsDiscardedThisCombat() {
        return cardsDiscardedThisCombat;
    }

    public static List<AbstractCard> cardsDiscardedThisTurn() {
        return cardsDiscardedThisTurn;
    }

    private static void clearStats()
    {
        refreshPlayer();
        EUIUtils.logInfoIfDebug(CombatManager.class, "Clearing PCL Player Stats");
        for (GameEvent<?> event : EVENTS)
        {
            event.clear();
        }

        CardGlowBorderPatches.overrideColor = null;
        dodgeChance = 0;
        AMPLIFIER_BONUSES.clear();
        EFFECT_BONUSES.clear();
        PASSIVE_DAMAGE_BONUSES.clear();
        PLAYER_EFFECT_BONUSES.clear();
        PGR.core.combatScreen.formulaDisplay.initialize();
        controlPile.clear();
        GridCardSelectScreenHelper.clear(true);
        playerSystem.initialize();
        playerSystem.setLastCardPlayed(null);
        summons.initialize();
        maxHPSinceLastTurn = AbstractDungeon.player == null ? 0 : AbstractDungeon.player.currentHealth;
        blockRetained = 0;
        battleID = null;

        turnCount = 0;
        cardsDrawnThisTurn = 0;
        orbsEvokedThisCombat.clear();
        orbsEvokedThisTurn.clear();
        cardsDiscardedThisCombat.clear();
        cardsDiscardedThisTurn.clear();
        cardsPlayedThisCombat.clear();
        cardsExhaustedThisCombat.clear();
        cardsExhaustedThisTurn.clear();
        matchesThisCombat.clear();
        matchesThisTurn.clear();
        hasteInfinitesThisTurn.clear();
        unplayableCards.clear();
        currentPhase = null;
        combatData.clear();
        turnData.clear();

        CardGlowBorderPatches.overrideColor = null;
        PURGED_CARDS.clear();

        // Add effects for vanilla relics
        addEffectBonus(VulnerablePower.POWER_ID, 50 + (GameUtilities.hasRelicEffect(PaperFrog.ID) ? 25 : 0));
        addEffectBonus(WeakPower.POWER_ID, 25 + (GameUtilities.hasRelicEffect(PaperCrane.ID) ? 15 : 0));
        addEffectBonus(PCLLockOnPower.POWER_ID, 50);
        addPlayerEffectBonus(VulnerablePower.POWER_ID, 50 + (GameUtilities.hasRelicEffect(OddMushroom.ID) ? -25 : 0));
        addPlayerEffectBonus(WeakPower.POWER_ID, 25);
        addPlayerEffectBonus(FrailPower.POWER_ID, 25);
        addPlayerEffectBonus(ImpairedPower.POWER_ID, 50);
    }

    public static Set<Map.Entry<String, Float>> getAllAmplifierBonuses()
    {
        return AMPLIFIER_BONUSES.entrySet();
    }

    public static Set<Map.Entry<String, Float>> getAllEffectBonuses()
    {
        return EFFECT_BONUSES.entrySet();
    }

    public static Set<Map.Entry<String, Float>> getAllPassiveDamageBonuses()
    {
        return PASSIVE_DAMAGE_BONUSES.entrySet();
    }

    public static Set<Map.Entry<String, Float>> getAllPlayerEffectBonuses()
    {
        return PLAYER_EFFECT_BONUSES.entrySet();
    }

    public static float getAmplifierBonus(String powerID)
    {
        return AMPLIFIER_BONUSES.getOrDefault(powerID, 0f);
    }

    public static float getBonus(String powerID, Type effectType)
    {
        return getEffectBonusMapForType(effectType).getOrDefault(powerID, 0f);
    }

    public static float getEffectBonus(String powerID)
    {
        return EFFECT_BONUSES.getOrDefault(powerID, 0f);
    }

    public static HashMap<String, Float> getEffectBonusMapForType(Type effectType)
    {
        switch (effectType)
        {
            case Amplifier:
                return AMPLIFIER_BONUSES;
            case Effect:
                return EFFECT_BONUSES;
            case PlayerEffect:
                return PLAYER_EFFECT_BONUSES;
            case PassiveDamage:
                return PASSIVE_DAMAGE_BONUSES;
        }
        throw new RuntimeException("Unsupported Effect Bonus type.");
    }

    public static float getPassiveDamageBonus(String powerID)
    {
        return PASSIVE_DAMAGE_BONUSES.getOrDefault(powerID, 0f);
    }

    public static float getPlayerEffectBonus(String powerID)
    {
        return PLAYER_EFFECT_BONUSES.getOrDefault(powerID, 0f);
    }

    public static List<AbstractCard> hasteInfinitesThisTurn()
    {
        return hasteInfinitesThisTurn;
    }

    public static List<AbstractCard> matchesThisCombat()
    {
        return matchesThisCombat;
    }

    public static List<AbstractCard> matchesThisTurn()
    {
        return matchesThisTurn;
    }

    public static void onAfterlife(AbstractCard playedCard, ArrayList<AbstractCard> fuelCards)
    {
        for (OnAfterlifeSubscriber s : onAfterlife.getSubscribers())
        {
            s.onAfterlife(playedCard, fuelCards);
        }
    }

    public static void onCardDiscarded(AbstractCard card)
    {
        cardsDiscardedThisCombat.add(card);
        cardsDiscardedThisTurn.add(card);

        for (SkillModifier wrapper : SkillModifier.getAll(card))
        {
            wrapper.onDiscard(card);
        }
        for (OnCardDiscardedSubscriber s : onCardDiscarded.getSubscribers())
        {
            s.onCardDiscarded(card);
        }
    }

    public static void onCardMoved(AbstractCard card, CardGroup source, CardGroup destination)
    {
        for (OnCardMovedSubscriber s : onCardMoved.getSubscribers())
        {
            s.onCardMoved(card, source, destination);
        }
    }

    public static void onCardPurged(AbstractCard card)
    {
        if (!PURGED_CARDS.contains(card))
        {
            PURGED_CARDS.group.add(card);
        }

        if (card instanceof PCLCard)
        {
            ((PCLCard) card).triggerOnPurge();
        }

        for (SkillModifier wrapper : SkillModifier.getAll(card))
        {
            wrapper.onPurged(card);
        }

        for (OnCardPurgedSubscriber s : onCardPurged.getSubscribers())
        {
            s.onPurge(card);
        }
    }

    public static void onCardReshuffled(AbstractCard card, CardGroup sourcePile)
    {
        if (card instanceof PCLCard)
        {
            ((PCLCard) card).triggerOnReshuffle(sourcePile);
        }

        for (SkillModifier wrapper : SkillModifier.getAll(card))
        {
            wrapper.onReshuffled(card, sourcePile);
        }

        for (OnCardReshuffledSubscriber s : onCardReshuffled.getSubscribers())
        {
            s.onCardReshuffled(card, sourcePile);
        }
    }

    public static boolean onClickablePowerUsed(PCLPower power, AbstractMonster target)
    {
        boolean shouldPayCost = true;
        for (OnPCLClickablePowerUsed s : onPCLClickablePowerUsed.getSubscribers())
        {
            shouldPayCost = shouldPayCost & s.onClickablePowerUsed(power, target);
        }
        return shouldPayCost;
    }

    public static boolean onCooldownTriggered(AbstractCard card, AbstractCreature m, CooldownProvider cooldown)
    {
        boolean canProgress = true;
        for (OnCooldownTriggeredSubscriber s : onCooldownTriggered.getSubscribers())
        {
            canProgress = canProgress & s.onCooldownTriggered(card, m, cooldown);
        }
        return canProgress;
    }

    public static void onDamageAction(AbstractGameAction action, AbstractCreature target, DamageInfo info, AbstractGameAction.AttackEffect effect)
    {
        if (GameUtilities.getEnemies(true).contains(action.source))
        {
            action.target = summons.getTarget(action.source);
        }

        for (OnDamageActionSubscriber s : onDamageAction.getSubscribers())
        {
            s.onDamageAction(action, target, info, effect);
        }
    }

    public static float onDamageOverride(AbstractCreature target, DamageInfo.DamageType type, float damage, AbstractCard card)
    {
        for (OnDamageOverrideSubscriber s : onDamageOverride.getSubscribers())
        {
            damage = s.onDamageOverride(target, type, damage, card);
        }

        return damage;
    }

    public static void onDeath()
    {
        clearStats();
    }

    public static void onElementReact(AffinityReactions reactions, AbstractCreature m)
    {
        for (OnElementReactSubscriber s : onElementReact.getSubscribers())
        {
            s.onElementReact(reactions, m);
        }
    }

    public static int onGainTempHP(int amount)
    {
        for (OnGainTempHPSubscriber s : onGainTempHP.getSubscribers())
        {
            amount = s.onGainTempHP(amount);
        }
        return amount;
    }

    public static float onGainTriggerablePowerBonus(String powerID, Type gainType, float amount)
    {
        for (OnGainPowerBonusSubscriber s : onGainTriggerablePowerBonus.getSubscribers())
        {
            amount = s.onGainPowerBonus(powerID, gainType, amount);
        }
        return amount;
    }

    public static void onGameStart()
    {
        clearStats();

        PGR.core.dungeon.reset();
    }

    public static void onIntensify(PCLAffinity button)
    {
        for (OnIntensifySubscriber s : onIntensify.getSubscribers())
        {
            s.onIntensify(button);
        }
    }

    public static void onMatch(AbstractCard card, PCLUseInfo info)
    {

        for (OnMatchSubscriber s : onMatch.getSubscribers())
        {
            s.onMatch(card, info);
        }

        matchesThisTurn.add(card);
        matchesThisCombat.add(card);
    }

    public static void onMatchBonus(AbstractCard card, PCLAffinity affinity)
    {
        for (OnMatchBonusSubscriber s : onMatchBonus.getSubscribers())
        {
            s.onMatchBonus(card, affinity);
        }
    }

    public static float onModifyMagicNumber(float amount, AbstractCard card)
    {
        for (OnModifyMagicNumberSubscriber s : onModifyMagicNumber.getSubscribers())
        {
            amount = s.onModifyMagicNumber(amount, card);
        }
        return amount;
    }

    public static boolean onMonsterMove(AbstractMonster target)
    {
        boolean canMove = true;
        for (OnMonsterMoveSubscriber s : onMonsterMove.getSubscribers())
        {
            canMove = canMove & s.onMonsterMove(target);
        }
        return canMove;
    }

    public static void onNotMatch(AbstractCard card, PCLUseInfo info)
    {
        for (OnNotMatchSubscriber s : onNotMatch.getSubscribers())
        {
            s.onNotMatch(card, info);
        }
    }

    public static void onOrbApplyFocus(AbstractOrb orb)
    {
        for (OnOrbApplyFocusSubscriber s : onOrbApplyFocus.getSubscribers())
        {
            s.onApplyFocus(orb);
        }
    }

    public static float onOrbApplyLockOn(AbstractCreature target, float dmg)
    {
        for (OnOrbApplyLockOnSubscriber s : onOrbApplyLockOn.getSubscribers())
        {
            dmg = s.onOrbApplyLockOn(target, dmg);
        }
        return (int) dmg;
    }

    public static void onPlayCardPostActions(AbstractCard card, AbstractMonster m)
    {
        CombatManager.playerSystem.trySynergize(card);
        if (PCLCardTag.Recast.has(card))
        {
            PCLCardTag.Recast.tryProgress(card);
            new DelayUse(1, DelayUse.Timing.StartOfTurnLast, new PCLUseInfo(card, AbstractDungeon.player, m), (i) -> PCLActions.bottom.playCopy(card, EUIUtils.safeCast(i.target, AbstractMonster.class))).start();
        }
    }

    public static void onStartOver()
    {
        clearStats();
        onBattleStart.clear();
        onBattleEnd.clear();

        PGR.core.dungeon.reset();
    }

    public static void onStartup()
    {
        refresh();
        clearStats();
    }

    public static void onAfterDeath()
    {
        for (OnAfterDeathSubscriber s : onAfterDeath.getSubscribers())
        {
            s.onAfterDeath();
        }

        clearStats();
    }

    public static int onModifyDamageFirst(AbstractCreature target, DamageInfo info, int damage)
    {
        for (OnModifyDamageFirstSubscriber s : onModifyDamageFirst.getSubscribers())
        {
            damage = s.onModifyDamageFirst(target, info, damage);
        }

        return damage;
    }

    public static int onModifyDamageLast(AbstractCreature target, DamageInfo info, int damage)
    {
        for (OnModifyDamageLastSubscriber s : onModifyDamageLast.getSubscribers())
        {
            damage = s.onModifyDamageLast(target, info, damage);
        }
        if (target == AbstractDungeon.player && info.type == DamageInfo.DamageType.NORMAL && GameUtilities.chance(dodgeChance))
        {
            AbstractDungeon.player.tint.color.a = 0;
            PCLActions.bottom.playSFX(SFX.NULLIFY_SFX, 1.6f, 1.6f);
            PCLActions.top.wait(0.15f);
            PCLEffects.Queue.add(new DodgeEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, PGR.core.strings.combat.dodged));
            return 0;
        }

        return damage;
    }

    public static void onModifyDebuff(AbstractPower debuff, int initialAmount, int newAmount)
    {
        for (OnModifyDebuffSubscriber s : onModifyDebuff.getSubscribers())
        {
            s.onModifyDebuff(debuff, initialAmount, newAmount);
        }
    }

    public static int onEnergyRecharge(int previousEnergy, int currentEnergy)
    {
        final MutableInt a = new MutableInt(previousEnergy);
        final MutableInt b = new MutableInt(currentEnergy);
        for (OnEnergyRechargeSubscriber s : onEnergyRecharge.getSubscribers())
        {
            s.onEnergyRecharge(a, b);
        }

        return b.getValue();
    }

    public static void onCardReset(AbstractCard card)
    {
        final OnCardResetListener c = EUIUtils.safeCast(card, OnCardResetListener.class);
        if (c != null)
        {
            c.onReset();
        }

        for (OnCardResetSubscriber s : onCardReset.getSubscribers())
        {
            s.onCardReset(card);
        }
    }

    public static void onCardCreated(AbstractCard card, boolean startOfBattle)
    {
        final PCLCard c = EUIUtils.safeCast(card, PCLCard.class);
        if (c != null)
        {
            c.triggerWhenCreated(startOfBattle);
        }

        for (OnCardCreatedSubscriber s : onCardCreated.getSubscribers())
        {
            s.onCardCreated(card, startOfBattle);
        }
    }

    public static void onShuffle(boolean triggerRelics)
    {
        for (OnShuffleSubscriber s : onShuffle.getSubscribers())
        {
            s.onShuffle(triggerRelics);
        }
    }

    public static void onRelicObtained(AbstractRelic relic, OnRelicObtainedSubscriber.Trigger trigger)
    {
        refreshPlayer();

        for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
        {
            if (c instanceof OnRelicObtainedSubscriber)
            {
                ((OnRelicObtainedSubscriber) c).onRelicObtained(relic, trigger);
            }
        }

        for (AbstractRelic r : AbstractDungeon.player.relics)
        {
            if (r instanceof OnRelicObtainedSubscriber)
            {
                ((OnRelicObtainedSubscriber) r).onRelicObtained(relic, trigger);
            }
        }
    }

    public static void onBattleStart()
    {
        refresh();

        onBattleEnd.clear();
        for (OnBattleStartSubscriber s : onBattleStart.getSubscribers())
        {
            s.onBattleStart();
        }
        onBattleStart.clear();

        final ArrayList<AbstractCard> cards = new ArrayList<>(AbstractDungeon.player.drawPile.group);
        cards.addAll(AbstractDungeon.player.hand.group);
        cards.addAll(AbstractDungeon.player.discardPile.group);
        cards.addAll(AbstractDungeon.player.exhaustPile.group);

        for (AbstractCard c : cards)
        {
            onCardCreated(c, true);
        }
    }

    public static void onBattleEnd()
    {
        for (OnBattleEndSubscriber s : onBattleEnd.getSubscribers())
        {
            s.onBattleEnd();
        }

        onBattleStart.clear();
        onBattleEnd.clear();
        clearStats();
    }

    public static void onTagChanged(AbstractCard card, PCLCardTag tag, int value)
    {
        for (OnTagChangedSubscriber s : onTagChanged.getSubscribers())
        {
            s.onTagChanged(card, tag, value);
        }
    }

    public static AbstractOrb onTryChannelOrb(AbstractOrb orb)
    {
        for (OnTryChannelOrbSubscriber s : onTryChannelOrb.getSubscribers())
        {
            orb = s.onTryChannelOrb(orb);
        }

        return orb;
    }

    public static int onTryElementReact(int amount, PCLAffinity button, PCLAffinity trigger)
    {
        for (OnTryElementReactSubscriber s : onTryElementReact.getSubscribers())
        {
            amount = s.onTryElementReact(amount, button, trigger);
        }
        return amount;
    }

    public static int onTryGainResolve(AbstractCard card, AbstractPlayer p, int cost, boolean isActuallyGaining, boolean isFromMatch)
    {
        for (OnTryGainResolveSubscriber s : onTryGainResolve.getSubscribers())
        {
            cost = s.onTryGainResolve(card, p, cost, isActuallyGaining, isFromMatch);
        }

        return cost;
    }

    public static int onTrySpendEnergy(AbstractCard card, AbstractPlayer p, int cost)
    {
        // Hardcoded base game logic
        if (p.hasPower(CorruptionPower.POWER_ID) && card.type != AbstractCard.CardType.SKILL)
        {
            cost = 0;
        }

        for (OnTrySpendEnergySubscriber s : onTrySpendEnergy.getSubscribers())
        {
            cost = s.onTrySpendEnergy(card, p, cost);
        }

        return cost;
    }

    public static int onTryUseResolve(int original, int toSpend, boolean fromButton)
    {
        for (OnTryUseResolveSubscriber s : onTryUseResolve.getSubscribers())
        {
            toSpend = s.onTryUseResolve(original, toSpend, fromButton);
        }

        return toSpend;
    }

    public static int onTryUseXCost(int original, AbstractCard card)
    {
        for (OnTryUseXCostSubscriber s : onTryUseXCost.getSubscribers())
        {
            original = s.onTryUseXCost(original, card);
        }

        return original;
    }

    public static void onAllySummon(PCLCard card, PCLCardAlly ally)
    {
        for (OnAllySummonSubscriber s : onAllySummon.getSubscribers())
        {
            s.onAllySummon(card, ally);
        }
    }

    public static void onAllyTrigger(PCLCardAlly ally)
    {
        for (OnAllyTriggerSubscriber s : onAllyTrigger.getSubscribers())
        {
            s.onAllyTrigger(ally);
        }
    }

    public static void onAllyWithdraw(PCLCardAlly ally, PCLCard returned)
    {
        for (OnAllyWithdrawSubscriber s : onAllyWithdraw.getSubscribers())
        {
            s.onAllyWithdraw(ally, returned);
        }
    }

    public static void onUsingCard(PCLCard card, AbstractPlayer p, AbstractMonster m)
    {
        if (card == null)
        {
            throw new RuntimeException("Card played is null");
        }

        card.unfadeOut();
        card.lighten(true);

        // The target may have been overwritten with a null value
        card.calculateCardDamage(m);
        final PCLUseInfo info = new PCLUseInfo(card, p, m);

        PCLAction.currentCard = card;
        if (card.type == PGR.Enums.CardType.SUMMON)
        {
            PCLCardAlly slot = EUIUtils.safeCast(info.target, PCLCardAlly.class);
            PCLActions.bottom.summonAlly(card, slot);
        }
        else
        {
            card.onPreUse(info);
            card.onUse(info);
        }
        PCLAction.currentCard = null;

        if (info.isMatch)
        {
            CombatManager.onMatch(card, info);
        }
        else
        {
            CombatManager.onNotMatch(card, info);
        }

        final ArrayList<AbstractGameAction> actions = PCLActions.getActions();

        cachedActions.clear();
        cachedActions.addAll(actions);

        actions.clear();
        if (card.type != PGR.Enums.CardType.SUMMON)
        {
            PCLAction.currentCard = card;
            card.onLateUse(info);
            PCLAction.currentCard = null;
        }

        playerSystem.onCardPlayed(card, m, info);
        if (info.isMatch)
        {
            playerSystem.onMatch(card);
        }
        else
        {
            playerSystem.onNotMatch(card);
        }

        if (actions.isEmpty())
        {
            actions.addAll(cachedActions);
        }
        else
        {
            for (int i = 0; i < cachedActions.size(); i++)
            {
                PCLActions.top.add(cachedActions.get(cachedActions.size() - 1 - i));
            }
        }

        onUsingCardPostActions(card, p, m);
    }

    // TODO Custom use card action
    public static void onUsingCardPostActions(AbstractCard card, AbstractPlayer p, AbstractMonster m)
    {
        PCLActions.bottom.add(new UseCardAction(card, m));
        if (!card.dontTriggerOnUseCard)
        {
            p.hand.triggerOnOtherCardPlayed(card);
        }

        p.hand.removeCard(card);
        p.cardInUse = card;
        card.target_x = (float) (Settings.WIDTH / 2);
        card.target_y = (float) (Settings.HEIGHT / 2);

        int spendEnergy = CombatManager.onTrySpendEnergy(card, p, card.costForTurn);
        if (spendEnergy > 0 && !card.freeToPlay() && !card.isInAutoplay)
        {
            p.energy.use(spendEnergy);
        }

        if (!p.hand.canUseAnyCard() && !p.endTurnQueued)
        {
            AbstractDungeon.overlayMenu.endTurnButton.isGlowing = true;
        }

        CombatManager.playerSystem.setLastCardPlayed(card);
        AbstractDungeon.player.hand.glowCheck();
    }

    public static void onVictory()
    {
        PGR.core.dungeon.updateLongestMatchCombo(playerSystem.getActiveMeter().getLongestMatchCombo());
        clearStats();
    }

    public static void refresh()
    {
        refreshPlayer();

        room = GameUtilities.getCurrentRoom(false);

        if (room == null || AbstractDungeon.player == null)
        {
            battleID = null;
        }
        else if (room.isBattleOver || AbstractDungeon.player.isDead)
        {
            if (room.phase != AbstractRoom.RoomPhase.COMBAT || room.monsters == null || room.monsters.areMonstersBasicallyDead())
            {
                battleID = null;
            }
        }
        else if (battleID == null && room.phase == AbstractRoom.RoomPhase.COMBAT)
        {
            battleID = UUID.randomUUID();
        }

        PGR.core.combatScreen.initialize();
    }


    public static AbstractPlayer refreshPlayer()
    {
        PCLCard.rng = PCLPower.rng = PCLRelic.rng = AbstractDungeon.cardRandomRng;
        return PCLCard.player = PCLPower.player = PCLRelic.player = AbstractDungeon.player;
    }

    protected static <T> GameEvent<T> registerEvent(GameEvent<T> event)
    {
        EVENTS.add(event);
        return event;
    }

    public static boolean onTryUsingCard(AbstractCard card, AbstractPlayer p, AbstractMonster m, boolean canPlay)
    {
        if (unplayableCards.contains(card.uuid))
        {
            return false;
        }

        for (OnTryUsingCardSubscriber s : onTryUsingCard.getSubscribers())
        {
            canPlay &= s.onTryUsingCard(card, p, m, canPlay);
        }

        return canPlay;
    }

    public static void onMonsterDeath(AbstractMonster monster, boolean triggerRelics)
    {
        for (OnMonsterDeathSubscriber s : onMonsterDeath.getSubscribers())
        {
            s.onMonsterDeath(monster, triggerRelics);
        }
    }

    public static void onHealthBarUpdated(AbstractCreature creature)
    {
        if (creature == AbstractDungeon.player && creature.currentHealth > maxHPSinceLastTurn)
        {
            maxHPSinceLastTurn = creature.currentHealth;
        }

        for (OnHealthBarUpdatedSubscriber s : onHealthBarUpdated.getSubscribers())
        {
            s.onHealthBarUpdated(creature);
        }

        GameUtilities.refreshHandLayout(true);
    }

    public static void onBlockGained(AbstractCreature creature, int block)
    {
        for (OnBlockGainedSubscriber s : onBlockGained.getSubscribers())
        {
            s.onBlockGained(creature, block);
        }
    }

    public static void onBlockBroken(AbstractCreature creature)
    {
        for (OnBlockBrokenSubscriber s : onBlockBroken.getSubscribers())
        {
            s.onBlockBroken(creature);
        }
    }

    public static void onBeforeLoseBlock(AbstractCreature creature, int amount, boolean noAnimation)
    {
        for (OnBeforeLoseBlockSubscriber s : onBeforeLoseBlock.getSubscribers())
        {
            s.onBeforeLoseBlock(creature, amount, noAnimation);
        }
    }

    public static void onOrbPassiveEffect(AbstractOrb orb)
    {
        for (OnOrbPassiveEffectSubscriber s : onOrbPassiveEffect.getSubscribers())
        {
            s.onOrbPassiveEffect(orb);
        }
    }

    public static void onAfterDraw(AbstractCard card)
    {
        cardsDrawnThisTurn += 1;
        for (OnAfterCardDrawnSubscriber s : onAfterCardDrawn.getSubscribers())
        {
            s.onAfterCardDrawn(card);
        }

        if (PCLCardTag.Haste.has(card))
        {
            PCLActions.top.add(new PCLHasteAction(card));
        }
    }

    public static <T> T setTurnData(String key, T data)
    {
        turnData.put(key, data);
        return data;
    }

    public static <T> T getTurnData(String key, T defaultData)
    {
        if (turnData.containsKey(key))
        {
            return (T) turnData.get(key);
        }
        else if (defaultData != null)
        {
            return setCombatData(key, defaultData);
        }

        return defaultData;
    }

    public static <T> T setCombatData(String key, T data)
    {
        combatData.put(key, data);
        return data;
    }

    public static <T> T getCombatData(String key, T defaultData)
    {
        if (combatData.containsKey(key))
        {
            return (T) combatData.get(key);
        }
        else if (defaultData != null)
        {
            return setCombatData(key, defaultData);
        }

        return defaultData;
    }

    public static List<AbstractCard> cardsExhaustedThisCombat()
    {
        return cardsExhaustedThisCombat;
    }

    public static List<AbstractCard> cardsExhaustedThisTurn()
    {
        return cardsExhaustedThisTurn;
    }

    public static List<AbstractCard> cardsPlayedThisCombat(int turn)
    {
        return cardsPlayedThisCombat.computeIfAbsent(turn, k -> new ArrayList<>());
    }

    public static List<UUID> unplayableCards()
    {
        return unplayableCards;
    }

    public static int cardsDrawnThisTurn()
    {
        return cardsDrawnThisTurn;
    }

    public static List<AbstractOrb> orbsEvokedThisCombat()
    {
        return orbsEvokedThisCombat;
    }

    public static List<AbstractOrb> orbsEvokedThisTurn()
    {
        return orbsEvokedThisTurn;
    }

    public static int turnCount(boolean fromZero)
    {
        return fromZero ? turnCount : (turnCount + 1);
    }

    public static void onApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower power)
    {
        for (OnApplyPowerSubscriber p : onApplyPower.getSubscribers())
        {
            p.onApplyPower(power, target, source);
        }
    }
    public static void onChannel(AbstractOrb orb)
    {
        if (orb != null && !(orb instanceof EmptyOrbSlot))
        {
            if (orb instanceof PCLOrb)
            {
                ((PCLOrb) orb).onChannel();
            }

            for (OnChannelOrbSubscriber p : onChannelOrb.getSubscribers())
            {
                p.onChannelOrb(orb);
            }
        }
    }

    public static void onEvokeOrb(AbstractOrb orb)
    {
        if (orb != null && !(orb instanceof EmptyOrbSlot))
        {
            for (OnEvokeOrbSubscriber p : onEvokeOrb.getSubscribers())
            {
                p.onEvokeOrb(orb);
            }
            orbsEvokedThisCombat.add(orb);
            orbsEvokedThisTurn.add(orb);
        }
    }

    public static void onAfterCardPlayed(AbstractCard card)
    {
        for (OnAfterCardPlayedSubscriber p : onAfterCardPlayed.getSubscribers())
        {
            p.onAfterCardPlayed(card);
        }

        cardsPlayedThisCombat(turnCount).add(card);

        if (AbstractDungeon.player.limbo.contains(card))
        {
            PCLActions.top.add(new UnlimboAction(card));
        }
    }

    public static void onExhaust(AbstractCard card)
    {
        card.targetDrawScale = 0.75F;
        card.setAngle(0.0F);
        card.lighten(false);
        card.clearPowers();

        for (OnAfterCardExhaustedSubscriber p : onAfterCardExhausted.getSubscribers())
        {
            p.onAfterCardExhausted(card);
        }
        cardsExhaustedThisCombat.add(card);
        cardsExhaustedThisTurn.add(card);
    }

    public static void onAttack(DamageInfo info, int damageAmount, AbstractCreature target)
    {
        for (OnAttackSubscriber p : onAttack.getSubscribers())
        {
            p.onAttack(info, damageAmount, target);
        }
    }

    public static void update()
    {
        if (currentPhase != AbstractDungeon.actionManager.phase)
        {
            currentPhase = AbstractDungeon.actionManager.phase;
            for (OnPhaseChangedSubscriber s : onPhaseChanged.getSubscribers())
            {
                s.onPhaseChanged(currentPhase);
            }
        }
    }

    public static void onChangeStance(AbstractStance oldStance, AbstractStance newStance)
    {
        for (OnStanceChangedSubscriber s : onStanceChanged.getSubscribers())
        {
            s.onStanceChanged(oldStance, newStance);
        }
    }

    public static int onPlayerLoseHP(AbstractPlayer p, DamageInfo info, int damageAmount)
    {
        for (OnLoseHPSubscriber s : onLoseHP.getSubscribers())
        {
            damageAmount = s.onLoseHP(p, info, damageAmount);
        }
        if (damageAmount > 0)
        {
            damageAmount = summons.tryDamage(info, damageAmount);
        }
        return damageAmount;
    }

    public static void atPlayerTurnStart()
    {
        isPlayerTurn = true;
        maxHPSinceLastTurn = GameActionManager.playerHpLastTurn;

        if (onStartOfTurn.count() > 0)
        {
            for (OnStartOfTurnSubscriber s : onStartOfTurn.getSubscribers())
            {
                s.onStartOfTurn();
            }
        }

        if (blockRetained > 0 && !AbstractDungeon.player.hasPower(BarricadePower.POWER_ID) && !AbstractDungeon.player.hasPower(BlurPower.POWER_ID))
        {
            blockRetained = 0;
        }
    }

    public static void atPlayerTurnStartPostDraw()
    {
        if (onStartOfTurnPostDraw.count() > 0)
        {
            for (OnStartOfTurnPostDrawSubscriber s : onStartOfTurnPostDraw.getSubscribers())
            {
                s.onStartOfTurnPostDraw();
            }
        }

        if (blockRetained > 0)
        {
            int temp = Math.max(0, AbstractDungeon.player.currentBlock - blockRetained);
            if (temp > 0)
            {
                if (AbstractDungeon.player.hasRelic(Calipers.ID))
                {
                    temp = Math.min(Calipers.BLOCK_LOSS, temp);
                }

                AbstractDungeon.player.loseBlock(temp, true);
            }

            blockRetained = 0;
        }
    }


    public static void atEndOfTurnPreEndTurnCards(boolean isPlayer)
    {
        isPlayerTurn = false;

        for (OnEndOfTurnFirstSubscriber s : onEndOfTurnFirst.getSubscribers())
        {
            s.onEndOfTurnFirst(isPlayer);
        }
    }

    public static void atEndOfTurn(boolean isPlayer)
    {
        for (OnEndOfTurnLastSubscriber s : onEndOfTurnLast.getSubscribers())
        {
            s.onEndOfTurnLast(isPlayer);
        }

        turnData.clear();
        cardsExhaustedThisTurn.clear();
        cardsDrawnThisTurn = 0;
        unplayableCards.clear();
        orbsEvokedThisTurn.clear();
        turnCount += 1;

        playerSystem.setLastCardPlayed(null);
    }

    public enum Type
    {
        Amplifier,
        Effect,
        PassiveDamage,
        PlayerEffect
    }
}
