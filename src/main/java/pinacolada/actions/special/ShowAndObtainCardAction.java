package pinacolada.actions.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.PCLAction;
import pinacolada.effects.PCLEffects;

public class ShowAndObtainCardAction extends PCLAction<AbstractCard> {
    protected final float x;
    protected final float y;

    public ShowAndObtainCardAction(AbstractCard card, float x, float y) {
        super(ActionType.SPECIAL);

        this.card = card;
        this.x = x;
        this.y = y;
        this.isRealtime = true;
        this.canCancel = false;

        initialize(1);
    }

    @Override
    protected void firstUpdate() {
        if (card == null) {
            complete(null);
        }
        PCLEffects.Queue.showAndObtain(card, x, y, true);
        complete(card);
    }
}
