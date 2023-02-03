package pinacolada.patches.library;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.ReplacementData;
import pinacolada.cards.pcl.special.QuestionMark;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CardLibraryPatches
{
    public static String[] splitCardID(String cardID)
    {
        return cardID.split(Pattern.quote(":"), 2);
    }

    public static void tryReplace(AbstractCard[] card)
    {
        card[0] = tryReplace(card[0]);
    }

    public static AbstractCard tryReplace(AbstractCard card)
    {
        AbstractPlayer.PlayerClass playerClass = GameUtilities.getPlayerClass();
        if (GameUtilities.isPCLPlayerClass(playerClass))
        {
            PCLResources<?,?,?> resources = PGR.getResources(playerClass);
            return tryReplace(resources, card);
        }
        return card;
    }

    public static AbstractCard tryReplace(PCLResources<?,?,?> resources, AbstractCard card)
    {
        if (card instanceof PCLCard)
        {
            return card;
        }
        PCLCardData data = resources.getReplacement(card.cardID);
        if (data != null)
        {
            return data.makeCopy(card.upgraded);
        }
        else if (PGR.core.config.replaceCardsPCL.get())
        {
            AbstractCard c = ReplacementData.makeReplacement(card, true);
            if (card.upgraded)
            {
                c.upgrade();
            }
            return c;
        }
        return card;
    }

    public static PCLCardData getStandardReplacement(String id)
    {
        AbstractPlayer.PlayerClass playerClass = GameUtilities.getPlayerClass();
        if (GameUtilities.isPCLPlayerClass(playerClass))
        {
            PCLResources<?,?,?> resources = PGR.getResources(playerClass);
            return resources.getReplacement(id);
        }
        return null;
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCard", paramtypez = {String.class})
    public static class CardLibraryPatches_GetCard
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(String key)
        {
            if (PGR.isLoaded())
            {
                final PCLCardData data = getStandardReplacement(key);
                if (data != null)
                {
                    return SpireReturn.Return(data.makeCopy(false));
                }

                PCLCustomCardSlot slot = PCLCustomCardSlot.get(key);
                if (slot != null)
                {
                    return SpireReturn.Return(slot.getBuilder(0).build(true));
                }
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class CardLibraryPatches_GetCopy
    {
        @SpirePostfixPatch
        public static AbstractCard postfix(AbstractCard __result, String key, int upgradeTime, int misc)
        {
            // If a card is not found, the base game will put a Madness in its place. This change makes it easier for players to see what card is missing
            if (__result instanceof Madness && !Madness.ID.equals(key))
            {
                __result = new QuestionMark();
                __result.name = __result.originalName = key;
                EUIUtils.logError(CardLibrary.class, "Card not found: " + key);
            }
            return CardLibraryPatches.tryReplace(__result);
        }

        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(String key, int upgradeTime, int misc)
        {
            if (key.equals(AscendersBane.ID))
            {
                AbstractPlayer.PlayerClass pClass = GameUtilities.getPlayerClass();
                if (GameUtilities.isPCLPlayerClass(pClass))
                {
                    PCLResources<?, ?, ?> resources = PGR.getResources(pClass);
                    PCLCardData bane = resources.getAscendersBane();
                    if (bane != null)
                    {
                        return SpireReturn.Return(bane.makeCopy(false));
                    }
                }
            }
            final PCLCardData data = getStandardReplacement(key);
            if (data != null)
            {
                return SpireReturn.Return(data.makeCopy(false));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCurse", paramtypez = {})
    public static class CardLibraryPatches_GetCurse
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix()
        {
            return CardLibraryPatches_GetCurse2.postfix(null, AbstractDungeon.cardRng);
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCurse", paramtypez = {AbstractCard.class, Random.class})
    public static class CardLibraryPatches_GetCurse2
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> postfix(AbstractCard ignore, Random rng)
        {
            final RandomizedList<String> cards = new RandomizedList<>();
            final HashMap<String, AbstractCard> curses = ReflectionHacks.getPrivateStatic(CardLibrary.class, "curses");
            for (Map.Entry<String, AbstractCard> entry : curses.entrySet())
            {
                final AbstractCard c = entry.getValue();
                if (c.rarity != AbstractCard.CardRarity.SPECIAL && (ignore == null || !c.cardID.equals(ignore.cardID)) && getStandardReplacement(c.cardID) == null)
                {
                    cards.add(entry.getKey());
                }
            }

            return SpireReturn.Return(CardLibrary.cards.get(cards.retrieve(rng)));
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardType.class, AbstractCard.CardRarity.class})
    public static class CardLibraryPatches_GetAnyColorCard
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardType type, AbstractCard.CardRarity rarity)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                return SpireReturn.Return(GameUtilities.getAnyColorCardFiltered(rarity, type, false));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardRarity.class})
    public static class CardLibraryPatches_GetAnyColorCard2
    {
        @SpirePrefixPatch
        public static SpireReturn<AbstractCard> prefix(AbstractCard.CardRarity rarity)
        {
            if (GameUtilities.isPCLPlayerClass())
            {
                return SpireReturn.Return(GameUtilities.getAnyColorCardFiltered(rarity, null, true));
            }
            return SpireReturn.Continue();
        }
    }
}
