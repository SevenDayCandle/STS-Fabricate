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
public class PowerDuplicationPatches {

    // Needs to be negated because these purgeOnUse checks were already negated
    public static boolean patch(AbstractCard card) {
        return !GameUtilities.canPlayTwice(card);
    }

    @SpirePatch(clz = DuplicationPower.class, method = "onUseCard")
    @SpirePatch(clz = EchoPower.class, method = "onUseCard")
    @SpirePatch(clz = DoubleTapPower.class, method = "onUseCard")
    public static class DuplicationPatches_DuplicationPower {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                // Check for reading calls only because otherwise we'll end up overwriting the writing calls as well
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("purgeOnUse") && m.isReader()) {
                        m.replace("{ $_ = pinacolada.patches.power.PowerDuplicationPatches.patch($0); }");
                    }
                }
            };
        }
    }
}
