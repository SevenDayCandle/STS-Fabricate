package pinacolada.patches.actions;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.misc.CombatManager;
import pinacolada.powers.common.PCLLockOnPower;
import pinacolada.powers.replacement.PCLConstrictedPower;
import pinacolada.powers.replacement.PCLFrailPower;
import pinacolada.powers.replacement.PCLVulnerablePower;
import pinacolada.powers.replacement.PCLWeakPower;
import pinacolada.utilities.GameUtilities;


public class ApplyPowerActionPatches
{
    @SpirePatch(
            clz = ApplyPowerAction.class,
            method = SpirePatch.CLASS
    )
    public static class IgnoreArtifact
    {
        public static SpireField<Boolean> ignoreArtifact = new SpireField<>(()->false);
    }

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

    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class ApplyPowerActionPatches_Update
    {
        @SpireInsertPatch(locator = LocatorApply.class)
        public static void insertPre(ApplyPowerAction __instance)
        {
            CombatManager.onApplyPower(__instance.source, __instance.target, ReflectionHacks.getPrivate(__instance, ApplyPowerAction.class, "powerToApply"));
        }

        private static class LocatorApply extends SpireInsertLocator
        {
            public int[] Locate(CtBehavior ctBehavior) throws Exception
            {
                final Matcher matcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                boolean notFirst = false;

                // The first hasPower is for no Draw, the others should be for Artifact. If someone added some others in a patch, then this will unfortunately catch them too :(
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractCreature.class.getName()) && m.getMethodName().equals("hasPower"))
                    {
                        if (!notFirst)
                        {
                            notFirst = true;
                        }
                        else
                        {
                            m.replace("{ $_ = pinacolada.patches.actions.ApplyPowerActionPatches.ApplyPowerActionPatches_Update.patch(this) && $proceed($$); }");
                        }
                    }
                }
            };
        }

        public static boolean patch(ApplyPowerAction action)
        {
            return !IgnoreArtifact.ignoreArtifact.get(action);
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
