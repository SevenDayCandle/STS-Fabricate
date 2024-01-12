package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.ui.editor.PCLCustomDescriptionDialog;

public class PCLCustomDescriptionEditEffect extends PCLEffectWithCallback<PCLCustomDescriptionDialog> {

    protected PCLCustomDescriptionDialog dialog;

    public PCLCustomDescriptionEditEffect(String title, EditorMaker<?,?> data, int index) {
        dialog = new PCLCustomDescriptionDialog(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(data, index);
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
