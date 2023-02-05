package pinacolada.ui.menu;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;

public class CustomMenuButton extends MenuButton
{
    public CustomMenuButton(ClickResult r, int index)
    {
        super(r, index);
    }

    @SpireOverride
    protected void setLabel() {
        if (result == PCLEnum.Buttons.CUSTOM)
        {
            ReflectionHacks.setPrivate(this, MenuButton.class, "label", PGR.core.strings.cedit_customCards);
        }
        else
        {
            SpireSuper.call();
        }
    }

    public void buttonEffect() {
        if (result == PCLEnum.Buttons.CUSTOM)
        {
            CardCrawlGame.mainMenuScreen.panelScreen.open(PCLEnum.Menus.CUSTOM);
        }
    }
}
