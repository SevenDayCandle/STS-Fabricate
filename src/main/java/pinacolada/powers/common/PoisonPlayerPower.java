package pinacolada.powers.common;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.effects.SFX;
import pinacolada.powers.PCLPower;
import pinacolada.resources.pcl.PCLCoreTooltips;
import pinacolada.ui.combat.CombatHelper;

public class PoisonPlayerPower extends PCLPower implements HealthBarRenderPower
{
    public static final String POWER_ID = createFullID(PoisonPlayerPower.class);

    private static final Color healthBarColor = Color.valueOf("78c13c");
    private AbstractCreature source;

    public PoisonPlayerPower(AbstractCreature owner, AbstractCreature source, int amount)
    {
        super(owner, source, POWER_ID);

        this.loadRegion(PCLCoreTooltips.ICON_POISON);

        initialize(amount, PowerType.DEBUFF, true);
    }

    public void atStartOfTurn()
    {
        PCLActions.bottom.loseHP(source, owner, amount, AttackEffect.POISON)
                .setSoundPitch(0.95f, 1.05f).canKill(!owner.isPlayer);
        PCLActions.bottom.reducePower(this, 1);

        this.flashWithoutSound();
    }

    @Override
    public int getHealthBarAmount()
    {
        return CombatHelper.getHealthBarAmount(owner, amount, false, true);
    }

    @Override
    public Color getColor()
    {
        return healthBarColor;
    }

    public void playApplyPowerSfx()
    {
        SFX.play(SFX.POWER_POISON, 0.95F, 1.05f);
    }
}
