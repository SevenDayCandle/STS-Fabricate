package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.relics.PCLStarterRelic;
import pinacolada.resources.PGR;
import pinacolada.ui.cardView.PCLAugmentList;

import java.util.Map;

public class PCLAugmentSelectionEffect extends PCLEffectWithCallback<PCLAugment>
{

    protected PCLAugmentList panel;

    public PCLAugmentSelectionEffect(PCLStarterRelic relic)
    {
        this(augment -> relic == null || augment.data.affinity == PCLAffinity.Blue);
    }

    public PCLAugmentSelectionEffect(PCLCard card)
    {
        this(augment -> card == null || augment.canApply(card));
    }

    public PCLAugmentSelectionEffect(FuncT1<Boolean, PCLAugment> evalFunc)
    {
        panel = new PCLAugmentList(this::complete);
        for (Map.Entry<String, Integer> params : PGR.core.dungeon.augments.entrySet())
        {
            PCLAugmentData data = PCLAugment.get(params.getKey());
            int amount = params.getValue();
            if (data != null && amount > 0)
            {
                PCLAugment augment = data.create();
                panel.addPanelItem(augment, amount, evalFunc.invoke(augment));
            }
        }
    }

    @Override
    public void render(SpriteBatch sb)
    {
        panel.renderImpl(sb);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        panel.updateImpl();
    }

}
