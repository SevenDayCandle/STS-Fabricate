package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

// Copied and modified from STS-AnimatorMod
public class VulnerablePowerPatches {
    public static float estimateDamage(AbstractPower power) {
        if (GameUtilities.inGame()) {
            float estimate = power.atDamageReceive(PCLPower.DUMMY_MULT, DamageInfo.DamageType.NORMAL);
            return estimate - PCLPower.DUMMY_MULT;
        }
        return PCLPower.DUMMY_MULT;
    }

    @SpirePatch(clz = VulnerablePower.class, method = "updateDescription")
    public static class VulnerablePowerPatches_UpdateDescription {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(VulnerablePower __instance) {
            __instance.description = VulnerablePower.DESCRIPTIONS[0] + PCLRenderHelpers.decimalFormat(estimateDamage(__instance)) + VulnerablePower.DESCRIPTIONS[1] + __instance.amount + (__instance.amount == 1 ? VulnerablePower.DESCRIPTIONS[2] : VulnerablePower.DESCRIPTIONS[3]);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = VulnerablePower.class, method = "atDamageReceive", paramtypez = {float.class, DamageInfo.DamageType.class})
    public static class VulnerablePowerPatches_AtDamageReceive {
        @SpirePostfixPatch
        public static float postfix(float result, VulnerablePower __instance, float damage, DamageInfo.DamageType type) {
            float bonus = CombatManager.getEffectBonusForPower(__instance);
            return bonus == 0 ? result : damage * ((result / damage) + bonus / 100f);
        }
    }
}
