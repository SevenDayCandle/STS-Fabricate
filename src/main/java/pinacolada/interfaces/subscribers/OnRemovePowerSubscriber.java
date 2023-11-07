package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnRemovePowerSubscriber extends PCLCombatSubscriber {
    void onRemovePower(AbstractPower power, AbstractCreature target, AbstractCreature source);
}
