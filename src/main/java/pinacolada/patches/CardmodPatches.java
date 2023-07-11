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
    @SpirePatch(clz = ExhaustMod.class, method = "modifyDescription", optional = true)
    @SpirePatch(clz = InnateMod.class, method = "modifyDescription", optional = true)
    @SpirePatch(clz = RetainMod.class, method = "modifyDescription", optional = true)
    @SpirePatch(cls = "CardAugments.cardmods.common.BootMod", method = "modifyDescription", optional = true)
    @SpirePatch(cls = "CardAugments.cardmods.event.GraveMod", method = "modifyDescription", optional = true)
    @SpirePatch(cls = "CardAugments.cardmods.common.StickyMod", method = "modifyDescription", optional = true)
    public static class CardmodPatches_EarlyReturn {

        @SpirePrefixPatch
        public static SpireReturn<String> prefix(Object mod, String rawDescription, AbstractCard card) {
            if (card instanceof PCLCard) {
                return SpireReturn.Return(rawDescription);
            }
            return SpireReturn.Continue();
        }
    }
}
