package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import pinacolada.annotations.VisiblePower;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreTooltips;

@VisiblePower
public class DrawMinusPower extends PCLPower {
    public static final PCLPowerData DATA = register(DrawMinusPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurnNext)
            .setTooltip(PGR.core.tooltips.bruised);

    public DrawMinusPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        AbstractDungeon.player.gameHandSize -= difference;

        super.onAmountChanged(previousAmount, difference);
    }
}
