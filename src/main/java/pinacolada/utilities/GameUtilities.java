package pinacolada.utilities;

import basemod.BaseMod;
import basemod.DevConsole;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.SoulboundField;
import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.stats.AchievementGrid;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.BobEffect;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.cardFilter.CountingPanelStats;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import org.scannotation.AnnotationDB;
import pinacolada.actions.PCLActions;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.blights.PCLBlightData;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.blights.PCLDynamicBlightData;
import pinacolada.blights.pcl.UpgradedHand;
import pinacolada.cardmods.*;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardAffinities;
import pinacolada.cards.base.fields.PCLCardAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.EphemeralField;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.characters.PCLCharacter;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLSFX;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.interfaces.subscribers.OnEndOfTurnLastSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.monsters.PCLIntentType;
import pinacolada.orbs.PCLOrb;
import pinacolada.orbs.PCLOrbData;
import pinacolada.patches.basemod.PotionPoolPatches;
import pinacolada.patches.library.BlightHelperPatches;
import pinacolada.patches.library.CardLibraryPatches;
import pinacolada.patches.library.RelicLibraryPatches;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotionData;
import pinacolada.potions.PCLPotionData;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PTriggerPower;
import pinacolada.relics.*;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_GainBlock;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.stances.PCLStanceHelper;

import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.*;

import static com.evacipated.cardcrawl.modthespire.Patcher.annotationDBMap;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.actionManager;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

// Copied and modified from STS-AnimatorMod
public class GameUtilities {
    private static final String PORTRAIT_PATH = "images/1024Portraits/";
    private static final String BETA_PATH = "images/1024PortraitsBeta/";
    public static final FilenameFilter JSON_FILTER = (dir, name) -> name.endsWith(".json");
    public static final int CHAR_OFFSET = 97;

    public static CountingPanelStats<PCLAffinity, PCLAffinity, AbstractCard> affinityStats(Iterable<? extends AbstractCard> cards) {
        return CountingPanelStats.basic(
                GameUtilities::getVisiblePCLAffinities,
                cards);
    }

    public static void applyPowerInstantly(Iterable<? extends AbstractCreature> targets, PCLPowerData powerHelper, int stacks) {
        for (AbstractCreature target : targets) {
            applyPowerInstantly(target, powerHelper, stacks);
        }
    }

    public static void applyPowerInstantly(AbstractCreature target, PCLPowerData powerHelper, int stacks) {
        applyPowerInstantly(target, powerHelper.create(target, player, stacks), stacks);
    }

    public static AbstractPower applyPowerInstantly(AbstractCreature target, AbstractPower power) {
        return applyPowerInstantly(target, power, power.amount);
    }

    public static AbstractPower applyPowerInstantly(AbstractCreature target, AbstractPower power, int stacks) {
        final AbstractPower existingPower = getPower(target, power.ID);
        if (existingPower != null) {
            // -1 stacks on nonnegative powers mean non-decreasing powers
            if (existingPower.amount < 0 && !existingPower.canGoNegative) {
                if (stacks > -1) {
                    target.powers.remove(existingPower);
                }
            }
            else {
                existingPower.amount += stacks;
                if ((existingPower.amount < 0 && !existingPower.canGoNegative) || existingPower.amount == 0) {
                    target.powers.remove(existingPower);
                }
            }

            return existingPower;
        }

        target.addPower(power);
        Collections.sort(target.powers);

        return power;
    }

    public static boolean areMonstersBasicallyDead() {
        final AbstractRoom room = getCurrentRoom();
        final MonsterGroup group = room != null ? room.monsters : null;
        return group == null || group.areMonstersBasicallyDead();
    }

    public static AbstractMonster asMonster(AbstractCreature c) {
        return EUIUtils.safeCast(c, AbstractMonster.class);
    }

    public static CountingPanelStats<PCLAugmentCategory, PCLAugment, PCLAugment> augmentStats(ArrayList<PCLAugment> augments) {
        return new CountingPanelStats<PCLAugmentCategory, PCLAugment, PCLAugment>(
                Collections::singleton,
                entry -> entry.data.category,
                entry -> 1,
                (entries, entry) -> 1,
                augments);
    }

    public static boolean canAcceptInput() {
        return isPlayerTurn(true) && actionManager.phase == GameActionManager.Phase.WAITING_ON_USER
                && AbstractDungeon.actionManager.cardQueue.isEmpty() && AbstractDungeon.actionManager.actions.isEmpty()
                && !DevConsole.visible && !AbstractDungeon.isScreenUp && !CardCrawlGame.isPopupOpen;
    }

    public static boolean canObtainCopy(AbstractCard card) {
        return PGR.dungeon.canObtainCopy(card);
    }

    public static boolean canOrbApplyFocus(AbstractOrb orb) {
        return (!Plasma.ORB_ID.equals(orb.ID) && !(orb instanceof PCLOrb && !((PCLOrb) orb).data.applyFocusToPassive));
    }

    public static boolean canOrbApplyFocusToEvoke(AbstractOrb orb) {
        return (!Dark.ORB_ID.equals(orb.ID) && !(orb instanceof PCLOrb && !((PCLOrb) orb).data.applyFocusToEvoke));
    }

    public static boolean canPlayCurse() {
        return GameUtilities.hasRelicEffect(BlueCandle.ID);
    }

    public static boolean canPlayStatus() {
        return GameUtilities.hasRelicEffect(MedicalKit.ID);
    }

    public static boolean canPlayTwice(AbstractCard card) {
        return !card.purgeOnUse && card.type != PCLEnum.CardType.SUMMON && !PCLCardTag.Fleeting.has(card);
    }

    // Does NOT patch card pool screen because this would cause the card pool screen to get messed up when you get a prismatic shard effect in battle
    public static boolean canReceiveAnyColorCard() {
        return GameUtilities.hasRelicEffect(PrismaticShard.ID) || ModHelper.isModEnabled(Diverse.ID);
    }

    public static boolean canRemoveFromDeck(AbstractCard card) {
        // Hardcoded checks from CardGroup -_-
        switch (card.cardID) {
            case Necronomicurse.ID:
            case AscendersBane.ID:
            case CurseOfTheBell.ID:
                return false;
        }
        return !SoulboundField.soulbound.get(card);
    }

    public static boolean canRetain(AbstractCard card) {
        return !card.isEthereal && !card.selfRetain && !EphemeralField.value.get(card);
    }

    public static boolean chance(float amount) {
        return PGR.dungeon.getRNG().random(100) < amount;
    }

    public static void changeCardForm(AbstractCard card, int amount) {
        if (card instanceof PCLCard) {
            ((PCLCard) card).changeForm(amount, card.timesUpgraded);
            card.flash();
        }
    }

    public static void changeCardName(AbstractCard card, String newName) {
        final String previousName = card.name;
        card.name = card.name.replace(card.originalName, newName);
        card.originalName = newName;
        if (card.name.equals(previousName)) {
            card.name = newName;
        }
    }

    public static void changeRelicForm(AbstractRelic relic, int amount) {
        if (relic instanceof PCLRelic) {
            ((PCLRelic) relic).setForm(amount);
            relic.flash();
        }
    }

    public static void clearPostCombatActions() {
        AbstractDungeon.actionManager.clearPostCombatActions();
    }

    public static void copyVisualProperties(AbstractCard copy, AbstractCard original) {
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

    public static CardGroup createCardGroup(List<? extends AbstractCard> cards) {
        return createCardGroup(cards, CardGroup.CardGroupType.UNSPECIFIED);
    }

    public static CardGroup createCardGroup(List<? extends AbstractCard> cards, CardGroup.CardGroupType type) {
        final CardGroup group = new CardGroup(type);
        group.group.addAll(cards);
        return group;
    }

    public static void fillWithAllCharacters(boolean aliveOnly, ArrayList<AbstractCreature> characters) {
        final AbstractRoom room = getCurrentRoom();
        if (room != null && room.monsters != null) {
            for (AbstractMonster m : room.monsters.monsters) {
                if (!aliveOnly || !isDeadOrEscaped(m)) {
                    characters.add(m);
                }
            }
        }

        for (PCLCardAlly summon : CombatManager.summons.summons) {
            if (!aliveOnly || summon.hasCard()) {
                characters.add(summon);
            }
        }

        if (!aliveOnly || !isDeadOrEscaped(player)) {
            characters.add(player);
        }
    }

    public static void fillWithEnemies(boolean aliveOnly, ArrayList<? super AbstractMonster> monsters) {
        final AbstractRoom room = getCurrentRoom();

        if (room != null && room.monsters != null) {
            if (!aliveOnly) {
                monsters.addAll(room.monsters.monsters);
            }
            else {
                for (AbstractMonster m : room.monsters.monsters) {
                    if (!isDeadOrEscaped(m)) {
                        monsters.add(m);
                    }
                }
            }
        }
    }

    public static void fillWithSummons(Boolean isAlive, ArrayList<? super PCLCardAlly> monsters) {
        if (isAlive == null) {
            monsters.addAll(CombatManager.summons.summons);
            return;
        }

        for (PCLCardAlly m : CombatManager.summons.summons) {
            if (!m.hasCard() ^ isAlive) {
                monsters.add(m);
            }
        }
    }


    public static CardGroup findCardGroup(AbstractCard card, boolean includeLimbo) {
        if (player.hand.contains(card)) {
            return player.hand;
        }
        else if (player.drawPile.contains(card)) {
            return player.drawPile;
        }
        else if (player.discardPile.contains(card)) {
            return player.discardPile;
        }
        else if (player.exhaustPile.contains(card)) {
            return player.exhaustPile;
        }
        else if (includeLimbo && player.limbo.contains(card)) {
            return player.limbo;
        }
        else if (CombatManager.PURGED_CARDS.contains(card)) {
            return CombatManager.PURGED_CARDS;
        }
        else {
            return null;
        }
    }

    public static void flash(AbstractCard card, boolean superFlash) {
        if (superFlash) {
            card.superFlash();
        }
        else {
            card.flash();
        }
    }

    public static void flash(AbstractCard card, Color color, boolean superFlash) {
        if (superFlash) {
            card.superFlash(color.cpy());
        }
        else {
            card.flash(color.cpy());
        }
    }

    public static void forceMarkCardAsSeen(String key) {
        AbstractCard c = CardLibrary.getCard(key);
        if (c != null) {
            c.isSeen = true;
            int val = UnlockTracker.seenPref.getInteger(key);
            if (val != 1) {
                UnlockTracker.seenPref.putInteger(key, 1);
                UnlockTracker.seenPref.flush();
            }
        }
    }

    public static Random generateNewRNG(int a, int b) {
        return new Random(Settings.seed + (AbstractDungeon.actNum * a) + (AbstractDungeon.floorNum * b));
    }

    public static AbstractCard.CardColor getActingCardColor(AbstractCard c) {
        return AbstractDungeon.player != null ? player.getCardColor() : c.color;
    }

    public static AbstractCard.CardColor getActingColor() {
        return player != null ? player.getCardColor() : EUI.actingColor;
    }

    public static ArrayList<AbstractCreature> getAllCharacters(boolean aliveOnly) {
        final ArrayList<AbstractCreature> characters = new ArrayList<>();
        fillWithAllCharacters(aliveOnly, characters);
        return characters;
    }

    public static HashSet<AbstractCard> getAllCopies(String cardID, CardGroup group) {
        return getAllCopies(new HashSet<>(), cardID, group);
    }

    public static HashSet<AbstractCard> getAllCopies(HashSet<AbstractCard> cards, String cardID, CardGroup group) {
        for (AbstractCard card : group.group) {
            if (cardID.equals(card.cardID)) {
                cards.add(card);
            }
        }

        return cards;
    }

    public static HashSet<AbstractCard> getAllCopies(String cardID) {
        HashSet<AbstractCard> cards = getAllInBattleCopies(cardID);
        cards.addAll(getMasterDeckCopies(cardID));

        return cards;
    }

    public static HashSet<AbstractCard> getAllInBattleCopies(String cardID) {
        HashSet<AbstractCard> cards = new HashSet<>();

        if (player.cardInUse != null && player.cardInUse.cardID.equals(cardID)) {
            cards.add(player.cardInUse);
        }

        getAllCopies(cards, cardID, player.hand);
        getAllCopies(cards, cardID, player.drawPile);
        getAllCopies(cards, cardID, player.discardPile);
        getAllCopies(cards, cardID, player.exhaustPile);
        getAllCopies(cards, cardID, player.limbo);

        return cards;
    }

    public static HashSet<AbstractCard> getAllInBattleInstances(UUID uuid) {
        final HashSet<AbstractCard> cards = new HashSet<>();

        if (player.cardInUse != null && player.cardInUse.uuid.equals(uuid)) {
            cards.add(player.cardInUse);
        }

        getAllInstances(cards, uuid, player.hand);
        getAllInstances(cards, uuid, player.drawPile);
        getAllInstances(cards, uuid, player.discardPile);
        getAllInstances(cards, uuid, player.exhaustPile);
        getAllInstances(cards, uuid, player.limbo);

        return cards;
    }

    public static HashSet<AbstractCard> getAllInstances(UUID uuid) {
        final HashSet<AbstractCard> cards = getAllInBattleInstances(uuid);
        final AbstractCard masterDeckInstance = getMasterDeckInstance(uuid);
        if (masterDeckInstance != null) {
            cards.add(masterDeckInstance);
        }

        return cards;
    }

    public static HashSet<AbstractCard> getAllInstances(HashSet<AbstractCard> cards, UUID uuid, CardGroup group) {
        for (AbstractCard card : group.group) {
            if (uuid.equals(card.uuid)) {
                cards.add(card);
            }
        }

        return cards;
    }

    public static int getAscensionLevel() {
        return AbstractDungeon.isAscensionMode ? Math.max(0, Math.min(20, AbstractDungeon.ascensionLevel)) : 0;
    }

    public static ArrayList<PCLAugment> getAugments(AbstractCard c) {
        return c instanceof PCLCard ? ((PCLCard) c).getAugments() : null;
    }

    public static ArrayList<AbstractCard> getAvailableCardsForAllColors(FuncT1<Boolean, AbstractCard> filter) {
        return EUIUtils.filter(CardLibrary.cards.values(), c -> EUIGameUtils.canSeeCard(c) && filter.invoke(c));
    }

    public static AbstractCard.CardColor getBlightColor(String relicID) {
        PCLBlightData data = PCLBlightData.getStaticData(relicID);
        if (data != null) {
            return data.cardColor;
        }
        PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(relicID);
        if (slot != null) {
            return slot.slotColor;
        }
        return AbstractCard.CardColor.COLORLESS;
    }

    public static String getBlightNameForID(String relicID) {
        if (relicID != null) {
            // NOT using BlightHelper.getBlight as the replacement patching on that method may cause text glitches or infinite loops in this method
            AbstractBlight c = BlightHelperPatches.getDirectBlight(relicID);
            if (c != null) {
                return c.name;
            }

            // Try to load data from slots. Do not actually create relics here to avoid infinite loops
            PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(relicID);
            if (slot != null) {
                HashMap<Settings.GameLanguage, BlightStrings> languageMap = PCLDynamicBlightData.parseLanguageStrings(slot.languageStrings);
                BlightStrings language = languageMap != null ? PCLDynamicBlightData.getStringsForLanguage(languageMap) : null;
                if (language != null) {
                    return language.NAME;
                }
            }
        }
        return "";
    }

    public static int getBlockedHits(AbstractCreature creature) {
        return GameUtilities.getPowerAmount(creature, BufferPower.POWER_ID);
    }

    public static BobEffect getBobEffect(AbstractMonster mo) {
        return ReflectionHacks.getPrivate(mo, AbstractMonster.class, "bobEffect");
    }

    public static String getCardNameForID(String cardID) {
        return getCardNameForID(cardID, 0, 0);
    }

    public static String getCardNameForID(String cardID, int upgrade, int form) {
        if (cardID != null) {
            // Try to load PCL data first to apply form/upgrade to
            PCLCardData data = PCLCardData.getStaticData(cardID);
            if (data != null) {
                return GameUtilities.getMultiformName(data.strings.NAME, form, upgrade, data.maxForms, data.maxUpgradeLevel, data.branchFactor);
            }

            // NOT using CardLibrary.getCard as the replacement patching on that method may cause text glitches or infinite loops in this method
            AbstractCard c = CardLibraryPatches.getDirectCard(cardID);
            if (c != null) {
                return upgrade > 0 ? c.name + "+" : c.name;
            }

            // Try to load data from slots. Do not actually create cards here to avoid infinite loops
            PCLCustomCardSlot slot = PCLCustomCardSlot.get(cardID);
            if (slot != null) {
                HashMap<Settings.GameLanguage, CardStrings> languageMap = PCLDynamicCardData.parseLanguageStrings(slot.languageStrings);
                CardStrings language = languageMap != null ? PCLDynamicCardData.getStringsForLanguage(languageMap) : null;
                if (language != null) {
                    return GameUtilities.getMultiformName(language.NAME, form, upgrade, slot.forms != null ? slot.forms.length : 1, slot.maxUpgradeLevel, slot.branchUpgradeFactor);
                }
            }

            return cardID;
        }
        return "";
    }

    public static AbstractCreature getCardOwner(AbstractCard card) {
        return card instanceof PCLCard ? ((PCLCard) card).getSourceCreature() : player;
    }

    public static CardGroup getCardPool(AbstractCard.CardRarity rarity) {
        if (rarity == null) {
            return AbstractDungeon.colorlessCardPool;
        }

        switch (rarity) {
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

    public static CardGroup getCardPoolSource(AbstractCard.CardRarity rarity) {
        if (rarity == null) {
            return AbstractDungeon.srcColorlessCardPool;
        }

        switch (rarity) {
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

    public static ArrayList<CardGroup> getCardPoolsInCombat() {
        return EUIUtils.arrayList(AbstractDungeon.colorlessCardPool, AbstractDungeon.commonCardPool, AbstractDungeon.uncommonCardPool, AbstractDungeon.rareCardPool);
    }

    public static ArrayList<AbstractCard> getCardsFromAllColorCombatPool(FuncT1<Boolean, AbstractCard> filter, int count) {
        return pickCardsFromList(new RandomizedList<>(getAvailableCardsForAllColors(filter)), count);
    }

    public static RandomizedList<AbstractCard> getCardsFromCombatPool(AbstractCard.CardRarity rarity) {
        return getCardsFromCombatPool(getCardPool(rarity), null);
    }

    public static RandomizedList<AbstractCard> getCardsFromCombatPool(CardGroup group, FuncT1<Boolean, AbstractCard> filter) {
        final RandomizedList<AbstractCard> cards = new RandomizedList<>();
        if (group != null) {
            for (AbstractCard c : group.group) {
                if (isObtainableInCombat(c) && (filter == null || filter.invoke(c))) {
                    cards.add(c);
                }
            }
        }

        return cards;
    }

    public static ArrayList<AbstractCard> getCardsFromFullCombatPool(FuncT1<Boolean, AbstractCard> filter, int count) {
        return pickCardsFromList(new RandomizedList<>(getCardsFromFullCombatPools(filter)), count);
    }

    public static RandomizedList<AbstractCard> getCardsFromFullCombatPools(FuncT1<Boolean, AbstractCard> filter) {
        return getCardsFromStandardPools(EUIGameUtils.getGameCardPools(), filter);
    }

    public static ArrayList<AbstractCard> getCardsFromStandardCombatPool(FuncT1<Boolean, AbstractCard> filter, int count) {
        return pickCardsFromList(new RandomizedList<>(getCardsFromStandardCombatPools(filter)), count);
    }

    public static RandomizedList<AbstractCard> getCardsFromStandardCombatPools(FuncT1<Boolean, AbstractCard> filter) {
        return getCardsFromStandardPools(getCardPoolsInCombat(), filter);
    }

    public static RandomizedList<AbstractCard> getCardsFromStandardPools(ArrayList<CardGroup> groups, FuncT1<Boolean, AbstractCard> filter) {
        final RandomizedList<AbstractCard> cards = new RandomizedList<>();
        for (CardGroup group : groups) {
            for (AbstractCard c : group.group) {
                if (isObtainableInCombat(c) && (filter == null || filter.invoke(c))) {
                    cards.add(c);
                }
            }
        }

        return cards;
    }

    public static ArrayList<AbstractCard> getCardsInAnyPile() {
        return getCardsInPile(player.hand, player.discardPile, player.drawPile, player.exhaustPile);
    }

    public static ArrayList<AbstractCard> getCardsInGame() {
        ArrayList<AbstractCard> baseList = getCardsInPile(player.hand, player.discardPile, player.drawPile, player.exhaustPile, player.masterDeck, player.limbo, CombatManager.PURGED_CARDS);
        for (PCLCardAlly ally : CombatManager.summons.summons) {
            if (ally.card != null) {
                baseList.add(ally.card);
            }
        }
        return baseList;
    }

    public static ArrayList<AbstractCard> getCardsInPile(CardGroup... groups) {
        return EUIUtils.flattenList(EUIUtils.map(groups, group -> group.group));
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<?> annotation) {
        final ArrayList<Class<?>> names = new ArrayList<>();
        for (AnnotationDB db : annotationDBMap.values()) {
            Map<String, Set<String>> annotations = db.getAnnotationIndex();
            for (String key : annotations.getOrDefault(annotation.getName(), new HashSet<>())) {
                try {
                    Class<?> annotatedClass = Class.forName(key);
                    names.add(annotatedClass);
                }
                catch (Exception ignored) {

                }
            }
        }
        return names;
    }

    public static CardGroup getColorlessCardPool() {
        return getCardPool(null);
    }

    public static RandomizedList<AbstractCard> getColorlessCardsFromCombatPool() {
        return getCardsFromCombatPool(getColorlessCardPool(), null);
    }

    // Do not return magic number for non-PCLCard cards
    public static int getCounter(Object card) {
        return card instanceof PCLCard ? ((PCLCard) card).magicNumber : 0;
    }

    public static AbstractRoom getCurrentRoom() {
        return (AbstractDungeon.currMapNode == null) ? null : AbstractDungeon.currMapNode.getRoom();
    }

    public static int getCurrentScore() {
        return CombatManager.playerSystem.getActiveMeter().getCurrentScore();
    }

    public static ArrayList<AbstractPower> getDebuffs(AbstractCreature creature) {
        final ArrayList<AbstractPower> result = new ArrayList<>();
        for (AbstractPower power : creature.powers) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                result.add(power);
            }
        }

        return result;
    }

    public static int getDebuffsCount(AbstractCreature creature) {
        return (creature == null || creature.powers == null) ? 0 : getDebuffsCount(creature.powers);
    }

    public static int getDebuffsCount(ArrayList<AbstractPower> powers) {
        int result = 0;
        for (AbstractPower power : powers) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                result += 1;
            }
        }

        return result;
    }

    public static int getDebuffsStacks(AbstractCreature creature) {
        return (creature == null || creature.powers == null) ? 0 : getDebuffsStacks(creature.powers);
    }

    public static int getDebuffsStacks(ArrayList<AbstractPower> powers) {
        int result = 0;
        for (AbstractPower power : powers) {
            if (power.type == AbstractPower.PowerType.DEBUFF) {
                result += power.amount;
            }
        }

        return result;
    }

    public static int getEndOfTurnBlock(AbstractCreature creature) {
        int amount = 0;
        if (creature != null) {
            // Check end of turn powers (base game powers plus custom Fabricate powers)
            if (creature.powers != null) {
                for (AbstractPower p : creature.powers) {
                    if (isPowerBlockGranting(p)) {
                        amount += p.amount;
                    }
                    else if (p instanceof PTriggerPower) {
                        amount += getEndOfTurnBlockFromTriggers(((PTriggerPower) p).ptriggers);
                    }
                }
            }


            if (creature instanceof AbstractPlayer && isPlayerTurn(true)) {
                // Check end of turn relics only during player turn
                for (AbstractRelic r : ((AbstractPlayer) creature).relics) {
                    if (Orichalcum.ID.equals(r.relicId) && creature.currentBlock == 0) {
                        amount += 6; // Hardcoded stuff, there's a constant but Orichalcum doesn't actually use it :(
                    }
                    else if (CloakClasp.ID.equals(r.relicId)) {
                        amount += ((AbstractPlayer) creature).hand.size(); // Hardcoded logic in Cloak Clasp
                    }
                    else if (r instanceof PCLPointerRelic) {
                        amount += getEndOfTurnBlockFromTriggers(((PCLPointerRelic) r).getEffects());
                    }
                }

                // TODO check for custom pointer orbs
                // Check end of turn orbs
                for (AbstractOrb o : ((AbstractPlayer) creature).orbs) {
                    if (o != null && isOrbBlockGranting(o)) {
                        amount += o.passiveAmount;
                    }
                }
            }

        }
        return amount;
    }

    // TODO less naive approach that accounts for custom conds and out-of-order move hierarchies
    protected static int getEndOfTurnBlockFromTriggers(Iterable<? extends PSkill> triggers) {
        int amount = 0;
        for (PSkill<?> trigger : triggers) {
            if (trigger instanceof PTrigger_When
                    && (trigger.hasChildType(OnEndOfTurnFirstSubscriber.class) || trigger.hasChildType(OnEndOfTurnLastSubscriber.class))) {
                PSkill<?> skill = trigger.getLowestChild();
                if (skill instanceof PMove_GainBlock) {
                    amount += skill.amount;
                }
                else if (skill instanceof PMultiBase<?>) {
                    for (PSkill<?> subskill : ((PMultiBase<?>) skill).getSubEffects()) {
                        if (subskill instanceof PMove_GainBlock) {
                            amount += subskill.amount;
                        }
                    }
                }
            }
        }
        return amount;
    }

    public static ArrayList<AbstractMonster> getEnemies(boolean aliveOnly) {
        final ArrayList<AbstractMonster> mo = new ArrayList<>();
        fillWithEnemies(aliveOnly, mo);
        return mo;
    }

    public static AbstractOrb getFirstOrb(String orbID) {
        for (AbstractOrb orb : player.orbs) {
            if (orb != null && (orbID == null || orbID.equals(orb.ID))) {
                return orb;
            }
        }

        return null;
    }

    public static int getGold() {
        return player != null ? player.gold : 0;
    }

    public static int getGroupAffinityCount(PCLAffinity affinity, CardGroup group) {
        return getGroupPCLAffinities(group).getLevel(affinity, false);
    }

    public static PCLCardAffinities getGroupPCLAffinities(CardGroup group) {
        return player == null ? new PCLCardAffinities(null) : getPCLCardAffinities(group.group);
    }

    public static int getHP(AbstractCreature creature, boolean addTempHP, boolean addBlock) {
        return creature.currentHealth + (addTempHP ? TempHPField.tempHp.get(creature) : 0) + (addBlock ? creature.currentBlock : 0);
    }

    public static int getHealthBarAmount(AbstractCreature c, int amount) {
        return getHealthBarAmount(c, amount, true, true);
    }

    public static int getHealthBarAmount(AbstractCreature c, int amount, boolean subtractBlock, boolean subtractTempHP) {
        return Math.min(getHealthBarDamage(c, amount, subtractBlock, subtractTempHP), c.currentHealth);
    }

    public static int getHealthBarDamage(AbstractCreature c, int amount) {
        return getHealthBarDamage(c, amount, true, true);
    }

    public static int getHealthBarDamage(AbstractCreature c, int amount, boolean subtractBlock, boolean subtractTempHP) {
        if (c == null || (!subtractBlock && !subtractTempHP)) {
            return amount;
        }

        if (amount > 0 && subtractBlock) {
            int blocked = Math.min(c.currentBlock + GameUtilities.getEndOfTurnBlock(c), amount);
            amount -= blocked;
        }

        if (amount > 0 && subtractTempHP) {
            int blocked = Math.min(TempHPField.tempHp.get(c), amount);
            amount -= blocked;
        }

        return Math.max(0, amount);
    }

    public static float getHealthPercentage(AbstractCreature creature) {
        return creature.currentHealth / (float) creature.maxHealth;
    }

    public static float getHealthPercentage(AbstractCreature creature, boolean addTempHP, boolean addBlock) {
        return getHP(creature, addTempHP, addBlock) / (float) getMaxHP(creature, addTempHP, addBlock);
    }

    public static PCLIntentInfo getIntent(AbstractMonster enemy) {
        return PCLIntentInfo.get(enemy);
    }

    public static ArrayList<PCLIntentInfo> getIntents() {
        final ArrayList<PCLIntentInfo> intents = new ArrayList<>();
        for (AbstractMonster m : getEnemies(true)) {
            intents.add(getIntent(m));
        }

        return intents;
    }

    public static AbstractCard getLastCardPlayed(boolean currentTurn) {
        return getLastCardPlayed(currentTurn, 0);
    }

    public static AbstractCard getLastCardPlayed(boolean currentTurn, int offset) {
        final ArrayList<AbstractCard> cards = currentTurn
                ? AbstractDungeon.actionManager.cardsPlayedThisTurn
                : AbstractDungeon.actionManager.cardsPlayedThisCombat;
        return cards.size() > offset ? cards.get(cards.size() - 1 - offset) : null;
    }

    public static AbstractOrb getLastOrb(String orbID) {
        final ArrayList<AbstractOrb> orbs = player.orbs;
        for (int i = player.maxOrbs - 1; i >= 0; i--) {
            final AbstractOrb orb = orbs.get(i);
            if (orb != null && (orbID == null || orbID.equals(orb.ID))) {
                return orb;
            }
        }

        return null;
    }

    public static HashSet<AbstractCard> getMasterDeckCopies(String cardID) {
        final HashSet<AbstractCard> cards = new HashSet<>();
        for (AbstractCard c : player.masterDeck.group) {
            if (c.cardID.equals(cardID)) {
                cards.add(c);
            }
        }

        return cards;
    }

    public static AbstractCard getMasterDeckInstance(UUID uuid) {
        for (AbstractCard c : player.masterDeck.group) {
            if (c.uuid == uuid) {
                return c;
            }
        }

        return null;
    }

    public static int getMaxAscensionLevel(AbstractPlayer player) {
        Prefs pref = player.getPrefs();
        if (pref != null) {
            return pref.getInteger("ASCENSION_LEVEL", 0);
        }
        return 0;
    }

    public static int getMaxHP(AbstractCreature creature, boolean addTempHP, boolean addBlock) {
        return creature.maxHealth + (addTempHP ? TempHPField.tempHp.get(creature) : 0) + (addBlock ? creature.currentBlock : 0);
    }

    public static String getMultiformName(String base, int form, int timesUpgraded, int maxForms, int maxUpgrades, int branchFactor) {
        return getMultiformName(base, form, timesUpgraded, maxForms, maxUpgrades, branchFactor, false);
    }

    public static String getMultiformName(String base, int form, int timesUpgraded, int maxForms, int maxUpgrades, int branchFactor, boolean alwaysShowUpgrade) {
        StringBuilder sb = new StringBuilder(base);

        if (alwaysShowUpgrade && maxUpgrades != 0) {
            sb.append(" ");
            sb.append(timesUpgraded);
        }
        else if (!alwaysShowUpgrade && timesUpgraded != 0) {
            sb.append("+");
            if (maxUpgrades < 0 || maxUpgrades > 1) {
                sb.append(timesUpgraded);
            }
        }

        // Do not show appended characters for non-multiform or linear upgrade path cards
        if (maxForms > 1 && branchFactor != 1) {
            // For branch factors of 2 or more, show the "path" that was taken
            if (branchFactor > 1) {
                int minForm = form;
                StringBuilder sb2 = new StringBuilder();
                while (minForm > 0) {
                    int eval = minForm - 1;
                    char appendix = (char) ((eval % branchFactor) + CHAR_OFFSET);
                    sb2.append(appendix);
                    minForm = eval / branchFactor;
                }
                sb2.reverse();
                sb.append(sb2);
            }
            else {
                char appendix = (char) (form + CHAR_OFFSET);
                sb.append(appendix);
            }
        }
        return sb.toString();
    }

    public static AbstractCard.CardRarity getNextRarity(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case BASIC:
            case CURSE:
                return AbstractCard.CardRarity.COMMON;
            case COMMON:
                return AbstractCard.CardRarity.UNCOMMON;
        }
        return AbstractCard.CardRarity.RARE;
    }

    public static ArrayList<AbstractCard> getObtainableCurses() {
        final ArrayList<AbstractCard> curses = new ArrayList<>();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c.type == AbstractCard.CardType.CURSE && c.rarity != AbstractCard.CardRarity.SPECIAL) {
                curses.add(c);
            }
        }

        return curses;
    }

    public static int getOrbBaseEvokeAmount(AbstractOrb orb) {
        Object f = getOrbField(orb, "baseEvokeAmount");
        return (f != null ? (int) f : 0);
    }

    public static int getOrbBasePassiveAmount(AbstractOrb orb) {
        Object f = getOrbField(orb, "basePassiveAmount");
        return (f != null ? (int) f : 0);
    }

    public static int getOrbCount() {
        return player != null && player.orbs != null ? player.filledOrbCount() : 0;
    }

    public static int getOrbCount(PCLOrbData orb) {
        return getOrbCount(orb.ID);
    }

    public static int getOrbCount(String orbID) {
        int count = 0;
        if (player != null && player.orbs != null) {
            for (AbstractOrb orb : player.orbs) {
                if (orbID.equals(orb.ID)) {
                    count += 1;
                }
            }
        }

        return count;
    }

    public static Object getOrbField(AbstractOrb orb, String field) {
        try {
            Field f = AbstractOrb.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(orb);
        }
        catch (NoSuchFieldException | IllegalAccessException var2) {
            EUIUtils.logWarning(orb, "Orb could not be modified");
            return null;
        }
    }

    public static HashSet<AbstractCard> getOtherCardsInHand(AbstractCard card) {
        final HashSet<AbstractCard> cards = new HashSet<>();
        for (AbstractCard c : player.hand.group) {
            if (c != card) {
                cards.add(c);
            }
        }

        return cards;
    }

    public static PCLCardAffinities getPCLCardAffinities(AbstractCard card) {
        if (card instanceof PCLCard) {
            return ((PCLCard) card).affinities;
        }
        if (card != null) {
            AffinityDisplayModifier mod = AffinityDisplayModifier.get(card);
            if (mod != null) {
                return mod.affinities;
            }
        }
        return null;
    }

    public static PCLCardAffinities getPCLCardAffinities(Iterable<? extends AbstractCard> cards) {
        final PCLCardAffinities affinities = new PCLCardAffinities(null);
        for (AbstractCard c : cards) {
            PCLCardAffinities cAffs = getPCLCardAffinities(c);
            if (cAffs != null) {
                affinities.add(cAffs, 1);
            }
        }

        return affinities;
    }

    public static PCLCardAffinity getPCLCardAffinity(AbstractCard card, PCLAffinity affinity) {
        final PCLCardAffinities a = getPCLCardAffinities(card);
        return a != null ? a.get(affinity, false) : null;
    }

    public static int getPCLCardAffinityLevel(AbstractCard card, PCLAffinity affinity, boolean useStarLevel) {
        final PCLCardAffinities a = getPCLCardAffinities(card);
        return a != null ? a.getLevel(affinity, useStarLevel) : 0;
    }

    public static PCLLoadout getLoadoutForCard(AbstractCard c) {
        if (c instanceof PCLCard) {
            return ((PCLCard) c).cardData.loadout;
        }
        return null;
    }

    public static String getLoadoutNameForCard(AbstractCard c) {
        PCLLoadout loadout = getLoadoutForCard(c);
        if (loadout != null) {
            return loadout.getName();
        }
        return EUIUtils.EMPTY_STRING;
    }

    public static AbstractPlayer.PlayerClass getPlayerClass() {
        return inGame() && player != null ? player.chosenClass : null;
    }

    public static <T extends AbstractRelic> T getPlayerRelic(String relicID) {
        if (player == null) {
            return null;
        }

        for (AbstractRelic relic : player.relics) {
            if (relic != null && relicID.equals(relic.relicId)) {
                try {
                    return (T) relic;
                }
                catch (ClassCastException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    public static <T> T getPlayerRelic(Class<T> relicType) {
        for (AbstractRelic relic : player.relics) {
            if (relicType.isInstance(relic)) {
                return relicType.cast(relic);
            }
        }

        return null;
    }

    public static <T> ArrayList<T> getPlayerRelics(Class<T> relicType) {
        return EUIUtils.mapAsNonnull(player.relics, r -> EUIUtils.safeCast(r, relicType));
    }

    public static ArrayList<AbstractCreature> getPlayerTeam(boolean isAlive) {
        ArrayList<AbstractCreature> creatures = new ArrayList<>();
        PCLCardTarget.fillWithPlayerTeam(creatures, isAlive);
        return creatures;
    }

    public static String getPotionNameForID(String potionID) {
        if (potionID != null) {
            // NOT using PotionHelper.getPotion as the replacement patching on that method may cause text glitches or infinite loops in this method
            AbstractPotion c = PotionPoolPatches.getDirectPotion(potionID);
            if (c != null) {
                return c.name;
            }

            // Try to load data on potions not in the library
            PCLPotionData data = PCLPotionData.getStaticData(potionID);
            if (data != null) {
                return data.strings.NAME;
            }

            // Try to load data from slots. Do not actually create potions here to avoid infinite loops
            PCLCustomPotionSlot slot = PCLCustomPotionSlot.get(potionID);
            if (slot != null) {
                HashMap<Settings.GameLanguage, PotionStrings> languageMap = PCLDynamicPotionData.parseLanguageStrings(slot.languageStrings);
                PotionStrings language = languageMap != null ? PCLDynamicPotionData.getStringsForLanguage(languageMap) : null;
                if (language != null) {
                    return language.NAME;
                }
            }
        }
        return "";
    }

    public static ArrayList<AbstractPotion> getPotions(AbstractCard.CardColor cardColor) {
        return EUIUtils.map(PotionHelper.getPotions(EUIGameUtils.getPlayerClassForCardColor(cardColor), cardColor == null), PotionHelper::getPotion);
    }

    public static AbstractPower getPower(AbstractCreature creature, String powerID) {
        if (creature != null && creature.powers != null) {
            for (AbstractPower p : creature.powers) {
                if (p != null && powerID.equals(p.ID)) {
                    return p;
                }
            }
        }

        return null;
    }

    public static <T> T getPower(AbstractCreature owner, Class<T> powerType) {
        if (owner != null && owner.powers != null) {
            for (AbstractPower power : owner.powers) {
                if (powerType.isInstance(power)) {
                    return powerType.cast(power);
                }
            }
        }

        return null;
    }

    public static int getPowerAmount(AbstractCreature owner, String powerID) {
        AbstractPower power = getPower(owner, powerID);
        return power != null ? power.amount : 0;
    }

    public static int getPowerAmount(String powerID) {
        return getPowerAmount(AbstractDungeon.player, powerID);
    }

    public static int getPowerAmountMatching(AbstractCreature owner, String powerID) {
        int res = 0;
        if (owner != null && owner.powers != null) {
            for (AbstractPower p : owner.powers) {
                if (p != null && p.ID != null && p.ID.contains(powerID)) {
                    res += p.canGoNegative ? p.amount : Math.max(1, p.amount);
                }
            }
        }

        return res;
    }

    public static int getPowerAmountMatching(String powerID) {
        return getPowerAmountMatching(AbstractDungeon.player, powerID);
    }

    public static ArrayList<AbstractPower> getPowerMatchingSingle(AbstractCreature creature, String powerID) {
        return getPowerMatchingSingle(new ArrayList<>(), creature, powerID);
    }

    public static ArrayList<AbstractPower> getPowerMatchingSingle(ArrayList<AbstractPower> list, AbstractCreature creature, String powerID) {
        if (creature != null && creature.powers != null) {
            for (AbstractPower p : creature.powers) {
                if (p != null && p.ID != null && p.ID.contains(powerID)) {
                    list.add(p);
                }
            }
        }

        return list;
    }

    public static ArrayList<AbstractPower> getPowers(String powerID) {
        return getPowers(GameUtilities.getAllCharacters(true), powerID);
    }

    public static ArrayList<AbstractPower> getPowers(List<AbstractCreature> creatures, String powerID) {
        final ArrayList<AbstractPower> result = new ArrayList<>();
        for (AbstractCreature c : creatures) {
            final AbstractPower t = getPower(c, powerID);
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    public static <T extends AbstractPower> ArrayList<T> getPowers(Class<T> powerType) {
        return getPowers(GameUtilities.getAllCharacters(true), powerType);
    }

    public static <T extends AbstractPower> ArrayList<T> getPowers(List<AbstractCreature> creatures, Class<T> powerType) {
        final ArrayList<T> result = new ArrayList<>();
        for (AbstractCreature c : creatures) {
            final T t = getPower(c, powerType);
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    public static ArrayList<AbstractPower> getPowersMatching(String powerID) {
        return getPowersMatching(GameUtilities.getAllCharacters(true), powerID);
    }

    public static ArrayList<AbstractPower> getPowersMatching(List<AbstractCreature> creatures, String powerID) {
        final ArrayList<AbstractPower> result = new ArrayList<>();
        for (AbstractCreature c : creatures) {
            getPowerMatchingSingle(result, c, powerID);
        }

        return result;
    }

    public static AbstractCard getRandomAnyColorCombatCard() {
        return getRandomElement(getAvailableCardsForAllColors(null), AbstractDungeon.cardRandomRng);
    }

    public static AbstractCard getRandomAnyColorCombatCard(FuncT1<Boolean, AbstractCard> filter) {
        return getRandomElement(getAvailableCardsForAllColors(filter), AbstractDungeon.cardRandomRng);
    }

    public static AbstractCreature getRandomCharacter(boolean aliveOnly) {
        return getRandomElement(getAllCharacters(aliveOnly), PGR.dungeon.getRNG());
    }

    public static AbstractCard getRandomCombatCard() {
        return getRandomElement(getCardsFromStandardCombatPools(null), AbstractDungeon.cardRandomRng);
    }

    // Create a random card that matches the given parameters. Note that these random card methods poll from the in-combat card pool, so healing cards are already filtered out
    public static AbstractCard getRandomCombatCard(FuncT1<Boolean, AbstractCard> filter) {
        return getRandomElement(getCardsFromStandardCombatPools(filter), AbstractDungeon.cardRandomRng);
    }

    public static <T> T getRandomElement(List<T> list) {
        return getRandomElement(list, PGR.dungeon.getRNG());
    }

    public static <T> T getRandomElement(List<T> list, Random rng) {
        int size = list.size();
        return (size > 0) ? list.get(rng.random(list.size() - 1)) : null;
    }

    public static <T> T getRandomElement(T[] arr) {
        return getRandomElement(arr, PGR.dungeon.getRNG());
    }

    public static <T> T getRandomElement(T[] arr, com.megacrit.cardcrawl.random.Random rng) {
        int size = arr.length;
        return (size > 0) ? arr[rng.random(arr.length - 1)] : null;
    }

    public static AbstractMonster getRandomEnemy(boolean aliveOnly) {
        return getRandomElement(getEnemies(aliveOnly), PGR.dungeon.getRNG());
    }

    public static PCLCardAlly getRandomSummon(Boolean isAlive) {
        return getRandomElement(getSummons(isAlive), PGR.dungeon.getRNG());
    }

    public static int getRelicCount() {
        return player != null ? player.relics.size() : 0;
    }

    public static String getRelicNameForID(String relicID) {
        if (relicID != null) {
            // NOT using RelicLibrary.getRelic as the replacement patching on that method may cause text glitches or infinite loops in this method
            AbstractRelic c = RelicLibraryPatches.getDirectRelic(relicID);
            if (c != null) {
                return c.name;
            }

            // Try to load data on relics not in the library
            PCLRelicData data = PCLRelicData.getStaticData(relicID);
            if (data != null) {
                return data.strings.NAME;
            }

            // Try to load data from slots. Do not actually create relics here to avoid infinite loops
            PCLCustomRelicSlot slot = PCLCustomRelicSlot.get(relicID);
            if (slot != null) {
                HashMap<Settings.GameLanguage, RelicStrings> languageMap = PCLDynamicRelicData.parseLanguageStrings(slot.languageStrings);
                RelicStrings language = languageMap != null ? PCLDynamicRelicData.getStringsForLanguage(languageMap) : null;
                if (language != null) {
                    return language.NAME;
                }
            }
        }
        return "";
    }

    public static ArrayList<String> getRelicPool(AbstractRelic.RelicTier tier) {
        switch (tier) {
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

    public static HashMap<String, AbstractRelic> getRelics(AbstractCard.CardColor cardColor) {
        switch (cardColor) {
            case COLORLESS:
                return ReflectionHacks.getPrivateStatic(RelicLibrary.class, "sharedRelics");
            case RED:
                return ReflectionHacks.getPrivateStatic(RelicLibrary.class, "redRelics");
            case GREEN:
                return ReflectionHacks.getPrivateStatic(RelicLibrary.class, "greenRelics");
            case BLUE:
                return ReflectionHacks.getPrivateStatic(RelicLibrary.class, "blueRelics");
            case PURPLE:
                return ReflectionHacks.getPrivateStatic(RelicLibrary.class, "purpleRelics");
            default:
                return BaseMod.getRelicsInCustomPool(cardColor);
        }
    }

    public static List<AbstractCard.CardRarity> getStandardCardRarities() {
        return Arrays.asList(
                AbstractCard.CardRarity.BASIC,
                AbstractCard.CardRarity.COMMON,
                AbstractCard.CardRarity.UNCOMMON,
                AbstractCard.CardRarity.RARE,
                AbstractCard.CardRarity.CURSE,
                AbstractCard.CardRarity.SPECIAL
        );
    }

    public static List<AbstractPotion.PotionRarity> getStandardPotionTiers() {
        return Arrays.asList(
                AbstractPotion.PotionRarity.COMMON,
                AbstractPotion.PotionRarity.UNCOMMON,
                AbstractPotion.PotionRarity.RARE
        );
    }

    public static List<AbstractRelic.RelicTier> getStandardRelicTiers() {
        return Arrays.asList(
                AbstractRelic.RelicTier.STARTER,
                AbstractRelic.RelicTier.COMMON,
                AbstractRelic.RelicTier.UNCOMMON,
                AbstractRelic.RelicTier.RARE,
                AbstractRelic.RelicTier.SHOP,
                AbstractRelic.RelicTier.BOSS,
                AbstractRelic.RelicTier.SPECIAL
        );
    }

    public static ArrayList<PCLCardAlly> getSummons(Boolean isAlive) {
        final ArrayList<PCLCardAlly> monsters = new ArrayList<>();
        fillWithSummons(isAlive, monsters);
        return monsters;
    }

    public static int getTempHP() {
        return getTempHP(player);
    }

    public static int getTempHP(AbstractCreature creature) {
        return creature != null ? TempHPField.tempHp.get(creature) : 0;
    }

    public static int getTimesPlayedThisTurn(AbstractCard card) {
        int result = 0;
        for (AbstractCard c : actionManager.cardsPlayedThisTurn) {
            if (c.uuid.equals(card.uuid)) {
                result += 1;
            }
        }

        return result;
    }

    public static EUITooltip getTooltipForPowerType(AbstractPower.PowerType type) {
        switch (type) {
            case BUFF:
                return PGR.core.tooltips.buff;
            case DEBUFF:
                return PGR.core.tooltips.debuff;
        }
        return null;
    }

    // TODO factor in prismatic shard
    public static int getTotalCardsInPlay() {
        return AbstractDungeon.colorlessCardPool.size()
                + AbstractDungeon.commonCardPool.size()
                + AbstractDungeon.uncommonCardPool.size()
                + AbstractDungeon.rareCardPool.size()
                + AbstractDungeon.curseCardPool.size();
    }

    // TODO factor in prismatic shard
    public static int getTotalCardsInRewardPool() {
        return AbstractDungeon.commonCardPool.size()
                + AbstractDungeon.uncommonCardPool.size()
                + AbstractDungeon.rareCardPool.size();
    }

    public static int getTotalCardsPlayed(AbstractCard ignoreLast, boolean currentTurn) {
        final ArrayList<AbstractCard> cards = currentTurn
                ? AbstractDungeon.actionManager.cardsPlayedThisTurn
                : AbstractDungeon.actionManager.cardsPlayedThisCombat;
        return (cards.size() > 0 && (cards.get(cards.size() - 1) == ignoreLast)) ? (cards.size() - 1) : cards.size();
    }

    public static <T> T getTrulyRandomElement(List<T> list) {
        int size = list.size();
        return (size > 0) ? list.get(MathUtils.random(size - 1)) : null;
    }

    public static <T> T getTrulyRandomElement(T[] arr) {
        int size = arr.length;
        return (size > 0) ? arr[MathUtils.random(size - 1)] : null;
    }

    public static ArrayList<AbstractOrb> getUniqueOrbs(int count) {
        final ArrayList<AbstractOrb> orbs = new ArrayList<>();
        for (AbstractOrb orb : player.orbs) {
            if (!isValidOrb(orb)) {
                continue;
            }

            boolean skip = false;
            for (AbstractOrb o : orbs) {
                if (o.ID.equals(orb.ID)) {
                    skip = true;
                    break;
                }
            }

            if (!skip) {
                orbs.add(orb);

                if (orbs.size() >= count) {
                    return orbs;
                }
            }
        }

        return orbs;
    }

    public static int getUniqueOrbsCount() {
        final HashSet<String> orbs = new HashSet<>();
        for (AbstractOrb orb : player.orbs) {
            if (isValidOrb(orb)) {
                orbs.add(orb.ID);
            }
        }

        return orbs.size();
    }

    public static List<PCLAffinity> getVisiblePCLAffinities(AbstractCard card) {
        PCLCardAffinities cardAffinities = getPCLCardAffinities(card);
        return cardAffinities != null ? cardAffinities.getAffinities(false, true) : Collections.singletonList(PCLAffinity.General);
    }

    public static int getXCostEnergy(AbstractCard card) {
        int amount = card != null && card.energyOnUse != -1 ? card.energyOnUse : EnergyPanel.getCurrentEnergy();

        return CombatManager.onModifyXCost(amount, card);
    }

    public static boolean hasAffinity(AbstractCard card, PCLAffinity affinity) {
        return hasAffinity(card, affinity, true);
    }

    public static boolean hasAffinity(AbstractCard card, PCLAffinity affinity, boolean useStar) {
        return getPCLCardAffinityLevel(card, affinity, useStar) > 0;
    }

    public static boolean hasAllAffinity(AbstractCard card, Collection<PCLAffinity> affinities) {
        return EUIUtils.all(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAllAffinity(AbstractCard card, PCLAffinity... affinities) {
        return EUIUtils.all(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAnyAffinity(AbstractCard card, Collection<PCLAffinity> affinities) {
        return EUIUtils.any(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasAnyAffinity(AbstractCard card, PCLAffinity... affinities) {
        return EUIUtils.any(affinities, affinity -> hasAffinity(card, affinity, true));
    }

    public static boolean hasArtifact(AbstractCreature creature) {
        return creature.hasPower(ArtifactPower.POWER_ID);
    }

    public static boolean hasDarkAffinity(AbstractCard card) {
        return getPCLCardAffinityLevel(card, PCLAffinity.Purple, true) > 0;
    }

    public static boolean hasEncounteredEvent(String eventID) {
        return PGR.dungeon.getMapData(eventID) != null;
    }

    public static boolean hasLightAffinity(AbstractCard card) {
        return getPCLCardAffinityLevel(card, PCLAffinity.Yellow, true) > 0;
    }

    public static boolean hasOrb(String orbID) {
        return getOrbCount(orbID) > 0;
    }

    public static boolean hasRelic(String relicID) {
        return player != null && player.hasRelic(relicID);
    }

    public static boolean hasRelicEffect(String relicID) {
        return hasRelic(relicID)
                || CombatManager.getCombatData(relicID, false)
                || CombatManager.getTurnData(relicID, false);
    }

    public static void highlightMatchingCards(PCLAffinity affinity) {
        highlightMatchingCards(c -> hasAffinity(c, affinity));
    }

    public static void highlightMatchingCards(FuncT1<Boolean, AbstractCard> cardFunc) {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (!cardFunc.invoke(c)) {
                c.transparency = 0.35f;
            }
        }
    }

    public static boolean inBossRoom() {
        return getCurrentRoom() instanceof MonsterRoomBoss;
    }

    public static boolean inEliteOrBossRoom() {
        final AbstractRoom room = getCurrentRoom();
        return room instanceof MonsterRoomBoss || (room != null && room.eliteTrigger);
    }

    public static boolean inEliteRoom() {
        final AbstractRoom room = getCurrentRoom();
        return (room != null) && room.eliteTrigger;
    }

    public static boolean inGame() {
        return CardCrawlGame.GameMode.GAMEPLAY.equals(CardCrawlGame.mode);
    }

    public static boolean inStance(PCLStanceHelper stance) {
        return player != null && player.stance != null && player.stance.ID.equals(stance.ID);
    }

    public static boolean inStance(String stanceID) {
        return player != null && player.stance != null && player.stance.ID.equals(stanceID);
    }

    public static void increaseHandSizePermanently() {
        for (AbstractBlight blight : player.blights) {
            if (blight instanceof UpgradedHand) {
                ((UpgradedHand) blight).addAmount(1);
                return;
            }
        }

        obtainBlight(player.hb.cX, player.hb.cY, new UpgradedHand());
    }

    public static boolean isActingColor(AbstractCard.CardColor co) {
        return getActingColor() == co;
    }

    // Move intent is the source of truth; the actual intent might not be set in time for start of turn effects
    public static boolean isAttacking(AbstractCreature monster) {
        return monster instanceof AbstractMonster && PCLIntentType.Attack.hasIntent(PCLIntentInfo.get((AbstractMonster) monster).getMoveIntent());
    }

    // Custom cards should always be treated as seen
    public static boolean isCardLocked(String id) {
        return UnlockTracker.isCardLocked(id) || (!UnlockTracker.isCardSeen(id) && PCLCustomCardSlot.get(id) == null);
    }

    // Both colorless and curse are character-independent
    public static boolean isColorlessCardColor(AbstractCard.CardColor cardColor) {
        return cardColor == AbstractCard.CardColor.COLORLESS || cardColor == AbstractCard.CardColor.CURSE;
    }

    public static boolean isCommonBuff(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null && helper.isCommon && helper.type == AbstractPower.PowerType.BUFF;
    }

    public static boolean isCommonDebuff(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null && helper.isCommon && helper.type == AbstractPower.PowerType.DEBUFF;
    }

    public static boolean isCommonPower(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null && helper.isCommon;
    }

    public static boolean isDeadOrEscaped(AbstractCreature target) {
        return target == null || target.isDeadOrEscaped() || target.currentHealth <= 0;
    }

    public static boolean isDebuff(AbstractPower power) {
        return power != null && power.type == AbstractPower.PowerType.DEBUFF;
    }

    public static boolean isEnemy(AbstractCreature c) {
        return c != null && !c.isPlayer && !(c instanceof PCLCardAlly);
    }

    public static boolean isFatal(AbstractCreature enemy, boolean includeMinions) {
        return (enemy.isDead || enemy.isDying || enemy.currentHealth <= 0)
                && !enemy.hasPower(RegrowPower.POWER_ID)
                && (includeMinions || (!enemy.hasPower(MinionPower.POWER_ID) && !(enemy instanceof PCLCardAlly)));
    }

    public static boolean isMonster(AbstractCreature c) {
        return c != null && !c.isPlayer;
    }

    public static boolean isObtainableInCombat(AbstractCard c) {
        return !c.hasTag(AbstractCard.CardTags.HEALING) && !PCLCardTag.Fleeting.has(c) && !c.isLocked;
    }

    protected static boolean isOrbBlockGranting(AbstractOrb o) {
        return Frost.ORB_ID.equals(o.ID);
    }

    public static boolean isPCLActingCardColor(AbstractCard card) {
        return isPCLCardColor(getActingCardColor(card));
    }

    public static boolean isPCLBuff(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null && helper.type == AbstractPower.PowerType.BUFF;
    }

    // PCL check that includes colorless/curse
    public static boolean isPCLCardColor(AbstractCard.CardColor cardColor) {
        return isPCLOnlyCardColor(cardColor) || isColorlessCardColor(cardColor);
    }

    public static boolean isPCLDebuff(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null && helper.type == AbstractPower.PowerType.DEBUFF;
    }

    // PCL check that excludes colorless/curse
    public static boolean isPCLOnlyCardColor(AbstractCard.CardColor cardColor) {
        return EUIUtils.any(PGR.getRegisteredResources(), r -> r.cardColor == cardColor);
    }

    public static boolean isPCLPlayerClass() {
        return AbstractDungeon.player != null && isPCLPlayerClass(AbstractDungeon.player.chosenClass);
    }

    public static boolean isPCLPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        return EUIUtils.any(PGR.getRegisteredResources(), r -> r.playerClass == playerClass);
    }

    public static boolean isPCLPower(AbstractPower power) {
        PCLPowerData helper = PCLPowerData.getStaticData(power.ID);
        return helper != null;
    }

    public static boolean isPlayer(AbstractCreature c) {
        return c != null && c.isPlayer;
    }

    public static boolean isPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        return player != null && player.chosenClass == playerClass;
    }

    public static boolean isPlayerTurn(boolean beforeEndTurnEvents) {
        boolean result = !AbstractDungeon.actionManager.turnHasEnded;
        if (beforeEndTurnEvents) {
            result &= CombatManager.isPlayerTurn && !player.isEndingTurn;
        }

        return result;
    }

    protected static boolean isPowerBlockGranting(AbstractPower p) {
        return PlatedArmorPower.POWER_ID.equals(p.ID) || MetallicizePower.POWER_ID.equals(p.ID);
    }

    public static boolean isPriorityTarget(AbstractCreature c) {
        return c != null && c.powers != null && EUIUtils.any(c.powers, p -> p instanceof PCLPower && ((PCLPower) p).isPriorityTarget());
    }

    // Custom relics should always be treated as seen
    public static boolean isRelicLocked(String id) {
        return UnlockTracker.isRelicLocked(id) || (!UnlockTracker.isRelicSeen(id) && PCLCustomRelicSlot.get(id) == null);
    }

    public static boolean isRelicTierSpawnable(AbstractRelic.RelicTier tier) {
        switch (tier) {
            case COMMON:
            case UNCOMMON:
            case RARE:
            case BOSS:
            case SHOP:
                return true;
        }
        return false;
    }

    public static boolean isStarter(AbstractCard card) {
        final ArrayList<AbstractCard> played = AbstractDungeon.actionManager.cardsPlayedThisTurn;
        return played == null || played.isEmpty() || (played.size() == 1 && played.get(0) == card);
    }

    public static boolean isTopPanelVisible() {
        return GameUtilities.inGame() && AbstractDungeon.topPanel != null && !Settings.hideTopBar;
    }

    public static boolean isTurnBasedPower(AbstractPower power) {
        return ReflectionHacks.getPrivate(power, AbstractPower.class, "isTurnBased");
    }

    public static boolean isUnplayableThisTurn(AbstractCard card) {
        return PCLCardTag.Unplayable.has(card) || CombatManager.unplayableCards().contains(card.uuid);
    }

    public static boolean isValidOrb(AbstractOrb orb) {
        return orb != null && !(orb instanceof EmptyOrbSlot);
    }

    public static boolean isValidTarget(AbstractCreature target) {
        return target != null && !isDeadOrEscaped(target);
    }

    public static CardGroup makeCardGroupRandomized(Collection<AbstractCard> source, int limit, boolean makeCopy) {
        final RandomizedList<AbstractCard> choices = new RandomizedList<>(source);
        CardGroup choice = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        while (choice.size() < limit) {
            AbstractCard c = choices.retrieve(AbstractDungeon.cardRandomRng);
            if (c != null) {
                choice.addToBottom(makeCopy ? c.makeCopy() : c);
            }
        }
        return choice;
    }

    public static void modifyAffinityLevel(AbstractCard card, PCLAffinity affinity, int level, boolean relative) {
        PCLCardAffinities affinities = null;
        if (card instanceof PCLCard) {
            affinities = ((PCLCard) card).affinities;
        }
        else {
            AffinityDisplayModifier mod = AffinityDisplayModifier.get(card);
            if (mod == null) {
                mod = new AffinityDisplayModifier();
                CardModifierManager.addModifier(card, mod);
            }
            affinities = mod.affinities;
        }

        if (relative) {
            affinities.add(affinity, level);
        }
        else {
            affinities.set(affinity, level);
        }
    }

    public static int modifyCardDrawPerTurn(int amount, int minimumCardDraw) {
        final int newAmount = player.gameHandSize + amount;
        if (newAmount < minimumCardDraw) {
            amount += (minimumCardDraw - newAmount);
        }

        player.gameHandSize += amount;
        return amount;
    }

    public static void modifyCostForCombat(AbstractCard card, int amount, boolean relative) {
        final int previousCost = card instanceof PCLCard ? ((PCLCard) card).cardData.getCost(((PCLCard) card).getForm()) : card.cost;
        if (relative) {
            card.costForTurn = Math.max(0, card.costForTurn + amount);
            card.cost = Math.max(0, card.cost + amount);
        }
        else {
            card.costForTurn = amount + (card.costForTurn - card.cost);
            card.cost = amount;
        }

        if (card.cost != previousCost) {
            card.isCostModified = true;
        }
    }

    public static void modifyCostForTurn(AbstractCard card, int amount, boolean relative) {
        card.costForTurn = relative ? Math.max(0, card.costForTurn + amount) : amount;
        card.isCostModifiedForTurn = (card.cost != card.costForTurn);
    }

    public static int modifyEnergyGainPerTurn(int amount, int minimumEnergy) {
        final int newAmount = player.energy.energy + amount;
        if (newAmount < minimumEnergy) {
            amount += (minimumEnergy - newAmount);
        }

        player.energy.energy += amount;
        return amount;
    }

    public static void modifyMagicNumber(AbstractCard card, int amount, boolean temporary) {
        card.magicNumber = amount;
        if (!temporary) {
            card.baseMagicNumber = card.magicNumber;
        }
        card.isMagicNumberModified = (card.magicNumber != card.baseMagicNumber);
    }

    public static void modifyOrbBaseEvokeAmount(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb) {
        if (canModifyNonFocusOrb || (canOrbApplyFocus(orb) && canOrbApplyFocusToEvoke(orb))) {
            modifyOrbField(orb, "baseEvokeAmount", amount, isRelative);
        }
    }

    public static void modifyOrbBaseFocus(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb) {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb)) {
            if (canModifyNonFocusOrb || canOrbApplyFocusToEvoke(orb)) {
                modifyOrbField(orb, "baseEvokeAmount", amount, isRelative);
            }
            modifyOrbField(orb, "basePassiveAmount", amount, isRelative);
        }
    }

    public static void modifyOrbBasePassiveAmount(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb) {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb)) {
            modifyOrbField(orb, "basePassiveAmount", amount, isRelative);
        }
    }

    public static void modifyOrbField(AbstractOrb orb, String field, int amount, boolean isRelative) {
        try {
            Field f = AbstractOrb.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(orb, isRelative ? amount + (int) f.get(orb) : amount);
            orb.applyFocus();
            orb.updateDescription();
        }
        catch (Exception e) {
            EUIUtils.logWarning(orb, "Orb could not be modified: " + e.getLocalizedMessage());
        }
    }

    public static void modifyOrbTemporaryFocus(AbstractOrb orb, int amount, boolean isRelative, boolean canModifyNonFocusOrb) {
        if (canModifyNonFocusOrb || canOrbApplyFocus(orb)) {
            orb.passiveAmount = isRelative ? orb.passiveAmount + amount : amount;
            if (canModifyNonFocusOrb || canOrbApplyFocusToEvoke(orb)) {
                orb.evokeAmount = isRelative ? orb.evokeAmount + amount : amount;
            }
        }
    }

    public static void modifyTag(AbstractCard card, PCLCardTag tag, int value) {
        modifyTag(card, tag, value, false);
    }

    public static void modifyTag(AbstractCard card, PCLCardTag tag, int value, boolean relative) {
        if (tag != null) {
            int targetValue = tag.add(card, value);
            PCLCard pCard = EUIUtils.safeCast(card, PCLCard.class);
            if (pCard != null) {
                // If tags are in the description, we need to update the card description to get it to show up
                if (PGR.config.displayCardTagDescription.get()) {
                    pCard.initializeDescription();
                }
            }
            // For non-PCL cards, add modifier so they can show them in the description
            else {
                TagDisplayModifier mod = TagDisplayModifier.get(card);
                if (mod == null) {
                    mod = new TagDisplayModifier();
                    CardModifierManager.addModifier(card, mod);
                }
            }
            CombatManager.onTagChanged(card, tag, value);
        }
    }

    public static void obtainBlight(float cX, float cY, AbstractBlight blight) {
        if (blight.unique) {
            for (AbstractBlight b : player.blights) {
                if (b.blightID.equals(blight.blightID)) {
                    b.incrementUp();
                    b.flash();
                    return;
                }
            }
        }
        AbstractRoom room = getCurrentRoom();
        if (room != null) {
            room.spawnBlightAndObtain(cX, cY, blight);
        }
        else {
            blight.instantObtain(player, player.blights.size(), false);
        }
    }

    // In spite of defining the unique field for the appropriate blights, the game never actually makes use of this field in spawning or obtaining logic...
    public static void obtainBlightWithoutEffect(AbstractBlight blight) {
        if (blight.unique) {
            for (AbstractBlight b : player.blights) {
                if (b.blightID.equals(blight.blightID)) {
                    b.incrementUp();
                    return;
                }
            }
        }
        blight.instantObtain(player, player.blights.size(), false);
    }

    public static void obtainRelic(float cX, float cY, AbstractRelic relic) {
        AbstractRoom room = getCurrentRoom();
        if (room != null) {
            room.spawnRelicAndObtain(cX, cY, relic);
        }
    }

    public static void obtainRelicFromEvent(AbstractRelic relic) {
        UnlockTracker.markRelicAsSeen(relic.relicId);
        relic.isSeen = true;
        relic.instantObtain();
        CardCrawlGame.metricData.addRelicObtainData(relic);
    }

    public static ArrayList<AbstractCard> pickCardsFromList(RandomizedList<AbstractCard> possible, int count) {
        ArrayList<AbstractCard> returned = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AbstractCard c = possible.retrieve(AbstractDungeon.cardRandomRng, true);
            if (c != null) {
                returned.add(c);
            }
        }
        return returned;
    }

    public static void refreshOrbs() {
        for (AbstractOrb orb : player.orbs) {
            if (orb != null && !(orb instanceof EmptyOrbSlot)) {
                orb.applyFocus();
            }
        }
    }

    public static void removeBlock(AbstractCard card) {
        card.baseBlock = card.block = 0;
        card.isBlockModified = false;
    }

    public static void removeRelics(AbstractRelic... r) {
        removeRelics(Arrays.asList(r));
    }

    public static void removeRelics(Collection<AbstractRelic> relics) {
        player.relics.removeAll(relics);
        for (AbstractRelic r : relics) {
            r.onUnequip();
        }
        if (relics.size() > 0) {
            player.reorganizeRelics();
        }
    }

    public static boolean requiresTarget(AbstractCard card) {
        return card.target == AbstractCard.CardTarget.ENEMY || card.target == AbstractCard.CardTarget.SELF_AND_ENEMY || card.type == PCLEnum.CardType.SUMMON;
    }

    public static void resetAffinityLevels(AbstractCard card) {
        if (card instanceof PCLCard) {
            ((PCLCard) card).affinities.clearLevelsOnly();
        }
        else {
            AffinityDisplayModifier mod = AffinityDisplayModifier.get(card);
            if (mod != null) {
                CardModifierManager.removeSpecificModifier(card, mod, true);
            }
        }
    }

    public static void resetVisualProperties(AbstractCard card) {
        card.untip();
        card.unhover();
        card.unfadeOut();
        card.targetDrawScale = card.drawScale = 0.75f;
        card.transparency = card.targetTransparency = 1;
        card.angle = card.targetAngle = 0;
    }

    public static boolean retain(AbstractCard card) {
        if (canRetain(card)) {
            PCLCardTag.Retain.add(card, 1);
            CombatManager.onTagChanged(card, PCLCardTag.Retain, 1);
            return true;
        }

        return false;
    }

    public static float scale(float value) {
        return Settings.scale * value;
    }

    public static float screenH(float value) {
        return Settings.HEIGHT * value;
    }

    public static float screenW(float value) {
        return Settings.WIDTH * value;
    }

    public static void setCardTag(AbstractCard card, AbstractCard.CardTags tag, boolean value) {
        if (value) {
            if (!card.tags.contains(tag)) {
                card.tags.add(tag);
            }
        }
        else {
            card.tags.remove(tag);
        }
    }

    public static void setCreatureAnimation(AbstractCreature creature, String id) {
        if (creature instanceof PCLCharacter) {
            ((PCLCharacter) creature).setCreature(id);
        }
    }

    public static void setCreatureAnimation(AbstractCreature creature, String id, String idleStr, String hitStr) {
        if (creature instanceof PCLCharacter) {
            ((PCLCharacter) creature).setCreature(id, idleStr, hitStr);
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

    public static void setTopPanelVisible(boolean visible) {
        Settings.hideTopBar = !visible;
        Settings.hideRelics = !visible;

        if (AbstractDungeon.topPanel != null) {
            AbstractDungeon.topPanel.unhoverHitboxes();
            //AbstractDungeon.topPanel.potionUi.isHidden = !visible;
        }
    }

    public static void setUnplayableThisTurn(AbstractCard card) {
        CombatManager.unplayableCards().add(card.uuid);
        CombatManager.onTagChanged(card, PCLCardTag.Unplayable, 1);
    }

    public static String textForPowerType(AbstractPower.PowerType type) {
        switch (type) {
            case BUFF:
                return PGR.core.tooltips.buff.title;
            case DEBUFF:
                return PGR.core.tooltips.debuff.title;
            default:
                return PGR.core.strings.power_neutral;
        }
    }

    public static String toInternalAtlasBetaPath(String path) {
        return BETA_PATH + path + ".png";
    }

    public static String toInternalAtlasPath(String path) {
        return PORTRAIT_PATH + path + ".png";
    }

    public static EUIKeywordTooltip tooltipForType(AbstractCard.CardType type) {
        switch (type) {
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

    public static Vector2 tryGetPosition(CardGroup group, AbstractCard card) {
        if (group != null) {
            if (group.type == CardGroup.CardGroupType.DRAW_PILE) {
                return new Vector2(CardGroup.DRAW_PILE_X, CardGroup.DRAW_PILE_Y);
            }
            else if (group.type == CardGroup.CardGroupType.DISCARD_PILE) {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y);
            }
            else if (group.type == CardGroup.CardGroupType.EXHAUST_PILE) {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y + (Settings.scale * 30f));
            }
            else if (group == CombatManager.PURGED_CARDS) {
                return new Vector2(CardGroup.DISCARD_PILE_X, CardGroup.DRAW_PILE_Y + (Settings.scale * 100f));
            }
        }

        return card == null ? null : new Vector2(card.current_x, card.current_y);
    }

    public static boolean trySetPosition(CardGroup group, AbstractCard card) {
        Vector2 pos = tryGetPosition(group, null);
        if (pos == null) {
            return false;
        }

        card.current_x = pos.x;
        card.current_y = pos.y;

        return true;
    }

    public static void unlockAllKeys() {
        if (!Settings.isFinalActAvailable) {
            Settings.isFinalActAvailable = true;
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.IRONCLAD.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.THE_SILENT.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.DEFECT.name() + "_WIN", true);
            CardCrawlGame.playerPref.putBoolean(AbstractPlayer.PlayerClass.WATCHER.name() + "_WIN", true);

            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.RUBY_PLUS_KEY)) {
                UnlockTracker.unlockAchievement(AchievementGrid.RUBY_PLUS_KEY);
            }
            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.EMERALD_PLUS_KEY)) {
                UnlockTracker.unlockAchievement(AchievementGrid.EMERALD_PLUS_KEY);
            }
            if (UnlockTracker.isAchievementUnlocked(AchievementGrid.SAPPHIRE_PLUS_KEY)) {
                UnlockTracker.unlockAchievement(AchievementGrid.SAPPHIRE_PLUS_KEY);
            }
        }
    }

    public static void unlockAscension(Prefs playerPrefs, int ascension) {
        if (playerPrefs.getInteger("ASCENSION_LEVEL", 0) < ascension) {
            playerPrefs.putInteger("ASCENSION_LEVEL", ascension);
            playerPrefs.putInteger("LAST_ASCENSION_LEVEL", ascension);
            playerPrefs.flush();
        }
    }

    public static void updatePowerDescriptions() {
        for (AbstractCreature c : GameUtilities.getAllCharacters(true)) {
            for (AbstractPower p : c.powers) {
                p.updateDescription();
            }
        }
    }

    public static void upgradeRelic(AbstractRelic relic, int amount) {
        if (relic instanceof PCLRelic) {
            for (int i = 0; i < amount; i++) {
                ((PCLRelic) relic).upgrade();
            }
            relic.flash();
        }
    }

    public static boolean useArtifact(AbstractCreature target) {
        final AbstractPower artifact = getPower(target, ArtifactPower.POWER_ID);
        if (artifact != null) {
            PCLActions.top.add(new TextAboveCreatureAction(target, ApplyPowerAction.TEXT[0]));
            PCLSFX.play(PCLSFX.NULLIFY_SFX);
            artifact.flashWithoutSound();
            artifact.onSpecificTrigger();

            return false;
        }

        return true;
    }

    public static int useXCostEnergy(AbstractCard card) {
        int amount = card.energyOnUse != -1 ? card.energyOnUse : EnergyPanel.getCurrentEnergy();
        amount = CombatManager.onTryUseXCost(amount, card);

        if (!card.freeToPlayOnce) {
            AbstractDungeon.player.energy.use(card.energyOnUse);
        }

        CombatManager.queueRefreshHandLayout();
        return amount;
    }

}
