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
    // Sadly have to hardcode the prefix for now to allow these to work with the switch statement
    public static final String AllowNeow = "pcl:AllowNeow";
    public static final String Augmented = "pcl:Augmented";
    public static final String Dearth = "pcl:Dearth";
    public static final String Hatred = "pcl:Hatred";

    public static void createCustomMods(List<CustomMod> mods) {
        mods.add(makeCustomMod(AllowNeow));
        mods.add(makeCustomMod(Augmented));
        mods.add(makeCustomMod(Dearth));
    }

    public static boolean isModPositive(String key) {
        switch (key) {
            case Dearth:
                return false;
        }
        return true;
    }

    private static CustomMod makeCustomMod(String key) {
        return new CustomMod(key, isModPositive(key) ? "b" : "r", false);
    }

    // Make these custom daily mods only if the user first enters a custom run. We don't want to pollute the random daily mods pool with these mods
    private static AbstractDailyMod makeDailyMod(String key) {
        // If there is a run mod strings entry, assume there's an abstract daily mod
        AbstractDailyMod res = null;
        RunModStrings strings = PGR.getRunModStrings(key);
        if (strings != null) {
            res = new AbstractDailyMod(key, strings.NAME, strings.DESCRIPTION, "flight.png", isModPositive(key));
            Texture tex = EUIRM.getTexture(PGR.getRunModImage(key));
            if (tex != null) {
                res.img = tex;
            }
        }
        return res;
    }

    @SpirePatch(clz = ModHelper.class, method = "getMod")
    public static class AbstractRoomPatches_EndTurn {
        @SpirePostfixPatch
        public static AbstractDailyMod postfix(AbstractDailyMod retVal, String key) {
            if (retVal != null) {
                return retVal;
            }

            AbstractDailyMod res = CUSTOM_DAILY_MODS.get(key);

            if (res == null) {
                res = makeDailyMod(key);
            }

            return res;
        }
    }

}



