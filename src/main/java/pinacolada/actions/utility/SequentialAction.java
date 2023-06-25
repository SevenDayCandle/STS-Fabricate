package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public class SequentialAction extends PCLAction<Void> {
    private final LinkedList<AbstractGameAction> actions = new LinkedList<>();
    private AbstractGameAction cur;

    public SequentialAction(AbstractGameAction... action) {
        super(action[0].actionType);

        actions.addAll(Arrays.asList(action));
        cur = actions.pop();
        initialize(cur.source, cur.target, cur.amount, "");
    }

    public SequentialAction(List<? extends AbstractGameAction> action) {
        super(action.get(0).actionType);

        actions.addAll(action);
        cur = actions.pop();
        initialize(cur.source, cur.target, cur.amount, "");
    }

    private boolean updateAction() {
        if (cur == null || cur.isDone) {
            cur = actions.poll();
        }

        if (cur != null) {
            cur.update();
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void updateInternal(float deltaTime) {
        if (updateAction()) {
            complete(null);
        }
    }
}
