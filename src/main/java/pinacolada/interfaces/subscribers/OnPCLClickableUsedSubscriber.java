package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.powers.PCLClickableUse;

public interface OnPCLClickableUsedSubscriber extends PCLCombatSubscriber
{
    boolean onClickablePowerUsed(PCLClickableUse power, AbstractMonster target, int uses);
}