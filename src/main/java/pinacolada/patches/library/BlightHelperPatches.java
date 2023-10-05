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
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
public class BlightHelperPatches {
    private static final HashMap<String, Class<? extends AbstractBlight>> additionalBlights = new HashMap<>();

    public static Collection<String> getAdditionalBlightIDs() {
        return additionalBlights.keySet();
    }

    public static ArrayList<AbstractBlight> getAdditionalBlights() {
        return EUIUtils.map(getAdditionalBlightIDs(), EUIGameUtils::getSeenBlight);
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

    @SpirePrefixPatch
    public static SpireReturn<AbstractBlight> method(String id) {
        final Class<? extends AbstractBlight> blight = additionalBlights.get(id);
        if (blight != null) {
            try {
                return SpireReturn.Return(blight.getConstructor().newInstance());
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return SpireReturn.Continue();
    }
}