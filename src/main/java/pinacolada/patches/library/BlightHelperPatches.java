package pinacolada.patches.library;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleBlight;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
public class BlightHelperPatches {
    private static final HashMap<String, Class<? extends AbstractBlight>> customBlights = new HashMap<>();

    public static void loadCustomBlights() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleBlight.class)) {
            try {
                VisibleBlight a = ct.getAnnotation(VisibleBlight.class);
                String id = ReflectionHacks.getPrivateStatic(ct, a.id());
                customBlights.put(id, (Class<? extends AbstractBlight>) ct);
                EUIUtils.logInfoIfDebug(BlightHelper.class, "Adding blight " + id);
            }
            catch (Exception e) {
                EUIUtils.logError(PGR.class, "Failed to load blight " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    @SpirePrefixPatch
    public static SpireReturn<AbstractBlight> method(String id) {
        final Class<? extends AbstractBlight> blight = customBlights.get(id);
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