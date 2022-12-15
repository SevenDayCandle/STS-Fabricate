package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.vfx.combat.OrbFlareEffect;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.SFX;
import pinacolada.powers.common.PCLLockOnPower;
import pinacolada.utilities.GameUtilities;

public class LightningOrbAction extends PCLAction
{
    public static final int ELECTRIFIED_AMOUNT = 1;
    private final AbstractOrb orb;
    private final boolean hitAll;

    public LightningOrbAction(AbstractOrb orb, int damage, boolean hitAll)
    {
        super(ActionType.DAMAGE);
        initialize(damage);

        this.orb = orb;
        this.hitAll = hitAll;
    }

    public void update()
    {
        if (!this.hitAll)
        {
            AbstractMonster enemy = null;

            for (AbstractMonster m : GameUtilities.getEnemies(true))
            {
                if (m.hasPower(PCLLockOnPower.POWER_ID))
                {
                    enemy = m;
                    break;
                }
            }

            if (enemy == null)
            {
                enemy = GameUtilities.getRandomEnemy(true);
            }

            if (enemy != null)
            {
                int actualDamage = AbstractOrb.applyLockOn(enemy, amount);
                if (actualDamage > 0)
                {
                    PCLActions.top.dealDamage(source, enemy, actualDamage, DamageInfo.DamageType.THORNS, AttackEffects.LIGHTNING)
                            .setVFX(Settings.FAST_MODE, false);
                }
            }
        }
        else
        {
            int[] damage = DamageInfo.createDamageMatrix(amount, true, true);
            PCLActions.top.dealDamageToAll(damage, DamageInfo.DamageType.THORNS, AttackEffects.LIGHTNING)
                    .setVFX(Settings.FAST_MODE, true);
            PCLActions.top.playSFX(SFX.ORB_LIGHTNING_EVOKE);
        }

        if (this.orb != null)
        {
            PCLActions.bottom.playVFX(new OrbFlareEffect(this.orb, OrbFlareEffect.OrbFlareColor.LIGHTNING), Settings.FAST_MODE ? 0.0F : 0.6F / (float) AbstractDungeon.player.orbs.size());
        }

        complete();
    }
}
