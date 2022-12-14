package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.controls.EUIDialogYesNo;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

public class PCLCustomCardDeletionConfirmationEffect extends PCLEffectWithCallback<PCLCustomCardSlot>
{

    protected EUIDialogYesNo dialog = new EUIDialogYesNo(PGR.core.strings.cardEditor.confirmDeletion, PGR.core.strings.cardEditor.confirmDeletionDesc);

    public PCLCustomCardDeletionConfirmationEffect(PCLCustomCardSlot slot)
    {
        this.dialog.setOnComplete((val) -> {
            complete(val ? slot : null);
        });
    }

    @Override
    public void render(SpriteBatch sb)
    {
        dialog.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        dialog.tryUpdate();
    }
}
