package pinacolada.patches.library;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class BlightHelperPatches {
    private static boolean TEMP_DISABLE_PATCH;
    private static final HashMap<String, Class<? extends AbstractBlight>> additionalBlights = new HashMap<>();

    public static Collection<String> getAdditionalBlightIDs() {
        return additionalBlights.keySet();
    }

    public static ArrayList<AbstractBlight> getAdditionalBlights() {
        return EUIUtils.map(getAdditionalBlightIDs(), EUIGameUtils::getSeenBlight);
    }

    public static AbstractBlight getDirectBlight(String id) {
        AbstractBlight blight = getDirectBlightInternal(id);
        if (blight != null) {
            return blight;
        }
        // Hardcoded stuff -_-
        TEMP_DISABLE_PATCH = true;
        blight = BlightHelper.getBlight(id);
        TEMP_DISABLE_PATCH = false;
        return blight;
    }

    private static AbstractBlight getDirectBlightInternal(String id) {
        PCLBlightData data = PCLBlightData.getStaticData(id);
        if (data != null) {
            return data.create();
        }
        final Class<? extends AbstractBlight> blight = additionalBlights.get(id);
        if (blight != null) {
            try {
                return blight.getConstructor().newInstance();
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void loadCustomBlights() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleBlight.class)) {
            try {
                VisibleBlight a = ct.getAnnotation(VisibleBlight.class);
                Object data = ReflectionHacks.getPrivateStatic(ct, a.data());
                String id = data instanceof PCLBlightData ? ((PCLBlightData) data).ID : String.valueOf(data);
                additionalBlights.put(id, (Class<? extends AbstractBlight>) ct);
                EUIUtils.logInfoIfDebug(BlightHelper.class, "Adding blight " + id);
            }
            catch (Exception e) {
                EUIUtils.logError(PGR.class, "Failed to load blight " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    @SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
    public static class BlightHelperPatches_GetBlight {
        @SpirePrefixPatch
        public static SpireReturn<AbstractBlight> method(String id) {
            if (!TEMP_DISABLE_PATCH) {
                AbstractBlight internal = getDirectBlightInternal(id);
                if (internal != null) {
                    return SpireReturn.Return(internal);
                }
                PCLCustomBlightSlot slot = PCLCustomBlightSlot.get(id);
                if (slot != null) {
                    return SpireReturn.Return(slot.make());
                }
            }
            return SpireReturn.Continue();
        }
    }
}