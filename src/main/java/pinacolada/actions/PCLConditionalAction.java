package pinacolada.actions;

import extendedui.interfaces.delegates.FuncT1;

// Copied and modified from STS-AnimatorMod
public abstract class PCLConditionalAction<T, C> extends PCLAction<T> {
    protected FuncT1<Boolean, C> condition = (__) -> true;

    public PCLConditionalAction(ActionType type) {
        super(type);
    }

    public PCLConditionalAction(ActionType type, float duration) {
        super(type, duration);
    }

    protected boolean checkCondition(C result) {
        return condition.invoke(result);
    }

    public PCLConditionalAction<T, C> setCondition(FuncT1<Boolean, C> condition) {
        this.condition = condition;

        return this;
    }
}
