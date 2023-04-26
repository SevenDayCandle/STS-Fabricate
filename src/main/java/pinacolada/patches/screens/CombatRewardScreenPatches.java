package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import extendedui.utilities.EUIClassUtils;
import pinacolada.effects.PCLEffects;

public class CombatRewardScreenPatches {
    @SpirePatch(clz = CombatRewardScreen.class, method = "update")
    public static class CombatRewardScreenPatches_Update {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CombatRewardScreen __instance) {
            if (PCLEffects.isEmpty()) {
                return SpireReturn.Continue();
            }
            EUIClassUtils.invoke(__instance, "updateEffects");
            return SpireReturn.Return();
        }
    }
}