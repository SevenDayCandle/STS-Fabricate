package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import pinacolada.dungeon.CombatManager;

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
