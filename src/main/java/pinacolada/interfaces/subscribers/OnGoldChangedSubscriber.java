package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnGoldChangedSubscriber extends PCLCombatSubscriber {
    int onGoldChanged(int amount);
}
