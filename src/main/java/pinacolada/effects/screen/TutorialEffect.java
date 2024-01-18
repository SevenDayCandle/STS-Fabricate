package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.ui.controls.EUITutorial;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.ui.editor.PCLCustomFlagDialog;

public class TutorialEffect extends PCLEffectWithCallback<EUITutorial> {

    protected EUITutorial tutorial;

    public TutorialEffect(EUITutorial tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public void complete(EUITutorial result) {
        super.complete(result);
        tutorial.close();
    }

    @Override
    public void render(SpriteBatch sb) {
        tutorial.renderImpl(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (InputHelper.justClickedLeft && !tutorial.isHovered() && !CardCrawlGame.isPopupOpen) {
            complete(tutorial);
        }
        tutorial.updateImpl();
    }
}
