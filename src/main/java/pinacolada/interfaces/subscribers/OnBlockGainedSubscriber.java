package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBlockGainedSubscriber extends PCLCombatSubscriber
{
    void onBlockGained(AbstractCreature creature, int block);
}
