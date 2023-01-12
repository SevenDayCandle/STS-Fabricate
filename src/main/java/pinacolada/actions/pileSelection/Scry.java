package pinacolada.actions.pileSelection;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.ui.GridCardSelectScreenHelper;
import pinacolada.actions.CardFilterAction;
import pinacolada.resources.PGR;

// TODO extend DiscardFromPile
public class Scry extends CardFilterAction
{
    public Scry(int amount)
    {
        super(ActionType.CARD_MANIPULATION, Settings.ACTION_DUR_FAST);

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        GridCardSelectScreenHelper.clear(true);
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead())
        {
            complete();
            return;
        }

        for (AbstractPower p : player.powers)
        {
            p.onScry();
        }

        if (player.drawPile.isEmpty())
        {
            complete();
            return;
        }

        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (amount != -1)
        {
            for (int i = 0; i < Math.min(amount, player.drawPile.size()); ++i)
            {
                group.addToTop(player.drawPile.group.get(player.drawPile.size() - i - 1));
            }
        }
        else
        {
            for (AbstractCard c : player.drawPile.group)
            {
                group.addToBottom(c);
            }
        }

        AbstractDungeon.gridSelectScreen.open(group, amount, true, PGR.core.strings.gridSelection.scry);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty())
        {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards)
            {
                c.triggerOnManualDiscard();
                player.drawPile.moveToDiscardPile(c);
                selectedCards.add(c);
            }

            GameActionManager.incrementDiscard(false);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

        for (AbstractCard c : player.discardPile.group)
        {
            c.triggerOnScry();
        }

        if (tickDuration(deltaTime))
        {
            complete(selectedCards);
        }
    }
}

