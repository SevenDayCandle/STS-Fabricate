package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameUtilities;

public class UsePotionAction extends PCLActionWithCallback<AbstractPotion>
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
        if (potion.canUse() && target != null && !GameUtilities.isDeadOrEscaped(target))
        {
            for (int i = 0; i < amount; i++)
            {
                potion.use(target);
            }
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

        complete();
    }

    public UsePotionAction setShouldRemove(boolean shouldRemove)
    {
        this.shouldRemove = shouldRemove;

        return this;
    }
}
