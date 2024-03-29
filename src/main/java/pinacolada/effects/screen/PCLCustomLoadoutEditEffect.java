package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.loadout.PCLCustomLoadout;
import pinacolada.ui.editor.PCLCustomLoadoutDialog;

public class PCLCustomLoadoutEditEffect extends PCLEffectWithCallback<PCLCustomLoadoutDialog> {

    protected PCLCustomLoadoutDialog dialog;

    public PCLCustomLoadoutEditEffect(String title, PCLCustomLoadout loadout, AbstractCard.CardColor color) {
        dialog = new PCLCustomLoadoutDialog(title);
        this.dialog.setOnComplete(this::complete);
        this.dialog.open(loadout, color);
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
