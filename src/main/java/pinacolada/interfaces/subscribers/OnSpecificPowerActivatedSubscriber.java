package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnSpecificPowerActivatedSubscriber extends PCLCombatSubscriber {
    boolean onPowerActivated(AbstractPower power, AbstractCreature source, boolean originalValue);
}
