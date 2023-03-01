package pinacolada.actions;

import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.utilities.GenericCondition;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public abstract class PCLActionWithCallbackT2<T, C> extends PCLAction<T>
{
    protected ArrayList<GenericCondition<C>> conditions = new ArrayList<>();

    public PCLActionWithCallbackT2(ActionType type)
    {
        super(type);
    }

    public PCLActionWithCallbackT2(ActionType type, float duration)
    {
        super(type, duration);
    }

    public <S> PCLActionWithCallbackT2<T, C> addCondition(S state, FuncT2<Boolean, S, C> condition)
    {
        conditions.add(GenericCondition.fromT2(condition, state));

        return this;
    }

    public PCLActionWithCallbackT2<T, C> addCondition(FuncT1<Boolean, C> condition)
    {
        conditions.add(GenericCondition.fromT1(condition));

        return this;
    }

    public PCLActionWithCallbackT2<T, C> addCondition(FuncT0<Boolean> condition)
    {
        conditions.add(GenericCondition.fromT0(condition));

        return this;
    }

    protected boolean checkConditions(C result)
    {
        for (GenericCondition<C> callback : conditions)
        {
            if (!callback.check(result))
            {
                return false;
            }
        }

        return true;
    }
}
