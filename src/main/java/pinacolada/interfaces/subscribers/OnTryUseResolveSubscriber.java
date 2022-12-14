package pinacolada.interfaces.subscribers;

public interface OnTryUseResolveSubscriber
{
    int onTryUseResolve(int original, int amount, boolean fromButton);
}