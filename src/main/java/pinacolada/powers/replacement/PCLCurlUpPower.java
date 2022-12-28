package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CurlUpPower;
import pinacolada.actions.PCLActions;
import pinacolada.resources.pcl.PCLCoreTooltips;

public class PCLCurlUpPower extends CurlUpPower implements CloneablePowerInterface
{
    public static final String POWER_ID = CurlUpPower.POWER_ID;
    public static final String NAME;
    public static final String[] DESCRIPTIONS;
    private static final PowerStrings powerStrings;

    static
    {
        powerStrings = CardCrawlGame.languagePack.getPowerStrings("Curl Up");
        NAME = powerStrings.NAME;
        DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    }

    public PCLCurlUpPower(AbstractCreature owner, int amount)
    {
        super(owner, amount);
        this.name = NAME;
        this.ID = "Curl Up";
        this.owner = owner;
        this.amount = amount;
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
        this.loadRegion(PCLCoreTooltips.ICON_CURLUP);
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PCLCurlUpPower(owner, amount);
    }

    public int onAttacked(DamageInfo info, int damageAmount)
    {
        if (damageAmount < this.owner.currentHealth && damageAmount > 0 && info.owner != null && info.type == DamageInfo.DamageType.NORMAL)
        {
            this.flash();
            PCLActions.bottom.gainBlock(this.owner, this.amount);
            PCLActions.last.removePower(owner, owner, this);
        }

        return damageAmount;
    }
}