package pinacolada.actions.piles;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;

import java.util.ArrayList;

public class ScoutCards extends FetchFromPile {
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount) {
        super(sourceName, amount, PCLCardSelection.Top.toSelection(), AbstractDungeon.player.drawPile);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        if (result.size() > 0) {
            SelectFromPile action = new ReshuffleFromPile(name, result.size(), player.hand).setDestination(PCLCardSelection.Top.toSelection());
            if (reshuffleInstantly) {
                PCLActions.top.add(action);
            }
            else {
                int count = AbstractDungeon.actionManager.actions.size();
                AbstractGameAction lastAction = count > 0 ? AbstractDungeon.actionManager.actions.get(count - 1) : null;
                if (lastAction instanceof ReshuffleFromPile) {
                    ReshuffleFromPile shuffle = (ReshuffleFromPile) lastAction;
                    if (shuffle.groups.length == 1 && shuffle.groups[0] == player.hand && shuffle.destination == PCLCardSelection.Top.toSelection()) {
                        shuffle.amount += result.size();
                    }
                    else {
                        PCLActions.bottom.add(action);
                    }
                }
                else {
                    PCLActions.bottom.add(action);
                }
            }
        }

        super.complete(result);
    }

    public ScoutCards drawInstantly(boolean value) {
        reshuffleInstantly = value;

        return this;
    }
}
