package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface OnTryReducePowerListener
{
    boolean tryReducePower(AbstractPower var1, AbstractCreature var2, AbstractCreature var3, AbstractGameAction var4);
}
