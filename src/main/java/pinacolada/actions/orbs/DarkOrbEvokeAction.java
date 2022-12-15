package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.effects.AttackEffects;
import pinacolada.utilities.GameUtilities;

public class DarkOrbEvokeAction extends PCLAction
{

    public DarkOrbEvokeAction(int damage)
    {
        super(ActionType.DAMAGE);

        initialize(damage);
    }

    @Override
    protected void firstUpdate()
    {
        int minHealth = Integer.MAX_VALUE;
        AbstractMonster enemy = null;

        for (AbstractMonster m : GameUtilities.getEnemies(true))
        {
            if (m.currentHealth < minHealth)
            {
                minHealth = m.currentHealth;
                enemy = m;
            }
        }

        if (enemy != null)
        {
            int actualDamage = AbstractOrb.applyLockOn(enemy, amount);
            if (actualDamage > 0)
            {
                PCLActions.top.dealDamage(source, enemy, actualDamage, DamageInfo.DamageType.THORNS, AttackEffects.DARKNESS)
                        .setVFX(true, false);
            }
        }

        complete();
    }
}
