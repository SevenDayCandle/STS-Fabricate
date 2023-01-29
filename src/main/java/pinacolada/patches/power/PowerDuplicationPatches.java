package pinacolada.patches.power;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.DoubleTapPower;
import com.megacrit.cardcrawl.powers.DuplicationPower;
import com.megacrit.cardcrawl.powers.EchoPower;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.utilities.GameUtilities;

// Use canPlayTwice for all powers that play cards twice
public class PowerDuplicationPatches
{
    @SpirePatch(clz = DuplicationPower.class, method = "onUseCard")
    public static class DuplicationPatches_DuplicationPower
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    doEdit(m);
                }
            };
        }
    }

    @SpirePatch(clz = EchoPower.class, method = "onUseCard")
    public static class DuplicationPatches_EchoPower
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    doEdit(m);
                }
            };
        }
    }

    @SpirePatch(clz = DoubleTapPower.class, method = "onUseCard")
    public static class DuplicationPatches_DoubleTapPower
    {
        @SpireInstrumentPatch
        public static ExprEditor instrument()
        {
            return new ExprEditor()
            {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException
                {
                    doEdit(m);
                }
            };
        }
    }

    public static void doEdit(javassist.expr.FieldAccess m) throws CannotCompileException
    {
        if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("purgeOnUse"))
        {
            m.replace("{ $_ = pinacolada.patches.power.PowerDuplicationPatches.patch($0); }");
        }
    }

    public static boolean patch(AbstractCard card)
    {
        return GameUtilities.canPlayTwice(card);
    }
}
