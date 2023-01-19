package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnApplyPowerSubscriber extends PCLCombatSubscriber
{
    void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source);
}
