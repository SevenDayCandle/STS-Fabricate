package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.powers.RegrowPower;
import extendedui.EUIUtils;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameActions;

public class StolenGoldPower extends PCLPower
{
    public static final String POWER_ID = createFullID(StolenGoldPower.class);

    public StolenGoldPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        final AbstractMonster m = EUIUtils.safeCast(owner, AbstractMonster.class);
        if (m == null || m.hasPower(MinionPower.POWER_ID) || m.hasPower(RegrowPower.POWER_ID))
        {
            maxAmount = 0;
        }
        else if (m.type == AbstractMonster.EnemyType.BOSS)
        {
            maxAmount = 50;
        }
        else if (m.type == AbstractMonster.EnemyType.ELITE)
        {
            maxAmount = 25;
        }
        else
        {
            maxAmount = 10;
        }

        initialize(amount, PowerType.DEBUFF, false);
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, amount, (maxAmount - this.amount));
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        GameActions.top.gainGold(amount);
    }

    @Override
    public void stackPower(int stackAmount)
    {
        int initialGold = this.amount;

        super.stackPower(stackAmount);

        int goldGain = this.amount - initialGold;
        if (goldGain > 0)
        {
            GameActions.top.gainGold(goldGain);
        }
    }
}
