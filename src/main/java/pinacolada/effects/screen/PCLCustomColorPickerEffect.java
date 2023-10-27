package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIColorPicker;
import extendedui.ui.controls.EUIDialogColorPicker;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.effects.PCLEffectWithCallback;

public class PCLCustomColorPickerEffect extends PCLEffectWithCallback<EUIColorPicker> {

    protected EUIDialogColorPicker dialog;

    public PCLCustomColorPickerEffect(String title, Color initial) {
        this(new EUIDialogColorPicker(title), initial);
    }

    public PCLCustomColorPickerEffect(EUIHitbox hb, String title, Color initial) {
        this(new EUIDialogColorPicker(hb, title, EUIUtils.EMPTY_STRING), initial);
    }

    protected PCLCustomColorPickerEffect(EUIDialogColorPicker picker, Color initial) {
        dialog = picker;
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(initial);
    }

    @Override
    public void render(SpriteBatch sb) {
        dialog.tryRender(sb);
    }

    public PCLCustomColorPickerEffect setOnUpdate(ActionT1<EUIColorPicker> onUpdate) {
        this.dialog.setOnChange(onUpdate);
        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        dialog.tryUpdate();
    }
}
