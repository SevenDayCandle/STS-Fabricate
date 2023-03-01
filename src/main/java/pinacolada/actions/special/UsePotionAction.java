package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameUtilities;

public class UsePotionAction extends PCLAction<AbstractPotion>
{
    protected AbstractPotion potion;
    protected AbstractCreature target;
    protected boolean shouldRemove = true;

    public UsePotionAction(AbstractPotion potion, AbstractCreature target)
    {
        this(potion, target, 1);
    }

    public UsePotionAction(AbstractPotion potion, AbstractCreature target, int amount)
    {
        super(ActionType.SPECIAL);
        this.target = target;
        this.potion = potion;
        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (potion != null && potion.canUse() && (!potion.targetRequired || !GameUtilities.isDeadOrEscaped(target)))
        {
            for (int i = 0; i < amount; i++)
            {
                potion.use(target);
            }

            if (shouldRemove)
            {
                int index = player.potions != null ? player.potions.indexOf(potion) : -1;
                if (index >= 0)
                {
                    player.potions.set(index, new PotionSlot(index));
                    player.adjustPotionPositions();
                }
            }

            complete(potion);
        }
        else
        {
            complete();
        }

    }

    public UsePotionAction setShouldRemove(boolean shouldRemove)
    {
        this.shouldRemove = shouldRemove;

        return this;
    }
}
