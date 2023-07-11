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
    @SpirePatch(clz = Duplicator.class, method = "use")
    @SpirePatch(clz = Falling.class, method = SpirePatch.CONSTRUCTOR)
    @SpirePatch(clz = WeMeetAgain.class, method = SpirePatch.CONSTRUCTOR)
    public static class CardDuplicationPatches_Do {
        @SpirePostfixPatch
        public static void postfix(Object __instance) {
            afterUse();
        }

        @SpirePrefixPatch
        public static void prefix(Object __instance) {
            beforeUse();
        }
    }
}