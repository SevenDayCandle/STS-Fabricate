package pinacolada.patches.effects;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;

public class BorderFlashPatches {
    @SpirePatch(clz = BorderFlashEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {Color.class, boolean.class})
    public static class BorderFlashEffect_Ctor {
        @SpirePostfixPatch
        public static void postfix(BorderFlashEffect __instance, Color color, boolean additive) {
            if (Settings.DISABLE_EFFECTS) {
                __instance.isDone = true;
            }
        }
    }

    @SpirePatch(clz = BorderLongFlashEffect.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {Color.class, boolean.class})
    public static class BorderLongFlashEffect_Ctor {
        @SpirePostfixPatch
        public static void postfix(BorderLongFlashEffect __instance, Color color, boolean additive) {
            if (Settings.DISABLE_EFFECTS) {
                __instance.isDone = true;
            }
        }
    }
}
