package pinacolada.powers.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.effects.AttackEffects;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class SupportDamagePower extends PCLPower
{
    public static final String POWER_ID = createFullID(SupportDamagePower.class);
    protected DamageInfo targetDamageInfo;
    protected AbstractCreature bestTarget;

    public SupportDamagePower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);
        targetDamageInfo = new DamageInfo(owner, amount, DamageInfo.DamageType.NORMAL);

        initialize(amount);
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, amount, bestTarget != null ? formatDescription(1, targetDamageInfo.output, EUIUtils.modifyString(bestTarget.name, w -> "#y" + w)) : "");
    }

    @Override
    public void update(int slot)
    {
        super.update(slot);
        hb.update();
        updateParameters();
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c)
    {
        super.renderIcons(sb, x, y, c);
        if (hb.cX != x || hb.cY != y)
        {
            hb.move(x, y);
        }
        if (hb.hovered && bestTarget != null)
        {
            bestTarget.renderReticle(sb);
        }
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();
        updateParameters();
    }

    protected void updateParameters()
    {
        targetDamageInfo.base = amount;
        if (GameUtilities.inBattle(false))
        {
            bestTarget = null;
            if (GameUtilities.isPlayer(owner))
            {
                List<AbstractMonster> enemies = GameUtilities.getEnemies(true);
                bestTarget = EUIUtils.find(enemies, m -> m.hasPower(PCLLockOnPower.POWER_ID));
                if (bestTarget == null)
                {
                    bestTarget = EUIUtils.findMin(enemies, m -> m.currentHealth);
                }
            }
            else
            {
                bestTarget = player;
            }
            if (bestTarget != null)
            {
                targetDamageInfo.applyPowers(owner, bestTarget);
            }
        }
        else
        {
            bestTarget = null;
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer)
    {
        super.atEndOfTurn(isPlayer);

        PCLActions.bottom.callback(() -> {
            updateParameters();
            if (bestTarget != null)
            {
                PCLActions.bottom.dealDamage(bestTarget, targetDamageInfo, AttackEffects.NONE)
                        .setPiercing(true, false)
                        .applyPowers(true)
                        .setVFX(true, false);
            }
            flashWithoutSound();
        });
    }
}