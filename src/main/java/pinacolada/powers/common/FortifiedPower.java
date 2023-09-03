package pinacolada.powers.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisiblePower;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

@VisiblePower
public class FortifiedPower extends PCLPower {
    public static final PCLPowerData DATA = register(FortifiedPower.class)
            .setType(PowerType.BUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.TurnBased)
            .setTooltip(PGR.core.tooltips.fortified);
    public static final int MULTIPLIER = 25;

    public FortifiedPower(AbstractCreature owner, AbstractCreature source, int amount) {
        super(DATA, owner, source, amount);
    }

    public static float calculateBlock(float block, float multiplier) {
        return Math.max(0, block + Math.max(0, block * (multiplier / 100f)));
    }

    public static float getMultiplier() {
        return (MULTIPLIER + CombatManager.getPlayerEffectBonus(DATA.ID));
    }

    @Override
    public void atEndOfRound() {
        super.atEndOfRound();

        reducePower(1);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, PCLRenderHelpers.decimalFormat(getMultiplier()), amount, amount == 1 ? powerStrings.DESCRIPTIONS[1] : powerStrings.DESCRIPTIONS[2]);
    }

    @Override
    public float modifyBlock(float block) {
        return calculateBlock(block, getMultiplier());
    }
}
