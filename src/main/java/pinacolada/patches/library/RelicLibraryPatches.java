package pinacolada.patches.library;

import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class RelicLibraryPatches {
    public static final String SEPARATOR = "~";

    public static void addRelic(ArrayList<String> list, AbstractRelic relic) {
        if (relic instanceof PCLRelic && relic instanceof CustomSavable<?>) {
            final CustomSavable<?> r = EUIUtils.safeCast(relic, CustomSavable.class);
            if (r != null && r.savedType() == Integer.class) {
                list.add(relic.relicId + SEPARATOR + r.onSave());
                return;
            }
        }

        list.add(relic.relicId);
    }

    public static AbstractRelic getRelic(String key) {
        if (PGR.isLoaded()) {
            // Do not replace customsavable relics
            final int sepIndex = key.indexOf(SEPARATOR);
            if (sepIndex >= 0) {
                final String baseID = key.substring(0, sepIndex);
                final AbstractRelic relic = RelicLibrary.getRelic(baseID).makeCopy();
                final CustomSavable<?> r = EUIUtils.safeCast(relic, CustomSavable.class);
                if (r != null && r.savedType() == Integer.class) {
                    //noinspection CastCanBeRemovedNarrowingVariableType
                    ((CustomSavable<Integer>) r).onLoad(EUIUtils.parseInt(key.substring(sepIndex + 1), -1));
                }

                return relic;
            }
        }

        return null;
    }

    @SpirePatch(clz = RelicLibrary.class, method = "getRelic", paramtypez = {String.class})
    public static class RelicLibraryPatches_GetRelic {
        @SpirePrefixPatch
        public static SpireReturn<AbstractRelic> prefix(String key) {
            AbstractRelic relic = getRelic(key);
            return relic != null ? SpireReturn.Return(relic) : SpireReturn.Continue();
        }
    }
}