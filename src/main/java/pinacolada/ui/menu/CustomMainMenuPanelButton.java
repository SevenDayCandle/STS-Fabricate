package pinacolada.ui.menu;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.editor.card.PCLCustomCardSelectorScreen;
import pinacolada.ui.editor.potion.PCLCustomPotionSelectorScreen;
import pinacolada.ui.editor.relic.PCLCustomRelicSelectorScreen;

public class CustomMainMenuPanelButton extends MainMenuPanelButton {
    protected static final int P_H = 206;
    protected static final int P_W = 317;
    public static final float START_Y = Settings.HEIGHT / 2.0F;
    public static final int PANEL_H = 800;
    public static final int PANEL_W = 512;

    public CustomMainMenuPanelButton(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y) {
        this(setResult, setColor, x, y, PANEL_W, PANEL_H);
    }

    public CustomMainMenuPanelButton(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y, float w, float h) {
        super(setResult, setColor, x, y);
    }

    @SpireOverride
    public void buttonEffect() {
        MainMenuPanelButton.PanelClickResult result = ReflectionHacks.getPrivate(this, MainMenuPanelButton.class, "result");
        if (result == PCLEnum.Panels.CUSTOM_CARDS) {
            PGR.customCards.open(null, PCLCustomCardSelectorScreen.currentColor, () -> {
            });
        }
        else if (result == PCLEnum.Panels.CUSTOM_RELICS) {
            PGR.customRelics.open(null, PCLCustomRelicSelectorScreen.currentColor, () -> {
            });
        }
        else if (result == PCLEnum.Panels.CUSTOM_POTIONS) {
            PGR.customPotions.open(null, PCLCustomPotionSelectorScreen.currentColor, () -> {
            });
        }
    }

    @SpireOverride
    public void setLabel() {
        MainMenuPanelButton.PanelClickResult result = ReflectionHacks.getPrivate(this, MainMenuPanelButton.class, "result");
        if (result == PCLEnum.Panels.CUSTOM_CARDS) {
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_BEIGE);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", PGR.core.strings.menu_card);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", PGR.core.strings.menu_cardDesc);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", PCLCoreImages.Menu.menuCard.texture());
        }
        else if (result == PCLEnum.Panels.CUSTOM_RELICS) {
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_BLUE);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", PGR.core.strings.menu_relic);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", PGR.core.strings.menu_relicDesc);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", PCLCoreImages.Menu.menuRelic.texture());
        }
        else {
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_RED);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", PGR.core.strings.menu_potion);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", PGR.core.strings.menu_potionDesc);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", PCLCoreImages.Menu.menuPotion.texture());
        }
    }
}
