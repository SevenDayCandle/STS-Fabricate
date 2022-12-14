package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnModifyDebuffSubscriber
{
    void onModifyDebuff(AbstractPower power, int initialAmount, int newAmount);
} 