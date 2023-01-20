package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnMonsterDeathSubscriber extends PCLCombatSubscriber
{
    void onMonsterDeath(AbstractMonster monster, boolean triggerRelics);
}
