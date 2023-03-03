package pinacolada.utilities;

import basemod.DevConsole;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RegrowPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BobEffect;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.GenericCondition;
import org.scannotation.AnnotationDB;
import pinacolada.actions.PCLActions;
import pinacolada.blights.common.UpgradedHand;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardAffinities;
import pinacolada.cards.base.fields.PCLCardAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.characters.PCLCharacter;
import pinacolada.effects.SFX;
import pinacolada.interfaces.listeners.OnTryApplyPowerListener;
import pinacolada.interfaces.listeners.OnTryReducePowerListener;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.orbs.PCLOrb;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.pcl.PCLCoreResources;
import pinacolada.stances.PCLStanceHelper;

import java.lang.reflect.Field;
import java.util.*;

import static com.evacipated.cardcrawl.modthespire.Patcher.annotationDBMap;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class GameUtilities
{
    protected static final String PORTRAIT_PATH = "images/1024Portraits/";
    protected static final String BETA_PATH = "images/1024PortraitsBeta/";
    private static final AbstractCard.CardRarity[] poolOrdering = AbstractCard.CardRarity.values();
    private static final RandomizedList<AbstractCard> fullCardPool = new RandomizedList<>();
    private static final RandomizedList<AbstractCard> characterCardPool = new RandomizedList<>();
    private static AbstractPlayer.PlayerClass lastPlayerClass;

    public static void applyPowerInstantly(Iterable<AbstractCreature> targets, PCLPowerHelper powerHelper, int stacks)
    {
        for (AbstractCreature target : targets)
        {
            applyPowerInstantly(target, powerHelper, stacks);
        }
    }

    public static void applyPowerInstantly(AbstractCreature target, PCLPowerHelper powerHelper, int stacks)
    {
        applyPowerInstantly(target, powerHelper.create(target, player, stacks), stacks);
    }

    public static AbstractPower applyPowerInstantly(AbstractCreature target, AbstractPower power, int stacks)
    {
        final AbstractPower existingPower = getPower(target, power.ID);
        if (existingPower != null)
        {
            if ((stacks != -1 || power.canGoNegative) && ((existingPower.amount += stacks) == 0))
            {
                if (!(existingPower instanceof PCLPower) || !((PCLPower) existingPower).canBeZero)
                {
                    target.powers.remove(existingPower);
                }
            }

            return existingPower;
        }

        target.addPower(power);
        Collections.sort(target.powers);

        return power;
    }

    public static boolean areMonstersBasicallyDead()
    {
        final AbstractRoom room = getCurrentRoom();
        final MonsterGroup group = room != null ? room.monsters : null;
        return group == null || group.areMonstersBasicallyDead();
    }

    public static AbstractMonster asMonster(AbstractCreature c)
    {
        return EUIUtils.safeCast(c, AbstractMonster.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canApplyPower(AbstractCreature source, AbstractCreature target, AbstractPower powerToApply, AbstractGameAction action)
    {
        boolean canApply = true;
        if (target != null)
        {
            for (AbstractPower power : target.powers)
            {
                if (power instanceof OnTryApplyPowerListener)
                {
                    canApply &= ((OnTryApplyPowerListener) power).tryApplyPower(powerToApply, target, source, action);
                }
            }

            if (canApply && target != source && source != null)
            {
                for (AbstractPower power : source.powers)
                {
                    if (power instanceof OnTryApplyPowerListener)
                    {
                        canApply &= ((OnTryApplyPowerListener) power).tryApplyPower(powerToApply, target, source, action);
                    }
                }
            }
        }

        return canApply;
    }

    public static boolean canObtainCopy(AbstractCard card)
    {
        return PGR.core.dungeon.canObtainCopy(card);
    }

    public static boolean canOrbApplyFocus(AbstractOrb orb)
    {
        return (!Plasma.ORB_ID.equals(orb.ID) && !(orb instanceof PCLOrb && !((PCLOrb) orb).canOrbApplyFocusToPassive));
    }

    public static boolean canOrbApplyFocusToEvoke(AbstractOrb orb)
    {
        return (!Dark.ORB_ID.equals(orb.ID) && !(orb instanceof PCLOrb && !((PCLOrb) orb).canOrbApplyFocusToEvoke));
    }

    public static boolean canPlayTwice(AbstractCard card)
    {
        return !card.purgeOnUse && card.type != PCLEnum.CardType.SUMMON && !PCLCardTag.Fleeting.has(card);
    }

    public static boolean canReceiveAnyColorCard()
    {
        return GameUtilities.hasRelicEffect(PrismaticShard.ID) || ModHelper.isModEnabled(Diverse.ID);
    }

    public static boolean canReducePower(AbstractCreature source, AbstractCreature target, String powerID, AbstractGameAction action)
    {
        AbstractPower power = powerID != null ? target.getPower(powerID) : null;
        return power == null || canReducePower(source, target, power, action);
    }

    public static boolean canReducePower(AbstractCreature source, AbstractCreature target, AbstractPower powerToApply, AbstractGameAction action)
    {
        boolean canReduce = true;
        if (target != null)
        {
            for (AbstractPower power : target.powers)
            {
                if (power instanceof OnTryReducePowerListener)
                {
                    canReduce &= ((OnTryReducePowerListener) power).tryReducePower(powerToApply, target, source, action);
                }
            }

            if (canReduce && target != source && source != null)
            {
                for (AbstractPower power : source.powers)
                {
                    if (power instanceof OnTryReducePowerListener)
                    {
                        canReduce &= ((OnTryReducePowerListener) power).tryReducePower(powerToApply, target, source, action);
                    }
                }
            }
        }

        return canReduce;
    }

    public static boolean canRemoveFromDeck(AbstractCard card)
    {
        return !SoulboundField.soulbound.get(card);
    }

    public static boolean canRetain(AbstractCard card)
    {
        return !card.isEthereal && !card.retain && !card.selfRetain;
    }

    public static boolean canShowUpgrades(boolean isLibrary)
    {
        return SingleCardViewPopup.isViewingUpgrade && (player == null || isLibrary
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD
                || AbstractDungeon.screen == AbstractScreen.EUI_SCREEN);
    }

    public static boolean chance(float amount)
    {
        return getRNG().random(100) < amount;
    }

    public static void changeCardName(AbstractCard card, String newName)
    {
        final String previousName = card.name;
        card.name = card.name.replace(card.originalName, newName);
        card.originalName = newName;
        if (card.name.equals(previousName))
        {
            card.name = newName;
        }
    }

    public static void clearPostCombatActions()
    {
        AbstractDungeon.actionManager.clearPostCombatActions();
    }

    public static void copyVisualProperties(AbstractCard copy, AbstractCard original)
    {
        copy.current_y = original.current_y;
        copy.current_x = original.current_x;
        copy.target_x = original.target_x;
        copy.target_y = original.target_y;
        copy.targetDrawScale = original.targetDrawScale;
        copy.drawScale = original.drawScale;
        copy.transparency = original.transparency;
        copy.targetTransparency = original.targetTransparency;
        copy.angle = original.angle;
        copy.targetAngle = original.targetAngle;
    }

    public static CardGroup createCardGroup(List<? extends AbstractCard> cards)
    {
        return createCardGroup(cards, CardGroup.CardGroupType.UNSPECIFIED);
    }

    public static CardGroup createCardGroup(List<? extends AbstractCard> cards, CardGroup.CardGroupType type)
    {
        final CardGroup group = new CardGroup(type);
        group.group.addAll(cards);
        return group;
    }

    public static int[] createOrbDamageMatrix(int baseDamage, AbstractOrb orb)
    {
        int[] retVal = new int[AbstractDungeon.getMonsters().monsters.size()];

        for (int i = 0; i < retVal.length; ++i)
        {
            DamageInfo info = new DamageInfo(AbstractDungeon.player, baseDamage, DamageInfo.DamageType.THORNS);
            retVal[i] = CombatManager.playerSystem.modifyOrbOutput(info.output, AbstractDungeon.getMonsters().monsters.get(i), orb);
        }

        return retVal;
    }

    public static void decreaseBlock(AbstractCard card, int amount, boolean temporary)
    {
        modifyBlock(card, Math.max(0, card.baseBlock - amount), temporary);
    }

    public static void decreaseDamage(AbstractCard card, int amount, boolean temporary)
    {
        modifyDamage(card, Math.max(0, card.baseDamage - amount), temporary);
    }

    public static void decreaseMagicNumber(AbstractCard card, int amount, boolean temporary)
    {
        modifyMagicNumber(card, Math.max(0, card.baseMagicNumber - amount), temporary);
    }

    public static void decreaseSecondaryValue(PCLCard card, int amount, boolean temporary)
    {
        modifySecondaryValue(card, Math.max(0, card.baseHeal - amount), temporary);
    }

    public static CardGroup findCardGroup(AbstractCard card, boolean includeLimbo)
    {
        if (player.hand.contains(card))
        {
            return player.hand;
        }
        else if (player.drawPile.contains(card))
        {
            return player.drawPile;
        }
        else if (player.discardPile.contains(card))
        {
            return player.discardPile;
        }
        else if (player.exhaustPile.contains(card))
        {
            return player.exhaustPile;
        }
        else if (includeLimbo && player.limbo.contains(card))
        {
            return player.limbo;
        }
        else if (CombatManager.PURGED_CARDS.contains(card))
        {
            return CombatManager.PURGED_CARDS;
        }
        else
        {
            return null;
        }
    }

    public static void flash(AbstractCard card, boolean superFlash)
    {
        if (superFlash)
        {
            card.superFlash();
        }
        else
        {
            card.flash();
        }
    }

    public static void flash(AbstractCard card, Color color, boolean superFlash)
    {
        if (superFlash)
        {
            card.superFlash(color.cpy());
        }
        else
        {
            card.flash(color.cpy());
        }
    }

    public static Random generateNewRNG(int a, int b)
    {
        return new Random(Settings.seed + (AbstractDungeon.actNum * a) + (AbstractDungeon.floorNum * b));
    }

    public static AbstractCard.CardColor getActingCardColor(AbstractCard c)
    {
        return AbstractDungeon.player != null ? player.getCardColor() : c.color;
    }

    public static AbstractCard.CardColor getActingColor()
    {
        return player != null ? player.getCardColor() : EUI.actingColor;
    }

    public static ArrayList<AbstractCreature> getAllCharacters(boolean aliveOnly)
    {
        final AbstractRoom room = getCurrentRoom();
        final ArrayList<AbstractCreature> characters = new ArrayList<>();
        if (room != null && room.monsters != null)
        {
            for (AbstractMonster m : room.monsters.monsters)
            {
                if (!aliveOnly || !isDeadOrEscaped(m))
                {
                    characters.add(m);
                }
            }
        }

        for (PCLCardAlly summon : CombatManager.summons.summons)
        {
            if (!aliveOnly || summon.hasCard())
            {
                characters.add(summon);
            }
        }

        if (!aliveOnly || !isDeadOrEscaped(player))
        {
            characters.add(player);
        }

        return characters;
    }

    public static HashSet<AbstractCard> getAllCopies(String cardID, CardGroup group)
    {
        return getAllCopies(new HashSet<>(), cardID, group);
    }

    public static HashSet<AbstractCard> getAllCopies(HashSet<AbstractCard> cards, String cardID, CardGroup group)
    {
        for (AbstractCard card : group.group)
        {
            if (cardID.equals(card.cardID))
            {
                cards.add(card);
            }
        }

        return cards;
    }

    public static HashSet<AbstractCard> getAllCopies(String cardID)
    {
        HashSet<AbstractCard> cards = getAllInBattleCopies(cardID);
        cards.addAll(getMasterDeckCopies(cardID));

        return cards;
    }

    public static HashSet<AbstractCard> getAllInBattleCopies(String cardID)
    {
        HashSet<AbstractCard> cards = new HashSet<>();

        if (player.cardInUse != null && player.cardInUse.cardID.equals(cardID))
        {
            cards.add(player.cardInUse);
        }

        getAllCopies(cards, cardID, player.hand);
        getAllCopies(cards, cardID, player.drawPile);
        getAllCopies(cards, cardID, player.discardPile);
        getAllCopies(cards, cardID, player.exhaustPile);
        getAllCopies(cards, cardID, player.limbo);

        return cards;
    }

    public static HashSet<AbstractCard> getAllInBattleInstances(UUID uuid)
    {
        final HashSet<AbstractCard> cards = new HashSet<>();

        if (player.cardInUse != null && player.cardInUse.uuid.equals(uuid))
        {
            cards.add(player.cardInUse);
        }

        getAllInstances(cards, uuid, player.hand);
        getAllInstances(cards, uuid, player.drawPile);
        getAllInstances(cards, uuid, player.discardPile);
        getAllInstances(cards, uuid, player.exhaustPile);
        getAllInstances(cards, uuid, player.limbo);

        return cards;
    }

    public static HashSet<AbstractCard> getAllInstances(UUID uuid)
    {
        final HashSet<AbstractCard> cards = getAllInBattleInstances(uuid);
        final AbstractCard masterDeckInstance = getMasterDeckInstance(uuid);
        if (masterDeckInstance != null)
        {
            cards.add(masterDeckInstance);
        }

        return cards;
    }

    public static HashSet<AbstractCard> getAllInstances(HashSet<AbstractCard> cards, UUID uuid, CardGroup group)
    {
        for (AbstractCard card : group.group)
        {
            if (uuid.equals(card.uuid))
            {
                cards.add(card);
            }
        }

        return cards;
    }

    public static AbstractCard getAnyColorCardFiltered(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean allowHealing)
    {
        refreshCardLists();
        if (fullCardPool.size() == 0)
        {
            boolean isPCL = GameUtilities.isPCLPlayerClass();
            RandomizedList<AbstractCard> cardPool = new RandomizedList<>();
            for (AbstractCard c : CardLibrary.cards.values())
            {
                if ((Settings.treatEverythingAsUnlocked() || !UnlockTracker.isCardLocked(c.cardID)) &&
                        (allowHealing || GameUtilities.isObtainableInCombat(c)) &&
                        (rarity == null || c.rarity == rarity) &&
                        ((type == null && !GameUtilities.isHindrance(c)) || c.type == type))
                {
                    fullCardPool.add(c);
                }
            }
        }

        return fullCardPool.retrieve(getRNG());
    }

    public static int getAscensionLevel()
    {
        return AbstractDungeon.isAscensionMode ? Math.max(0, Math.min(20, AbstractDungeon.ascensionLevel)) : 0;
    }

    public static ArrayList<AbstractCard> getAvailableCards()
    {
        return getAvailableCards(null);
    }

    public static ArrayList<AbstractCard> getAvailableCards(GenericCondition<AbstractCard> filter)
    {
        ArrayList<AbstractCard> result = new ArrayList<>();
        for (CardGroup pool : getCardPools())
        {
            for (AbstractCard card : pool.group)
            {
                if (filter == null || filter.check(card))
                {
                    result.add(card);
                }
            }
        }

        return result;
    }

    public static BobEffect getBobEffect(AbstractMonster mo)
    {
        return ReflectionHacks.getPrivate(mo, AbstractMonster.class, "bobEffect");
    }

    public static RandomizedList<AbstractCard> getCardsInCombat(GenericCondition<AbstractCard> filter)
    {
        final RandomizedList<AbstractCard> cards = new RandomizedList<>();
        for (CardGroup group : getCardPools())
        {
            for (AbstractCard c : group.group)
            {
                if (isObtainableInCombat(c) && (filter == null || filter.check(c)))
                {
                    cards.add(c);
                }
            }
        }

        return cards;
    }

    public static WeightedList<AbstractCard> getCardsInCombatWeighted(GenericCondition<AbstractCard> filter)
    {
        final WeightedList<AbstractCard> cards = new WeightedList<>();
        for (CardGroup group : getCardPools())
        {
            for (AbstractCard c : group.group)
            {
                if (isObtainableInCombat(c) && (filter == null || filter.check(c)))
                {
                    switch (c.rarity)
                    {
                        case COMMON:
                            cards.add(c, 9);
                            break;

                        case UNCOMMON:
                            cards.add(c, 7);
                            break;

                        default:
                            cards.add(c, 2);
                            break;
                    }
                }
            }
        }

        return cards;
    }

    public static RandomizedList<AbstractCard> getColorlessCardPoolInCombat()
    {
        return getCardPoolInCombat(getColorlessCardPool(), null);
    }

    public static RandomizedList<AbstractCard> getCardPoolInCombat(AbstractCard.CardRarity rarity)
    {
        return getCardPoolInCombat(getCardPool(rarity), null);
    }

    public static RandomizedList<AbstractCard> getCardPoolInCombat(CardGroup group, FuncT1<Boolean, AbstractCard> filter)
    {
        final RandomizedList<AbstractCard> cards = new RandomizedList<>();
        if (group != null)
        {
            for (AbstractCard c : group.group)
            {
                if (isObtainableInCombat(c) && (filter == null || filter.invoke(c)))
                {
                    cards.add(c);
                }
            }
        }

        return cards;
    }

    public static CardGroup getColorlessCardPool()
    {
        return getCardPool(null);
    }

    public static CardGroup getCardPool(AbstractCard.CardRarity rarity)
    {
        if (rarity == null)
        {
            return AbstractDungeon.colorlessCardPool;
        }

        switch (rarity)
        {
            case CURSE:
                return AbstractDungeon.curseCardPool;
            case COMMON:
                return AbstractDungeon.commonCardPool;
            case UNCOMMON:
                return AbstractDungeon.uncommonCardPool;
            case RARE:
                return AbstractDungeon.rareCardPool;
            default:
                return null;
        }
    }

    public static CardGroup getCardPoolSource(AbstractCard.CardRarity rarity)
    {
        if (rarity == null)
        {
            return AbstractDungeon.srcColorlessCardPool;
        }

        switch (rarity)
        {
            case CURSE:
                return AbstractDungeon.srcCurseCardPool;
            case COMMON:
                return AbstractDungeon.srcCommonCardPool;
            case UNCOMMON:
                return AbstractDungeon.srcUncommonCardPool;
            case RARE:
                return AbstractDungeon.srcRareCardPool;
            default:
                return null;
        }
    }

    public static ArrayList<CardGroup> getCardPools()
    {
        final ArrayList<CardGroup> result = new ArrayList<>();
        result.add(AbstractDungeon.colorlessCardPool);
        result.add(AbstractDungeon.commonCardPool);
        result.add(AbstractDungeon.uncommonCardPool);
        result.add(AbstractDungeon.rareCardPool);
        result.add(AbstractDungeon.curseCardPool);
        return result;
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<?> annotation)
    {
        final ArrayList<Class<?>> names = new ArrayList<>();
        for (AnnotationDB db : annotationDBMap.values())
        {
            Map<String, Set<String>> annotations = db.getAnnotationIndex();
            for (String key : annotations.getOrDefault(annotation.getName(), new HashSet<>()))
            {
                try
                {
                    Class<?> annotatedClass = Class.forName(key);
                    names.add(annotatedClass);
                }
                catch (Exception ignored)
                {

                }
            }
        }
        return names;
    }

    public static AbstractRoom getCurrentRoom()
    {
        return (AbstractDungeon.currMapNode == null) ? null : AbstractDungeon.currMapNode.getRoom();
    }

    public static ArrayList<AbstractPower> getDebuffs(AbstractCreature creature)
    {
        final ArrayList<AbstractPower> result = new ArrayList<>();
        for (AbstractPower power : creature.powers)
        {
            if (power.type == AbstractPower.PowerType.DEBUFF)
            {
                result.add(power);
            }
        }

        return result;
    }

    public static int getHP(AbstractCreature creature, boolean addTempHP, boolean addBlock)
    {
        return creature.currentHealth + (addTempHP ? TempHPField.tempHp.get(creature) : 0) + (addBlock ? creature.currentBlock : 0);
    }

    public static float getHealthPercentage(AbstractCreature creature)
    {
        return creature.currentHealth / (float) creature.maxHealth;
    }

    public static float getHealthPercentage(AbstractCreature creature, boolean addTempHP, boolean addBlock)
    {
        return getHP(creature, addTempHP, addBlock) / (float) getMaxHP(creature, addTempHP, addBlock);
    }

    public static PCLIntentInfo getIntent(AbstractMonster enemy)
    {
        return PCLIntentInfo.get(enemy);
    }

    public static ArrayList<PCLIntentInfo> getIntents()
    {
        final ArrayList<PCLIntentInfo> intents = new ArrayList<>();
        for (AbstractMonster m : getEnemies(true))
        {
            intents.add(getIntent(m));
        }

        return intents;
    }

    public static AbstractCard getLastCardPlayed(boolean currentTurn)
    {
        return getLastCardPlayed(currentTurn, 0);
    }

    public static AbstractCard getLastCardPlayed(boolean currentTurn, int offset)
    {
        final ArrayList<AbstractCard> cards = currentTurn
                ? AbstractDungeon.actionManager.cardsPlayedThisTurn
                : AbstractDungeon.actionManager.cardsPlayedThisCombat;
        return cards.size() > offset ? cards.get(cards.size() - 1 - offset) : null;
    }

    public static AbstractOrb getLastOrb(String orbID)
    {
        final ArrayList<AbstractOrb> orbs = player.orbs;
        for (int i = player.maxOrbs - 1; i >= 0; i--)
        {
            final AbstractOrb orb = orbs.get(i);
            if (orb != null && (orbID == null || orbID.equals(orb.ID)))
            {
                return orb;
            }
        }

        return null;
    }

    public static HashSet<AbstractCard> getMasterDeckCopies(String cardID)
    {
        final HashSet<AbstractCard> cards = new HashSet<>();
        for (AbstractCard c : player.masterDeck.group)
        {
            if (c.cardID.equals(cardID))
            {
                cards.add(c);
            }
        }

        return cards;
    }

    public static AbstractCard getMasterDeckInstance(UUID uuid)
    {
        for (AbstractCard c : player.masterDeck.group)
        {
            if (c.uuid == uuid)
            {
                return c;
            }
        }

        return null;
    }

    public static int getMaxHP(AbstractCreature creature, boolean addTempHP, boolean addBlock)
    {
        return creature.maxHealth + (addTempHP ? TempHPField.tempHp.get(creature) : 0) + (addBlock ? creature.currentBlock : 0);
    }

    public static ArrayList<AbstractCard> getObtainableCurses()
    {
        final ArrayList<AbstractCard> curses = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards())
        {
            if (c.type == AbstractCard.CardType.CURSE && c.rarity != AbstractCard.CardRarity.SPECIAL)
            {
                curses.add(c);
            }
        }

        return curses;
    }

    public static HashSet<AbstractCard> getOtherCardsInHand(AbstractCard card)
    {
        final HashSet<AbstractCard> cards = new HashSet<>();
        for (AbstractCard c : player.hand.group)
        {
            if (c != card)
            {
                cards.add(c);
            }
        }

        return cards;
    }

    public static PCLLoadout getPCLSeries(AbstractCard c)
    {
        if (c instanceof PCLCard)
        {
            return ((PCLCard) c).cardData.loadout;
        }
        return null;
    }

    public static <T extends AbstractPower> ArrayList<T> getPowers(String powerID)
    {
        return getPowers(GameUtilities.getAllCharacters(true), powerID);
    }

    public static <T extends AbstractPower> ArrayList<T> getPowers(List<AbstractCreature> creatures, String powerID)
    {
        final ArrayList<T> result = new ArrayList<>();
        for (AbstractCreature c : creatures)
        {
            final T t = getPower(c, powerID);
            if (t != null)
            {
                result.add(t);
            }
        }

        return result;
    }

    public static ArrayList<ArrayList<String>> getRelicPools()
    {
        final ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(AbstractDungeon.commonRelicPool);
        result.add(AbstractDungeon.uncommonRelicPool);
        result.add(AbstractDungeon.rareRelicPool);
        result.add(AbstractDungeon.bossRelicPool);
        result.add(AbstractDungeon.shopRelicPool);
        return result;
    }

    public static ArrayList<CardGroup> getSourceCardPools()
    {
        final ArrayList<CardGroup> result = new ArrayList<>();
        result.add(AbstractDungeon.srcColorlessCardPool);
        result.add(AbstractDungeon.srcCommonCardPool);
        result.add(AbstractDungeon.srcUncommonCardPool);
        result.add(AbstractDungeon.srcRareCardPool);
        result.add(AbstractDungeon.srcCurseCardPool);
        return result;
    }

    public static ArrayList<AbstractCard> getCardsInAnyPile()
    {
        return getCardsInPile(player.hand, player.discardPile, player.drawPile, player.exhaustPile);
    }

    public static ArrayList<AbstractCard> getCardsInGame()
    {
        return getCardsInPile(player.hand, player.discardPile, player.drawPile, player.exhaustPile, player.masterDeck, player.limbo, CombatManager.PURGED_CARDS);
    }

    public static ArrayList<AbstractCard> getCardsInPile(CardGroup... groups)
    {
        return EUIUtils.flattenList(EUIUtils.map(groups, group -> group.group));
    }

    public static PCLAffinity getCurrentAffinity()
    {
        return CombatManager.playerSystem.getCurrentAffinity();
    }

    public static int getCurrentMatchCombo()
    {
        return CombatManager.playerSystem.getActiveMeter().getCurrentMatchCombo();
    }

    public static int getDebuffsCount(AbstractCreature creature)
    {
        return (creature == null || creature.powers == null) ? 0 : getDebuffsCount(creature.powers);
    }

    public static int getDebuffsCount(ArrayList<AbstractPower> powers)
    {
        int result = 0;
        for (AbstractPower power : powers)
        {
            if (power.type == AbstractPower.PowerType.DEBUFF)
            {
                result += 1;
            }
        }

        return result;
    }

    public static int getDebuffsStacks(AbstractCreature creature)
    {
        return (creature == null || creature.powers == null) ? 0 : getDebuffsStacks(creature.powers);
    }

    public static int getDebuffsStacks(ArrayList<AbstractPower> powers)
    {
        int result = 0;
        for (AbstractPower power : powers)
        {
            if (power.type == AbstractPower.PowerType.DEBUFF)
            {
                result += power.amount;
            }
        }

        return result;
    }

    public static ArrayList<AbstractMonster> getEnemies(boolean aliveOnly)
    {
        final AbstractRoom room = getCurrentRoom();
        final ArrayList<AbstractMonster> monsters = new ArrayList<>();

        if (room != null && room.monsters != null)
        {
            if (!aliveOnly)
            {
                return room.monsters.monsters;
            }

            for (AbstractMonster m : room.monsters.monsters)
            {
                if (!isDeadOrEscaped(m))
                {
                    monsters.add(m);
                }
            }
        }

        return monsters;
    }

    public static AbstractOrb getFirstOrb(String orbID)
    {
        for (AbstractOrb orb : player.orbs)
        {
            if (orb != null && (orbID == null || orbID.equals(orb.ID)))
            {
                return orb;
            }
        }

        return null;
    }

    public static int getGold()
    {
        return player != null ? player.gold : 0;
    }

    public static int getGroupAffinityCount(PCLAffinity affinity, CardGroup group)
    {
        return getGroupPCLAffinities(group).getLevel(affinity, false);
    }

    public static PCLCardAffinities getGroupPCLAffinities(CardGroup group)
    {
        return player == null ? new PCLCardAffinities(null) : getPCLCardAffinities(group.group);
    }

    public static int getMaxAscensionLevel(AbstractPlayer player)
    {
        Prefs pref = player.getPrefs();
        if (pref != null)
        {
            return pref.getInteger("ASCENSION_LEVEL", 0);
        }
        return 0;
    }

    public static AbstractCard.CardRarity getNextRarity(AbstractCard.CardRarity rarity)
    {
        switch (rarity)
        {
            case BASIC:
            case CURSE:
                return AbstractCard.CardRarity.COMMON;
            case COMMON:
                return AbstractCard.CardRarity.UNCOMMON;
        }
        return AbstractCard.CardRarity.RARE;
    }

    public static int getOrbBaseEvokeAmount(AbstractOrb orb)
    {
        Object f = getOrbField(orb, "baseEvokeAmount");
        return (f != null ? (int) f : 0);
    }

    public static int getOrbBasePassiveAmount(AbstractOrb orb)
    {
        Object f = getOrbField(orb, "basePassiveAmount");
        return (f != null ? (int) f : 0);
    }

    public static int getOrbCount()
    {
        return player != null && player.orbs != null ? player.filledOrbCount() : 0;
    }

    public static int getOrbCount(PCLOrbHelper orb)
    {
        return getOrbCount(orb.ID);
    }

    public static int getOrbCount(String orbID)
    {
        int count = 0;
        if (player != null && player.orbs != null)
        {
            for (AbstractOrb orb : player.orbs)
            {
                if (orbID.equals(orb.ID))
                {
                    count += 1;
                }
            }
        }

        return count;
    }

    public static Object getOrbField(AbstractOrb orb, String field)
    {
        try
        {
            Field f = AbstractOrb.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(orb);
        }
        catch (NoSuchFieldException | IllegalAccessException var2)
        {
            EUIUtils.logWarning(orb, "Orb could not be modified");
            return null;
        }
    }

    public static int getPCLAffinityLevel(PCLAffinity affinity)
    {
        return CombatManager.playerSystem.getLevel(affinity);
    }

    public static PCLCardAffinities getPCLCardAffinities(Iterable<AbstractCard> cards)
    {
        final PCLCardAffinities affinities = new PCLCardAffinities(null);
        for (AbstractCard c : cards)
        {
            PCLCard card = EUIUtils.safeCast(c, PCLCard.class);
            if (card != null)
            {
                affinities.add(card.affinities, 1);
            }
        }

        return affinities;
    }

    public static PCLCardAffinities getPCLCardAffinities(AbstractCard card)
    {
        return card instanceof PCLCard ? ((PCLCard) card).affinities : null;
    }

    public static PCLCardAffinity getPCLCardAffinity(AbstractCard card, PCLAffinity affinity)
    {
        final PCLCardAffinities a = getPCLCardAffinities(card);
        return a != null ? a.get(affinity, false) : null;
    }

    public static int getPCLCardAffinityLevel(AbstractCard card, PCLAffinity affinity, boolean useStarLevel)
    {
        final PCLCardAffinities a = getPCLCardAffinities(card);
        return a != null ? a.getLevel(affinity, useStarLevel) : 0;
    }

    public static EUITooltip getPCLOrbTooltip(AbstractOrb orb)
    {
        // These two Orb tooltips use custom text
        if (Lightning.ORB_ID.equals(orb.ID))
        {
            return PGR.core.tooltips.lightning;
        }
        else if (Dark.ORB_ID.equals(orb.ID))
        {
            return PGR.core.tooltips.dark;
        }
        return EUITooltip.findByID(orb.ID.replace(PCLCoreResources.ID + ":", ""));
    }

    public static <T> T getPower(AbstractCreature owner, Class<T> powerType)
    {
        if (owner != null && owner.powers != null)
        {
            for (AbstractPower power : owner.powers)
            {
                if (powerType.isInstance(power))
                {
                    return powerType.cast(power);
                }
            }
        }

        return null;
    }

    public static <T extends AbstractPower> T getPower(AbstractCreature creature, String powerID)
    {
        if (creature != null && creature.powers != null)
        {
            for (AbstractPower p : creature.powers)
            {
                if (p != null && powerID.equals(p.ID))
                {
                    try
                    {
                        return (T) p;
                    }
                    catch (ClassCastException e)
                    {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return null;
    }

    public static int getPowerAmount(PCLAffinity affinity)
    {
        return CombatManager.playerSystem.getPowerAmount(affinity);
    }

    public static int getPowerAmount(String powerID)
    {
        return getPowerAmount(AbstractDungeon.player, powerID);
    }

    public static int getPowerAmount(AbstractCreature owner, String powerID)
    {
        AbstractPower power = getPower(owner, powerID);
        return power != null ? power.amount : 0;
    }

    // Create a random card that matches the given parameters. Note that these random card methods poll from the in-combat card pool, so healing cards are already filtered out
    public static AbstractCard getRandomCard(List<AbstractCard.CardRarity> rarity, List<AbstractCard.CardType> type, List<PCLAffinity> affinity)
    {
        refreshCardLists();
        setupCharacterCardPool();
        return getRandomElement(EUIUtils.filter(characterCardPool,
                        c -> ((rarity.isEmpty() || EUIUtils.any(rarity, r -> c.rarity == r)) &&
                                (type.isEmpty() || EUIUtils.any(type, t -> c.type == t)) &&
                                (affinity.isEmpty() || EUIUtils.any(affinity, a -> GameUtilities.hasAffinity(c, a, true)))
                        )
                )
        );
    }

    public static AbstractCard getRandomCard()
    {
        refreshCardLists();
        setupCharacterCardPool();
        return characterCardPool.retrieve(getRNG());
    }

    public static AbstractCard getRandomCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type)
    {
        return getRandomCard(rarity, type, true, false);
    }

    public static AbstractCard getRandomCard(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean useRng, boolean allowOtherRarities)
    {
        CardGroup pool = getCardPool(rarity);
        if (pool != null)
        {
            AbstractCard c = type != null ? pool.getRandomCard(type, useRng) : pool.getRandomCard(useRng);
            if (!allowOtherRarities || c != null)
            {
                return c;
            }
            // Try to get a card from a different rarity pool. If you exhaust all the pools, fall back on the colorless pool
            // Note that the basic and special rarities have no pools so we ignore them
            if (rarity != null)
            {
                EUIUtils.logWarning(null, "No cards found for Rarity " + rarity + ", Type " + type);
                int nextRarityIndex = Math.max(0, rarity.ordinal() - 1);
                return getRandomCard(nextRarityIndex > 1 ? poolOrdering[nextRarityIndex] : null, type, useRng, allowOtherRarities);
            }
        }
        return null;
    }

    public static Random getRNG()
    {
        if (PCLCard.rng == null)
        {
            EUIUtils.logInfo(GameUtilities.class, "PCLCard.rng was null");
            return new Random();
        }

        return PCLCard.rng;
    }

    public static AbstractCreature getRandomCharacter(boolean aliveOnly)
    {
        return getRandomElement(getAllCharacters(aliveOnly), getRNG());
    }

    public static <T> T getRandomElement(List<T> list)
    {
        return getRandomElement(list, getRNG());
    }

    public static <T> T getRandomElement(T[] arr)
    {
        return getRandomElement(arr, getRNG());
    }

    public static <T> T getRandomElement(T[] arr, com.megacrit.cardcrawl.random.Random rng)
    {
        int size = arr.length;
        return (size > 0) ? arr[rng.random(arr.length - 1)] : null;
    }

    public static <T> T getRandomElement(List<T> list, Random rng)
    {
        int size = list.size();
        return (size > 0) ? list.get(rng.random(list.size() - 1)) : null;
    }

    public static AbstractMonster getRandomEnemy(boolean aliveOnly)
    {
        return getRandomElement(getEnemies(aliveOnly), getRNG());
    }

    public static PCLCardAlly getRandomSummon(Boolean isAlive)
    {
        return getRandomElement(getSummons(isAlive), getRNG());
    }

    public static <T extends AbstractRelic> T getRelic(String relicID)
    {
        if (player == null)
        {
            return null;
        }

        for (AbstractRelic relic : player.relics)
        {
            if (relic != null && relicID.equals(relic.relicId))
            {
                try
                {
                    return (T) relic;
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    public static <T> T getRelic(Class<T> relicType)
    {
        for (AbstractRelic relic : player.relics)
        {
            if (relicType.isInstance(relic))
            {
                return relicType.cast(relic);
            }
        }

        return null;
    }

    public static ArrayList<String> getRelicPool(AbstractRelic.RelicTier tier)
    {
        switch (tier)
        {
            case COMMON:
                return AbstractDungeon.commonRelicPool;
            case UNCOMMON:
                return AbstractDungeon.uncommonRelicPool;
            case RARE:
                return AbstractDungeon.rareRelicPool;
            case BOSS:
                return AbstractDungeon.bossRelicPool;
            case SHOP:
                return AbstractDungeon.shopRelicPool;
            default:
                return null;
        }
    }

    public static ArrayList<PCLCardAlly> getSummons(Boolean isAlive)
    {
        if (isAlive == null)
        {
            return CombatManager.summons.summons;
        }

        final ArrayList<PCLCardAlly> monsters = new ArrayList<>();
        for (PCLCardAlly m : CombatManager.summons.summons)
        {
            if (!m.hasCard() ^ isAlive)
            {
                monsters.add(m);
            }
        }

        return monsters;
    }

    public static List<AbstractCard.CardRarity> getStandardRarities()
    {
        return EUIUtils.list(
                AbstractCard.CardRarity.BASIC,
                AbstractCard.CardRarity.COMMON,
                AbstractCard.CardRarity.UNCOMMON,
                AbstractCard.CardRarity.RARE,
                AbstractCard.CardRarity.SPECIAL
        );
    }

    public static int getTempHP(AbstractCreature creature)
    {
        return creature != null ? TempHPField.tempHp.get(creature) : 0;
    }

    public static int getTempHP()
    {
        return getTempHP(player);
    }

    public static int getTimesPlayedThisTurn(AbstractCard card)
    {
        int result = 0;
        for (AbstractCard c : actionManager.cardsPlayedThisTurn)
        {
            if (c.uuid.equals(card.uuid))
            {
                result += 1;
            }
        }

        return result;
    }

    public static int getTotalCardsInPlay()
    {
        return AbstractDungeon.colorlessCardPool.size()
                + AbstractDungeon.commonCardPool.size()
                + AbstractDungeon.uncommonCardPool.size()
                + AbstractDungeon.rareCardPool.size()
                + AbstractDungeon.curseCardPool.size();
    }

    public static int getTotalCardsPlayed(AbstractCard ignoreLast, boolean currentTurn)
    {
        final ArrayList<AbstractCard> cards = currentTurn
                ? AbstractDungeon.actionManager.cardsPlayedThisTurn
                : AbstractDungeon.actionManager.cardsPlayedThisCombat;
        return (cards.size() > 0 && (cards.get(cards.size() - 1) == ignoreLast)) ? (cards.size() - 1) : cards.size();
    }

    public static EUITooltip getTooltipForType(AbstractCard.CardType type)
    {
        if (type == PCLEnum.CardType.SUMMON)
        {
            return PGR.core.tooltips.summon;
        }
        switch (type)
        {
            case ATTACK:
                return PGR.core.tooltips.attack;
            case SKILL:
                return PGR.core.tooltips.skill;
            case POWER:
                return PGR.core.tooltips.power;
            case CURSE:
                return PGR.core.tooltips.curse;
            case STATUS:
                return PGR.core.tooltips.status;
        }
        return null;
    }

    public static ArrayList<AbstractOrb> getUniqueOrbs(int count)
    {
        final ArrayList<AbstractOrb> orbs = new ArrayList<>();
        for (AbstractOrb orb : player.orbs)
        {
            if (!isValidOrb(orb))
            {
                continue;
            }

            boolean skip = false;
            for (AbstractOrb o : orbs)
            {
                if (o.ID.equals(orb.ID))
                {
                    skip = true;
                    break;
                }
            }

            if (!skip)
            {
                orbs.add(orb);

                if (orbs.size() >= count)
                {
                    return orbs;
                }
            }
        }

        return orbs;
    }

    public static int getUniqueOrbsCount()
    {
        final HashSet<String> orbs = new HashSet<>();
        for (AbstractOrb orb : player.orbs)
        {
            if (isValidOrb(orb))
            {
                orbs.add(orb.ID);
            }
        }

        return orbs.size();
    }

    public static int getXCostEnergy(AbstractCard card)
    {
        return getXCostEnergy(card, false);
    }

    public static int getXCostEnergy(AbstractCard card, boolean forceCountAll)
    {
        int amount = !forceCountAll && card != null && card.energyOnUse != -1 ? card.energyOnUse : EnergyPanel.getCurrentEnergy();

        return CombatManager.onTryUseXCost(amount, card);
    }

    public static boolean hasAffinity(AbstractCard card, PCLAffinity affinity)
    {
        return hasAffinity(card, affinity, true);
    }

    public static boolean hasAffinity(AbstractCard card, PCLAffinity affinity, boolean useStar)
    {
        return getPCLCardAffinityLevel(card, affinity, useStar) > 0;
    }

    public static boolean hasAllAffinity(AbstractCard card, Collection<PCLAffinity> affinities)
    {
        return EUIUtils.all(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAllAffinity(AbstractCard card, PCLAffinity... affinities)
    {
        return EUIUtils.all(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAnyAffinity(AbstractCard card, Collection<PCLAffinity> affinities)
    {
        return EUIUtils.any(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAnyAffinity(AbstractCard card, PCLAffinity... affinities)
    {
        return EUIUtils.any(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasArtifact(AbstractCreature creature)
    {
        return creature.hasPower(ArtifactPower.POWER_ID);
    }

    public static boolean hasDarkAffinity(AbstractCard card)
    {
        return getPCLCardAffinityLevel(card, PCLAffinity.Purple, true) > 0;
    }

    public static boolean hasEncounteredEvent(String eventID)
    {
        return PGR.core.dungeon.getMapData(eventID) != null;
    }

    public static boolean hasLightAffinity(AbstractCard card)
    {
        return getPCLCardAffinityLevel(card, PCLAffinity.Yellow, true) > 0;
    }

    public static boolean hasOrb(String orbID)
    {
        return getOrbCount(orbID) > 0;
    }

    public static boolean hasRelic(String relicID)
    {
        return player != null && player.hasRelic(relicID);
    }

    public static boolean hasRelicEffect(String relicID)
    {
        return hasRelic(relicID)
                || CombatManager.getCombatData(relicID, false)
                || CombatManager.getTurnData(relicID, false);
    }

    public static void highlightMatchingCards(PCLAffinity affinity)
    {
        for (AbstractCard c : AbstractDungeon.player.hand.group)
        {
            final PCLCard temp = EUIUtils.safeCast(c, PCLCard.class);
            if (temp == null || (temp.affinities.getLevel(affinity) == 0))
            {
                c.transparency = 0.35f;
            }
        }
    }

    public static boolean inBattle()
    {
        return inBattle(false);
    }

    public static boolean inBattle(boolean forceRefresh)
    {
        if (forceRefresh)
        {
            CombatManager.refresh();
        }

        return CombatManager.battleID != null;
    }

    public static boolean inBossRoom()
    {
        return getCurrentRoom() instanceof MonsterRoomBoss;
    }

    public static boolean inEliteOrBossRoom()
    {
        final AbstractRoom room = getCurrentRoom();
        return room instanceof MonsterRoomBoss || (room != null && room.eliteTrigger);
    }

    public static boolean inEliteRoom()
    {
        final AbstractRoom room = getCurrentRoom();
        return (room != null) && room.eliteTrigger;
    }

    public static boolean inGame()
    {
        return CardCrawlGame.GameMode.GAMEPLAY.equals(CardCrawlGame.mode);
    }

    public static boolean inStance(PCLStanceHelper stance)
    {
        return player != null && player.stance != null && player.stance.ID.equals(stance.ID);
    }

    public static boolean inStance(String stanceID)
    {
        return player != null && player.stance != null && player.stance.ID.equals(stanceID);
    }

    public static void increaseBlock(AbstractCard card, int amount, boolean temporary)
    {
        modifyBlock(card, card.baseBlock + amount, temporary);
    }

    public static void increaseDamage(AbstractCard card, int amount, boolean temporary)
    {
        modifyDamage(card, card.baseDamage + amount, temporary);
    }

    public static void increaseHandSizePermanently()
    {
        for (AbstractBlight blight : player.blights)
        {
            if (blight instanceof UpgradedHand)
            {
                ((UpgradedHand) blight).addAmount(1);
                return;
            }
        }

        obtainBlight(player.hb.cX, player.hb.cY, new UpgradedHand());
    }

    public static void increaseHitCount(PCLCard card, int amount, boolean temporary)
    {
        modifyHitCount(card, card.baseHitCount + amount, temporary);
    }

    public static void increaseMagicNumber(AbstractCard card, int amount, boolean temporary)
    {
        modifyMagicNumber(card, card.baseMagicNumber + amount, temporary);
    }

    public static void increaseSecondaryValue(PCLCard card, int amount, boolean temporary)
    {
        modifySecondaryValue(card, card.baseHeal + amount, temporary);
    }

    public static boolean isActingColor(AbstractCard.CardColor co)
    {
        return getActingColor() == co;
    }

    public static boolean isAffinityPowerActive(PCLAffinity affinity)
    {
        return CombatManager.playerSystem.isPowerActive(affinity);
    }

    public static boolean isAttacking(AbstractCreature monster)
    {
        return monster instanceof AbstractMonster && isAttacking(((AbstractMonster) monster).intent);
    }

    public static boolean isAttacking(AbstractMonster monster)
    {
        return isAttacking(monster.intent);
    }

    public static boolean isAttacking(AbstractMonster.Intent intent)
    {
        return (intent == AbstractMonster.Intent.ATTACK_DEBUFF || intent == AbstractMonster.Intent.ATTACK_BUFF ||
                intent == AbstractMonster.Intent.ATTACK_DEFEND || intent == AbstractMonster.Intent.ATTACK);
    }

    // Both colorless and curse are character-independent
    public static boolean isColorlessCardColor(AbstractCard.CardColor cardColor)
    {
        return cardColor == AbstractCard.CardColor.COLORLESS || cardColor == AbstractCard.CardColor.CURSE;
    }

    public static boolean isCommonBuff(AbstractPower power)
    {
        PCLPowerHelper helper = PCLPowerHelper.get(power.ID);
        return helper != null && helper.isCommon && !helper.isDebuff;
    }

    public static boolean isCommonDebuff(AbstractPower power)
    {
        PCLPowerHelper helper = PCLPowerHelper.get(power.ID);
        return helper != null && helper.isCommon && helper.isDebuff;
    }

    public static boolean isCommonPower(AbstractPower power)
    {
        PCLPowerHelper helper = PCLPowerHelper.get(power.ID);
        return helper != null && helper.isCommon;
    }

    public static boolean isDeadOrEscaped(AbstractCreature target)
    {
        return target == null || target.isDeadOrEscaped() || target.currentHealth <= 0;
    }

    public static boolean isDebuff(AbstractPower power)
    {
        return power != null && power.type == AbstractPower.PowerType.DEBUFF;
    }

    public static boolean isDebuffing(AbstractMonster.Intent intent)
    {
        return (intent == AbstractMonster.Intent.ATTACK_DEBUFF || intent == AbstractMonster.Intent.DEBUFF ||
                intent == AbstractMonster.Intent.DEFEND_DEBUFF || intent == AbstractMonster.Intent.STRONG_DEBUFF);
    }

    public static boolean isDefending(AbstractMonster.Intent intent)
    {
        return (intent == AbstractMonster.Intent.DEFEND_DEBUFF || intent == AbstractMonster.Intent.DEFEND_BUFF ||
                intent == AbstractMonster.Intent.ATTACK_DEFEND || intent == AbstractMonster.Intent.DEFEND);
    }

    public static boolean isFatal(AbstractCreature enemy, boolean includeMinions)
    {
        return (enemy.isDead || enemy.isDying || enemy.currentHealth <= 0)
                && !enemy.hasPower(RegrowPower.POWER_ID)
                && (includeMinions || !enemy.hasPower(MinionPower.POWER_ID));
    }

    public static boolean isHindrance(AbstractCard card)
    {
        return card.type == AbstractCard.CardType.CURSE || card.type == AbstractCard.CardType.STATUS;
    }

    public static boolean isMonster(AbstractCreature c)
    {
        return c != null && !c.isPlayer;
    }

    public static boolean isObtainableInCombat(AbstractCard c)
    {
        return !c.hasTag(AbstractCard.CardTags.HEALING) && c.rarity != AbstractCard.CardRarity.SPECIAL && !PCLCardTag.Fleeting.has(c) && !c.isLocked;
    }

    public static boolean isPlayable(AbstractCard card)
    {
        final boolean temp = card.freeToPlayOnce;
        card.freeToPlayOnce = true;
        boolean canUse = card.canUse(player, null);

        if (!canUse && (card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY))
        {
            final ArrayList<AbstractMonster> enemies = getEnemies(true);
            for (AbstractMonster m : enemies)
            {
                if (card.canUse(player, m))
                {
                    canUse = true;
                    break;
                }
            }
        }

        card.freeToPlayOnce = temp;
        return canUse;
    }

    public static boolean isPlayable(AbstractCard card, AbstractMonster target)
    {
        final boolean temp = card.freeToPlayOnce;
        card.freeToPlayOnce = true;
        boolean canUse = card.canUse(player, target);
        card.freeToPlayOnce = temp;

        return canUse;
    }

    public static boolean isPlayer(AbstractCreature c)
    {
        return c != null && c.isPlayer;
    }

    public static AbstractPlayer.PlayerClass getPlayerClass()
    {
        return inGame() && player != null ? player.chosenClass : null;
    }

    public static boolean isPlayerClass(AbstractPlayer.PlayerClass playerClass)
    {
        return player != null && player.chosenClass == playerClass;
    }

    public static boolean isPlayerTurn(boolean beforeEndTurnEvents)
    {
        boolean result = !AbstractDungeon.actionManager.turnHasEnded;
        if (beforeEndTurnEvents)
        {
            result &= CombatManager.isPlayerTurn && !player.isEndingTurn;
        }

        return result;
    }

    public static boolean canAcceptInput(boolean canHoverCard)
    {
        return isPlayerTurn(true) && actionManager.phase == GameActionManager.Phase.WAITING_ON_USER
                && !player.isDraggingCard && !player.inSingleTargetMode && (canHoverCard || player.hoveredCard == null)
                && AbstractDungeon.actionManager.cardQueue.isEmpty() && AbstractDungeon.actionManager.actions.isEmpty()
                && !DevConsole.visible && !AbstractDungeon.isScreenUp && !CardCrawlGame.isPopupOpen;
    }

    public static boolean isPCLActingCardColor(AbstractCard card)
    {
        return isPCLCardColor(getActingCardColor(card));
    }

    public static boolean isPCLEYBActingColor()
    {
        return isPCLCardColor(getActingColor());
    }

    // PCL check that includes colorless/curse
    public static boolean isPCLCardColor(AbstractCard.CardColor cardColor)
    {
        return isPCLOnlyCardColor(cardColor) || isColorlessCardColor(cardColor);
    }

    // PCL check that excludes colorless/curse
    public static boolean isPCLOnlyCardColor(AbstractCard.CardColor cardColor)
    {
        return EUIUtils.any(PGR.getRegisteredResources(), r -> r.cardColor == cardColor);
    }

    public static boolean isPCLPlayerClass()
    {
        return AbstractDungeon.player != null && isPCLPlayerClass(AbstractDungeon.player.chosenClass);
    }

    public static boolean isPCLPlayerClass(AbstractPlayer.PlayerClass playerClass)
    {
        return EUIUtils.any(PGR.getRegisteredResources(), r -> r.playerClass == playerClass);
    }

    public static boolean isStarter(AbstractCard card)
    {
        final ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        return played == null || played.isEmpty() || (played.size() == 1 && played.get(0) == card);
    }

    public static boolean isTopPanelVisible()
    {
        return GameUtilities.inGame() && AbstractDungeon.topPanel != null && !Settings.hideTopBar;
    }

    public static boolean isTurnBasedPower(AbstractPower power)
    {
        return ReflectionHacks.getPrivate(power, AbstractPower.class, "isTurnBased");
    }

    public static boolean isUnplayableThisTurn(AbstractCard card)
    {
        return CombatManager.unplayableCards().contains(card.uuid);
    }

    public static boolean isValidOrb(AbstractOrb orb)
    {
        return orb != null && !(orb instanceof EmptyOrbSlot);
    }

    public static boolean isValidTarget(AbstractCreature target)
    {
        return target != null && !isDeadOrEscaped(target);
    }

    public static CardGroup makeCardGroup(Collection<AbstractCard> cards)
    {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        group.group.addAll(cards);
        return group;
    }

    public static CardGroup makeCardGroupRandomized(Collection<AbstractCard> source, int limit, boolean makeCopy)
    {
        final RandomizedList<AbstractCard> choices = new RandomizedList<>(source);
        CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        while (choice.size() < limit)
        {
            AbstractCard c = choices.retrieve(PCLCard.rng);
            if (c != null)
            {
                choice.addToBottom(makeCopy ? c.makeCopy() : c);
            }
        }
        return choice;
    }

    public static CardStrings mockCardStrings()
    {
        CardStrings s = new CardStrings();
        s.NAME = "";
        s.DESCRIPTION = "";
        s.EXTENDED_DESCRIPTION = new String[]{};
        return s;
    }

    public static void modifyAffinityLevel(AbstractCard card, PCLAffinity affinity, int level, boolean relative)
    {
        PCLCard pC = EUIUtils.safeCast(card, PCLCard.class);
        if (pC == null)
        {
            return;
        }

        if (relative)
        {
            pC.affinities.add(affinity, level);
        }
        else
        {
            pC.affinities.set(affinity, level);
        }
    }

    public static void modifyBlock(AbstractCard card, int amount, boolean temporary)
    {
        if (card instanceof PCLCard)
        {
            if (temporary)
            {
                ((PCLCard) card).updateBlock(amount);
            }
            else
            {
                ((PCLCard) card).updateMaxBlock(amount);
            }
        }
        else
        {
            card.block = Math.max(0, amount);
            if (!temporary)
            {
                card.baseBlock = card.block;
            }
            card.isBlockModified = (card.block != card.baseBlock);
        }
    }

    public static void modifyCardBaseCost(PCLCard card, int amount, boolean relative)
    {
        if (relative)
        {
            card.costForTurn = Math.max(0, card.costForTurn + amount);
            card.cost = Math.max(0, card.cost + amount);
        }
        else
        {
            card.costForTurn = amount + (card.costForTurn - card.cost);
            card.cost = amount;
        }

        if (card.cost != card.cardData.getCost(card.getForm()))
        {
            card.isCostModified = true;
        }
    }

    public static int modifyCardDrawPerTurn(int amount, int minimumCardDraw)
    {
        final int newAmount = player.gameHandSize + amount;
        if (newAmount < minimumCardDraw)
        {
            amount += (minimumCardDraw - newAmount);
        }

        player.gameHandSize += amount;
        return amount;
    }

    public static void modifyCostForCombat(AbstractCard card, int amount, boolean relative)
    {
        final int previousCost = card.cost;
        if (relative)
        {
            card.costForTurn = Math.max(0, card.costForTurn + amount);
            card.cost = Math.max(0, card.cost + amount);
        }
        else
        {
            card.costForTurn = amount + (card.costForTurn - card.cost);
            card.cost = amount;
        }

        if (card.cost != previousCost)
        {
            card.isCostModified = true;
        }
    }

    public static void modifyCostForTurn(AbstractCard card, int amount, boolean relative)
    {
        card.costForTurn = relative ? Math.max(0, card.costForTurn + amount) : amount;
        card.isCostModifiedForTurn = (card.cost != card.costForTurn);
    }

    public static void modifyDamage(AbstractCard card, int amount, boolean temporary)
    {
        if (card instanceof PCLCard)
        {
            if (temporary)
            {
                ((PCLCard) card).updateDamage(amount);
            }
            else
            {
                ((PCLCard) card).updateMaxDamage(amount);
            }
        }
        else
        {
            card.damage = Math.max(0, amount);
            if (!temporary)
            {
                card.baseDamage = card.damage;
            }
            card.isDamageModified = (card.damage != card.baseDamage);
        }
    }

    public static int modifyEnergyGainPerTurn(int amount, int minimumEnergy)
    {
        final int newAmount = player.energy.energy + amount;
        if (newAmount < minimumEnergy)
        {
            amount += (minimumEnergy - newAmount);
        }

        player.energy.energy += amount;
        return amount;
    }

    public static void modifyHitCount(PCLCard card, int amount, boolean temporary)
    {
        card.hitCount = amount;
        if (!temporary)
        {
            card.baseHitCount = card.hitCount;
        }
        card.isHitCountModified = (card.hitCount != card.baseHitCount);
    }

    public static void modifyMagicNumber(AbstractCard card, int amount, boolean temporary)
    {
        card.magicNumber = amount;
        if (!temporary)
        {
            card.baseMagicNumber = card.magicNumber;
        }
        card.isMagicNumberModified = (card.magicNumber != card.baseMagicNumber);
    }

    public static void modifyOrbBaseEvokeAmount(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb)
    {
        if (canModifyNonFocusOrb || (canOrbApplyFocus(orb) && canOrbApplyFocusToEvoke(orb)))
        {
            modifyOrbField(orb, "baseEvokeAmount", amount, isRelative);
        }

    }

    public static void modifyOrbBaseFocus(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb)
    {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb))
        {
            if (canModifyNonFocusOrb || canOrbApplyFocusToEvoke(orb))
            {
                modifyOrbField(orb, "baseEvokeAmount", amount, isRelative);
            }
            modifyOrbField(orb, "basePassiveAmount", amount, isRelative);
        }
    }

    public static void modifyOrbBasePassiveAmount(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb)
    {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb))
        {
            modifyOrbField(orb, "basePassiveAmount", amount, isRelative);
        }
    }

    public static void modifyOrbField(AbstractOrb orb, String field, int amount, boolean isRelative)
    {
        try
        {
            Field f = AbstractOrb.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(orb, isRelative ? amount + (int) f.get(orb) : amount);
            orb.applyFocus();
            orb.updateDescription();
        }
        catch (Exception e)
        {
            EUIUtils.logWarning(orb, "Orb could not be modified: " + e.getLocalizedMessage());
        }
    }

    public static void modifyOrbTemporaryFocus(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb)
    {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb))
        {
            orb.passiveAmount = isRelative ? orb.passiveAmount + amount : amount;
            if (canModifyNonFocusOrb || canOrbApplyFocusToEvoke(orb))
            {
                orb.evokeAmount = isRelative ? orb.evokeAmount + amount : amount;
            }
        }
    }

    public static void modifyRightCount(PCLCard card, int amount, boolean temporary)
    {
        card.rightCount = amount;
        if (!temporary)
        {
            card.baseRightCount = card.rightCount;
        }
        card.isRightCountModified = (card.rightCount != card.baseRightCount);
    }

    public static void modifySecondaryValue(PCLCard card, int amount, boolean temporary)
    {
        card.heal = amount;
        if (!temporary)
        {
            card.baseHeal = card.heal;
        }
        card.isHealModified = (card.heal != card.baseHeal);
    }

    public static void modifySecondaryValueRelative(PCLCard card, int amount, boolean temporary)
    {
        if (!temporary)
        {
            card.heal += amount;
            card.baseHeal += amount;
        }
        else
        {
            card.heal = Math.min(card.baseHeal, card.heal + amount);
        }
        card.isHealModified = (card.heal != card.baseHeal);
    }

    public static void modifyTag(AbstractCard card, PCLCardTag tag, int value)
    {
        modifyTag(card, tag, value, false);
    }

    public static void modifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative)
    {
        if (tag != null)
        {
            int targetValue = tag.add(card, value);
            PCLCard pCard = EUIUtils.safeCast(card, PCLCard.class);
            if (pCard != null && pCard.auxiliaryData != null)
            {
                if (targetValue != 0)
                {
                    pCard.auxiliaryData.addedTags.add(tag);
                }
                else
                {
                    pCard.auxiliaryData.removedTags.add(tag);
                }
            }
            CombatManager.onTagChanged(card, tag, value);
        }
    }

    public static void obtainBlight(float cX, float cY, AbstractBlight blight)
    {
        AbstractRoom room = getCurrentRoom();
        if (room != null)
        {
            room.spawnBlightAndObtain(cX, cY, blight);
        }
    }

    public static void obtainBlightWithoutEffect(AbstractBlight blight)
    {
        blight.instantObtain(player, player.blights.size(), false);
    }

    public static void obtainRelic(float cX, float cY, AbstractRelic relic)
    {
        AbstractRoom room = getCurrentRoom();
        if (room != null)
        {
            room.spawnRelicAndObtain(cX, cY, relic);
        }
    }

    public static void obtainRelicFromEvent(AbstractRelic relic)
    {
        relic.instantObtain();
        CardCrawlGame.metricData.addRelicObtainData(relic);
    }

    public static void playManually(AbstractCard card, AbstractMonster m)
    {
        card.applyPowers();
        card.calculateCardDamage(m);
        card.use(player, m);
        actionManager.cardsPlayedThisTurn.add(card);
        actionManager.cardsPlayedThisCombat.add(card);
        CombatManager.playerSystem.setLastCardPlayed(card);
    }

    protected static void refreshCardLists()
    {
        if (lastPlayerClass == null || (AbstractDungeon.player != null && lastPlayerClass != AbstractDungeon.player.chosenClass))
        {
            lastPlayerClass = AbstractDungeon.player != null ? AbstractDungeon.player.chosenClass : null;
            fullCardPool.clear();
            characterCardPool.clear();
        }
    }

    public static void refreshOrbs()
    {
        for (AbstractOrb orb : player.orbs)
        {
            if (orb != null && !(orb instanceof EmptyOrbSlot))
            {
                orb.applyFocus();
            }
        }
    }

    public static void removeBlock(AbstractCard card)
    {
        card.baseBlock = card.block = 0;
        card.isBlockModified = false;
    }

    public static boolean requiresTarget(AbstractCard card)
    {
        return card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY || card.type == PCLEnum.CardType.SUMMON;
    }

    public static void resetAffinityLevels(AbstractCard card)
    {
        PCLCard pC = EUIUtils.safeCast(card, PCLCard.class);
        if (pC == null)
        {
            return;
        }

        pC.affinities.clearLevelsOnly();
    }

    public static void resetVisualProperties(AbstractCard card)
    {
        card.untip();
        card.unhover();
        card.unfadeOut();
        card.targetDrawScale = card.drawScale = 0.75f;
        card.transparency = card.targetTransparency = 1;
        card.angle = card.targetAngle = 0;
    }

    public static boolean retain(AbstractCard card)
    {
        if (canRetain(card))
        {
            PCLCardTag.Retain.add(card, 1);
            CombatManager.onTagChanged(card, PCLCardTag.Retain, 1);
            CombatManager.onCardRetain(card);
            return true;
        }

        return false;
    }

    public static float scale(float value)
    {
        return Settings.scale * value;
    }

    public static float screenH(float value)
    {
        return Settings.HEIGHT * value;
    }

    public static float screenW(float value)
    {
        return Settings.WIDTH * value;
    }

    public static void setCardTag(AbstractCard card, AbstractCard.CardTags tag, boolean value)
    {
        if (value)
        {
            if (!card.tags.contains(tag))
            {
                card.tags.add(tag);
            }
        }
        else
        {
            card.tags.remove(tag);
        }
    }

    public static void setCreatureAnimation(AbstractCreature creature, String id)
    {
        if (creature instanceof PCLCharacter)
        {
            ((PCLCharacter) creature).setCreature(id);
        }
        // TODO implement a substitute creature for this because hit animations can cause crashes
/*        else
        {
            TupleT2<String, String> animation = GetAnimationForID(id);
            if (animation.V1 != null && animation.V2 != null)
            {
                ClassUtils.Invoke(creature, "loadAnimation", animation.V1, animation.V2, 1f);
            }
        }*/
    }

    public static void setFtueTip(String name, String message, FtueTip.TipType type)
    {
        AbstractDungeon.ftue = new FtueTip(name, "~" + message, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, type);
    }

    public static void setTopPanelVisible(boolean visible)
    {
        Settings.hideTopBar = !visible;
        Settings.hideRelics = !visible;

        if (AbstractDungeon.topPanel != null)
        {
            AbstractDungeon.topPanel.unhoverHitboxes();
            //AbstractDungeon.topPanel.potionUi.isHidden = !visible;
        }
    }

    public static void setUnplayableThisTurn(AbstractCard card)
    {
        CombatManager.unplayableCards().add(card.uuid);
        CombatManager.onTagChanged(card, PCLCardTag.Unplayable, 1);
    }

    // Sets up the available pool of generated cards in combat (so no curses/statuses, basics, or unobtainable cards)
    protected static void setupCharacterCardPool()
    {
        if (characterCardPool.size() == 0)
        {
            for (AbstractCard c : GameUtilities.getAvailableCards())
            {
                if (!GameUtilities.isHindrance(c) && GameUtilities.isObtainableInCombat(c) && c.rarity != AbstractCard.CardRarity.BASIC)
                {
                    characterCardPool.add(c);
                }
            }
        }
    }

    public static String toInternalAtlasPath(String path)
    {
        return PORTRAIT_PATH + path + ".png";
    }

    public static String toInternalAtlasBetaPath(String path)
    {
        return BETA_PATH + path + ".png";
    }

    public static EUITooltip tooltipForType(AbstractCard.CardType type)
    {
        switch (type)
        {
            case ATTACK:
                return PGR.core.tooltips.attack;
            case CURSE:
                return PGR.core.tooltips.curse;
            case POWER:
                return PGR.core.tooltips.power;
            case SKILL:
                return PGR.core.tooltips.skill;
            case STATUS:
                return PGR.core.tooltips.status;
            default:
                return PGR.core.tooltips.summon;
        }
    }

    public static Vector2 tryGetPosition(CardGroup group, AbstractCard card)
    {
        if (group != null)
        {
            if (group.type == CardGroup.CardGroupType.DRAW_PILE)
            {
                return new Vector2(CardGroup.DRAW_PILE_X, CardGroup.DRAW_PILE_Y);
            }
            else if (group.type == CardGroup.CardGroupType.DISCARD_PILE)
            {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y);
            }
            else if (group.type == CardGroup.CardGroupType.EXHAUST_PILE)
            {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y + (Settings.scale * 30f));
            }
            else if (group == CombatManager.PURGED_CARDS)
            {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y + (Settings.scale * 100f));
            }
        }

        return card == null ? null : new Vector2(card.current_x, card.current_y);
    }

    public static boolean trySetPosition(CardGroup group, AbstractCard card)
    {
        Vector2 pos = tryGetPosition(group, null);
        if (pos == null)
        {
            return false;
        }

        card.current_x = pos.x;
        card.current_y = pos.y;

        return true;
    }

    public static void unlockAllKeys()
    {
        if (!Settings.isFinalActAvailable)
        {
            Settings.isFinalActAvailable = true;
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.IRONCLAD.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.THE_SILENT.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.DEFECT.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.WATCHER.name() + "_WIN", true);

            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.RUBY_PLUS_KEY))
            {
                UnlockTracker.unlockAchievement(AchievementGrid.RUBY_PLUS_KEY);
            }
            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.EMERALD_PLUS_KEY))
            {
                UnlockTracker.unlockAchievement(AchievementGrid.EMERALD_PLUS_KEY);
            }
            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.SAPPHIRE_PLUS_KEY))
            {
                UnlockTracker.unlockAchievement(AchievementGrid.SAPPHIRE_PLUS_KEY);
            }
        }
    }

    public static void unlockAscension(Prefs playerPrefs, int ascension)
    {
        if (playerPrefs.getInteger("ASCENSION_LEVEL", 0) < ascension)
        {
            playerPrefs.putInteger("ASCENSION_LEVEL", ascension);
            playerPrefs.putInteger("LAST_ASCENSION_LEVEL", ascension);
            playerPrefs.flush();
        }
    }

    public static void updatePowerDescriptions()
    {
        for (AbstractCreature c : GameUtilities.getAllCharacters(true))
        {
            for (AbstractPower p : c.powers)
            {
                p.updateDescription();
            }
        }
    }

    public static boolean useArtifact(AbstractCreature target)
    {
        final AbstractPower artifact = getPower(target, ArtifactPower.POWER_ID);
        if (artifact != null)
        {
            PCLActions.top.add(new TextAboveCreatureAction(target, ApplyPowerAction.TEXT[0]));
            SFX.play(SFX.NULLIFY_SFX);
            artifact.flashWithoutSound();
            artifact.onSpecificTrigger();

            return false;
        }

        return true;
    }

    public static int useXCostEnergy(AbstractCard card)
    {
        int amount = getXCostEnergy(card, true);

        if (!card.freeToPlayOnce) {
            AbstractDungeon.player.energy.use(card.energyOnUse);
        }

        CombatManager.queueRefreshHandLayout();
        return amount;
    }

}
