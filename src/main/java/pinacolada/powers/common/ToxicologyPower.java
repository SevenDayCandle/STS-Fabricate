package pinacolada.powers.common;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import extendedui.EUIUtils;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class ToxicologyPower extends PCLPower {
    public static final PCLPowerData DATA = register(ToxicologyPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.toxicology);

    public ToxicologyPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        super.onApplyPower(power, target, source);

        if (GameUtilities.isPlayer(source) && (power.ID.equals(PoisonPower.POWER_ID) || power.ID.equals(BruisedPower.DATA.ID) || power.ID.equals(BlindedPower.DATA.ID) || power.ID.equals(ShacklesPower.DATA.ID))) {
            power.amount += this.amount;

            final AbstractGameAction action = AbstractDungeon.actionManager.currentAction;
            if (action instanceof ApplyPowerAction) {
                action.amount += this.amount;
            }
            else {
                EUIUtils.logWarning(this, "Unknown action type: " + action.getClass().getName());
            }
        }
    }
}
