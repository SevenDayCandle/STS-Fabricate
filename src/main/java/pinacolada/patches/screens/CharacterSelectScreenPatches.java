package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import extendedui.EUI;
import extendedui.ui.AbstractScreen;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;

public class CharacterSelectScreenPatches
{
    @SpirePatch(clz = CharacterSelectScreen.class, method = "initialize")
    public static class CharacterSelectScreen_Initialize
    {
        @SpirePostfixPatch
        public static void initialize(CharacterSelectScreen __instance)
        {
            PGR.core.charSelectProvider.initialize(__instance);
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "open")
    public static class CharacterSelectScreen_Open
    {
        @SpirePostfixPatch
        public static void open(CharacterSelectScreen __instance)
        {
            PCLCard.refreshSimpleModePreview(PGR.core.config.simpleMode.get());
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "render")
    public static class CharacterSelectScreen_Render
    {
//        @SpirePrefixPatch
//        public static SpireReturn prefix(CharacterSelectScreen __instance, SpriteBatch sb)
//        {
//            return AbstractDungeon.isScreenUp ? SpireReturn.Return() : SpireReturn.Continue();
//        }

        @SpirePostfixPatch
        public static void postfix(CharacterSelectScreen __instance, SpriteBatch sb)
        {
            PGR.core.charSelectProvider.render(__instance, sb);
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class CharacterSelectScreen_Update
    {
        @SpirePostfixPatch
        public static void postfix(CharacterSelectScreen __instance)
        {
            PGR.core.charSelectProvider.update(__instance);
        }

        @SpirePrefixPatch
        public static SpireReturn prefix(CharacterSelectScreen __instance)
        {
            return (EUI.currentScreen != null && AbstractDungeon.screen == AbstractScreen.EUI_SCREEN) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }
}
