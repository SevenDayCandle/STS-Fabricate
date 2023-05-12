package pinacolada.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;

public class ChimeraCardsPatches {

    // Skip description additions of tag mods, because PCLCard already handles them

    @SpirePatch(cls = "CardAugments.cardmods.common.BootMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_BootMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            if (card instanceof PCLCard) {
                return SpireReturn.Return(rawDescription);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(cls = "CardAugments.cardmods.event.GraveMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_GraveMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            if (card instanceof PCLCard) {
                return SpireReturn.Return(rawDescription);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(cls = "CardAugments.cardmods.common.StickyMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_StickyMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            if (card instanceof PCLCard) {
                return SpireReturn.Return(rawDescription);
            }
            return SpireReturn.Continue();
        }
    }
}
