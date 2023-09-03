package pinacolada.powers.common;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisiblePower;
import pinacolada.effects.PCLSFX;
import pinacolada.powers.PCLPower;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreTooltips;
import pinacolada.utilities.GameUtilities;

@VisiblePower
public class DelayedDamagePower extends PCLPower implements HealthBarRenderPower {
    public static final PCLPowerData DATA = register(DelayedDamagePower.class)
            .setType(PowerType.DEBUFF)
            .setEndTurnBehavior(PCLPowerData.Behavior.SingleTurn)
            .setPriority(97)
            .setTooltip(PGR.core.tooltips.delayedDamage);
    private static final Color healthBarColor = Color.PURPLE.cpy();
    private final AbstractGameAction.AttackEffect attackEffect;

    public DelayedDamagePower(AbstractCreature owner, AbstractCreature source, int amount) {
        this(owner, source, amount, AbstractGameAction.AttackEffect.NONE);
    }
    public DelayedDamagePower(AbstractCreature owner, AbstractCreature source, int amount, AbstractGameAction.AttackEffect attackEffect) {
        super(DATA, owner, source, amount);
        this.attackEffect = attackEffect;
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        int damageAmount = owner.isPlayer ? Math.max(0, Math.min(GameUtilities.getHP(owner, true, true) - 1, amount)) : amount;
        PCLActions.bottom.takeDamage(owner, damageAmount, attackEffect);

        playApplyPowerSfx();
        flashWithoutSound();

        super.atEndOfTurn(isPlayer);
    }

    @Override
    public Color getColor() {
        return healthBarColor;
    }

    @Override
    public int getHealthBarAmount() {
        return GameUtilities.getHealthBarAmount(owner, amount, true, true);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, amount, owner.isPlayer ? powerStrings.DESCRIPTIONS[1] : "");
    }

    @Override
    public void playApplyPowerSfx() {
        PCLSFX.play(PCLSFX.HEART_BEAT, 1.25f, 1.35f, 0.9f);
    }
}