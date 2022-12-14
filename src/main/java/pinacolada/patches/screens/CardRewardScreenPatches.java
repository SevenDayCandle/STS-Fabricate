package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import pinacolada.ui.cardReward.PCLCardRewardScreen;

import java.util.ArrayList;

public class CardRewardScreenPatches
{
    private static final PCLCardRewardScreen screen = PCLCardRewardScreen.Instance;

    @SpirePatch(clz = CardRewardScreen.class, method = "update")
    public static class CardRewardScreen_Update
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance)
        {
            screen.updateImpl();
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "render")
    public static class CardRewardScreen_Render
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, SpriteBatch sb)
        {
            screen.renderImpl(sb);
        }

        @SpirePrefixPatch
        public static void prefix(CardRewardScreen __instance, SpriteBatch sb)
        {
            screen.preRender(sb);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "draftOpen")
    public static class CardRewardScreen_DraftOpen
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance)
        {
            screen.open(null, null, null);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "chooseOneOpen", paramtypez = {ArrayList.class})
    public static class CardRewardScreen_ChooseOneOpen
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> choices)
        {
            screen.open(choices, null, null);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "customCombatOpen", paramtypez = {ArrayList.class, String.class, boolean.class})
    public static class CardRewardScreen_CustomCombatOpen
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> choices, String text, boolean skippable)
        {
            screen.open(choices, null, text);
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "open", paramtypez = {ArrayList.class, RewardItem.class, String.class})
    public static class CardRewardScreen_Open
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header)
        {
            screen.open(cards, rItem, header);

/*            for (AbstractCard c : cards)
            {
                if (c instanceof FoolCard_UltraRare)
                {
                    FoolCard_UltraRare.MarkAsSeen(c.cardID);
                }
            }*/
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "onClose")
    public static class CardRewardScreen_OnClose
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance)
        {
            screen.close();
        }
    }

    @SpirePatch(clz = CardRewardScreen.class, method = "acquireCard")
    public static class CardRewardScreen_AcquireCard
    {
        @SpirePostfixPatch
        public static void postfix(CardRewardScreen __instance, AbstractCard hoveredCard)
        {
            screen.onCardObtained(hoveredCard);
        }
    }
}
