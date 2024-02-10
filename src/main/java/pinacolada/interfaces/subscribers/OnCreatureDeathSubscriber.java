package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnCreatureDeathSubscriber extends PCLCombatSubscriber {
    boolean onDeath(AbstractCreature monster, boolean triggerRelics);
}
