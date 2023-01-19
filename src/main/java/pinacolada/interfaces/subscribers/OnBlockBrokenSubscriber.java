package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBlockBrokenSubscriber extends PCLCombatSubscriber
{
    void onBlockBroken(AbstractCreature creature);
}
