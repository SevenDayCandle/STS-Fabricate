package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.screens.DrawPileViewScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class DrawPileViewScreenPatches
{
    @SpirePatch(clz = DrawPileViewScreen.class, method = "open", optional = true)
    public static class DrawPileViewScreenPatches_open
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                @Override
                public void edit(MethodCall m) throws CannotCompileException
                {
                    if (m.getMethodName().equals("hasRelic"))
                    {
                        m.replace("$_ = pinacolada.utilities.GameUtilities.hasRelicEffect(\"" + FrozenEye.ID + "\");");
                    }
                }
            };
        }
    }
}
