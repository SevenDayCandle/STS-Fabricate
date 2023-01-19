package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface OnMonsterMoveSubscriber extends PCLCombatSubscriber
{
    boolean onMonsterMove(AbstractMonster monster);
}
