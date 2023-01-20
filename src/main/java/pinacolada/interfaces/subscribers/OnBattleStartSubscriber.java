package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnBattleStartSubscriber extends PCLCombatSubscriber
{
    void onBattleStart();
}
