package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.DrawReductionPower;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreTooltips;

public class DrawLessPower extends PCLPower
{
    public static final String POWER_ID = createFullID(DrawLessPower.class);

    public DrawLessPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);
        this.powerStrings = CardCrawlGame.languagePack.getPowerStrings(DrawReductionPower.POWER_ID);
        this.loadRegion(PCLCoreTooltips.ICON_NEXT_TURN_DRAW_LESS);
        initialize(amount, PowerType.DEBUFF, false);
        updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw()
    {
        super.atStartOfTurnPostDraw();
        removePower();
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference)
    {
        AbstractDungeon.player.gameHandSize -= difference;

        super.onAmountChanged(previousAmount, difference);
    }
}
