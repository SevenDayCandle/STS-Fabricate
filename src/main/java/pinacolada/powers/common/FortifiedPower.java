package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.PCLRenderHelpers;

public class FortifiedPower extends PCLPower implements MultiplicativePower {
    public static final String POWER_ID = createFullID(FortifiedPower.class);
    public static final int MULTIPLIER = 25;

    public FortifiedPower(AbstractCreature owner, int amount) {
        super(owner, POWER_ID);

        initialize(amount, PowerType.BUFF, true);
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();

        reducePower(1);
    }

    @Override
    public float modifyBlock(float block) {
        return calculateBlock(block, getMultiplier());
    }

    public static float calculateBlock(float block, float multiplier) {
        return Math.max(0, block + Math.max(0, block * (multiplier / 100f)));
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    public static float getMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(POWER_ID));
    }
}
