package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FrailPower;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLFrailPower extends FrailPower implements CloneablePowerInterface, MultiplicativePower
{
    protected static final String FAKE_POWER_ID = PGR.core.createID(FrailPower.class.getSimpleName());
    protected static final PowerStrings fakePowerStrings;

    static
    {
        fakePowerStrings = CardCrawlGame.languagePack.getPowerStrings(FAKE_POWER_ID);
    }

    private boolean justApplied = false;

    public PCLFrailPower(AbstractCreature owner, int amount, boolean isSourceMonster)
    {
        super(owner, amount, isSourceMonster);
        if (isSourceMonster)
        {
            this.justApplied = true;
        }
    }

    public static float calculateBlock(float block, float multiplier)
    {
        return Math.max(0, block - block * (multiplier / 100f));
    }

    public float getMultiplier()
    {
        return (GameUtilities.isPlayer(owner)) ? (CombatStats.getPlayerEffectBonus(ID)) : (CombatStats.getEffectBonus(ID));
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PCLFrailPower(owner, amount, justApplied);
    }

    @Override
    public void updateDescription()
    {
        this.description = EUIUtils.format(fakePowerStrings.DESCRIPTIONS[0], PCLRenderHelpers.decimalFormat(getMultiplier()), amount, amount == 1 ? fakePowerStrings.DESCRIPTIONS[1] : fakePowerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public float modifyBlock(float blockAmount)
    {
        return calculateBlock(blockAmount, getMultiplier());
    }
}
