package pinacolada.patches.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.compendium.RelicViewScreen;
import com.megacrit.cardcrawl.vfx.combat.PowerExpireTextEffect;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.powers.PCLExpirePowerEffect;
import pinacolada.powers.PCLPower;

import java.util.ArrayList;

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
    }
}


