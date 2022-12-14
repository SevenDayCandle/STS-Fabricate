package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;

public class SummonCardAction extends PCLActionWithCallback<PCLCard>
{
    public final PCLCard card;
    public final PCLCardAlly ally;
    public boolean retainPowers;
    public boolean stun = true;
    public boolean showEffect = true;

    public SummonCardAction(PCLCard card, PCLCardAlly slot)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, target, 1);
        this.card = card;
        this.ally = slot;
    }

    public SummonCardAction setOptions(boolean retainPowers, boolean stun, boolean showEffect)
    {
        this.retainPowers = retainPowers;
        this.stun = stun;
        this.showEffect = showEffect;
        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (this.card == null || this.ally == null)
        {
            complete();
            return;
        }

        PCLCard returnedCard = null;

        if (this.ally.hasCard())
        {
            returnedCard = this.ally.releaseCard();
            if (returnedCard != null)
            {
                GameActions.bottom.makeCard(this.card, AbstractDungeon.player.discardPile).setMakeCopy(true);
            }
        }

        this.ally.initializeForCard(card, retainPowers, stun);

        // TODO effects
        if (showEffect)
        {
            GameEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
        }

        complete(returnedCard);
    }
}
