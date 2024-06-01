package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.annotations.CombatSubscriber;

@CombatSubscriber
public interface OnTryReducePowerSubscriber extends PCLCombatSubscriber {
    boolean tryReducePower(AbstractPower power, AbstractCreature source, AbstractCreature target, AbstractGameAction action);
}
