package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class CardRewardScreenPatches {
    @SpirePatch(clz = CardRewardScreen.class, method = "update")
    public static class CardRewardScreen_Update {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance) {
            PGR.rewardScreen.updateImpl();
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "render")
    public static class CardRewardScreen_Render {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, SpriteBatch sb) {
            PGR.rewardScreen.renderImpl(sb);
        }

        @SpirePrefixPatch
        public static void prefix(CardRewardScreen __instance, SpriteBatch sb) {
            PGR.rewardScreen.preRender(sb);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "draftOpen")
    public static class CardRewardScreen_DraftOpen {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance) {
            PGR.rewardScreen.open(null, null, null);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "chooseOneOpen", paramtypez = {ArrayList.class})
    public static class CardRewardScreen_ChooseOneOpen {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> choices) {
            PGR.rewardScreen.open(choices, null, null);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "customCombatOpen", paramtypez = {ArrayList.class, String.class, boolean.class})
    public static class CardRewardScreen_CustomCombatOpen {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> choices, String text, boolean skippable) {
            PGR.rewardScreen.open(choices, null, text);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "open", paramtypez = {ArrayList.class, RewardItem.class, String.class})
    public static class CardRewardScreen_Open {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            PGR.rewardScreen.open(cards, rItem, header);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "onClose")
    public static class CardRewardScreen_OnClose {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance) {
            PGR.rewardScreen.close(false);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "acquireCard")
    public static class CardRewardScreen_AcquireCard {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, AbstractCard hoveredCard) {
            PGR.rewardScreen.onCardObtained(hoveredCard);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
    public static class CardRewardScreen_RenderCardReward {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName())) {
                        if (m.getMethodName().equals("render")) {
                            m.replace("{ pinacolada.resources.PGR.rewardScreen.renderCardReward($1, $0);}");
                        }
                        else if (m.getMethodName().equals("renderCardTip")) {
                            m.replace("{ if (pinacolada.resources.PGR.rewardScreen.renderCardRewardTip($1, $0)) $proceed($$); }");
                        }
                    }
                }
            };
        }
    }
}
