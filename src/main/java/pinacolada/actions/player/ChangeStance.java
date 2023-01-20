package pinacolada.actions.player;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.CannotChangeStancePower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.stances.PCLStance;
import pinacolada.stances.PCLStanceHelper;

// Copied and modified from STS-AnimatorMod
public class ChangeStance extends PCLActionWithCallback<AbstractStance>
{
    private final String ID;
    private PCLStanceHelper helper;
    private AbstractStance newStance;
    private AbstractStance previousStance;
    private boolean requireNeutralStance;
    private boolean triggerOnSameStance;

    public ChangeStance(String stanceId)
    {
        super(ActionType.SPECIAL);

        this.ID = stanceId;
    }

    public ChangeStance(PCLStanceHelper stanceHelper)
    {
        super(ActionType.SPECIAL);

        this.ID = stanceHelper.ID;
        this.helper = stanceHelper;
    }


    public ChangeStance(AbstractStance stance)
    {
        this(stance.ID);

        this.newStance = stance;
    }

    @Override
    protected void firstUpdate()
    {
        if (player.hasPower(CannotChangeStancePower.POWER_ID))
        {
            complete(null);
            return;
        }

        if (requireNeutralStance && !player.stance.ID.equals(NeutralStance.STANCE_ID))
        {
            complete(null);
            return;
        }

        previousStance = player.stance;
        if (previousStance.ID.equals(ID))
        {
            if (previousStance instanceof PCLStance)
            {
                ((PCLStance) previousStance).onRefreshStance();
            }

            complete(triggerOnSameStance ? previousStance : null);
            return;
        }

        if (newStance == null)
        {
            newStance = helper != null ? helper.create() : AbstractStance.getStanceFromName(ID);
        }

        for (AbstractPower p : player.powers)
        {
            p.onChangeStance(previousStance, newStance);
        }

        for (AbstractRelic r : player.relics)
        {
            r.onChangeStance(previousStance, newStance);
        }

        previousStance.onExitStance();
        player.stance = newStance;
        newStance.onEnterStance();

        AbstractDungeon.actionManager.uniqueStancesThisCombat.merge(newStance.ID, 1, Integer::sum);

        player.switchedStance();

        for (AbstractCard c : player.discardPile.group)
        {
            c.triggerExhaustedCardsOnStanceChange(newStance);
        }

        player.onStanceChange(this.ID);

        AbstractDungeon.onModifyPower();

        if (Settings.FAST_MODE)
        {
            complete(previousStance);
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(previousStance);
        }
    }

    public ChangeStance requireNeutralStance(boolean value)
    {
        this.requireNeutralStance = value;

        return this;
    }

    public ChangeStance triggerOnSameStance(boolean value)
    {
        this.triggerOnSameStance = value;

        return this;
    }
}
