package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import pinacolada.resources.PGR;

public class CustomModeScreenPatches
{
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "initializeCharacters"
    )
    public static class CustomModeScreen_Initialize
    {
        @SpirePostfixPatch
        public static void postfix(CustomModeScreen screen)
        {
            // Must perform initialization right after mods are first initialized
            PGR.core.customMode.initialize(screen);
        }
    }

    @SpirePatch(clz = CustomModeScreen.class, method = "open")
    public static class CustomModeScreen_Open
    {

        @SpirePrefixPatch
        public static void prefix(CustomModeScreen screen)
        {
            if (!PGR.config.vanillaLibraryScreen.get())
            {
                PGR.core.customMode.open();
            }
        }
    }

    @SpirePatch(clz = CustomModeScreen.class, method = "update")
    public static class CustomModeScreen_Update
    {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CustomModeScreen screen)
        {
            if (!PGR.config.vanillaLibraryScreen.get())
            {
                PGR.core.customMode.updateImpl();
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = CustomModeScreen.class, method = "render")
    public static class CustomModeScreen_Render
    {

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CustomModeScreen screen, SpriteBatch sb)
        {
            if (!PGR.config.vanillaLibraryScreen.get())
            {
                PGR.core.customMode.renderImpl(sb);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
