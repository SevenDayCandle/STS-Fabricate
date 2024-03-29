package pinacolada.actions.piles;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.utility.CardFilterAction;

public class DrawCards extends CardFilterAction {
    protected boolean canDrawUnfiltered;
    protected boolean shuffleIfEmpty = true;

    protected DrawCards(DrawCards other, int amount) {
        this(amount);

        shuffleIfEmpty = other.shuffleIfEmpty;
        filter = other.filter;
        canDrawUnfiltered = other.canDrawUnfiltered;
        callbacks.addAll(other.callbacks);
        selectedCards.addAll(other.selectedCards);
    }

    public DrawCards(int amount) {
        super(ActionType.DRAW);

        initialize(amount);
    }

    public DrawCards(String name, int amount) {
        super(ActionType.DRAW);

        initialize(amount, name);
    }

    @Override
    protected void firstUpdate() {
        if (player.hasPower(NoDrawPower.POWER_ID)) {
            player.getPower(NoDrawPower.POWER_ID).flash();
            complete(selectedCards);
            return;
        }

        if (amount <= 0) {
            complete(selectedCards);
            return;
        }

        if (player.drawPile.isEmpty()) {
            if (shuffleIfEmpty && !player.discardPile.isEmpty()) {
                PCLActions.top.sequential(
                        new EmptyDeckShuffleAction(),
                        new DrawCards(this, amount)
                );

                completeImpl(); // Do not trigger callback
            }
            else {
                complete(selectedCards);
            }
        }
        else if (player.hand.size() >= BaseMod.MAX_HAND_SIZE) {
            player.createHandIsFullDialog();
            complete(selectedCards);
        }
        else {
            if (filter != null) {
                AbstractCard filtered = null;
                // Iterate from top to bottom
                for (int i = player.drawPile.group.size() - 1; i >= 0; i--) {
                    AbstractCard card = player.drawPile.group.get(i);
                    if (filter.invoke(card)) {
                        filtered = card;
                        break;
                    }
                }

                if (filtered != null) {
                    player.drawPile.removeCard(filtered);
                    player.drawPile.addToTop(filtered);
                }
                else if (!canDrawUnfiltered) {
                    complete(selectedCards);
                    return;
                }
            }

            selectedCards.add(player.drawPile.getTopCard());

            PCLActions.top.sequential(
                    new DrawCardAction(source, 1, false),
                    new DrawCards(this, amount - 1)
            );

            completeImpl(); // Do not trigger callback
        }
    }

    public DrawCards setFilter(FuncT1<Boolean, AbstractCard> filter, boolean canDrawUnfiltered) {
        setFilter(filter);
        this.canDrawUnfiltered = canDrawUnfiltered;

        return this;
    }

    public DrawCards shuffleIfEmpty(boolean shuffleIfEmpty) {
        this.shuffleIfEmpty = shuffleIfEmpty;

        return this;
    }
}
