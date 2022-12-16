package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;

public class WithdrawAllyAction extends PCLActionWithCallback<PCLCard>
{
    public final PCLCardAlly ally;
    public boolean trigger = true;
    public boolean showEffect = true;

    public WithdrawAllyAction(PCLCardAlly slot)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, 1);
        this.ally = slot;
    }

    public WithdrawAllyAction setOptions(boolean trigger, boolean showEffect)
    {
        this.trigger = trigger;
        this.showEffect = showEffect;
        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (this.ally == null)
        {
            complete();
            return;
        }

        PCLCard returnedCard = this.ally.card;

        if (returnedCard != null)
        {
            if (trigger)
            {
                PCLActions.top.triggerAlly(ally).addCallback(this::releaseCard);
            }
            else
            {
                releaseCard();
            }
        }

        complete(returnedCard);
    }

    protected void releaseCard()
    {
        PCLCard returnedCard = this.ally.releaseCard();
        if (returnedCard != null)
        {
            PCLActions.bottom.makeCard(this.card, AbstractDungeon.player.discardPile).setMakeCopy(true);
            returnedCard.triggerWhenWithdrawn(ally);
        }

        // TODO effects
        if (showEffect)
        {
            PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
        }

        CombatManager.onAllyWithdraw(returnedCard, ally);
    }
}
