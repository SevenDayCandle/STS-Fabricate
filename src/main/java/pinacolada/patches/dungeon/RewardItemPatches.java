package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.relics.PCLRelic;

@SpirePatch(
        clz = RewardItem.class,
        method = "render"
)
public class RewardItemPatches {
    public static boolean isRenderingForSapphire;

    @SpireInstrumentPatch
    public static ExprEditor instrument() {
        return new ExprEditor() {
            int count = 0;

            @Override
            public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                if (count == 0 && m.getFieldName().equals("relicLink")) {
                    ++count;
                    m.replace("$_ = pinacolada.patches.dungeon.RewardItemPatches.isPCL(relic) ? null : $proceed($$);");
                }
            }
        };
    }

    public static boolean isPCL(AbstractRelic relic) {
        if (relic instanceof PCLRelic) {
            isRenderingForSapphire = true;
            return true;
        }
        return false;
    }
}
