package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLAction;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

public abstract class GenericOrbAction extends PCLAction<ArrayList<AbstractOrb>> {
    protected final ArrayList<AbstractOrb> orbs = new ArrayList<>();
    protected FuncT1<Boolean, AbstractOrb> filter;
    protected boolean isRandom;
    protected AbstractOrb orb;
    protected int limit = 1;

    public GenericOrbAction(int times) {
        this(times, 1, false, null);
    }

    public GenericOrbAction(int times, int limit, boolean random, AbstractOrb orb) {
        super(ActionType.WAIT);

        this.isRandom = random;
        this.limit = limit;
        this.orb = orb;

        initialize(times);
    }

    public GenericOrbAction(AbstractOrb orb, int times) {
        this(times, 1, false, orb);
    }

    public GenericOrbAction(int times, int limit) {
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
            doEffect(orb, amount);
        }
        else if (isRandom) {
            final RandomizedList<AbstractOrb> randomOrbs = new RandomizedList<>();
            for (AbstractOrb temp : player.orbs) {
                if (checkOrb(temp)) {
                    randomOrbs.add(temp);
                }
            }

            for (int i = 0; i < limit; i++) {
                doEffect(randomOrbs.retrieve(PGR.dungeon.getRNG(), false), amount);
            }
        }
        else {
            int i = 0;
            int orbs = 0;
            while (orbs < limit && i < player.orbs.size()) {
                final AbstractOrb orb = player.orbs.get(i++);
                if (checkOrb(orb)) {
                    doEffect(orb, amount);
                    orbs += 1;
                }
            }
        }


        complete(orbs);
    }

    public GenericOrbAction setFilter(FuncT1<Boolean, AbstractOrb> filter) {
        this.filter = filter;

        return this;
    }

    public GenericOrbAction setOrb(AbstractOrb orb) {
        this.orb = orb;

        return this;
    }

    public GenericOrbAction setRandom(boolean random) {
        this.isRandom = random;

        return this;
    }

    protected void doEffect(AbstractOrb orb, int am) {
        if (checkOrb(orb)) {
            doEffectImpl(orb, am);
            orbs.add(orb);
        }
    }

    protected abstract void doEffectImpl(AbstractOrb orb, int am);
}
