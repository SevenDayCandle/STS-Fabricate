package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.misc.CombatStats;
import pinacolada.powers.PCLPower;

public class ResistancePower extends PCLPower implements MultiplicativePower
{
    public static final String POWER_ID = createFullID(ResistancePower.class);
    public static final float MULTIPLIER = 5;
    public static final float MULTIPLIER2 = 2.5f;
    private float totalMultiplier = 0;
    private float totalMultiplier2 = 0;

    public ResistancePower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);
        initialize(amount);
        this.canGoNegative = true;
        this.maxAmount = 10;
    }

    public static float calculatePercentage(int amount)
    {
        return Math.max(0.1f, 1f - amount * MULTIPLIER / 100f);
    }

    @Override
    public String getUpdatedDescription()
    {
        if (amount > 0)
        {
            this.type = PowerType.BUFF;
        }
        else
        {
            this.type = PowerType.DEBUFF;
        }
        return formatDescription(amount >= 0 ? 0 : 1, Math.abs(amount * MULTIPLIER), Math.abs(totalMultiplier), Math.abs(totalMultiplier2), maxAmount);
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        updatePercentage();
    }

    @Override
    public void onRemove()
    {
        this.amount = 0;
        updatePercentage();
    }

    @Override
    public void stackPower(int stackAmount)
    {
        super.stackPower(stackAmount);

        updatePercentage();
    }

    @Override
    public void reducePower(int reduceAmount)
    {
        super.reducePower(reduceAmount);
        updatePercentage();
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference)
    {
        super.onAmountChanged(previousAmount, difference);

        if (amount == 0)
        {
            removePower();
        }
    }

    public void updatePercentage()
    {
        //Undo the previous changes made by this power
        CombatStats.addPlayerEffectBonus(ImpairedPower.POWER_ID, this.totalMultiplier);
        CombatStats.addPlayerEffectBonus(VulnerablePower.POWER_ID, this.totalMultiplier);
        CombatStats.addPlayerEffectBonus(WeakPower.POWER_ID, this.totalMultiplier2);
        CombatStats.addPlayerEffectBonus(FrailPower.POWER_ID, this.totalMultiplier2);

        this.totalMultiplier = MULTIPLIER * this.amount;
        this.totalMultiplier2 = MULTIPLIER2 * this.amount;

        CombatStats.addPlayerEffectBonus(ImpairedPower.POWER_ID, -this.totalMultiplier);
        CombatStats.addPlayerEffectBonus(VulnerablePower.POWER_ID, -this.totalMultiplier);
        CombatStats.addPlayerEffectBonus(WeakPower.POWER_ID, -this.totalMultiplier2);
        CombatStats.addPlayerEffectBonus(FrailPower.POWER_ID, -this.totalMultiplier2);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type)
    {
        if (type == DamageInfo.DamageType.NORMAL)
        {
            damage *= calculatePercentage(amount);
        }

        return super.atDamageReceive(damage, type);
    }
}
