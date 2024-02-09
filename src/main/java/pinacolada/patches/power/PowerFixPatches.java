package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;

public class PowerFixPatches {
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

    // Prevent Vigor from being removed from non-player characters when you play a card
    @SpirePatch(clz = VigorPower.class, method = "onUseCard")
    public static class VigorPowerPatches_OnUseCard {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(VigorPower __instance, AbstractCard card, UseCardAction action) {
            if (__instance.owner != AbstractDungeon.player) {
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
