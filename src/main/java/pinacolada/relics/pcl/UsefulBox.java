package pinacolada.relics.pcl;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleRelic;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnAllySummonSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;

@VisibleRelic
public class UsefulBox extends PCLRelic implements OnAllySummonSubscriber {
    public static final PCLRelicData DATA = register(UsefulBox.class).setTier(RelicTier.SPECIAL, LandingSound.SOLID);

    public UsefulBox() {
        super(DATA);
    }

    @Override
    public void atBattleStart() {
        CombatManager.subscribe(OnAllySummonSubscriber.class, this);
        setCounter(1);
    }

    public void atTurnStart() {
        setCounter(1);
    }

    public int getValue() {
        return 2;
    }

    @Override
    public void onAllySummon(PCLCard card, PCLCardAlly ally) {
        if (counter > 0) {
            PCLActions.delayed.gainBlock(getValue());
            PCLActions.delayed.gainBlock(ally, getValue());
            addCounter(-1);
            flash();
        }
    }
}