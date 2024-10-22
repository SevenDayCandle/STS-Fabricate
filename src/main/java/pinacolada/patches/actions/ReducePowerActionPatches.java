package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.utilities.EUIClassUtils;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import pinacolada.dungeon.CombatManager;

public class ReducePowerActionPatches {

    @SpirePatch(
            clz = ReducePowerAction.class,
            method = SpirePatch.CLASS
    )
    public static class Recursive {
        public static SpireField<Boolean> recursive = new SpireField<>(() -> false);
    }

    @SpirePatch(clz = ReducePowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, String.class, int.class})
    public static class ReducePowerAction_Ctor {
        @SpirePostfixPatch
        public static void postfix(ReducePowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   String power, int amount) {
            if (!ReducePowerActionPatches.Recursive.recursive.get(__instance) && !CombatManager.canReducePower(source, target, power, __instance)) {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = ReducePowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, AbstractPower.class, int.class})
    public static class ReducePowerAction_Ctor2 {
        @SpirePostfixPatch
        public static void postfix(ReducePowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   AbstractPower powerInstance, int amount) {
            if (!ReducePowerActionPatches.Recursive.recursive.get(__instance) && !CombatManager.canReducePower(source, target, powerInstance, __instance)) {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = ReducePowerAction.class, method = "update")
    public static class ReducePowerAction_Update {

        @SpireInsertPatch(locator = Locator.class, localvars = {"reduceMe"})
        public static void insert(ReducePowerAction __instance, AbstractPower reduceMe) {
            EUIClassUtils.setField(__instance, "powerInstance", reduceMe); // Force set the powerInstance field so we can grab it through callbacks
        }

        @SpireInsertPatch(locator = Locator2.class, localvars = {"reduceMe"})
        public static void insert2(ReducePowerAction __instance, AbstractPower reduceMe) {
            EUIClassUtils.setField(__instance, "powerInstance", reduceMe); // Force set the powerInstance field so we can grab it through callbacks
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.NewExprMatcher(RemoveSpecificPowerAction.class);
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}


