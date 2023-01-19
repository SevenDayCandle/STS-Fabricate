package pinacolada.interfaces.subscribers;

public interface OnEndOfTurnFirstSubscriber extends PCLCombatSubscriber
{
    void onEndOfTurnFirst(boolean isPlayer);
}