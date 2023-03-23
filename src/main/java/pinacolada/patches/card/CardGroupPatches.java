package pinacolada.patches.card;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.interfaces.listeners.OnRemovedFromDeckListener;
import pinacolada.misc.CombatManager;

import java.util.ArrayList;

public class CardGroupPatches
{
    private static void delay(ArrayList<AbstractCard> cards, Random rng)
    {
        int delayedIndex = 0;
        for (int i = 0; i < cards.size(); i++)
        {
            final AbstractCard c = cards.get(i);
            if (PCLCardTag.Delayed.has(c))
            {
                if (i != delayedIndex)
                {
                    final AbstractCard temp = cards.get(delayedIndex);
                    cards.set(delayedIndex, c);
                    cards.set(i, temp);
                }

                delayedIndex += 1;
                PCLCardTag.Delayed.tryProgress(c);
            }
        }
    }

    private static void shuffle(CardGroup group, Random rng)
    {
        final ArrayList<AbstractCard> cards = group.group;
        int innateIndex = cards.size() - 1;

        delay(cards, rng);

        for (int i = cards.size() - 1; i >= 0; i--)
        {
            final AbstractCard c = cards.get(i);
            if (PCLCardTag.Innate.has(c))
            {
                if (i != innateIndex)
                {
                    final AbstractCard temp = cards.get(innateIndex);
                    cards.set(innateIndex, c);
                    cards.set(i, temp);
                }
                innateIndex -= 1;
                PCLCardTag.Innate.tryProgress(c);
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "removeCard", paramtypez = {AbstractCard.class})
    public static class CardGroupPatches_RemoveCard
    {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance, AbstractCard c)
        {
            if (__instance.type == CardGroup.CardGroupType.MASTER_DECK)
            {
                OnRemovedFromDeckListener card = EUIUtils.safeCast(c, OnRemovedFromDeckListener.class);
                if (card != null)
                {
                    card.onRemovedFromDeck();
                }
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {})
    public static class CardGroupPatches_Shuffle1
    {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance)
        {
            shuffle(__instance, AbstractDungeon.shuffleRng);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {Random.class})
    public static class CardGroupPatches_Shuffle2
    {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance, Random rng)
        {
            shuffle(__instance, rng);
        }
    }

    // These card getter methods crash if the group is empty
    // Casting is intentional to avoid crashes from incorrect type inferences in patching
    @SpirePatch(clz = CardGroup.class, method = "getBottomCard", paramtypez = {})
    public static class CardGroupPatches_GetBottomCard
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance)
        {
            if (__instance.group.size() == 0)
            {
                return SpireReturn.Return((AbstractCard) null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getTopCard", paramtypez = {})
    public static class CardGroupPatches_GetTopCard
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance)
        {
            if (__instance.group.size() == 0)
            {
                return SpireReturn.Return((AbstractCard) null);
            }
            else
            {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {Random.class})
    public static class CardGroupPatches_GetRandomCard1
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance, Random rng)
        {
            if (__instance.group.size() == 0)
            {
                return SpireReturn.Return((AbstractCard) null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {boolean.class})
    public static class CardGroupPatches_GetRandomCard2
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance, boolean useRng)
        {
            if (__instance.group.size() == 0)
            {
                return SpireReturn.Return((AbstractCard) null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "triggerOnOtherCardPlayed")
    public static class CardGroupPatches_OnAfterPlayCard
    {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard usedCard)
        {
            CombatManager.onAfterCardPlayed(usedCard);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class CardGroupPatches_MoveToExhaustPile
    {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard card)
        {
            CombatManager.onExhaust(card);
        }
    }
}
