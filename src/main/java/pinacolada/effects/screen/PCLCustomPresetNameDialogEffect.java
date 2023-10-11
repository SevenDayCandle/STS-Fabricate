package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.ui.editor.PCLCustomFlagDialog;
import pinacolada.ui.editor.card.PCLCustomPresetNameDialog;

public class PCLCustomPresetNameDialogEffect extends PCLEffectWithCallback<String> {

    protected PCLCustomPresetNameDialog dialog;

    public PCLCustomPresetNameDialogEffect(String title, String existingName) {
        dialog = new PCLCustomPresetNameDialog(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(existingName);
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
