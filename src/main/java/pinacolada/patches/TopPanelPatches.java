package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

public class TopPanelPatches
{
    @SpirePatch(clz = TopPanel.class, method = "renderHP")
    public static class TopPanelPatches_Render
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    if (m.getClassName().equals(AbstractPlayer.class.getName()))
                    {
                        if (m.getFieldName().equals("currentHealth"))
                        {
                            m.replace("{ $_ = pinacolada.resources.PGR.core.dungeon.getCurrentHealth($0); }");
                        }
                        else if (m.getFieldName().equals("maxHealth"))
                        {
                            m.replace("{ $_ = pinacolada.resources.PGR.core.dungeon.getMaxHealth($0); }");
                        }
                    }
                }
            };
        }
    }
}
