package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIDialogDropdown;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

import java.util.List;

public class PCLCustomCardCopyConfirmationEffect extends PCLEffectWithCallback<AbstractCard.CardColor>
{

    protected EUIDialogDropdown<AbstractCard.CardColor> dialog = new EUIDialogDropdown<AbstractCard.CardColor>(PGR.core.strings.cardEditor.duplicateToColor, PGR.core.strings.cardEditor.duplicateToColorDesc);

    public PCLCustomCardCopyConfirmationEffect(List<AbstractCard.CardColor> colors)
    {
        this.dialog
                .setItems(colors)
                .setLabelFunctionForOption(e -> EUIUtils.capitalize(e.name().replace("_", " ")), false)
                .setOnComplete((val) -> {
                    complete(val != null && val.size() > 0 ? val.get(0) : null);
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
