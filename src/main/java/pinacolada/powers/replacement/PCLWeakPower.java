package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.misc.CombatManager;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLWeakPower extends WeakPower implements CloneablePowerInterface, MultiplicativePower
{
    private boolean justApplied = false;

    public PCLWeakPower(AbstractCreature owner, int amount)
    {
        this(owner, amount, false);
    }

    public PCLWeakPower(AbstractCreature owner, int amount, boolean isSourceMonster)
    {
        super(owner, amount, isSourceMonster);
        if (isSourceMonster)
        {
            this.justApplied = true;
        }
    }

    public static float calculateDamage(float damage, float multiplier)
    {
        return Math.max(0, damage - damage * (multiplier / 100f));
    }

    public float getMultiplier()
    {
        return (GameUtilities.isPlayer(owner)) ? (CombatManager.getPlayerEffectBonus(ID)) : (CombatManager.getEffectBonus(ID));
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PCLWeakPower(owner, amount, justApplied);
    }

    @Override
    public void updateDescription()
    {
        this.description = DESCRIPTIONS[0] + PCLRenderHelpers.decimalFormat(getMultiplier()) + DESCRIPTIONS[1] + this.amount + (this.amount == 1 ? DESCRIPTIONS[2] : DESCRIPTIONS[3]);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type)
    {
        return type == DamageInfo.DamageType.NORMAL ? calculateDamage(damage, getMultiplier()) : damage;
    }
}
