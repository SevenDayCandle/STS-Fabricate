package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class FrailPowerPatches
{
    @SpirePatch(clz = FrailPower.class, method = "updateDescription")
    public static class FrailPowerPatches_UpdateDescription
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(FrailPower __instance)
        {
            // HARD CODE IS HARD
            __instance.description = FrailPower.DESCRIPTIONS[0].replace("25", PCLRenderHelpers.decimalFormat(estimateDamage(__instance))) + __instance.amount + (__instance.amount == 1 ? FrailPower.DESCRIPTIONS[1] : FrailPower.DESCRIPTIONS[2]);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = FrailPower.class, method = "modifyBlock", paramtypez = {float.class})
    public static class FrailPowerPatches_AtDamageReceive
    {
        @SpirePostfixPatch
        public static float postfix(float result, FrailPower __instance, float damage)
        {
            float bonus = CombatManager.getEffectBonusForPower(__instance) / 100f;
            return bonus == 0 ? damage : damage * ((result / damage) - bonus);
        }
    }

    public static float estimateDamage(AbstractPower power)
    {
        float estimate = power.modifyBlock(PCLPower.DUMMY_MULT);
        return PCLPower.DUMMY_MULT - estimate;
    }
}
