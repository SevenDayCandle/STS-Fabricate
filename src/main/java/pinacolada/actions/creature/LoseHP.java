package pinacolada.actions.creature;

import com.evacipated.cardcrawl.mod.stslib.patches.core.AbstractCreature.TempHPField;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class LoseHP extends PCLAction<Void>
{
    protected boolean ignoreTempHP = false;
    protected boolean canKill = true;
    protected float pitchMin;
    protected float pitchMax;

    public LoseHP(AbstractCreature target, AbstractCreature source, int amount)
    {
        this(target, source, amount, AttackEffect.NONE);
    }

    public LoseHP(AbstractCreature target, AbstractCreature source, int amount, AttackEffect effect)
    {
        super(ActionType.DAMAGE, 0.33f);

        this.attackEffect = effect;
        this.pitchMin = this.pitchMax = (attackEffect == AttackEffect.NONE) ? 0 : 1;

        initialize(source, target, amount);
    }

    public LoseHP canKill(boolean value)
    {
        this.canKill = value;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        PCLAttackVFX attackVFX = PCLAttackVFX.get(this.attackEffect);
        if (attackVFX != null && this.target.currentHealth > 0)
        {
            PCLEffects.List.attack(source, target, attackVFX, pitchMin, pitchMax, null);
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            int tempHP = 0;
            if (ignoreTempHP)
            {
                tempHP = TempHPField.tempHp.get(target);
                TempHPField.tempHp.set(target, 0);
            }

            if (!canKill)
            {
                amount = Math.max(0, Math.min(GameUtilities.getHP(target, true, false) - 1, amount));
            }

            this.target.damage(new DamageInfo(this.source, this.amount, DamageInfo.DamageType.HP_LOSS));

            if (GameUtilities.areMonstersBasicallyDead())
            {
                GameUtilities.clearPostCombatActions();
            }
            else if (tempHP > 0)
            {
                TempHPField.tempHp.set(target, tempHP);
            }

            if (!Settings.FAST_MODE)
            {
                PCLActions.top.wait(0.1f);
            }
        }
    }

    public LoseHP ignoreTempHP(boolean ignoreTempHP)
    {
        this.ignoreTempHP = ignoreTempHP;

        return this;
    }

    public LoseHP setSoundPitch(float pitchMin, float pitchMax)
    {
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;

        return this;
    }
}
