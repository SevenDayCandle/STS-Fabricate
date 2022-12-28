package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.misc.CombatManager;

public class AbstractRoomPatches
{
    @SpirePatch(clz = AbstractRoom.class, method = "endTurn")
    public static class AbstractRoomPatches_EndTurn
    {
        @SpirePrefixPatch
        public static void prefix(AbstractRoom __instance)
        {
            CombatManager.atEndOfTurn(true);
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "applyEndOfTurnPreCardPowers")
    public static class AbstractRoomPatches_ApplyEndOfTurnPreCardPowers
    {
        @SpirePrefixPatch
        public static void prefix(AbstractRoom __instance)
        {
            CombatManager.atEndOfTurnPreEndTurnCards(true);
        }
    }
}



