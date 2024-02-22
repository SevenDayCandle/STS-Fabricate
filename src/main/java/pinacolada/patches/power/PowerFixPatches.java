package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.watcher.BlockReturnPower;
import com.megacrit.cardcrawl.powers.watcher.VigorPower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

public class PowerFixPatches {
    // Make block return power give block to the attacker instead of always to the player
    @SpirePatch(clz = BlockReturnPower.class, method = "onAttacked")
    public static class BlockReturnPower_OnAttacked {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getFieldName().equals("player")) {
                        m.replace("{ $_ = info.owner; }");
                    }
                }
            };
        }
    }

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
