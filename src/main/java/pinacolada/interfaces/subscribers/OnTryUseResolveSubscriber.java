package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryUseResolveSubscriber extends PCLCombatSubscriber
{
    int onTryUseResolve(int original, int amount, boolean fromButton);
}