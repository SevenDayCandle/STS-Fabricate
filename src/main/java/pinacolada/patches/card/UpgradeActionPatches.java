package pinacolada.patches.card;


import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.UpgradeRandomCardAction;
import com.megacrit.cardcrawl.actions.common.UpgradeSpecificCardAction;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.dungeon.CombatManager;

public class UpgradeActionPatches {

    public static void doEdit(javassist.expr.MethodCall m) throws CannotCompileException {
        if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("upgrade")) {
            m.replace("{ pinacolada.patches.card.UpgradeActionPatches.patch($0); $proceed($$);}");
        }
    }

    public static void patch(AbstractCard c) {
        CombatManager.onCardUpgrade(c);
    }

    @SpirePatch(clz = ArmamentsAction.class, method = "update")
    public static class ArmamentsAction_Execute {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    doEdit(m);
                }
            };
        }
    }

    @SpirePatch(clz = ApotheosisAction.class, method = "upgradeAllCardsInGroup")
    public static class ApotheosisAction_Update {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    doEdit(m);
                }
            };
        }
    }

    @SpirePatch(clz = UpgradeRandomCardAction.class, method = "update")
    public static class UpgradeRandomCardAction_Update {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    doEdit(m);
                }
            };
        }
    }

    @SpirePatch(clz = UpgradeSpecificCardAction.class, method = "update")
    public static class UpgradeSpecificCardAction_Update {
        @SpireInsertPatch(localvars = {"c"}, locator = Locator.class)
        public static void insertPre(UpgradeSpecificCardAction __instance, AbstractCard c) {
            CombatManager.onCardUpgrade(c);
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
