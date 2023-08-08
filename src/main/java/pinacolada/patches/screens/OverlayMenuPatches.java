package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.OverlayMenu;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

public class OverlayMenuPatches {
    @SpirePatch(clz = OverlayMenu.class, method = "update")
    public static class OverlayMenuPatches_Update {

        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(CardGroup.class.getName()) && m.getMethodName().equals("update")) {
                        m.replace("{ if (!pinacolada.dungeon.GridCardSelectScreenHelper.isActive()) $proceed($$); }");
                    }
                }
            };
        }
    }
}
