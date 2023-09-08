package pinacolada.actions.creature;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.interfaces.delegates.FuncT2;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class DealDamage extends PCLAction<AbstractCreature> {
    protected final DamageInfo info;

    protected FuncT2<Float, AbstractCreature, AbstractCreature> onDamageEffect;
    protected AbstractOrb orb;
    protected boolean applyPowers;
    protected boolean bypassBlock;
    protected boolean bypassThorns;
    protected boolean canKill = true;
    protected boolean canRedirect;
    protected boolean hasPlayedEffect;
    protected boolean skipWait;

    protected Color vfxColor = null;
    protected Color enemyTint = null;
    protected float pitchMin = 0.95f;
    protected float pitchMax = 1.05f;

    public DealDamage(AbstractCreature target, DamageInfo info, AttackEffect effect) {
        this(null, target, info, effect);
    }

    public DealDamage(AbstractCard card, AbstractCreature target, DamageInfo info, AttackEffect effect) {
        super(ActionType.DAMAGE, Settings.ACTION_DUR_XFAST);

        this.card = card;
        this.skipWait = false;
        this.info = info;
        this.attackEffect = effect;

        initialize(info.owner, target, info.output);
        this.applyPowers = card != null;
    }

    public DealDamage(AbstractCreature target, DamageInfo info) {
        this(target, info, AttackEffect.NONE);
    }

    public DealDamage(AbstractCard card, AbstractCreature source, AbstractCreature target, AttackEffect effect) {
        this(card, target, new DamageInfo(source, card.damage, card.damageTypeForTurn), effect);
    }

    public DealDamage applyPowers(boolean applyPowers) {
        this.applyPowers = applyPowers;
        return this;
    }

    public DealDamage canKill(boolean value) {
        this.canKill = value;

        return this;
    }

    public DealDamage canRedirect(boolean value) {
        this.canRedirect = value;

        return this;
    }

    @Override
    protected void firstUpdate() {
        if (target == null || GameUtilities.isDeadOrEscaped(target)) {
            if (canRedirect && GameUtilities.getEnemies(true).size() > 0) {
                target = GameUtilities.getRandomEnemy(true);
                applyPowers = applyPowers || card != null;
                amount = info.base;
            }
            else {
                complete(null);
                return;
            }
        }

        if (this.info.type != DamageInfo.DamageType.THORNS && shouldCancelAction()) {
            complete(null);
            return;
        }

        if (onDamageEffect != null) {
            addDuration(onDamageEffect.invoke(source, target));
        }
    }

    public DealDamage setDamageEffect(EffekseerEFK effekseerKey) {
        this.onDamageEffect = (s, m) -> EffekseerEFK.efk(effekseerKey, m.hb).duration;
        return this;
    }

    public DealDamage setDamageEffect(FuncT2<Float, AbstractCreature, AbstractCreature> onDamageEffect) {
        this.onDamageEffect = onDamageEffect;

        return this;
    }

    public DealDamage setOptions(boolean superFast, boolean canKill, boolean canRedirect) {
        this.skipWait = superFast;
        this.canKill = canKill;
        this.canRedirect = canRedirect;
        return this;
    }

    public DealDamage setOrb(AbstractOrb orb) {
        this.orb = orb;
        return this;
    }

    public DealDamage setPiercing(boolean bypassThorns, boolean bypassBlock) {
        this.bypassBlock = bypassBlock;
        this.bypassThorns = bypassThorns;

        return this;
    }

    public DealDamage setSoundPitch(float pitchMin, float pitchMax) {
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;

        return this;
    }

    public DealDamage setVFX(boolean superFast, boolean muteSfx) {
        this.skipWait = superFast;

        if (muteSfx) {
            this.pitchMin = this.pitchMax = 0;
        }

        return this;
    }

    public DealDamage setVFXColor(Color color) {
        this.vfxColor = color.cpy();

        return this;
    }

    public DealDamage setVFXColor(Color color, Color enemyTint) {
        this.vfxColor = color.cpy();
        this.enemyTint = enemyTint.cpy();

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        PCLAttackVFX attackVFX = PCLAttackVFX.get(this.attackEffect);

        if (attackVFX != null && !hasPlayedEffect && duration <= 0.1f) {
            addDuration(attackVFX.damageDelay);
            attackVFX.attack(source, target, pitchMin, pitchMax, vfxColor);
            hasPlayedEffect = true;
        }

        if (tickDuration(deltaTime)) {
            if (applyPowers) {
                if (card != null) {
                    card.calculateCardDamage(GameUtilities.asMonster(target));
                    this.info.output = card.damage;
                }
                else {
                    this.info.applyPowers(this.info.owner, target);
                }
            }

            if (orb != null) {
                this.info.output = CombatManager.playerSystem.modifyOrbOutput(this.info.output, target, orb);
            }

            if (!canKill) {
                info.output = Math.max(0, Math.min(GameUtilities.getHP(target, true, true) - 1, info.output));
            }
            DamageHelper.applyTint(target, enemyTint, attackVFX);
            DamageHelper.dealDamage(target, info, bypassBlock, bypassThorns);

            if (GameUtilities.areMonstersBasicallyDead()) {
                GameUtilities.clearPostCombatActions();
            }

            if (!this.skipWait && !Settings.FAST_MODE) {
                PCLActions.top.wait(0.1f);
            }

            complete(target);
        }
    }
}
