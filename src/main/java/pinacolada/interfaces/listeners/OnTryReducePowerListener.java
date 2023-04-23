package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnTryReducePowerListener
{
    boolean tryReducePower(AbstractPower power, AbstractCreature target, AbstractCreature source, AbstractGameAction action);
}
