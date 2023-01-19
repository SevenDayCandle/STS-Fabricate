package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.beyond.Falling;
import com.megacrit.cardcrawl.events.shrines.Duplicator;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.relics.DollysMirror;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class CardDuplicationPatches
{
    private static CardGroup masterDeckCache;

    protected static void beforeUse()
    {
        final CardGroup temp = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
        {
            if (GameUtilities.canObtainCopy(c))
            {
                temp.group.add(c);
            }
        }

        masterDeckCache = AbstractDungeon.player.masterDeck;
        AbstractDungeon.player.masterDeck = temp;
    }

    protected static void afterUse()
    {
        AbstractDungeon.player.masterDeck = masterDeckCache;
    }

    @SpirePatch(clz = DollysMirror.class, method = "onEquip")
    public static class CardDuplicationPatches_DollysMirror_OnEquip
    {
        @SpirePrefixPatch
        public static void prefix(DollysMirror __instance)
        {
            beforeUse();
        }

        @SpirePostfixPatch
        public static void postfix(DollysMirror __instance)
        {
            afterUse();
        }
    }

    @SpirePatch(clz = Duplicator.class, method = "use")
    public static class CardDuplicationPatches_Duplicator_Use
    {
        @SpirePrefixPatch
        public static void prefix(Duplicator __instance)
        {
            beforeUse();
        }

        @SpirePostfixPatch
        public static void postfix(Duplicator __instance)
        {
            afterUse();
        }
    }

    @SpirePatch(clz = WeMeetAgain.class, method = SpirePatch.CONSTRUCTOR)
    public static class CardRemovalPatches_WeMeetAgain_Ctor
    {
        @SpirePrefixPatch
        public static void prefix(WeMeetAgain __instance)
        {
            beforeUse();
        }

        @SpirePostfixPatch
        public static void postfix(WeMeetAgain __instance)
        {
            afterUse();
        }
    }

    @SpirePatch(clz = Falling.class, method = SpirePatch.CONSTRUCTOR)
    public static class CardRemovalPatches_FallingCtor
    {
        @SpirePrefixPatch
        public static void prefix(Falling __instance)
        {
            beforeUse();
        }

        @SpirePostfixPatch
        public static void postfix(Falling __instance)
        {
            afterUse();
        }
    }
}