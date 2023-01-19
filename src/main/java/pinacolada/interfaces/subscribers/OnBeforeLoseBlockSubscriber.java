package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnBeforeLoseBlockSubscriber extends PCLCombatSubscriber
{
    void onBeforeLoseBlock(AbstractCreature creature, int amount, boolean noAnimation);
}
