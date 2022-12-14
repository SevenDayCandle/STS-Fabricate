package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.powers.PCLPower;

import java.text.DecimalFormat;

// TODO unused, remove
public class EnchantedArmorPower extends PCLPower implements MultiplicativePower
{
    public static final String POWER_ID = createFullID(EnchantedArmorPower.class);
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.0");

    public EnchantedArmorPower(AbstractCreature owner, int resistance)
    {
        super(owner, POWER_ID);

        initialize(resistance);
    }

    public static float calculatePercentage(int amount)
    {
        return 100f / (100f + amount);
    }

    private String getExampleDamage(int value)
    {
        return value + " -> " + "#g" + (int) (value * calculatePercentage(amount));
    }

    @Override
    public String getUpdatedDescription()
    {
        if (amount > 0)
        {
            return formatDescription(0, decimalFormat.format(((1 - calculatePercentage(amount)) * 100)));
        }
        else
        {
            return formatDescription(0, 0);
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type)
    {
        if (type == DamageInfo.DamageType.NORMAL)
        {
            damage *= calculatePercentage(amount + (int) damage);
        }

        return super.atDamageReceive(damage, type);
    }

    @Override
    public int onAttackedToChangeDamage(DamageInfo info, int damageAmount)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && info.owner != null)
        {
            increasePower(damageAmount);
        }

        return super.onAttackedToChangeDamage(info, damageAmount);
    }
}
