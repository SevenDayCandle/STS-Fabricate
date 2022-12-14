package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface OnMonsterMoveSubscriber
{
    boolean onMonsterMove(AbstractMonster monster);
}
