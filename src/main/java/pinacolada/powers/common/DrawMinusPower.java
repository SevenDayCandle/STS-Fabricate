package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;

@VisiblePower
public class DrawMinusPower extends PCLPower {
    public static final PCLPowerData DATA = register(DrawMinusPower.class)
            .setType(PowerType.DEBUFF)
            .setImageRegion(PCLPowerData.ICON_NEXT_TURN_DRAW_LESS)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurnNext)
            .setTooltip(PGR.core.tooltips.nextTurnDrawMinus);

    public DrawMinusPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        AbstractDungeon.player.gameHandSize -= difference;

        super.onAmountChanged(previousAmount, difference);
    }
}
