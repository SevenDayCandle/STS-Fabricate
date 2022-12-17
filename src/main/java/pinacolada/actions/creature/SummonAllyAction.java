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
import pinacolada.resources.PCLEnum;

public class SummonAllyAction extends PCLActionWithCallback<PCLCard>
{
    public final PCLCard card;
    public final PCLCardAlly ally;
    public boolean retainPowers;
    public boolean stun = true;
    public boolean showEffect = true;
    public boolean summonCardOnly = true;

    public SummonAllyAction(PCLCard card, PCLCardAlly slot)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, 1);
        this.card = card;
        this.ally = slot;
    }

    public SummonAllyAction setOptions(boolean retainPowers, boolean stun, boolean showEffect, boolean summonCardOnly)
    {
        this.retainPowers = retainPowers;
        this.stun = stun;
        this.showEffect = showEffect;
        this.summonCardOnly = summonCardOnly;
        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if ((this.card == null || summonCardOnly && this.card.type != PCLEnum.CardType.SUMMON) || this.ally == null)
        {
            complete();
            return;
        }

        PCLCard returnedCard = this.ally.card;
        // If ally is withdrawed, setting up the new card must come after the previous card is withdrawn
        if (returnedCard != null)
        {
            PCLActions.top.withdrawAlly(ally).addCallback(this::initializeAlly);
        }
        else
        {
            initializeAlly();
        }

        CombatManager.onAllySummon(card, ally);
        complete(returnedCard);
    }

    protected void initializeAlly()
    {
        this.ally.initializeForCard(card, retainPowers, stun);

        // TODO effects
        if (showEffect)
        {
            PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
        }
    }
}
