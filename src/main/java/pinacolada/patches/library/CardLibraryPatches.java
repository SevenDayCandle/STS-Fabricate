package pinacolada.patches.library;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.*;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.HashMap;
import java.util.Map;

public class CardLibraryPatches {
    protected static SpireReturn<AbstractCard> getActualCard(String key) {
        if (PGR.isLoaded()) {
            // Only make replacements in game
            if (EUIGameUtils.inGame()) {
                AbstractCard res = getReplacement(key);
                if (res != null) {
                    return SpireReturn.Return(res);
                }
            }

            // Allow getCard to get custom cards too
            PCLCustomCardSlot slot = PCLCustomCardSlot.get(key);
            if (slot != null) {
                return SpireReturn.Return(slot.make(true));
            }
        }

        return SpireReturn.Continue();
    }

    /**
     * Directly get a card from the card library, bypassing postfixes attached to getCard
     */
    public static AbstractCard getDirectCard(String id) {
        return CardLibrary.cards.get(id);
    }

    protected static AbstractCard getRandomFilteredCurse(FuncT1<Boolean, AbstractCard> filterFunc, Random rng) {
        final RandomizedList<String> cards = new RandomizedList<>();
        final HashMap<String, AbstractCard> curses = ReflectionHacks.getPrivateStatic(CardLibrary.class, "curses");
        for (Map.Entry<String, AbstractCard> entry : curses.entrySet()) {
            final AbstractCard c = entry.getValue();
            if (filterFunc.invoke(c)) {
                cards.add(entry.getKey());
            }
        }
        return CardLibrary.cards.get(cards.retrieve(rng));
    }

    public static AbstractCard getReplacement(String cardID) {
        return getReplacement(cardID, 0);
    }

    // When playing as a PCL class, always replace
    // When playing as a non-PCL class:
    //  - If a template card is found, replace it if replaceCardsPCL is not set
    //  - If a non-template card is found, replace it if replaceCardsPCL is set
    public static AbstractCard getReplacement(String cardID, int upgradeTimes) {
        AbstractCard replacement = null;
        AbstractPlayer.PlayerClass playerClass = GameUtilities.getPlayerClass();
        boolean alwaysReplace = PGR.config.replaceCardsPCL.get();

        String replacementID = getStandardReplacementID(cardID);
        if (replacementID != null) {
            PCLCardData data = PCLCardData.getStaticData(replacementID);
            if (data != null) {
                replacement = data.makeCardFromLibrary(upgradeTimes);
            }
            else if (!PGR.config.replaceCardsPCL.get()) {
                replacement = getDirectCard(replacementID);
                if (replacement != null) {
                    replacement = replacement.makeCopy();
                    for (int i = 0; i < upgradeTimes; i++) {
                        replacement.upgrade();
                    }
                }
            }
        }
        return replacement;
    }

    public static AbstractCard getReplacement(AbstractCard card) {
        return getReplacement(card, card.timesUpgraded);
    }

    public static AbstractCard getReplacement(AbstractCard card, int upgradeTimes) {
        AbstractCard replacement = getReplacement(card.cardID, upgradeTimes);
        if (replacement != null) {
            return replacement;
        }
        else if (PGR.config.replaceCardsPCL.get()) {
            return makeReplacementCard(card);
        }

        return replacement;
    }

    public static String getStandardReplacementID(String id) {
        return PGR.getResources(GameUtilities.getPlayerClass()).getReplacement(id);
    }

    public static AbstractCard makeReplacementCard(AbstractCard card) {
        if (!(card instanceof PCLCard)) {
            PCLDynamicCard c = ReplacementCardData.makeReplacement(card, true);
            if (card.upgraded) {
                c.upgrade();
            }
            return c;
        }
        return card;
    }

    public static void tryReplace(AbstractCard[] card) {
        AbstractCard c = getReplacement(card[0]);
        if (c != null) {
            card[0] = c;
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCard", paramtypez = {String.class})
    public static class CardLibraryPatches_GetCard {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(String key) {
            return getActualCard(key);
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCard", paramtypez = {AbstractPlayer.PlayerClass.class, String.class})
    public static class CardLibraryPatches_GetCard2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractPlayer.PlayerClass plyrClass, String key) {
            return getActualCard(key);
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class CardLibraryPatches_GetCopy {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard __result, String key, int upgradeTime, int misc) {
            // If a card is not found, the base game will put a Madness in its place. This change makes it easier for players to see what card is missing
            if (__result instanceof Madness && !Madness.ID.equals(key)) {
                if (!PGR.config.madnessReplacements.get()) {
                    __result = new QuestionMark();
                    __result.cardID = key;
                }
                __result.name = __result.originalName = key;
                EUIUtils.logError(CardLibrary.class, "Card not found: " + key);
                return __result;
            }
            return __result;
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCurse", paramtypez = {})
    public static class CardLibraryPatches_GetCurse {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix() {
            AbstractCard curse = getRandomFilteredCurse(c -> c.rarity != AbstractCard.CardRarity.SPECIAL && getStandardReplacementID(c.cardID) == null, AbstractDungeon.cardRng);
            if (curse != null) {
                return SpireReturn.Return(curse);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCurse", paramtypez = {AbstractCard.class, Random.class})
    public static class CardLibraryPatches_GetCurse2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> postfix(AbstractCard ignore, Random rng) {
            AbstractCard curse = getRandomFilteredCurse(c -> c.rarity != AbstractCard.CardRarity.SPECIAL && (ignore == null || !c.cardID.equals(ignore.cardID)) && getStandardReplacementID(c.cardID) == null, rng);
            if (curse != null) {
                return SpireReturn.Return(curse);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardType.class, AbstractCard.CardRarity.class})
    public static class CardLibraryPatches_GetAnyColorCard {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardType type, AbstractCard.CardRarity rarity) {
            return SpireReturn.Return(PGR.dungeon.getAnyColorRewardCard(rarity, type, true, false));
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardRarity.class})
    public static class CardLibraryPatches_GetAnyColorCard2 {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardRarity rarity) {
            return SpireReturn.Return(PGR.dungeon.getAnyColorRewardCard(rarity, null, true, true));
        }
    }
}
