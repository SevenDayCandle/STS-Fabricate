package pinacolada.actions.special;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.misc.CombatStats;
import pinacolada.utilities.GameActions;

public class PCLHasteAction extends PCLAction
{
    public PCLHasteAction(AbstractCard card)
    {
        super(ActionType.SPECIAL);

        this.isRealtime = true;
        this.card = card;

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        if (PCLCardTag.Haste.has(card) && !CombatStats.hasteInfinitesThisTurn().contains(card))
        {
            GameActions.top.draw(1);
            GameActions.top.flash(card);
        }
        else
        {
            isDone = true;
        }
    }

    @Override
    protected void complete()
    {
        if (PCLCardTag.Haste.has(card) && !CombatStats.hasteInfinitesThisTurn().contains(card))
        {
            CardCrawlGame.sound.playA("POWER_FLIGHT", MathUtils.random(0.3f, 0.4f));
            PCLCardTag.Haste.tryProgress(card);
            CombatStats.hasteInfinitesThisTurn().add(card);
        }

        isDone = true;
    }
}
