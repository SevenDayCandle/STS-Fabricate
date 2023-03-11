package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.WeakPower;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class WeakPowerPatches
{
    @SpirePatch(clz = WeakPower.class, method = "updateDescription")
    public static class WeakPowerPatches_UpdateDescription
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(WeakPower __instance)
        {
            __instance.description = WeakPower.DESCRIPTIONS[0] + PCLRenderHelpers.decimalFormat(estimateDamage(__instance)) + WeakPower.DESCRIPTIONS[1] + __instance.amount + (__instance.amount == 1 ? WeakPower.DESCRIPTIONS[2] : WeakPower.DESCRIPTIONS[3]);
            return SpireReturn.Return();
        }
    }

    @SpirePatch(clz = WeakPower.class, method = "atDamageGive", paramtypez = {float.class, DamageInfo.DamageType.class})
    public static class WeakPowerPatches_AtDamageReceive
    {
        @SpirePostfixPatch
        public static float postfix(float result, WeakPower __instance, float damage, DamageInfo.DamageType type)
        {
            float bonus = CombatManager.getEffectBonusForPower(__instance);
            return bonus == 0 ? result : damage * ((result / damage) - bonus / 100f);
        }
    }

    public static float estimateDamage(WeakPower power)
    {
        float estimate = power.atDamageGive(PCLPower.DUMMY_MULT, DamageInfo.DamageType.NORMAL);
        return PCLPower.DUMMY_MULT - estimate;
    }
}