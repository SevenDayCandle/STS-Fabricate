package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import extendedui.EUIUtils;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

public class ToxicologyPower extends PCLPower
{
    public static final String POWER_ID = createFullID(ToxicologyPower.class);

    public ToxicologyPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source)
    {
        super.onApplyPower(power, target, source);

        if (GameUtilities.isPlayer(source) && (power.ID.equals(PoisonPower.POWER_ID) || power.ID.equals(BruisedPower.POWER_ID) || power.ID.equals(BlindedPower.POWER_ID) || power.ID.equals(ShacklesPower.POWER_ID)))
        {
            power.amount += this.amount;

            final AbstractGameAction action = AbstractDungeon.actionManager.currentAction;
            if (action instanceof ApplyPowerAction)
            {
                action.amount += this.amount;
            }
            else
            {
                EUIUtils.logWarning(this, "Unknown action type: " + action.getClass().getName());
            }
        }
    }
}
