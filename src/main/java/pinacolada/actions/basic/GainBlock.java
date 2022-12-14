package pinacolada.actions.basic;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.AttackEffects;
import pinacolada.utilities.GameEffects;

public class GainBlock extends PCLActionWithCallback<AbstractCreature>
{
    protected float pitchMin;
    protected float pitchMax;

    public GainBlock(AbstractCreature target, AbstractCreature source, int amount)
    {
        this(target, source, amount, false);
    }

    public GainBlock(AbstractCreature target, AbstractCreature source, int amount, boolean superFast)
    {
        super(ActionType.BLOCK, superFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.attackEffect = AttackEffects.SHIELD;
        this.pitchMin = 0.95f;
        this.pitchMax = 1.05f;

        initialize(source, target, amount);

        if (amount <= 0)
        {
            complete();
        }
    }

    @Override
    protected void firstUpdate()
    {
        if (!target.isDying && !target.isDead && amount > 0)
        {
            GameEffects.List.attack(source, target, attackEffect, 0.95f, 1.05f, null);

            target.addBlock(amount);
            player.hand.applyPowers();
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(target);
        }
    }

    public GainBlock setVFX(boolean mute, boolean superFast)
    {
        this.startDuration = this.duration = superFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST;

        if (mute)
        {
            this.pitchMin = this.pitchMax = 0;
        }

        return this;
    }
}
