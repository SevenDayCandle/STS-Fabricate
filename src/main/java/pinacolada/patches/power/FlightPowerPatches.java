package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;

public class FlightPowerPatches {
    // Prevents Flight from crashing when removed from non-Byrds
    @SpirePatch(clz = FlightPower.class, method = "onRemove")
    public static class FlightPower_OnRemove {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(FlightPower __instance) {
            if (__instance.owner instanceof Byrd) {
                return SpireReturn.Continue();
            }
            return SpireReturn.Return();
        }
    }
}
