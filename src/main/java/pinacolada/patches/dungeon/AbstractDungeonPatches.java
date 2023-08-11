package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
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

    // If no suitable card is found, make a replacement because the method will crash if no card is actually found
    protected static AbstractCard tryReturnCard(AbstractCard card, AbstractCard.CardRarity rarity, Random rng) {
        if (card == null) {
            EUIUtils.logError(AbstractDungeonPatches.class, "Failed to find card with specified rarity " + rarity);

            // First try to find a replacement of another rarity if a rarity was specified
            if (rarity != null) {
                card = PGR.dungeon.getRandomCard(PCLDungeon.getNextRarity(rarity), rng, true);
                if (card != null) {
                    return card;
                }
            }

            // Otherwise, create a dummy card
            card = new QuestionMark();
            // Modify ID to avoid infinite loops in AbstractDungeon
            card.cardID = card.cardID + tempID;
            tempID += 1;
        }
        return card;
    }

    // Same as above but with type
    protected static AbstractCard tryReturnCard(AbstractCard card, AbstractCard.CardRarity rarity, AbstractCard.CardType type, Random rng) {
        if (card == null) {
            EUIUtils.logError(AbstractDungeonPatches.class, "Failed to find card with specified rarity " + rarity + " and type " + type);

            if (rarity != null) {
                card = PGR.dungeon.getRandomCard(PCLDungeon.getNextRarity(rarity), type, rng, true);
                if (card != null) {
                    return card;
                }
            }

            card = new QuestionMark();
            // Modify ID to avoid infinite loops in AbstractDungeon
            card.cardID = card.cardID + tempID;
            tempID += 1;
            // Also modify type in case the function calling this is verifying the card's type
            card.type = type;
        }
        return card;
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

    @SpirePatch(clz = AbstractDungeon.class, method = "getCard", paramtypez = {AbstractCard.CardRarity.class}, optional = true)
    public static class AbstractDungeonPatches_GetRewardCards {
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
    public static class AbstractDungeonPatches_GetRewardCards2 {
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