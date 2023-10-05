package pinacolada.patches.screens;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import extendedui.patches.screens.MenuPanelScreenPatches;
import extendedui.ui.controls.EUIMainMenuPanelButton;
import extendedui.utilities.EUIClassUtils;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.editor.blight.PCLCustomBlightSelectorScreen;
import pinacolada.ui.editor.card.PCLCustomCardSelectorScreen;
import pinacolada.ui.editor.potion.PCLCustomPotionSelectorScreen;
import pinacolada.ui.editor.relic.PCLCustomRelicSelectorScreen;

import java.util.ArrayList;

import static pinacolada.resources.PCLEnum.Menus.CUSTOM;

public class CustomEditorMenuScreenPatches {

    public static ArrayList<EUIMainMenuPanelButton> getEditors() {
        ArrayList<EUIMainMenuPanelButton> available = new ArrayList<>();
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BEIGE, PCLCoreImages.Menu.menuCard.texture(), PGR.core.strings.menu_card, PGR.core.strings.menu_cardDesc, () -> PGR.customCards.open(null, PCLCustomCardSelectorScreen.currentColor)));
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_BLUE, PCLCoreImages.Menu.menuRelic.texture(), PGR.core.strings.menu_relic, PGR.core.strings.menu_relicDesc, () -> PGR.customRelics.open(null, PCLCustomRelicSelectorScreen.currentColor)));
        available.add(new EUIMainMenuPanelButton(ImageMaster.MENU_PANEL_BG_RED, PCLCoreImages.Menu.menuPotion.texture(), PGR.core.strings.menu_potion, PGR.core.strings.menu_potionDesc, () -> PGR.customPotions.open(null, PCLCustomPotionSelectorScreen.currentColor)));
        available.add(new EUIMainMenuPanelButton(new Color(0.6f, 0.7f, 0.5f, 1f), ImageMaster.MENU_PANEL_BG_BEIGE, PCLCoreImages.Menu.menuBlight.texture(), PGR.core.strings.menu_blight, PGR.core.strings.menu_blightDesc, () -> PGR.customBlights.open(null, PCLCustomBlightSelectorScreen.currentColor)));
        available.add(new EUIMainMenuPanelButton(new Color(0.8f, 0.65f, 0.4f, 1f), ImageMaster.MENU_PANEL_BG_BEIGE, PCLCoreImages.Menu.menuPower.texture(), PGR.core.strings.menu_power, PGR.core.strings.menu_powerDesc, () -> PGR.customPowers.open(null, AbstractCard.CardColor.COLORLESS)));
        return available;
    }

    @SpirePatch(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class MenuPanelScreen_InitializePanels {
        @SpirePrefixPatch
        public static SpireReturn<Void> prefix(MenuPanelScreen __instance) {
            MenuPanelScreen.PanelScreen screen = EUIClassUtils.getField(__instance, "screen");
            if (screen == CUSTOM) {
                __instance.panels.clear();
                MenuPanelScreenPatches.setupPanels(getEditors());
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
}
