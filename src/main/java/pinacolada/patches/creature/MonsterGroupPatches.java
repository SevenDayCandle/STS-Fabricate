package pinacolada.patches.creature;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import pinacolada.dungeon.CombatManager;

public class MonsterGroupPatches
{
    @SpirePatch(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class MonsterGroup_ApplyEndOfTurnPowers
    {
        @SpirePostfixPatch
        public static void postfix(MonsterGroup __instance)
        {
            CombatManager.summons.onEndOfRound();
        }
    }
}
