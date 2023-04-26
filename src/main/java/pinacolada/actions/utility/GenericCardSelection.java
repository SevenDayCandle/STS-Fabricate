package pinacolada.actions.utility;

import com.megacrit.cardcrawl.cards.AbstractCard;

// Copied and modified from STS-AnimatorMod
public class GenericCardSelection extends CardFilterAction {
    public GenericCardSelection(AbstractCard card) {
        this(card, 1);
    }

    protected GenericCardSelection(AbstractCard card, int amount) {
        super(ActionType.CARD_MANIPULATION);

        this.card = card;

        initialize(amount);
    }

    @Override
    protected void firstUpdate() {
        if (card != null) {
            if (!canPlayerCancel || canSelect(card)) {
                selectCard(card);
            }
            complete(selectedCards);
        }
    }


    protected void selectCard(AbstractCard card) {
        selectedCards.add(card);
    }
}