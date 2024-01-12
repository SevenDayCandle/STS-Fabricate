package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.characters.PCLCharacterAnimation;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.ui.characterSelection.PCLSkinDialog;

import java.util.Collection;

public class PCLSkinConfirmationEffect extends PCLEffectWithCallback<String> {

    protected PCLSkinDialog dialog;

    public PCLSkinConfirmationEffect(String name) {
        this(name, PCLCharacterAnimation.getAll());
    }

    public PCLSkinConfirmationEffect(String name, Collection<String> available) {
        this.dialog = new PCLSkinDialog(name, available);
        this.dialog.setOnComplete(this::complete);
    }

    @Override
    public void render(SpriteBatch sb) {
        dialog.tryRender(sb);
    }

    public void setPage(String page) {
        dialog.setPage(page);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        dialog.tryUpdate();
    }
}
