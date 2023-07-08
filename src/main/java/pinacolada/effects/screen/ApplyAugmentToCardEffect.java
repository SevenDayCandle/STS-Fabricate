package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;
import pinacolada.ui.cardView.PCLAugmentList;

import java.util.Map;

public class ApplyAugmentToCardEffect extends PCLEffectWithCallback<PCLAugment> {
    protected PCLAugmentList panel;

    public ApplyAugmentToCardEffect(PCLCard card) {
        panel = new PCLAugmentList(this::complete);
        for (Map.Entry<String, Integer> params : PGR.dungeon.augments.entrySet()) {
            PCLAugmentData data = PCLAugmentData.get(params.getKey());
            int amount = params.getValue();
            if (data != null && amount > 0 && data.canApply(card)) {
                PCLAugment augment = data.create();
                panel.addPanelItem(augment, amount);
            }
        }
        if (panel.augments.size() == 0) {
            complete();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        panel.renderImpl(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        panel.updateImpl();
    }

}
