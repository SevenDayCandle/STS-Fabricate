package pinacolada.patches.card;

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
public class CardDuplicationPatches {
    private static CardGroup masterDeckCache;

    protected static void afterUse() {
        AbstractDungeon.player.masterDeck = masterDeckCache;
    }

    protected static void beforeUse() {
        final CardGroup temp = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (GameUtilities.canObtainCopy(c)) {
                temp.group.add(c);
            }
        }

        masterDeckCache = AbstractDungeon.player.masterDeck;
        AbstractDungeon.player.masterDeck = temp;
    }

    @SpirePatch(clz = DollysMirror.class, method = "onEquip")
    public static class CardDuplicationPatches_DollysMirror_OnEquip {
        @SpirePostfixPatch
        public static void postfix(DollysMirror __instance) {
            afterUse();
        }

        @SpirePrefixPatch
        public static void prefix(DollysMirror __instance) {
            beforeUse();
        }
    }

    @SpirePatch(clz = Duplicator.class, method = "use")
    public static class CardDuplicationPatches_Duplicator_Use {
        @SpirePostfixPatch
        public static void postfix(Duplicator __instance) {
            afterUse();
        }

        @SpirePrefixPatch
        public static void prefix(Duplicator __instance) {
            beforeUse();
        }
    }

    @SpirePatch(clz = WeMeetAgain.class, method = SpirePatch.CONSTRUCTOR)
    public static class CardRemovalPatches_WeMeetAgain_Ctor {
        @SpirePostfixPatch
        public static void postfix(WeMeetAgain __instance) {
            afterUse();
        }

        @SpirePrefixPatch
        public static void prefix(WeMeetAgain __instance) {
            beforeUse();
        }
    }

    @SpirePatch(clz = Falling.class, method = SpirePatch.CONSTRUCTOR)
    public static class CardRemovalPatches_FallingCtor {
        @SpirePostfixPatch
        public static void postfix(Falling __instance) {
            afterUse();
        }

        @SpirePrefixPatch
        public static void prefix(Falling __instance) {
            beforeUse();
        }
    }
}