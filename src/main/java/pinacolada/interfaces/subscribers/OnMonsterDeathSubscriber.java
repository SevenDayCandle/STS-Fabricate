package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface OnMonsterDeathSubscriber extends PCLCombatSubscriber
{
    void onMonsterDeath(AbstractMonster monster, boolean triggerRelics);
}
