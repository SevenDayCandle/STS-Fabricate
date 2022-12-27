package pinacolada.relics.pcl;

import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.subscribers.OnAllySummonSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;

public class UsefulBox extends AbstractBox implements OnAllySummonSubscriber
{
    public static final String ID = createFullID(UsefulBox.class);

    public UsefulBox()
    {
        super(ID, RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void atBattleStart()
    {
        CombatManager.onAllySummon.subscribe(this);
    }

    @Override
    public void onAllySummon(PCLCard card, PCLCardAlly ally)
    {
        PCLActions.delayed.gainBlock(ally, getValue());
    }

    public int getValue()
    {
        return 8;
    }
}