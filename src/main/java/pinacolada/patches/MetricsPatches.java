package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import extendedui.utilities.EUIClassUtils;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class MetricsPatches
{
    @SpirePatch(clz = Metrics.class, method = "gatherAllData", paramtypez = {boolean.class, boolean.class, MonsterGroup.class})
    public static class MetricPatches_gatherAllData
    {
        @SpirePostfixPatch
        public static void postfix(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                // Reusing an unused field in RunData.class
                EUIClassUtils.setField(__instance, "loadout", AbstractDungeon.player.name);
            }
        }

    }
}
