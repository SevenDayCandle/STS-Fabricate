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
        for (PCLAugment.SaveData save : PGR.dungeon.augments) {
            PCLAugment augment = save.create();
            if (augment != null) {
                panel.addPanelItem(augment);
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
