package pinacolada.patches.dungeon;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.daily.mods.AbstractDailyMod;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.RunModStrings;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import extendedui.EUIRM;
import pinacolada.resources.PGR;

import java.util.HashMap;
import java.util.List;

public class ModHelperPatches {
    private static final HashMap<String, AbstractDailyMod> CUSTOM_DAILY_MODS = new HashMap<>();
    public static final String AllowNeow = "pcl:AllowNeow";
    public static final String Augmented = "pcl:Augmented";
    public static final String Dearth = "pcl:Dearth";

    public static void createCustomMods(List<CustomMod> mods) {
        mods.add(makeCustomMod(AllowNeow));
        mods.add(makeCustomMod(Augmented));
        mods.add(makeCustomMod(Dearth));
    }

    // TODO make into switch if more mods get added in the future
    public static boolean isModPositive(String key) {
        return !key.equals(Dearth);
    }

    private static CustomMod makeCustomMod(String key) {
        return new CustomMod(key, isModPositive(key) ? "b" : "r", false);
    }

    // Make these custom daily mods only if the user first enters a custom run. We don't want to pollute the random daily mods pool with these mods
    private static AbstractDailyMod makeDailyMod(String key) {
        // If there is a run mod strings entry, assume there's an abstract daily mod
        AbstractDailyMod res = null;
        RunModStrings strings = PGR.getRunModStrings(key);
        String path = PGR.getRunModImage(key);
        // Apparently some vanilla non-daily mods get through this check so we need to ensure they don't actually appear in the modifiers list
        if (strings != null && path != null) {
            res = new AbstractDailyMod(key, strings.NAME, strings.DESCRIPTION, "flight.png", isModPositive(key));
            Texture tex = EUIRM.getTexture(path);
            if (tex != null) {
                res.img = tex;
            }
        }
        return res;
    }

    @SpirePatch(clz = ModHelper.class, method = "getMod")
    public static class ModHelperPatches_GetMod {
        @SpirePostfixPatch
        public static AbstractDailyMod postfix(AbstractDailyMod retVal, String key) {
            if (retVal != null) {
                return retVal;
            }

            AbstractDailyMod res = CUSTOM_DAILY_MODS.get(key);

            if (res == null) {
                res = makeDailyMod(key);
                CUSTOM_DAILY_MODS.put(key, res);
            }

            return res;
        }
    }

}



