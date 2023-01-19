package pinacolada.interfaces.subscribers;

public interface OnEndOfTurnLastSubscriber extends PCLCombatSubscriber
{
    void onEndOfTurnLast(boolean isPlayer);
}