package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBeforeLoseBlockSubscriber
{
    void onBeforeLoseBlock(AbstractCreature creature, int amount, boolean noAnimation);
}
