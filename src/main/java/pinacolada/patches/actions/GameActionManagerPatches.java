package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import pinacolada.actions.PCLAction;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class GameActionManagerPatches
{

    @SpirePatch(clz = GameActionManager.class, method = "clearPostCombatActions")
    public static class GameActionManager_ClearPostCombatActions
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(GameActionManager __instance)
        {
            final ArrayList<AbstractGameAction> actions = __instance.actions;
            for (int i = actions.size() - 1; i >= 0; i--)
            {
                AbstractGameAction action = actions.get(i);
                if (action instanceof PCLAction)
                {
                    if (((PCLAction) action).canCancel)
                    {
                        actions.remove(i);
                    }
                }
                else if (!(action instanceof HealAction
                        || action instanceof GainBlockAction
                        || action instanceof UseCardAction
                        || action.actionType == AbstractGameAction.ActionType.DAMAGE))
                {
                    actions.remove(i);
                }
            }

            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = GameActionManager.class,
            method = "getNextAction"
    )
    public static class GameActionManager_GetNextAction
    {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"m"}
        )
        public static void insert(GameActionManager __instance, AbstractMonster m)
        {
            if (!(m instanceof PCLCardAlly))
            {
                CombatManager.removeDamagePowers(m);
            }
        }

        private static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractMonster.class, "applyTurnPowers");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}