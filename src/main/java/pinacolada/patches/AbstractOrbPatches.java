package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.*;
import com.megacrit.cardcrawl.powers.LockOnPower;
import pinacolada.dungeon.CombatManager;
import pinacolada.utilities.GameUtilities;

public class AbstractOrbPatches
{
    @SpirePatch(clz = Dark.class, method = "onEndOfTurn")
    @SpirePatch(clz = Frost.class, method = "onEndOfTurn")
    @SpirePatch(clz = Lightning.class, method = "onEndOfTurn")
    public static class AbstractOrb_onEndOfTurn
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            if (!(__instance instanceof EmptyOrbSlot))
            {
                CombatManager.onOrbPassiveEffect(__instance);
            }
        }
    }

    @SpirePatch(clz = Plasma.class, method = "onStartOfTurn")
    public static class AbstractOrb_onStartOfTurn
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            if (!(__instance instanceof EmptyOrbSlot))
            {
                CombatManager.onOrbPassiveEffect(__instance);
            }
        }
    }

    @SpirePatch(clz = AbstractOrb.class, method = "applyFocus")
    public static class AbstractOrbPatches_ApplyFocus
    {
        @SpirePostfixPatch
        public static void postfix(AbstractOrb __instance)
        {
            // Certain mods may instantiate Orb instances in static methods before CombatStats is initialized, which will cause this method call to crash without this in-game check
            if (GameUtilities.inGame())
            {
                CombatManager.onOrbApplyFocus(__instance);
            }

        }
    }

    @SpirePatch(clz = AbstractOrb.class, method = "applyLockOn", paramtypez = {AbstractCreature.class, int.class})
    public static class AbstractOrbPatches_ApplyLockOn
    {
        @SpirePostfixPatch
        public static int postfix(int retVal, AbstractCreature target, int dmg)
        {
            // Use float for more accurate values
            float inputDmg = retVal;
            if (GameUtilities.getPowerAmount(target, LockOnPower.POWER_ID) >= 1)
            {
                float modifier = CombatManager.getEffectBonus(LockOnPower.POWER_ID);
                inputDmg = modifier > 0 ? (dmg * ((retVal / (float) dmg) + modifier)) : retVal;
            }
            return (int) CombatManager.onOrbApplyLockOn(target, inputDmg);
        }
    }

    @SpirePatch(clz = Dark.class, method = "applyFocus")
    public static class DarkPatches_ApplyFocus
    {
        @SpirePostfixPatch
        public static void postfix(Dark __instance)
        {
            if (GameUtilities.inGame())
            {
                CombatManager.onOrbApplyFocus(__instance);
            }
        }
    }
}