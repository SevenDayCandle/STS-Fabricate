package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.dungeon.CombatManager;

public class AbstractRoomPatches {
    @SpirePatch(clz = AbstractRoom.class, method = "endTurn")
    public static class AbstractRoomPatches_EndTurn {
        @SpirePrefixPatch
        public static void prefix(AbstractRoom __instance) {
            CombatManager.atEndOfTurn(true);
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "applyEndOfTurnPreCardPowers")
    public static class AbstractRoomPatches_ApplyEndOfTurnPreCardPowers {
        @SpirePrefixPatch
        public static void prefix(AbstractRoom __instance) {
            CombatManager.atEndOfTurnPreEndTurnCards(true);
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "addNoncampRelicToRewards")
    @SpirePatch(clz = AbstractRoom.class, method = "addRelicToRewards", paramtypez = {AbstractRelic.RelicTier.class})
    public static class AbstractRoomPatches_addRelicToRewards {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(AbstractRoom __instance, AbstractRelic.RelicTier tier) {
            if (ModHelper.isModEnabled(ModHelperPatches.Dearth) && (tier == AbstractRelic.RelicTier.COMMON || tier == AbstractRelic.RelicTier.UNCOMMON || tier == AbstractRelic.RelicTier.RARE)) {
                __instance.rewards.add(new RewardItem(RelicLibrary.getRelic(Circlet.ID)));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}



