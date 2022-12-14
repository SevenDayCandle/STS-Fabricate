package pinacolada.actions.basic;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameUtilities;

public class RemoveBlock extends PCLAction
{
    protected boolean skipAnimation;
    protected boolean instant;

    public RemoveBlock(AbstractCreature target, AbstractCreature source)
    {
        this(target, source, -1);
    }

    public RemoveBlock(AbstractCreature target, AbstractCreature source, int amount)
    {
        super(ActionType.BLOCK, Settings.ACTION_DUR_FAST);

        initialize(source, target, amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (target != null && !GameUtilities.isDeadOrEscaped(target) && target.currentBlock > 0)
        {
            if (amount > 0)
            {
                target.loseBlock(amount, skipAnimation);
            }
            else
            {
                target.loseBlock(skipAnimation);
            }
        }

        if (instant)
        {
            complete();
        }
    }

    public RemoveBlock setVFX(boolean instant, boolean skipAnimation)
    {
        this.instant = instant;
        this.skipAnimation = skipAnimation;

        return this;
    }
}
