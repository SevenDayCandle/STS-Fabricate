package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBlockGainedSubscriber
{
    void onBlockGained(AbstractCreature creature, int block);
}
