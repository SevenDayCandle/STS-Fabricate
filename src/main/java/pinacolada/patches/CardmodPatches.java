package pinacolada.patches;

import basemod.cardmods.EtherealMod;
import basemod.cardmods.ExhaustMod;
import basemod.cardmods.InnateMod;
import basemod.cardmods.RetainMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;

public class CardmodPatches {

    // Skip description additions of tag mods, because PCLCard already handles them

    @SpirePatch(clz = EtherealMod.class, method = "modifyDescription", optional = true)
    public static class CardmodPatches_EtherealMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(clz = ExhaustMod.class, method = "modifyDescription", optional = true)
    public static class CardmodPatches_ExhaustMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(clz = InnateMod.class, method = "modifyDescription", optional = true)
    public static class CardmodPatches_InnateMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(clz = RetainMod.class, method = "modifyDescription", optional = true)
    public static class CardmodPatches_RetainMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(cls = "CardAugments.cardmods.common.BootMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_BootMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(cls = "CardAugments.cardmods.event.GraveMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_GraveMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    @SpirePatch(cls = "CardAugments.cardmods.common.StickyMod", method = "modifyDescription", optional = true)
    public static class CardAugmentPatches_StickyMod {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            return earlyReturn(rawDescription, card);
        }
    }

    private static SpireReturn<String> earlyReturn(String rawDescription, AbstractCard card) {
        if (card instanceof PCLCard) {
            return SpireReturn.Return(rawDescription);
        }
        return SpireReturn.Continue();
    }
}
