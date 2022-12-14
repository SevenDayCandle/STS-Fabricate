package pinacolada.actions.damage;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.effects.VFX;
import pinacolada.misc.CombatStats;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class DealDamageToAll extends PCLActionWithCallback<ArrayList<AbstractCreature>>
{
    public final int[] damage;

    protected final ArrayList<AbstractCreature> targets = new ArrayList<>();
    protected AbstractOrb orb;
    protected BiConsumer<AbstractCreature, Boolean> onDamageEffect;
    protected boolean applyPowers;
    protected boolean applyPowerRemovalMultiplier;
    protected boolean bypassBlock;
    protected boolean bypassThorns;
    protected boolean isFast;

    protected Color vfxColor = null;
    protected Color enemyTint = null;
    protected float pitchMin = 0.95f;
    protected float pitchMax = 1.05f;

    public DealDamageToAll(AbstractCreature source, int[] amount, DamageInfo.DamageType damageType, AttackEffect attackEffect)
    {
        this(source, amount, damageType, attackEffect, false);
    }

    public DealDamageToAll(AbstractCreature source, int[] amount, DamageInfo.DamageType damageType, AttackEffect attackEffect, boolean isFast)
    {
        super(ActionType.DAMAGE, isFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.attackEffect = attackEffect;
        this.damageType = damageType;
        this.damage = amount;

        initialize(source, null, amount[0]);
    }

    public DealDamageToAll applyPowers(boolean applyPowers)
    {
        this.applyPowers = applyPowers;
        return this;
    }

    @Override
    protected void firstUpdate()
    {
        boolean mute = pitchMin == 0;
        int i = 0;
        for (AbstractMonster enemy : GameUtilities.getEnemies(false))
        {
            if (!GameUtilities.isDeadOrEscaped(enemy))
            {
                if (mute)
                {
                    GameEffects.List.attackWithoutSound(source, enemy, this.attackEffect, vfxColor, 0.15f);
                }
                else
                {
                    GameEffects.List.attack(source, enemy, this.attackEffect, pitchMin, pitchMax, vfxColor, 0.15f);
                }

                if (onDamageEffect != null)
                {
                    onDamageEffect.accept(enemy, !mute);
                }

                mute = true;
            }

            i += 1;
        }

        addDuration(AttackEffects.getDamageDelay(attackEffect));
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            for (AbstractPower p : player.powers)
            {
                p.onDamageAllEnemies(this.damage);
            }

            int i = 0;
            for (AbstractMonster enemy : GameUtilities.getEnemies(false))
            {
                if (!GameUtilities.isDeadOrEscaped(enemy))
                {
                    final DamageInfo info = new DamageInfo(this.source, this.damage[i], this.damageType);
                    if (orb != null)
                    {
                        info.output = CombatStats.playerSystem.modifyOrbOutput(info.output, enemy, orb);
                    }
                    if (applyPowers)
                    {
                        info.applyPowers(source, enemy);
                    }
                    DamageHelper.applyTint(enemy, enemyTint, attackEffect);
                    DamageHelper.dealDamage(enemy, info, bypassBlock, bypassThorns);
                    targets.add(enemy);
                }

                i += 1;
            }

            if (GameUtilities.getCurrentRoom(true).monsters.areMonstersBasicallyDead())
            {
                GameUtilities.clearPostCombatActions();
            }

            if (!isFast && !Settings.FAST_MODE)
            {
                GameActions.top.wait(0.1f);
            }

            complete(targets);
        }
    }

    public DealDamageToAll setDamageEffect(PCLEffekseerEFX effekseerKey)
    {
        this.onDamageEffect = (m, __) -> VFX.eFX(effekseerKey, m.hb);
        return this;
    }

    public DealDamageToAll setDamageEffect(BiConsumer<AbstractCreature, Boolean> onDamageEffect)
    {
        this.onDamageEffect = onDamageEffect;

        return this;
    }

    public DealDamageToAll setOrb(AbstractOrb orb)
    {
        this.orb = orb;
        return this;
    }

    public DealDamageToAll setPiercing(boolean bypassThorns, boolean bypassBlock)
    {
        this.bypassBlock = bypassBlock;
        this.bypassThorns = bypassThorns;

        return this;
    }

    public DealDamageToAll setSoundPitch(float pitchMin, float pitchMax)
    {
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;

        return this;
    }

    public DealDamageToAll setVFX(boolean superFast, boolean muteSfx)
    {
        this.isFast = superFast;

        if (muteSfx)
        {
            this.pitchMin = this.pitchMax = 0;
        }

        return this;
    }

    public DealDamageToAll setVFXColor(Color color)
    {
        this.vfxColor = color.cpy();

        return this;
    }

    public DealDamageToAll setVFXColor(Color color, Color enemyTint)
    {
        this.vfxColor = color.cpy();
        this.enemyTint = enemyTint.cpy();

        return this;
    }
}
