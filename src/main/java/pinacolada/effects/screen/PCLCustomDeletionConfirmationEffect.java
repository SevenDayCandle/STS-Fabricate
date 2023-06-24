package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.controls.EUIDialogYesNo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.resources.PGR;

public class PCLCustomDeletionConfirmationEffect<T extends PCLCustomEditorLoadable<?, ?>> extends PCLEffectWithCallback<T> {

    protected EUIDialogYesNo dialog = new EUIDialogYesNo(PGR.core.strings.cedit_confirmDeletion, PGR.core.strings.cedit_confirmDeletionDesc);

    public PCLCustomDeletionConfirmationEffect(T slot) {
        this.dialog.setOnComplete((val) -> {
            complete(val ? slot : null);
        });
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
