package pinacolada.patches.card;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;
import pinacolada.patches.dungeon.AbstractDungeonPatches;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class CardGroupPatches {
    private static ArrayList<AbstractCard> tmpCards;

    public static AbstractCard.CardRarity checkRarity(AbstractCard c) {
        if (!AbstractDungeonPatches.filterCardGroupForValid) {
            return c.rarity;
        }
        return PGR.dungeon.canObtainCopy(c) ? c.rarity : null;
    }

    public static AbstractCard.CardType checkType(AbstractCard c) {
        if (!AbstractDungeonPatches.filterCardGroupForValid) {
            return c.type;
        }
        return PGR.dungeon.canObtainCopy(c) ? c.type : null;
    }

    private static void delay(ArrayList<AbstractCard> cards, Random rng) {
        int delayedIndex = 0;
        for (int i = 0; i < cards.size(); i++) {
            final AbstractCard c = cards.get(i);
            if (PCLCardTag.Delayed.has(c)) {
                if (i != delayedIndex) {
                    final AbstractCard temp = cards.get(delayedIndex);
                    cards.set(delayedIndex, c);
                    cards.set(i, temp);
                }

                delayedIndex += 1;
                PCLCardTag.Delayed.tryProgress(c);
            }
        }
    }

    private static void shuffle(CardGroup group, Random rng) {
        final ArrayList<AbstractCard> cards = group.group;
        int innateIndex = cards.size() - 1;

        delay(cards, rng);

        for (int i = cards.size() - 1; i >= 0; i--) {
            final AbstractCard c = cards.get(i);
            if (PCLCardTag.Innate.has(c)) {
                if (i != innateIndex) {
                    final AbstractCard temp = cards.get(innateIndex);
                    cards.set(innateIndex, c);
                    cards.set(i, temp);
                }
                innateIndex -= 1;
                PCLCardTag.Innate.tryProgress(c);
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {})
    public static class CardGroupPatches_Shuffle1 {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance) {
            shuffle(__instance, AbstractDungeon.shuffleRng);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "shuffle", paramtypez = {Random.class})
    public static class CardGroupPatches_Shuffle2 {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance, Random rng) {
            shuffle(__instance, rng);
        }
    }

    // These card getter methods crash if the group is empty
    // Casting is intentional to avoid crashes from incorrect type inferences in patching
    @SpirePatch(clz = CardGroup.class, method = "getBottomCard", paramtypez = {})
    public static class CardGroupPatches_GetBottomCard {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance) {
            if (__instance.group.size() == 0) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getTopCard", paramtypez = {})
    public static class CardGroupPatches_GetTopCard {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance) {
            if (__instance.group.size() == 0) {
                return SpireReturn.Return(null);
            }
            else {
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {Random.class})
    public static class CardGroupPatches_GetRandomCard1 {

        @SpirePostfixPatch
        public static void postfix(CardGroup __instance) {
            __instance.group = tmpCards;
        }

        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance, Random rng) {
            tmpCards = __instance.group;
            if (AbstractDungeonPatches.filterCardGroupForValid) {
                __instance.group = EUIUtils.filter(__instance.group, PGR.dungeon::canObtainCopy);
            }
            if (__instance.group.size() == 0) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {boolean.class})
    public static class CardGroupPatches_GetRandomCard2 {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance) {
            __instance.group = tmpCards;
        }

        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(CardGroup __instance, boolean useRng) {
            tmpCards = __instance.group;
            if (AbstractDungeonPatches.filterCardGroupForValid) {
                __instance.group = EUIUtils.filter(__instance.group, PGR.dungeon::canObtainCopy);
            }
            if (__instance.group.size() == 0) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {boolean.class, AbstractCard.CardRarity.class})
    public static class CardGroupPatches_GetRandomCard3 {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("rarity") && m.isReader()) {
                        m.replace("$_ = pinacolada.patches.card.CardGroupPatches.checkRarity($0);");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {Random.class, AbstractCard.CardRarity.class})
    public static class CardGroupPatches_GetRandomCard4 {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("rarity") && m.isReader()) {
                        m.replace("$_ = pinacolada.patches.card.CardGroupPatches.checkRarity($0);");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "getRandomCard", paramtypez = {AbstractCard.CardType.class, boolean.class})
    public static class CardGroupPatches_GetRandomCard5 {
        @SpireInstrumentPatch
        public static ExprEditor instrument() {
            return new ExprEditor() {
                public void edit(javassist.expr.FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(AbstractCard.class.getName()) && m.getFieldName().equals("type") && m.isReader()) {
                        m.replace("$_ = pinacolada.patches.card.CardGroupPatches.checkType($0);");
                    }
                }
            };
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "triggerOnOtherCardPlayed")
    public static class CardGroupPatches_OnAfterPlayCard {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard usedCard) {
            CombatManager.onAfterCardPlayed(usedCard);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToBottomOfDeck")
    public static class CardGroupPatches_MoveToBottomOfDeck {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard card) {
            CombatManager.onCardReshuffled(card, __instance);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToDeck")
    public static class CardGroupPatches_MoveToDrawPile {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard card, boolean randomSpot) {
            CombatManager.onCardReshuffled(card, __instance);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class CardGroupPatches_MoveToExhaustPile {
        @SpirePrefixPatch
        public static void prefix(CardGroup __instance, AbstractCard card) {
            CombatManager.onExhaust(card);
        }
    }

    @SpirePatch(clz = CardGroup.class, method = "applyPowers", paramtypez = {})
    public static class CardGroupPatches_ApplyPowers {
        @SpirePostfixPatch
        public static void postfix(CardGroup __instance) {
            if (CombatManager.inBattle()) {
                CombatManager.summons.applyPowers();
            }
        }
    }
}
