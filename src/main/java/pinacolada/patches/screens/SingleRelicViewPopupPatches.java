package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class SingleRelicViewPopupPatches {
    @SpirePatch(clz = SingleRelicViewPopup.class, method = "open", paramtypez = {AbstractRelic.class})
    public static class SingleRelicViewPopup_Open {
        @SpirePrefixPatch
        public static SpireReturn<Void> insert(SingleRelicViewPopup __instance, AbstractRelic relic) {
            if (PGR.config.fabricatePopup.get() || relic instanceof PCLRelic) {
                PGR.relicPopup.open(relic, null);
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "open", paramtypez = {AbstractRelic.class, ArrayList.class})
    public static class SingleRelicViewPopup_Open2 {
        @SpirePrefixPatch
        public static SpireReturn<Void> insert(SingleRelicViewPopup __instance, AbstractRelic relic, ArrayList<AbstractRelic> group) {
            if (PGR.config.fabricatePopup.get() || relic instanceof PCLRelic) {
                PGR.relicPopup.open(relic, group);
                return SpireReturn.Return(null);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "openNext")
    public static class SingleRelicViewPopup_OpenNext {
        @SpirePostfixPatch
        public static void insert(SingleRelicViewPopup __instance) {
            PGR.relicPopup.forceUnfade();
        }
    }

    @SpirePatch(clz = SingleRelicViewPopup.class, method = "openPrev")
    public static class SingleRelicViewPopup_OpenPrev {
        @SpirePostfixPatch
        public static void insert(SingleRelicViewPopup __instance) {
            PGR.relicPopup.forceUnfade();
        }
    }

}
