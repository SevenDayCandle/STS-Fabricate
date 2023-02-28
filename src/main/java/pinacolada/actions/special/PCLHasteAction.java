package pinacolada.actions.special;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.CombatManager;

public class PCLHasteAction extends PCLAction<Void>
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
        if (PCLCardTag.Haste.has(card) && !CombatManager.hasteInfinitesThisTurn().contains(card))
        {
            PCLActions.top.draw(1);
            PCLActions.top.flash(card);
        }
        else
        {
            complete();
        }
    }

    @Override
    protected void complete()
    {
        super.complete();
        if (PCLCardTag.Haste.has(card) && !CombatManager.hasteInfinitesThisTurn().contains(card))
        {
            CardCrawlGame.sound.playA("POWER_FLIGHT", MathUtils.random(0.3f, 0.4f));
            PCLCardTag.Haste.tryProgress(card);
            CombatManager.hasteInfinitesThisTurn().add(card);
        }
    }
}
