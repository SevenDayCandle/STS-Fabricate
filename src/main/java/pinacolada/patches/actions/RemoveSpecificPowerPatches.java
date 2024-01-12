package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.PowerExpireTextEffect;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.powers.PCLExpirePowerEffect;
import pinacolada.powers.PCLPower;

public class RemoveSpecificPowerPatches {

    @SpirePatch(clz = RemoveSpecificPowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, String.class})
    public static class RemoveSpecificPowerAction_Ctor {
        @SpirePostfixPatch
        public static void postfix(RemoveSpecificPowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   String powerToRemove) {
            if (!CombatManager.canReducePower(source, target, powerToRemove, __instance)) {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = RemoveSpecificPowerAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez =
            {AbstractCreature.class, AbstractCreature.class, AbstractPower.class})
    public static class RemoveSpecificPowerAction_Ctor2 {
        @SpirePostfixPatch
        public static void postfix(RemoveSpecificPowerAction __instance, AbstractCreature target, AbstractCreature source,
                                   AbstractPower powerInstance) {
            if (!CombatManager.canReducePower(source, target, powerInstance, __instance)) {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = RemoveSpecificPowerAction.class, method = "update")
    public static class RemoveSpecificPowerAction_Update {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.NewExpr m) throws CannotCompileException {
                    if (m.getClassName().equals(PowerExpireTextEffect.class.getName())) {
                        m.replace("{ $_ = removeMe instanceof " + PCLPower.class.getName() + " ? new " + PCLExpirePowerEffect.class.getName() + "(removeMe, $1, $2, $3) : $proceed($$); }");
                    }
                }
            };
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"removeMe"})
        public static void insert(RemoveSpecificPowerAction __instance, AbstractPower removeMe) {
            CombatManager.onRemovePower(__instance.source, __instance.target, removeMe);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "onModifyPower");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}


