package pinacolada.ui.common;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUITutorial;

public class PCLFtueScreen extends AbstractScreen
{
    public final MenuCancelButton button;
    protected EUITutorial current;

    public PCLFtueScreen()
    {
        super();
        button = new MenuCancelButton();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        if (current != null)
        {
            current.updateImpl();
        }

        button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape)
        {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            this.button.hide();
            CardCrawlGame.mainMenuScreen.panelScreen.refresh();
            dispose();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        if (current != null)
        {
            current.renderImpl(sb);
        }
        button.render(sb);
    }

    public void open(EUITutorial ftue)
    {
        super.open(false, true);
        current = ftue;
        this.button.show(CardLibraryScreen.TEXT[0]);
    }
}
