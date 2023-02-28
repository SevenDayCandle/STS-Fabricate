package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

// Copied and modified from STS-AnimatorMod
public class ShuffleOrbs extends PCLAction<Void>
{
    public ShuffleOrbs(int times)
    {
        super(ActionType.WAIT);

        initialize(times);
    }

    @Override
    protected void firstUpdate()
    {
        if (player.orbs == null || player.orbs.size() < 2)
        {
            complete();
            return;
        }

        RandomizedList<AbstractOrb> randomOrbs = new RandomizedList<>();
        for (int i = 0; i < player.orbs.size(); i++)
        {
            if (GameUtilities.isValidOrb(player.orbs.get(i)))
            {
                randomOrbs.add(player.orbs.get(i));
            }
        }

        while (randomOrbs.size() >= 2)
        {
            AbstractOrb orb1 = randomOrbs.retrieve(PCLCard.rng);
            AbstractOrb orb2 = randomOrbs.retrieve(PCLCard.rng);

            int index1 = player.orbs.indexOf(orb1);
            int index2 = player.orbs.indexOf(orb2);

            player.orbs.set(index1, orb2);
            player.orbs.set(index2, orb1);

            orb1.setSlot(index2, player.maxOrbs);
            orb2.setSlot(index1, player.maxOrbs);
        }

        if (amount > 1)
        {
            PCLActions.bottom.add(new ShuffleOrbs(amount - 1));
        }

        complete();
    }
}
