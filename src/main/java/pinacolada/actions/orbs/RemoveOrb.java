package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLAction;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class RemoveOrb extends PCLAction<ArrayList<AbstractOrb>> {
    protected final ArrayList<AbstractOrb> orbs = new ArrayList<>();
    protected FuncT1<Boolean, AbstractOrb> filter;
    protected AbstractOrb orb;
    protected boolean isRandom;

    public RemoveOrb(int limit) {
        this(limit, false);
    }

    public RemoveOrb(int limit, boolean isRandom) {
        super(ActionType.WAIT);

        this.isRandom = isRandom;
        this.orb = null;

        initialize(limit);
    }

    public RemoveOrb(AbstractOrb orb) {
        super(ActionType.WAIT);

        this.orb = orb;

        initialize(1);
    }

    protected boolean checkOrb(AbstractOrb orb) {
        return GameUtilities.isValidOrb(orb) && (filter == null || filter.invoke(orb));
    }

    @Override
    protected void firstUpdate() {
        if (player.orbs == null || player.orbs.isEmpty()) {
            complete(orbs);
            return;
        }

        if (orb != null) {
            removeOrb(orb);
        }
        else if (isRandom) {
            final RandomizedList<AbstractOrb> randomOrbs = new RandomizedList<>();
            for (AbstractOrb temp : player.orbs) {
                if (checkOrb(temp)) {
                    randomOrbs.add(temp);
                }
            }

            for (int i = 0; i < amount; i++) {
                removeOrb(randomOrbs.retrieve(PGR.dungeon.getRNG(), false));
            }
        }
        else {
            int i = 0;
            int orbs = 0;
            while (orbs < amount && i < player.orbs.size()) {
                final AbstractOrb orb = player.orbs.get(i++);
                if (checkOrb(orb)) {
                    removeOrb(orb);
                    orbs += 1;
                }
            }
        }

        complete(orbs);
    }

    protected void removeOrb(AbstractOrb orb) {
        if (player.orbs.remove(orb)) {
            player.orbs.add(0, orb);
            player.removeNextOrb();
            orbs.add(orb);
        }
    }

    public RemoveOrb setFilter(FuncT1<Boolean, AbstractOrb> filter) {
        this.filter = filter;

        return this;
    }
}
