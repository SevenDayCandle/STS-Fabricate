package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnApplyPowerSubscriber extends PCLCombatSubscriber {
    void onApplyPower(AbstractPower power, AbstractCreature source, AbstractCreature target);
}
