package pinacolada.patches.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.*;
import extendedui.utilities.EUIClassUtils;
import pinacolada.actions.powers.ApplyPower;
import pinacolada.powers.common.PCLLockOnPower;
import pinacolada.powers.replacement.PCLConstrictedPower;
import pinacolada.powers.replacement.PCLFrailPower;
import pinacolada.powers.replacement.PCLVulnerablePower;
import pinacolada.powers.replacement.PCLWeakPower;
import pinacolada.utilities.GameUtilities;


public class ApplyPowerActionPatches
{
    @SpirePatch(clz = ApplyPowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class})
    public static class ApplyPowerActionPatches_Vanilla
    {
        @SpirePostfixPatch
        public static void postfix(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   AbstractPower powerToApply, int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect)
        {
            if (!GameUtilities.canApplyPower(source, target, powerToApply, __instance))
            {
                __instance.isDone = true;
            }
        }

        @SpirePrefixPatch
        public static void prefix(ApplyPowerAction __instance, AbstractCreature target, AbstractCreature source, @ByRef AbstractPower[] powerToApply,
                                  int stackAmount, boolean isFast, AbstractGameAction.AttackEffect effect)
        {
            AbstractPower power = powerToApply[0];
            AbstractPower replacement = getReplacement(power, target, source);
            if (power != replacement)
            {
                powerToApply[0] = replacement;
                EUIClassUtils.setField(__instance,  "powerToApply", powerToApply[0]);
            }
        }
    }

    @SpirePatch(clz = ApplyPower.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class, boolean.class, AbstractGameAction.AttackEffect.class})
    public static class ApplyPowerActionPatches_EYB
    {
        @SpirePostfixPatch
        public static void postfix(ApplyPower __instance, AbstractCreature source, AbstractCreature target,
                                   AbstractPower powerToApply, int amount, boolean isFast, AbstractGameAction.AttackEffect effect)
        {
            if (!GameUtilities.canApplyPower(source, target, powerToApply, __instance))
            {
                __instance.isDone = true;
            }
        }

        @SpirePrefixPatch
        public static void prefix(ApplyPower __instance, AbstractCreature source, AbstractCreature target, @ByRef AbstractPower[] powerToApply,
                                  int amount, boolean isFast, AbstractGameAction.AttackEffect effect)
        {
            AbstractPower power = powerToApply[0];
            AbstractPower replacement = getReplacement(power, target, source);
            if (power != replacement)
            {
                powerToApply[0] = replacement;
                EUIClassUtils.setField(__instance,  "powerToApply", powerToApply[0]);
            }
        }
    }

    public static AbstractPower getReplacement(AbstractPower power, AbstractCreature target, AbstractCreature source)
    {
        if (power instanceof ConstrictedPower && !(power instanceof PCLConstrictedPower))
        {
            return new PCLConstrictedPower(power.owner, ((ConstrictedPower) power).source, power.amount);
        }

        if (GameUtilities.isPCLPlayerClass())
        {
            if (power instanceof VulnerablePower && !(power instanceof PCLVulnerablePower))
            {
                boolean justApplied = ReflectionHacks.getPrivate(power, VulnerablePower.class, "justApplied");
                return new PCLVulnerablePower(power.owner, power.amount, justApplied);
            }
            else if (power instanceof WeakPower && !(power instanceof PCLWeakPower))
            {
                boolean justApplied = ReflectionHacks.getPrivate(power, WeakPower.class, "justApplied");
                return new PCLWeakPower(power.owner, power.amount, justApplied);
            }
            else if (power instanceof FrailPower && !(power instanceof PCLFrailPower))
            {
                return new PCLFrailPower(power.owner, power.amount, !GameUtilities.isPlayer(source));
            }
            else if (power instanceof com.megacrit.cardcrawl.powers.LockOnPower)
            {
                return new PCLLockOnPower(power.owner, power.amount);
            }
        }

        else
        {
            if (power instanceof PCLVulnerablePower)
            {
                boolean justApplied = ReflectionHacks.getPrivate(power, VulnerablePower.class, "justApplied");
                return new VulnerablePower(power.owner, power.amount, justApplied);
            }
            else if (power instanceof PCLWeakPower)
            {
                boolean justApplied = ReflectionHacks.getPrivate(power, WeakPower.class, "justApplied");
                return new WeakPower(power.owner, power.amount, justApplied);
            }
            else if (power instanceof PCLFrailPower)
            {
                return new PCLFrailPower(power.owner, power.amount, !GameUtilities.isPlayer(source));
            }
            else if (power instanceof PCLLockOnPower)
            {
                return new com.megacrit.cardcrawl.powers.LockOnPower(power.owner, power.amount);
            }
        }
        return power;
    }
}
