package pinacolada.patches.gameActionManager;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import pinacolada.actions.PCLAction;

import java.util.ArrayList;

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
}