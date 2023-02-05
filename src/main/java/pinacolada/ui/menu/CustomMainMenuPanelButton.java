package pinacolada.ui.menu;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardSelectorScreen;

public class CustomMainMenuPanelButton extends MainMenuPanelButton
{
    public static final float START_Y = Settings.HEIGHT / 2.0F;
    public static final int PANEL_H = 800;
    public static final int PANEL_W = 512;
    protected static final int P_H = 206;
    protected static final int P_W = 317;

    public CustomMainMenuPanelButton(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y)
    {
        this(setResult, setColor, x, y, PANEL_W, PANEL_H);
    }

    public CustomMainMenuPanelButton(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y, float w, float h)
    {
        super(setResult, setColor, x, y);
    }

    @SpireOverride
    public void setLabel() {
        MainMenuPanelButton.PanelClickResult result = ReflectionHacks.getPrivate(this, MainMenuPanelButton.class, "result");
        if (result == PCLEnum.Panels.CUSTOM_CARDS)
        {
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_BEIGE);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", PGR.core.strings.menu_card);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", PGR.core.strings.menu_cardDesc);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", ImageMaster.P_INFO_CARD);
        }
        else
        {
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_GRAY);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", PGR.core.strings.menu_comingsoon);
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", "");
            ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", ImageMaster.P_LOCK);
        }
    }

    @SpireOverride
    public void buttonEffect() {
        MainMenuPanelButton.PanelClickResult result = ReflectionHacks.getPrivate(this, MainMenuPanelButton.class, "result");
        if (result == PCLEnum.Panels.CUSTOM_CARDS)
        {
            PGR.core.customCards.open(null, PCLCustomCardSelectorScreen.currentColor, () -> {});
        }
    }
}
