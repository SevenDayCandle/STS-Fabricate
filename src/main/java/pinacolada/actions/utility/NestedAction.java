package pinacolada.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.actions.PCLAction;

// Copied and modified from STS-AnimatorMod
public abstract class NestedAction<T> extends PCLAction<T> {
    protected AbstractGameAction action;

    public NestedAction(AbstractGameAction.ActionType type) {
        super(type);
    }

    public NestedAction(AbstractGameAction.ActionType type, float duration) {
        super(type, duration);
    }

    protected boolean updateAction() {
        if (action == null) {
            return true;
        }

        if (!action.isDone) {
            action.update();
        }

        return action.isDone;
    }

    @Override
    public void updateInternal(float deltaTime) {
        if (updateAction()) {
            onNestCompleted();
        }
    }

    protected abstract void onNestCompleted();
}
