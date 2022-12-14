package pinacolada.actions.basic;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLActionWithCallback;

public class LoseBlock extends PCLActionWithCallback<AbstractCreature>
{
    protected boolean canLoseLess;
    protected boolean skipAnimation;

    public LoseBlock(AbstractCreature target, AbstractCreature source, int amount)
    {
        super(ActionType.BLOCK, Settings.ACTION_DUR_XFAST);

        initialize(source, target, amount);

        if (amount <= 0)
        {
            complete();
        }
    }

    public LoseBlock canLoseLess(boolean canLoseLess)
    {
        this.canLoseLess = canLoseLess;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (!target.isDying && !target.isDead && amount > 0)
        {
            if (target.currentBlock < amount && canLoseLess)
            {
                complete();
                return;
            }

            target.loseBlock(amount, skipAnimation);
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

    public LoseBlock skipAnimation(boolean skipAnimation)
    {
        this.skipAnimation = skipAnimation;

        return this;
    }
}
