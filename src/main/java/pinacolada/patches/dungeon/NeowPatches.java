package pinacolada.patches.dungeon;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.trials.PCLCustomTrial;

public class NeowPatches {

    @SpirePatch(clz = NeowEvent.class,
            method = "shouldSkipNeowDialog")
    public static class NeowEvent_ShouldSkipDialog {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> prefix(NeowEvent event) {
            if (CardCrawlGame.trial instanceof PCLCustomTrial && ((PCLCustomTrial) CardCrawlGame.trial).allowNeow) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

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
