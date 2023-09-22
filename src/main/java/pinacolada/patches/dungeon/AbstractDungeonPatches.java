package pinacolada.patches.dungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.SpawnModificationCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.monsters.PCLTutorialMonster;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public class AbstractDungeonPatches {
    private static int tempID;
    public static boolean filterCardGroupForValid;

    public static AbstractCard makeTempCard() {
        // Otherwise, create a dummy card
        AbstractCard card = new QuestionMark();
        // Modify ID to avoid infinite loops in AbstractDungeon
        card.cardID = card.cardID + tempID;
        tempID += 1;
        return card;
    }

    protected static AbstractCard makeTempCard(AbstractCard.CardType type) {
        AbstractCard card = makeTempCard();
        card.type = type;
        return card;
    }

    // If the targeted pool size is less than or equal the number of cards we currently have picked out, then getRewardCards will spin into infinity
    public static boolean notEnoughCards(AbstractCard.CardRarity rarity, ArrayList<AbstractCard> retVal) {
        CardGroup g = GameUtilities.getCardPool(rarity);
        return g == null || g.size() <= retVal.size();
    }

    // If no suitable card is found, make a replacement because the method will crash if no card is actually found
    protected static AbstractCard tryReturnCard(AbstractCard card, AbstractCard.CardRarity rarity, Random rng) {
        return tryReturnCard(card, rarity, rng, PGR.dungeon::canObtainCopy);
    }

    protected static AbstractCard tryReturnCard(AbstractCard card, AbstractCard.CardRarity rarity, Random rng, FuncT1<Boolean, AbstractCard> filterFunc) {
        if (card == null) {
            EUIUtils.logError(AbstractDungeonPatches.class, "Failed to find card with specified rarity " + rarity);

            // First try to find a replacement of another rarity if a rarity was specified
            rarity = rarity != null ? PCLDungeon.getNextRarity(rarity) : null;
            if (rarity != null) {
                card = PGR.dungeon.getRandomCard(rarity, filterFunc, rng, true);
                if (card != null) {
                    return card;
                }
            }

            // Otherwise, create a dummy card
            card = makeTempCard();
        }
        return card;
    }

    // Same as above but with type
    protected static AbstractCard tryReturnCard(AbstractCard card, AbstractCard.CardRarity rarity, AbstractCard.CardType type, Random rng) {
        if (card == null) {
            EUIUtils.logError(AbstractDungeonPatches.class, "Failed to find card with specified rarity " + rarity + " and type " + type);

            rarity = rarity != null ? PCLDungeon.getNextRarity(rarity) : null;
            if (rarity != null) {
                card = PGR.dungeon.getRandomCard(rarity, type, rng, true);
                if (card != null) {
                    return card;
                }
            }

            card = makeTempCard(type);
        }
        return card;
    }

    // Have to do these patches manually to avoid having the card pool get initialized twice on game start
    @SpirePatch(
            clz=AbstractDungeon.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez={
                    String.class,
                    String.class,
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class AbstractDungeonPatches_StartGame {

        public static void Postfix(AbstractDungeon __instance,
                                   String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
            if (levelId.equals(Exordium.ID) && AbstractDungeon.floorNum == 0) {
                PGR.dungeon.initializeData();
            }
            PGR.dungeon.initializeCardPool();
        }

    }

    @SpirePatch(
            clz=AbstractDungeon.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez={
                    String.class,
                    AbstractPlayer.class,
                    SaveFile.class
            }
    )
    public static class AbstractDungeonPatches_ContinueGame {

        public static void Postfix(Object __obj_instance,
                                   String name, AbstractPlayer p, SaveFile saveFile) {
            PGR.dungeon.initializeData();
            PGR.dungeon.initializeCardPool();
        }

    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getMonsterForRoomCreation")
    public static class AbstractDungeonPatches_GetMonsterForRoomCreation {
        @SpirePostfixPatch
        public static MonsterGroup postfix(MonsterGroup retVal, AbstractDungeon __instance) {
            // Note that this will not affect boss rooms because they use a different method to obtain their monsters. We do not want these tutorials to pop up over boss rooms
            MonsterGroup group = PCLTutorialMonster.tryStart();
            return group != null ? group : retVal;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "initializeRelicList")
    public static class AbstractDungeonPatches_InitializeRelicList {
        @SpirePostfixPatch
        public static void postfix(AbstractDungeon __instance) {
            PGR.dungeon.loadCustomRelics(AbstractDungeon.player);
            PGR.dungeon.banRelicsFromPools();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "dungeonTransitionSetup")
    public static class AbstractDungeonPatches_DungeonTransitionSetup {
        @SpirePostfixPatch
        public static void postfix() {
            AbstractPlayer.PlayerClass pClass = GameUtilities.getPlayerClass();
            if (GameUtilities.isPCLPlayerClass(pClass)) {
                PCLResources<?, ?, ?, ?> resources = PGR.getResources(pClass);
                PCLCardData bane = resources.getAscendersBane();
                if (bane != null) {
                    final ArrayList<AbstractCard> cards = AbstractDungeon.player.masterDeck.group;
                    for (int i = 0; i < cards.size(); i++) {
                        if (cards.get(i).cardID.equals(AscendersBane.ID)) {
                            cards.set(i, bane.makeCardFromLibrary(0));
                            UnlockTracker.markCardAsSeen(bane.ID);
                        }
                    }
                }
            }

        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "addCurseCards", optional = true)
    public static class AbstractDungeonPatches_AddCurseCards {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix() {
            if (GameUtilities.isPCLPlayerClass()) {
                for (Map.Entry<String, AbstractCard> entry : CardLibrary.cards.entrySet()) {
                    AbstractCard c = entry.getValue();
                    if (c.type == AbstractCard.CardType.CURSE && c.rarity != AbstractCard.CardRarity.SPECIAL) {
                        AbstractDungeon.curseCardPool.addToTop(c);
                    }
                }

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "onModifyPower", optional = true)
    public static class AbstractDungeonPatches_OnModifyPower {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasPower")) {
                        //onModifyPower checks if the player has focus to update orbs, it doesn't update them if focus is reduced to 0...
                        m.replace("$_ = true;");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCardFromPool", optional = true)
    public static class AbstractDungeonPatches_GetCardFromPool {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard found, AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean useRng) {
            filterCardGroupForValid = false;
            return tryReturnCard(found, rarity, type, useRng ? AbstractDungeon.cardRng : null);
        }

        @SpirePrefixPatch
        public static void prefix(AbstractCard.CardRarity rarity, AbstractCard.CardType type, boolean useRng) {
            filterCardGroupForValid = true;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getColorlessCardFromPool", optional = true)
    public static class AbstractDungeonPatches_GetColorlessCardFromPool {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard found, AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = false;
            return tryReturnCard(found, null, AbstractDungeon.cardRng);
        }

        @SpirePrefixPatch
        public static void prefix(AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = true;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class}, optional = true)
    public static class AbstractDungeonPatches_GetCard {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard found, AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = false;
            return tryReturnCard(found, rarity, AbstractDungeon.cardRng);
        }

        @SpirePrefixPatch
        public static void prefix(AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = true;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class, Random.class}, optional = true)
    public static class AbstractDungeonPatches_GetCard2 {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard found, AbstractCard.CardRarity rarity, Random rng) {
            filterCardGroupForValid = false;
            return tryReturnCard(found, rarity, rng);
        }

        @SpirePrefixPatch
        public static void prefix(AbstractCard.CardRarity rarity, Random rng) {
            filterCardGroupForValid = true;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getCardWithoutRng", paramtypez = {AbstractCard.CardRarity.class}, optional = true)
    public static class AbstractDungeonPatches_GetCardWithoutRng {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard found, AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = false;
            return tryReturnCard(found, rarity, AbstractDungeon.cardRng);
        }

        @SpirePrefixPatch
        public static void prefix(AbstractCard.CardRarity rarity) {
            filterCardGroupForValid = true;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class AbstractDungeon_GetRewardCards {
        //Patches into 1840 hopefully
        @SpireInsertPatch(rloc = 48, localvars = {"card", "containsDupe", "rarity", "retVal"})
        public static void patch(@ByRef AbstractCard[] card, @ByRef boolean[] containsDupe, @ByRef AbstractCard.CardRarity[] rarity, ArrayList<AbstractCard> retVal) {
            if (containsDupe[0] && notEnoughCards(rarity[0], retVal)) {
                containsDupe[0] = false;
                card[0] = tryReturnCard(null, rarity[0], AbstractDungeon.cardRng, c -> PGR.dungeon.canObtainCopy(c) && !retVal.contains(c));
            }
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "nextRoomTransition"
            , paramtypez = {SaveFile.class}
    )
    public static class AbstractDungeonPatches_NextRoomTransition {

        // This must be initialized before the battle starts
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractDungeon __instance) {
            CombatManager.onBattleStart();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "preBattlePrep");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
}