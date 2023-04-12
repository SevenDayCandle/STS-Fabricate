package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.interfaces.subscribers.OnOrbApplyFocusSubscriber;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.PCLRenderHelpers;

public class ImpairedPower extends PCLPower implements OnOrbApplyFocusSubscriber, MultiplicativePower
{
    public static final String POWER_ID = createFullID(ImpairedPower.class);
    public static final int MULTIPLIER = 50;
    public boolean justApplied;

    public ImpairedPower(AbstractCreature owner, int amount)
    {
        this(owner, amount, false);
    }

    public ImpairedPower(AbstractCreature owner, int amount, boolean isSourceMonster)
    {
        super(owner, POWER_ID);
        justApplied = isSourceMonster;

        initialize(amount, PowerType.DEBUFF, true);
    }

    public static float getOrbMultiplier()
    {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(POWER_ID));
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getOrbMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
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
    public float modifyOrbOutgoing(float initial)
    {
        return initial * Math.max(0, getOrbMultiplier() / 100f);
    }

    @Override
    public void onApplyFocus(AbstractOrb orb)
    {
        if (GameUtilities.canOrbApplyFocus(orb))
        {
            orb.passiveAmount = (int) modifyOrbOutgoing(orb.passiveAmount);
            if (GameUtilities.canOrbApplyFocusToEvoke(orb))
            {
                orb.evokeAmount = (int) modifyOrbOutgoing(orb.evokeAmount);
            }
        }
    }

    @Override
    public void atStartOfTurnPostDraw()
    {
        super.atStartOfTurnPostDraw();
        if (justApplied)
        {
            justApplied = false;
        }
        else
        {
            reducePower(1);
        }

    }
}