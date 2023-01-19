package pinacolada.interfaces.subscribers;

public interface OnGainTempHPSubscriber extends PCLCombatSubscriber
{
    int onGainTempHP(int amount);
}
