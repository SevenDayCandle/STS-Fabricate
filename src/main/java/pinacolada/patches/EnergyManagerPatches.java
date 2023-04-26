package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CtBehavior;
import pinacolada.dungeon.CombatManager;

// Copied and modified from STS-AnimatorMod
public class EnergyManagerPatches {
    @SpirePatch(clz = EnergyManager.class, method = "recharge")
    public static class EnergyManagerPatches_recharge {
        private static int previousEnergy;

        @SpireInsertPatch(locator = Locator.class)
        public static void insert(EnergyManager __instance) {
            final int currentEnergy = EnergyPanel.getCurrentEnergy();
            final int newEnergyCount = CombatManager.onEnergyRecharge(previousEnergy, currentEnergy);
            if (newEnergyCount != currentEnergy) {
                EnergyPanel.setEnergy(newEnergyCount);
            }
        }

        @SpirePrefixPatch
        public static void prefix(EnergyManager __instance) {
            previousEnergy = EnergyPanel.getCurrentEnergy();
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            final Matcher finalMatcher = new Matcher.MethodCallMatcher(EnergyPanel.class, "setEnergy");
            return new int[]{LineFinder.findInOrder(ctMethodToPatch, finalMatcher)[0] + 1};
        }
    }
}