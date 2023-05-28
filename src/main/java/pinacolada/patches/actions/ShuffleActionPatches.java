package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import extendedui.utilities.EUIClassUtils;
import pinacolada.dungeon.CombatManager;

public class ShuffleActionPatches {
    @SpirePatch(clz = EmptyDeckShuffleAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShuffleActionPatches_EmptyDeckShuffleAction {
        @SpirePostfixPatch
        public static void postfix(EmptyDeckShuffleAction __instance) {
            CombatManager.onShuffle(true);
        }
    }

    @SpirePatch(clz = ShuffleAllAction.class, method = SpirePatch.CONSTRUCTOR)
    public static class ShuffleActionPatches_ShuffleAllAction {
        @SpirePostfixPatch
        public static void postfix(ShuffleAllAction __instance) {
            CombatManager.onShuffle(true);
        }
    }

    @SpirePatch(clz = ShuffleAction.class, method = "update")
    public static class ShuffleActionPatches_ShuffleAction {
        @SpirePostfixPatch
        public static void postfix(ShuffleAction __instance) {
            CombatManager.onShuffle(EUIClassUtils.getField(__instance, "triggerRelics"));
        }
    }
}

