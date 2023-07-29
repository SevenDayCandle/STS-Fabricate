package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.OverlayMenu;
import extendedui.EUI;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;

public class OverlayMenuPatches {
    @SpirePatch(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuPatches_Update {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(CardGroup.class.getName()) && m.getMethodName().equals("update")) {
                        m.replace("{ if (!pinacolada.ui.combat.GridCardSelectScreenHelper.isActive()) $proceed($$); }");
                    }
                }
            };
        }
    }
}
