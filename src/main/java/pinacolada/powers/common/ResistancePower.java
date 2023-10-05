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
public class ResistancePower extends PCLPower {
    public static final PCLPowerData DATA = register(ResistancePower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.Permanent)
            .setTooltip(PGR.core.tooltips.resistance)
            .setLimits(-15, 15);
    public static final int MULTIPLIER = 5;

    public ResistancePower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public static float calculatePercentage(int amount) {
        return Math.max(0.1f, 1f - amount * getMultiplier() / 100f);
    }

    public static float getMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(DATA.ID));
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            damage *= calculatePercentage(amount);
        }

        return super.atDamageReceive(damage, type);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(amount < 0 ? 1 : 0, PCLRenderHelpers.decimalFormat(Math.abs(amount * getMultiplier())));
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        super.onAmountChanged(previousAmount, difference);

        if (amount == 0) {
            removePower();
        }
        else {
            this.type = amount < 0 ? PowerType.DEBUFF : PowerType.BUFF;
        }
    }
}
