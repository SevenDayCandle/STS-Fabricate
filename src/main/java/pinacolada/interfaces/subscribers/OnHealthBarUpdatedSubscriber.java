package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnHealthBarUpdatedSubscriber extends PCLCombatSubscriber
{
    void onHealthBarUpdated(AbstractCreature creature);
}
