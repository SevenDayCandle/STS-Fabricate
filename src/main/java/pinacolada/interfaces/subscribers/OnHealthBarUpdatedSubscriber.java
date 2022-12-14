package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnHealthBarUpdatedSubscriber
{
    void onHealthBarUpdated(AbstractCreature creature);
}
