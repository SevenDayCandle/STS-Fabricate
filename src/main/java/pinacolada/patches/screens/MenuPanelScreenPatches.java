package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.utilities.EUIClassUtils;
import pinacolada.ui.PCLCustomMenuPanel;

import static com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen.COMPENDIUM;
import static extendedui.ui.AbstractScreen.EUI_MENU;
import static pinacolada.ui.PCLCustomMenuPanel.CUSTOM_CARDS;

public class MenuPanelScreenPatches
{

    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class MenuPanelScreen_InitializePanels
    {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(MenuPanelScreen __instance)
        {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == COMPENDIUM)
            {
                __instance.panels.clear();
                __instance.panels.add(new PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult.INFO_CARD, MainMenuPanelButton.PanelColor.BLUE, (float) Settings.WIDTH / 2.0F - 480.0F * Settings.scale, Settings.HEIGHT * 0.5f));
                __instance.panels.add(new PCLCustomMenuPanel(CUSTOM_CARDS, MainMenuPanelButton.PanelColor.BEIGE, (float) Settings.WIDTH / 2.0F - 80.0F * Settings.scale, Settings.HEIGHT * 0.5f).setPanelColor(new Color(0.3F, 0.5F, 0.3F, 0.2F)));
                __instance.panels.add(new PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult.INFO_RELIC, MainMenuPanelButton.PanelColor.BEIGE, (float) Settings.WIDTH / 2.0F + 320.0F * Settings.scale, Settings.HEIGHT * 0.5f));
                __instance.panels.add(new PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult.INFO_POTION, MainMenuPanelButton.PanelColor.RED, (float) Settings.WIDTH / 2.0F + 720.0F * Settings.scale, Settings.HEIGHT * 0.5f));
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "update")
    public static class MenuPanelScreen_Update
    {
        @SpirePostfixPatch
        public static SpireReturn<Void> postfix(MenuPanelScreen __instance)
        {
            return (CardCrawlGame.mainMenuScreen.screen == EUI_MENU) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }
}
