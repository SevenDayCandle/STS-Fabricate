package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.interfaces.subscribers.OnOrbApplyLockOnSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreTooltips;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLLockOnPower extends PCLPower implements OnOrbApplyLockOnSubscriber, MultiplicativePower
{
    public static final String POWER_ID = createFullID(PCLLockOnPower.class);

    public PCLLockOnPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        this.loadRegion(PCLCoreTooltips.ICON_LOCKON);

        initialize(amount, PowerType.DEBUFF, true);
    }

    public static float getOrbMultiplier()
    {
        return 1 + (getOrbMultiplierForDescription() / 100f);
    }

    public static float getOrbMultiplierForDescription()
    {
        return (CombatManager.getEffectBonus(POWER_ID));
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        CombatManager.subscribe(this);
    }

    @Override
    public void onRemove()
    {
        super.onRemove();

        CombatManager.unsubscribe(this);
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
