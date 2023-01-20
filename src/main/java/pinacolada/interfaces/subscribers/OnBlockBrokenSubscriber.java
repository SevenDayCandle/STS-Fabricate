package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnBlockBrokenSubscriber extends PCLCombatSubscriber
{
    void onBlockBroken(AbstractCreature creature);
}
