package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import pinacolada.actions.PCLAction;
import pinacolada.dungeon.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.patches.card.AbstractCardPatches;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class GameActionManagerPatches {

    // Allow actions that can result in permanent changes to your character to continue running after battle ends
    @SpirePatch(clz = GameActionManager.class, method = "clearPostCombatActions")
    public static class GameActionManager_ClearPostCombatActions {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GameActionManager __instance) {
            final ArrayList<AbstractGameAction> actions = __instance.actions;
            actions.removeIf(CombatManager::isActionCancellable);
            CombatManager.executeRemainingActions(actions);

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class GameActionManager_GetNextAction {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"m"}
        )
        public static void insert(GameActionManager __instance, AbstractMonster m) {
            if (!(m instanceof PCLCardAlly)) {
                CombatManager.removeDamagePowers(m);
            }
        }

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void insert2(GameActionManager __instance) {
            AbstractCardPatches.forcePlay = false;
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "applyTurnPowers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "energyOnUse");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}