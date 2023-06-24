package pinacolada.patches.screens;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.ui.AbstractMenuScreen;
import extendedui.utilities.EUIClassUtils;
import pinacolada.resources.PCLEnum;
import pinacolada.ui.menu.CustomMainMenuPanelButton;

import static pinacolada.resources.PCLEnum.Menus.CUSTOM;

public class MenuPanelScreenPatches {

    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class MenuPanelScreen_InitializePanels {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(MenuPanelScreen __instance) {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == CUSTOM) {
                __instance.panels.clear();
                __instance.panels.add(new CustomMainMenuPanelButton(PCLEnum.Panels.CUSTOM_CARDS, MainMenuPanelButton.PanelColor.BEIGE, (float) Settings.WIDTH / 2.0F - 450F * Settings.scale, Settings.HEIGHT * 0.5f));
                __instance.panels.add(new CustomMainMenuPanelButton(PCLEnum.Panels.CUSTOM_RELICS, MainMenuPanelButton.PanelColor.BLUE, (float) Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.5f));
                __instance.panels.add(new CustomMainMenuPanelButton(PCLEnum.Panels.CUSTOM_POTIONS, MainMenuPanelButton.PanelColor.RED, (float) Settings.WIDTH / 2.0F + 450F * Settings.scale, Settings.HEIGHT * 0.5f));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "update")
    public static class MenuPanelScreen_Update {
        @SpirePostfixPatch
        public static SpireReturn<Void> postfix(MenuPanelScreen __instance) {
            return (CardCrawlGame.mainMenuScreen.screen == AbstractMenuScreen.EUI_MENU) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }
}
