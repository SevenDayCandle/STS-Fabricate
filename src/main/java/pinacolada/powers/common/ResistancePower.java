package pinacolada.powers.common;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class ResistancePower extends PCLPower implements MultiplicativePower {
    public static final String POWER_ID = createFullID(ResistancePower.class);
    public static final float MULTIPLIER = 5;
    public static final int MAX_AMOUNT = 15;

    public ResistancePower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);
        initialize(amount);
        this.canGoNegative = true;
        this.maxAmount = MAX_AMOUNT;
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            damage *= calculatePercentage(amount);
        }

        return super.atDamageReceive(damage, type);
    }

    public static float calculatePercentage(int amount) {
        return Math.max(0.1f, 1f - amount * getMultiplier() / 100f);
    }

    @Override
    public String getUpdatedDescription() {
        this.type = amount < 0 ? PowerType.DEBUFF : PowerType.BUFF;
        return formatDescription(amount < 0 ? 1 : 0, PCLRenderHelpers.decimalFormat(Math.abs(amount * getMultiplier())));
    }

    public static float getMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(POWER_ID));
    }

    @Override
    protected void onAmountChanged(int previousAmount, int difference) {
        super.onAmountChanged(previousAmount, difference);

        if (amount == 0) {
            removePower();
        }
    }
}
