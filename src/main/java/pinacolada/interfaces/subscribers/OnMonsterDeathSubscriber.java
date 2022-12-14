package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface OnMonsterDeathSubscriber
{
    void onMonsterDeath(AbstractMonster monster, boolean triggerRelics);
}
