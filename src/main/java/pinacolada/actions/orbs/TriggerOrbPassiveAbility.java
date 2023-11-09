package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLAction;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

public class TriggerOrbPassiveAbility extends PCLAction<ArrayList<AbstractOrb>> {
    protected final ArrayList<AbstractOrb> orbs = new ArrayList<>();
    protected FuncT1<Boolean, AbstractOrb> filter;
    protected boolean isRandom;
    protected AbstractOrb orb;
    protected int limit = 1;

    public TriggerOrbPassiveAbility(int times) {
        this(times, 1, false, null);
    }

    public TriggerOrbPassiveAbility(int times, int limit, boolean random, AbstractOrb orb) {
        super(ActionType.WAIT);

        this.isRandom = random;
        this.limit = limit;
        this.orb = orb;

        initialize(times);
    }

    public TriggerOrbPassiveAbility(AbstractOrb orb, int times) {
        this(times, 1, false, orb);
    }

    public TriggerOrbPassiveAbility(int times, int limit) {
        this(times, limit, false, null);
    }

    protected boolean checkOrb(AbstractOrb orb) {
        return GameUtilities.isValidOrb(orb) && (filter == null || filter.invoke(orb));
    }

    @Override
    protected void firstUpdate() {
        if (player.orbs == null || player.orbs.isEmpty()) {
            complete(new ArrayList<>());
            return;
        }

        if (orb != null) {
            triggerPassiveEffect(orb, amount);
        }
        else if (isRandom) {
            final RandomizedList<AbstractOrb> randomOrbs = new RandomizedList<>();
            for (AbstractOrb temp : player.orbs) {
                if (checkOrb(temp)) {
                    randomOrbs.add(temp);
                }
            }

            for (int i = 0; i < limit; i++) {
                triggerPassiveEffect(randomOrbs.retrieve(PGR.dungeon.getRNG(), false), amount);
            }
        }
        else {
            int i = 0;
            int orbs = 0;
            while (orbs < limit && i < player.orbs.size()) {
                final AbstractOrb orb = player.orbs.get(i++);
                if (checkOrb(orb)) {
                    triggerPassiveEffect(orb, amount);
                    orbs += 1;
                }
            }
        }


        complete(orbs);
    }

    public TriggerOrbPassiveAbility setFilter(FuncT1<Boolean, AbstractOrb> filter) {
        this.filter = filter;

        return this;
    }

    public TriggerOrbPassiveAbility setOrb(AbstractOrb orb) {
        this.orb = orb;

        return this;
    }

    public TriggerOrbPassiveAbility setRandom(boolean random) {
        this.isRandom = random;

        return this;
    }

    protected void triggerPassiveEffect(AbstractOrb orb, int times) {
        if (checkOrb(orb)) {
            for (int i = 0; i < times; i++) {
                orb.onStartOfTurn();
                orb.onEndOfTurn();
                if (orb instanceof OnStartOfTurnPostDrawSubscriber) {
                    ((OnStartOfTurnPostDrawSubscriber) orb).onStartOfTurnPostDraw();
                }
                orbs.add(orb);
            }
        }
    }
}
