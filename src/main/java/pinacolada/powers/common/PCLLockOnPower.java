package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.interfaces.subscribers.OnOrbApplyLockOnSubscriber;
import pinacolada.misc.CombatStats;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLLockOnPower extends PCLPower implements OnOrbApplyLockOnSubscriber, MultiplicativePower
{
    public static final String POWER_ID = createFullID(PCLLockOnPower.class);

    public PCLLockOnPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        this.loadRegion("lockon");

        initialize(amount, PowerType.DEBUFF, true);
    }

    public static float getOrbMultiplier()
    {
        return 1 + (getOrbMultiplierForDescription() / 100f);
    }

    public static float getOrbMultiplierForDescription()
    {
        return (CombatStats.getEffectBonus(POWER_ID));
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getOrbMultiplierForDescription()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public float onOrbApplyLockOn(AbstractCreature target, float dmg)
    {
        return dmg * getOrbMultiplier();
    }
}
