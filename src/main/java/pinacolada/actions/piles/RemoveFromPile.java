package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;

public class RemoveFromPile extends SelectFromPile
{
    public RemoveFromPile(String sourceName, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, groups);
    }

    public RemoveFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, null, amount, origin, groups);
        forPurge = true;
    }

    public RemoveFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
        forPurge = true;
    }

    public RemoveFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
        forPurge = true;
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return GameUtilities.canRemoveFromDeck(card) && super.canSelect(card);
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result)
    {
        for (AbstractCard c : result)
        {
            AbstractCard masterCopy = GameUtilities.getMasterDeckInstance(c.uuid);
            if (masterCopy != null)
            {
                PCLEffects.TopLevelQueue.showCardBriefly(masterCopy);
                AbstractDungeon.player.masterDeck.removeCard(masterCopy);
            }
            moveToPile(GameUtilities.getAllInBattleInstances(c.uuid), CombatManager.PURGED_CARDS);
        }

        CombatManager.queueRefreshHandLayout();

        super.complete(result);
    }

    @Override
    public String getActionMessage()
    {
        return PGR.core.strings.grid_chooseCards(amount);
    }
}