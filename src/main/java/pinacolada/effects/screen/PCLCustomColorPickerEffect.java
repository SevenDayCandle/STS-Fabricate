package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.controls.EUIDialogColorPicker;
import pinacolada.effects.PCLEffectWithCallback;

public class PCLCustomColorPickerEffect extends PCLEffectWithCallback<Color> {

    protected EUIDialogColorPicker dialog;

    public PCLCustomColorPickerEffect(String title, Color initial) {
        dialog = new EUIDialogColorPicker(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(initial);
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
