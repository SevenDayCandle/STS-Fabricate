package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.neow.NeowReward;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;

public class NeowPatches {
    @SpirePatch(
            clz = NeowReward.class,
            method = "getRewardCards"
    )
    public static class NeowReward_GetRewardCards {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("getCard")) {
                        m.replace("{ $_ = pinacolada.patches.dungeon.AbstractDungeonPatches.notEnoughCards(rarity, retVal) ? pinacolada.patches.dungeon.AbstractDungeonPatches.makeTempCard() : $proceed($$); }");
                    }
                }
            };
        }
    }
}
