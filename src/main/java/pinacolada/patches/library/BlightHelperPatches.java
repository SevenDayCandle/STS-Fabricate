package pinacolada.patches.library;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import pinacolada.blights.common.GlyphBlight;
import pinacolada.blights.common.GlyphBlight1;
import pinacolada.blights.common.GlyphBlight2;
import pinacolada.blights.common.UpgradedHand;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@SpirePatch(clz = BlightHelper.class, method = "getBlight", paramtypez = {String.class})
public class BlightHelperPatches
{
    private static final HashMap<String, Class<? extends AbstractBlight>> customBlights = new HashMap<>();

    static
    {
        //TODO make this automatic
        customBlights.put(UpgradedHand.ID, UpgradedHand.class);
        customBlights.put(GlyphBlight.ID, GlyphBlight.class);
        customBlights.put(GlyphBlight1.ID, GlyphBlight1.class);
        customBlights.put(GlyphBlight2.ID, GlyphBlight2.class);
    }

    @SpirePrefixPatch
    public static SpireReturn<AbstractBlight> method(String id)
    {
        final Class<? extends AbstractBlight> blight = customBlights.get(id);
        if (blight != null)
        {
            try
            {
                return SpireReturn.Return(blight.getConstructor().newInstance());
            }
            catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        return SpireReturn.Continue();
    }
}