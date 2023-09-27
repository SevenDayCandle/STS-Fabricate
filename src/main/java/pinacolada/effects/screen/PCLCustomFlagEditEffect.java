package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.ui.editor.PCLCustomFlagDialog;
import pinacolada.ui.editor.PCLCustomLoadoutDialog;

public class PCLCustomFlagEditEffect extends PCLEffectWithCallback<PCLCustomFlagDialog> {

    protected PCLCustomFlagDialog dialog;

    public PCLCustomFlagEditEffect(String title, PCLCustomFlagInfo info) {
        dialog = new PCLCustomFlagDialog(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(info);
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
