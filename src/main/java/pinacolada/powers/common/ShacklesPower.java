package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import extendedui.EUIUtils;
import pinacolada.effects.SFX;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

public class ShacklesPower extends PCLPower
{
    public static final String POWER_ID = createFullID(ShacklesPower.class);

    public ShacklesPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        this.loadRegion("shackle");

        initialize(amount, PowerType.DEBUFF, false);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference)
    {
        GameActions.top.applyPower(new StrengthPower(owner, -difference));

        super.onAmountChanged(previousAmount, difference);
    }

    @Override
    public void playApplyPowerSfx()
    {
        SFX.play(SFX.POWER_SHACKLE, 0.95F, 1.05f);
    }

    @Override
    public void duringTurn()
    {
        super.duringTurn();

        final AbstractMonster m = EUIUtils.safeCast(owner, AbstractMonster.class);
        if (m != null && !GameUtilities.isAttacking(m.intent))
        {
            GameActions.top.removePower(owner, this);
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer)
    {
        removePower();
    }
}