package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.controls.EUIDialogYesNo;
import pinacolada.effects.PCLEffectWithCallback;

public class PCLYesNoConfirmationEffect extends PCLEffectWithCallback<Boolean> {

    protected EUIDialogYesNo dialog;

    public PCLYesNoConfirmationEffect(String name, String desc) {
        this.dialog = new EUIDialogYesNo(name, desc);
        this.dialog.setOnComplete(this::complete);
    }

    @Override
    public void render(SpriteBatch sb) {
        dialog.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        dialog.tryUpdate();
    }
}
