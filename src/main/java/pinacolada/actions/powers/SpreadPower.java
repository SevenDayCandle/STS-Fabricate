package pinacolada.actions.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class SpreadPower extends PCLAction<AbstractPower> {
    public String powerID;
    public boolean showEffect = true;
    public boolean isFast = true;

    public SpreadPower(AbstractCreature source, AbstractCreature target, PCLPowerHelper ph, int amount) {
        this(source, target, ph.ID, amount);
    }

    public SpreadPower(AbstractCreature source, AbstractCreature target, String powerID, int amount) {
        super(ActionType.POWER, Settings.ACTION_DUR_XFAST);
        initialize(source, target, amount);
        this.powerID = powerID;
    }

    @Override
    protected void firstUpdate() {
        final ArrayList<? extends AbstractCreature> enemies = !GameUtilities.isEnemy(target) ? GameUtilities.getPlayerTeam(true) : GameUtilities.getEnemies(true);
        final AbstractPower sourcePower = GameUtilities.getPower(target, powerID);
        int sourceAmount = sourcePower != null ? sourcePower.amount : 0;
        int spreadAmount = amount <= 0 ? sourceAmount : Math.min(amount, sourceAmount);

        if (spreadAmount > 0) {
            for (AbstractCreature enemy : enemies) {
                if (enemy != target) {
                    PCLActions.top.applyPower(source, enemy, sourcePower, spreadAmount);//.showEffect(showEffect, isFast);
                }
            }
        }

        complete(sourcePower);
    }

    public SpreadPower showEffect(boolean showEffect, boolean isFast) {
        this.showEffect = showEffect;
        this.isFast = isFast;

        return this;
    }

}
