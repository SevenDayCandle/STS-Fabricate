package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class LoseHP extends PCLAction<Void> {
    protected boolean ignorePowers = false;
    protected boolean ignoreTempHP = false;
    protected boolean canKill = true;
    protected float pitchMin;
    protected float pitchMax;

    public LoseHP(AbstractCreature target, AbstractCreature source, int amount) {
        this(target, source, amount, AttackEffect.NONE);
    }

    public LoseHP(AbstractCreature target, AbstractCreature source, int amount, AttackEffect effect) {
        super(ActionType.DAMAGE, 0.33f);

        this.attackEffect = effect;
        this.pitchMin = this.pitchMax = (attackEffect == AttackEffect.NONE) ? 0 : 1;

        initialize(source, target, amount);
    }

    public LoseHP canKill(boolean value) {
        this.canKill = value;

        return this;
    }

    @Override
    protected void firstUpdate() {
        PCLAttackVFX attackVFX = PCLAttackVFX.get(this.attackEffect);
        if (attackVFX != null && this.target.currentHealth > 0) {
            attackVFX.attack(source, target, pitchMin, pitchMax, null);
        }
    }

    public LoseHP ignorePowers(boolean ignorePowers) {
        this.ignorePowers = ignorePowers;

        return this;
    }

    public LoseHP ignoreTempHP(boolean ignoreTempHP) {
        this.ignoreTempHP = ignoreTempHP;

        return this;
    }

    public LoseHP setSoundPitch(float pitchMin, float pitchMax) {
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            if (!canKill) {
                amount = Math.max(0, Math.min(GameUtilities.getHP(target, true, false) - 1, amount));
            }

            DamageHelper.dealDirectHPLoss(source, target, amount, ignoreTempHP, ignorePowers);

            if (!Settings.FAST_MODE) {
                PCLActions.top.wait(0.1f);
            }
        }
    }
}
