package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.powers.PCLPower;

public interface OnPCLClickablePowerUsed
{
    boolean onClickablePowerUsed(PCLPower power, AbstractMonster target);
}