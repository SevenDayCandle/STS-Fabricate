package pinacolada.ui.menu;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;

public class PCLCustomMenuPanel extends MainMenuPanelButton
{
    public static final float START_Y = Settings.HEIGHT / 2.0F;
    public static final int PANEL_H = 800;
    public static final int PANEL_W = 512;
    protected static final int P_H = 206;
    protected static final int P_W = 317;

    public PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y)
    {
        this(setResult, setColor, x, y, PANEL_W, PANEL_H);
    }

    public PCLCustomMenuPanel(MainMenuPanelButton.PanelClickResult setResult, MainMenuPanelButton.PanelColor setColor, float x, float y, float w, float h)
    {
        super(setResult, setColor, x, y);
    }
}
