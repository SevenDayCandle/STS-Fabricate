package pinacolada.actions.piles;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;

import java.util.ArrayList;
import java.util.Arrays;

public class ScoutCards extends FetchFromPile {
    public boolean reshuffleInstantly = false;

    public ScoutCards(String sourceName, int amount) {
        super(sourceName, amount, PCLCardSelection.Top, AbstractDungeon.player.drawPile);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        if (result.size() > 0) {
            SelectFromPile action = new ReshuffleFromPile(name, result.size(), player.hand).setDestination(PCLCardSelection.Top);
            if (reshuffleInstantly) {
                AbstractGameAction firstAction = PCLCardSelection.getLast(AbstractDungeon.actionManager.actions, 0);
                if (firstAction instanceof ReshuffleFromPile && ((ReshuffleFromPile) firstAction).destination == PCLCardSelection.Top && Arrays.equals(action.groups, ((ReshuffleFromPile) firstAction).groups)) {
                    firstAction.amount += result.size();
                }
                else {
                    PCLActions.top.add(action);
                }
            }
            else {
                AbstractGameAction lastAction = PCLCardSelection.getLast(AbstractDungeon.actionManager.actions, 0);
                if (lastAction instanceof ReshuffleFromPile && ((ReshuffleFromPile) lastAction).destination == PCLCardSelection.Top && Arrays.equals(action.groups, ((ReshuffleFromPile) lastAction).groups)) {
                    lastAction.amount += result.size();
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
