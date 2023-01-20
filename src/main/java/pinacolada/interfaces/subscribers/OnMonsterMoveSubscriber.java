package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnMonsterMoveSubscriber extends PCLCombatSubscriber
{
    boolean onMonsterMove(AbstractMonster monster);
}
