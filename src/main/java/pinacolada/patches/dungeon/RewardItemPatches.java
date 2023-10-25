package pinacolada.patches.dungeon;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.ui.tooltips.EUITooltip;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.potions.PCLPotion;
import pinacolada.relics.PCLRelic;

public class RewardItemPatches {
    public static boolean isRenderingForSapphire;

    @SpirePatch(
            clz = RewardItem.class,
            method = "render"
    )
    public static class RewardItemPatches_Render {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                int count = 0;

                @Override
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (count == 0 && m.getFieldName().equals("relicLink")) {
                        ++count;
                        m.replace("$_ = pinacolada.patches.dungeon.RewardItemPatches.isPCL(relic, $proceed($$)) ? null : $proceed($$);");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = RewardItem.class,
            method = "update"
    )
    public static class RewardItemPatches_Update {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                @Override
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(TipHelper.class.getName()) && m.getMethodName().equals("queuePowerTips")) {
                        m.replace("{ if (!pinacolada.patches.dungeon.RewardItemPatches.tryQueuePCLPotion(potion)) $proceed($$); }");
                    }
                }
            };
        }
    }

    public static boolean isPCL(AbstractRelic relic, RewardItem relicLink) {
        if (relic instanceof PCLRelic) {
            if (relicLink != null && relicLink.type == RewardItem.RewardType.SAPPHIRE_KEY) {
                isRenderingForSapphire = true;
            }
            return true;
        }
        return false;
    }

    public static boolean tryQueuePCLPotion(AbstractPotion potion) {
        if (potion instanceof PCLPotion) {
            EUITooltip.queueTooltips(potion);
            return true;
        }
        return false;
    }
}
