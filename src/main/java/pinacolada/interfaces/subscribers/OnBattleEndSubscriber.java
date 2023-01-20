package pinacolada.interfaces.subscribers;

import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnBattleEndSubscriber extends PCLCombatSubscriber
{
    void onBattleEnd();
}
