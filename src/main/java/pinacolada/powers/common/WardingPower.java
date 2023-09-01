package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.powers.PCLPower;

public class WardingPower extends PCLPower {
    public static final String POWER_ID = createFullID(WardingPower.class);

    public WardingPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return super.modifyBlock(block, card) + amount;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        super.onUseCard(card, action);
        if (card.baseBlock > 0 || (card instanceof EditorCard && ((EditorCard) card).getCardBlock() != null)) {
            removePower();
        }
    }
}
