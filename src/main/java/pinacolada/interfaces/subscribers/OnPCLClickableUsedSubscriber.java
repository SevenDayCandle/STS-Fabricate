package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.CombatSubscriber;
import pinacolada.powers.PCLClickableUse;

@CombatSubscriber
public interface OnPCLClickableUsedSubscriber extends PCLCombatSubscriber {
    boolean onClickablePowerUsed(PCLClickableUse power, AbstractMonster target, int uses);
}