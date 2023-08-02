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
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsForMultiformUpgradeEffect;

public class UpgradeActionPatches {

    public static void patch(AbstractCard c) {
        CombatManager.onCardUpgrade(c);
    }

    public static boolean tryBranch(AbstractCard c) {
        if (c instanceof PCLCard && ((PCLCard) c).isBranchingUpgrade()) {
            PCLEffects.Queue.add(new ChooseCardsForMultiformUpgradeEffect((PCLCard) c));
            return true;
        }
        return false;
    }

    @SpirePatch(clz = ArmamentsAction.class, method = "update")
    @SpirePatch(clz = ApotheosisAction.class, method = "upgradeAllCardsInGroup")
    @SpirePatch(clz = UpgradeRandomCardAction.class, method = "update")
    @SpirePatch(clz = UpgradeSpecificCardAction.class, method = "update")
    public static class UpgradeActionPatches_Execute {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getMethodName().equals("upgrade")) {
                        m.replace("{ pinacolada.patches.card.UpgradeActionPatches.patch($0); if (!pinacolada.patches.card.UpgradeActionPatches.tryBranch($0)) $proceed($$);}");
                    }
                }
            };
        }
    }
}
