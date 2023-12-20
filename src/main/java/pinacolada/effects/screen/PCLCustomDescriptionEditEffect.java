package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.interfaces.markers.EditorMaker;
import pinacolada.ui.editor.PCLCustomDescriptionDialog;
import pinacolada.ui.editor.PCLCustomFlagDialog;

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
