package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBlockBrokenSubscriber
{
    void onBlockBroken(AbstractCreature creature);
}
