package pinacolada.ui.menu;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardSelectorScreen;

public class PCLCustomMenuButton extends MenuButton
{
    public PCLCustomMenuButton(ClickResult r, int index)
    {
        super(r, index);
    }

    @SpireOverride
    protected void setLabel() {
        if (result == PCLEnum.Buttons.CUSTOM)
        {
            ReflectionHacks.setPrivate(this, MenuButton.class, "label", PGR.core.strings.cardEditor.customCards);
        }
        else
        {
            SpireSuper.call();
        }
    }

    public void buttonEffect() {
        if (result == PCLEnum.Buttons.CUSTOM)
        {
            PGR.core.customCards.open(null, PCLCustomCardSelectorScreen.currentColor, () -> {});
        }
    }
}
