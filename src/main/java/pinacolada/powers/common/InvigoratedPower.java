package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

@VisiblePower
public class InvigoratedPower extends PCLPower {
    public static final PCLPowerData DATA = register(InvigoratedPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setIsCommon(true)
            .setTooltip(PGR.core.tooltips.invigorated);
    public static final int MULTIPLIER = 25;

    public InvigoratedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public static float calculateDamage(float damage, float multiplier) {
        return Math.max(0, damage + Math.max(0, damage * (multiplier / 100f)));
    }

    public static float getMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(DATA.ID));
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return type == DamageInfo.DamageType.NORMAL ? calculateDamage(damage, getMultiplier()) : damage;
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }
}
