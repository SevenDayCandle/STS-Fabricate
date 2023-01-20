package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnBlockGainedSubscriber extends PCLCombatSubscriber
{
    void onBlockGained(AbstractCreature creature, int block);
}
