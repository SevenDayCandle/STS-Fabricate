package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;

// Copied and modified from STS-AnimatorMod
public class SequentialAction extends PCLAction<Void> {
    private final AbstractGameAction action;
    private final AbstractGameAction action2;

    public SequentialAction(AbstractGameAction action, AbstractGameAction action2) {
        super(action.actionType);

        this.action = action;
        this.action2 = action2;

        initialize(action.source, action.target, action.amount, "");
    }

    @Override
    public void update() {
        if (updateAction()) {
            PCLActions.top.add(action2);

            this.isDone = true;
        }
    }

    private boolean updateAction() {
        if (!action.isDone) {
            action.update();
        }

        return action.isDone;
    }
}
