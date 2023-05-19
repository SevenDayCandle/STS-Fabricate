package pinacolada.patches.library;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class RelicLibraryPatches {
    public static final String SEPARATOR = "~";

    /**
     * Directly get a card from the card library, bypassing postfixes attached to getRelic. Returns NULL instead of circlet if nothing was found
     */
    public static AbstractRelic getDirectRelic(String id) {
        AbstractRelic relic = null;
        relic = GameUtilities.getRelics(AbstractCard.CardColor.COLORLESS).get(id);
        if (relic != null) {
            return relic;
        }
        relic = GameUtilities.getRelics(AbstractCard.CardColor.RED).get(id);
        if (relic != null) {
            return relic;
        }
        relic = GameUtilities.getRelics(AbstractCard.CardColor.GREEN).get(id);
        if (relic != null) {
            return relic;
        }
        relic = GameUtilities.getRelics(AbstractCard.CardColor.BLUE).get(id);
        if (relic != null) {
            return relic;
        }
        relic = GameUtilities.getRelics(AbstractCard.CardColor.PURPLE).get(id);
        if (relic != null) {
            return relic;
        }
        return BaseMod.getCustomRelic(id);
    }

    @SpirePatch(clz = RelicLibrary.class, method = "getRelic", paramtypez = {String.class})
    public static class RelicLibraryPatches_GetRelic {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> prefix(String key) {
            if (PGR.isLoaded()) {
                // TODO Replacements

                // Allow getRelic to get custom relics too
                PCLCustomRelicSlot slot = PCLCustomRelicSlot.get(key);
                if (slot != null) {
                    return SpireReturn.Return(slot.makeRelic());
                }
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = RelicLibrary.class, method = "isARelic", paramtypez = {String.class})
    public static class RelicLibraryPatches_IsARelic {
        @SpirePostfixPatch
        public static boolean postfix(boolean retVal, String key) {
            return retVal || PCLCustomRelicSlot.get(key) != null;
        }
    }
}