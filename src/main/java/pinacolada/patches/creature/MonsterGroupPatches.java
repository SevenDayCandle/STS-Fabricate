package pinacolada.patches.creature;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;

public class MonsterGroupPatches {
    @SpirePatch(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class MonsterGroup_ApplyEndOfTurnPowers {
        @SpirePostfixPatch
        public static void postfix(MonsterGroup __instance) {
            CombatManager.summons.onEndOfRound();
        }
    }

    // Ensure that estimated HPs show up at the start of battle
    @SpirePatch(clz = MonsterGroup.class, method = "showIntent")
    public static class MonsterGroup_ShowIntent {
        @SpirePostfixPatch
        public static void postfix(MonsterGroup __instance) {
            if (PGR.config.showEstimatedDamage.get()) {
                CombatManager.updateEstimatedDamage();
            }
        }
    }
}
