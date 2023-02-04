package pinacolada.powers.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.effects.SFX;
import pinacolada.powers.PCLPower;
import pinacolada.ui.combat.CombatHelper;

public class BlastedPower extends PCLPower implements HealthBarRenderPower
{
    public static final Color healthBarColor = Color.ORANGE.cpy();
    public static final String POWER_ID = createFullID(BlastedPower.class);

    public BlastedPower(AbstractCreature owner, AbstractCreature source, int amount)
    {
        super(owner, source, POWER_ID);

        initialize(amount, PowerType.DEBUFF, true);
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, getPassiveDamage(), getDamage());
    }

    @Override
    public void playApplyPowerSfx()
    {
        SFX.play(SFX.ATTACK_FIRE, 0.95f, 1.05f);
    }

    @Override
    public void atStartOfTurn()
    {
        this.flashWithoutSound();

        PCLActions.bottom.loseHP(source, owner, getPassiveDamage(), PCLAttackVFX.SMALL_EXPLOSION)
                .canKill(owner == null || !owner.isPlayer);
        reducePower(1);
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type)
    {
        return super.atDamageReceive(type == DamageInfo.DamageType.NORMAL ? damage + getDamage() : damage, type);
    }

    @Override
    public int getHealthBarAmount()
    {
        return CombatHelper.getHealthBarAmount(owner, getPassiveDamage(), false, true);
    }

    @Override
    public Color getColor()
    {
        return healthBarColor;
    }

    public int getDamage()
    {
        return MathUtils.ceil(amount / 5f);
    }

    public int getPassiveDamage()
    {
        return MathUtils.ceil(amount / 2f);
    }
}
