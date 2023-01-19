package pinacolada.interfaces.subscribers;

public interface OnTryUseResolveSubscriber extends PCLCombatSubscriber
{
    int onTryUseResolve(int original, int amount, boolean fromButton);
}