package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ScryCards extends DiscardFromPile {
    public ScryCards(String sourceName, int amount) {
        super(sourceName, amount, AbstractDungeon.player.drawPile);
        setMaxChoices(amount, PCLCardSelection.Top);

        initialize(amount);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        CombatManager.onScryAction(this);
        for (AbstractPower p : player.powers) {
            p.onScry();
        }
        for (AbstractCard c : result) {
            c.triggerOnScry();
            CombatManager.onCardScry(c);
        }

        super.complete(result);
    }

    @Override
    public String updateMessage() {
        return super.updateMessageInternal(PGR.core.strings.grid_scry);
    }
}

