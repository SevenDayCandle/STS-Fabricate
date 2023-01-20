package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnModifyDebuffSubscriber extends PCLCombatSubscriber
{
    void onModifyDebuff(AbstractPower power, int initialAmount, int newAmount);
} 