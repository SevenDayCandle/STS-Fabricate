package pinacolada.actions.cards;

import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import pinacolada.actions.utility.NestedAction;

// Copied and modified from STS-AnimatorMod
public class ReshuffleDiscardPile extends NestedAction<CardGroup> {
    protected boolean onlyIfEmpty;

    public ReshuffleDiscardPile(boolean onlyIfEmpty) {
        super(ActionType.WAIT);

        this.onlyIfEmpty = onlyIfEmpty;

        initialize(1);
    }

    @Override
    protected void firstUpdate() {
        if (onlyIfEmpty && !player.drawPile.isEmpty()) {
            complete(null);
            return;
        }
        action = new EmptyDeckShuffleAction();
    }

    @Override
    protected void onNestCompleted() {
        complete(player.drawPile);
    }
}
