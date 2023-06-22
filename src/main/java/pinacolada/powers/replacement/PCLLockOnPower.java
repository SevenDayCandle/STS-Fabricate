package pinacolada.powers.replacement;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbApplyLockOnSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreTooltips;
import pinacolada.utilities.PCLRenderHelpers;

// Deliberately not extending LockOnPower or inheriting its ID because this power behaves slightly differently and we also want to avoid this being used in hardcoded base game checks in AbstractOrb
public class PCLLockOnPower extends PCLPower implements OnOrbApplyLockOnSubscriber {
    public static final String POWER_ID = createFullID(PCLLockOnPower.class);
    public static final int BASE = 50;

    public PCLLockOnPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        this.loadRegion(PCLCoreTooltips.ICON_LOCKON);

        initialize(amount, PowerType.DEBUFF, true);
    }

    public static float getOrbMultiplier() {
        return 1 + (getOrbMultiplierForDescription() / 100f);
    }

    public static float getOrbMultiplierForDescription() {
        return BASE + (CombatManager.getEffectBonus(POWER_ID));
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getOrbMultiplierForDescription()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public boolean isPriorityTarget() {
        return true;
    }

    @Override
    public void onRemove() {
        super.onRemove();

        CombatManager.unsubscribe(this);
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();

        CombatManager.subscribe(this);
    }

    @Override
    public float onOrbApplyLockOn(AbstractCreature target, float dmg) {
        return dmg * getOrbMultiplier();
    }
}
