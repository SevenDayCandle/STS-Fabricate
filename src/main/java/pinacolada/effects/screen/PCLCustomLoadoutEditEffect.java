package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.ui.customRun.PCLCustomLoadoutDialog;

public class PCLCustomLoadoutEditEffect extends PCLEffectWithCallback<PCLCustomLoadoutDialog> {

    protected PCLCustomLoadoutDialog dialog;

    public PCLCustomLoadoutEditEffect(String title, PCLCustomLoadout loadout) {
        dialog = new PCLCustomLoadoutDialog(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(loadout);
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
