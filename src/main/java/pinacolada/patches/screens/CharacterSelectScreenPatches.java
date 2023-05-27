package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import extendedui.EUI;
import extendedui.ui.AbstractMenuScreen;
import pinacolada.resources.PGR;

public class CharacterSelectScreenPatches {
    @SpirePatch(clz = CharacterSelectScreen.class, method = "initialize")
    public static class CharacterSelectScreen_Initialize {
        @SpirePostfixPatch
        public static void initialize(CharacterSelectScreen __instance) {
            PGR.charSelectProvider.initialize(__instance);
        }
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class CharacterSelectScreen_Update {
        @SpirePostfixPatch
        public static void postfix(CharacterSelectScreen __instance) {
            PGR.charSelectProvider.updateSelectedCharacter(__instance);
        }

        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(CharacterSelectScreen __instance) {
            return (EUI.currentScreen != null && (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == AbstractMenuScreen.EUI_MENU)) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }
}
