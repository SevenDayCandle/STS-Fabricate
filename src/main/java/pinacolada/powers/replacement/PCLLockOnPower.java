package pinacolada.powers.replacement;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.subscribers.OnOrbApplyLockOnSubscriber;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.powers.PCLSubscribingPower;
import pinacolada.powers.common.VitalityPower;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreTooltips;
import pinacolada.utilities.PCLRenderHelpers;

// Deliberately not extending LockOnPower or inheriting its ID because this power behaves slightly differently and we also want to avoid this being used in hardcoded base game checks in AbstractOrb
@VisiblePower
public class PCLLockOnPower extends PCLSubscribingPower implements OnOrbApplyLockOnSubscriber {
    public static final PCLPowerData DATA = register(PCLLockOnPower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.lockOn);
    public static final int BASE = 50;

    public PCLLockOnPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public static float getOrbMultiplier(boolean isPlayer) {
        return 1 + (getOrbMultiplierForDescription(isPlayer) / 100f);
    }

    public static float getOrbMultiplierForDescription(boolean isPlayer) {
        return BASE + (CombatManager.getBonus(DATA.ID, isPlayer));
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getOrbMultiplierForDescription(owner.isPlayer)), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public boolean isPriorityTarget() {
        return true;
    }

    @Override
    public float onOrbApplyLockOn(AbstractCreature target, float dmg) {
        return dmg * getOrbMultiplier(owner.isPlayer);
    }
}
