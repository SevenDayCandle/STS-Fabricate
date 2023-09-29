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
    protected boolean shouldRandomize;
    protected Color vfxColor = null;
    protected Color enemyTint = null;
    protected float pitchMin = 0.95f;
    protected float pitchMax = 1.05f;

    public DealDamage(AbstractCreature target, DamageInfo info, AttackEffect effect) {
        this(null, target, info, effect, 1);
    }

    public DealDamage(AbstractCreature target, DamageInfo info) {
        this(target, info, AttackEffect.NONE);
    }

    public DealDamage(AbstractCard card, AbstractCreature source, AbstractCreature target, AttackEffect effect) {
        this(card, target, new DamageInfo(source, card.damage, card.damageTypeForTurn), effect, 1);
    }

    public DealDamage(AbstractCard card, AbstractCreature source, AbstractCreature target, AttackEffect effect, int times) {
        this(card, target, new DamageInfo(source, card.damage, card.damageTypeForTurn), effect, times);
    }

    public DealDamage(AbstractCard card, AbstractCreature target, DamageInfo info, AttackEffect effect) {
        this(card, target, info, effect, 1);
    }

    public DealDamage(AbstractCard card, AbstractCreature target, DamageInfo info, AttackEffect effect, int times) {
        super(ActionType.DAMAGE, Settings.ACTION_DUR_XFAST);

        this.card = card;
        this.info = info;
        this.attackEffect = effect;

        initialize(info.owner, target, times);
        this.applyPowers = card != null;
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

    public DealDamage shouldRandomize(boolean value) {
        this.shouldRandomize = value;

        return this;
    }

    @Override
    protected void firstUpdate() {
        if (this.info.type != DamageInfo.DamageType.THORNS && this.source != null && this.source.isDying) {
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

    public DealDamage setOptions(boolean canKill, boolean canRedirect) {
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

    public DealDamage setVFXColor(Color color) {
        this.vfxColor = color.cpy();

        return this;
    }

    public DealDamage setVFXColor(Color color, Color enemyTint) {
        this.vfxColor = color.cpy();
        this.enemyTint = enemyTint.cpy();

        return this;
    }

    private boolean updateAttack() {
        if (shouldRandomize) {
            if (GameUtilities.getEnemies(true).size() > 0) {
                target = GameUtilities.getRandomEnemy(true);
                applyPowers = applyPowers || card != null;
            }
            else {
                return false;
            }
        }
        else if (target == null || GameUtilities.isDeadOrEscaped(target)) {
            if (canRedirect && GameUtilities.getEnemies(true).size() > 0) {
                target = GameUtilities.getRandomEnemy(true);
                applyPowers = applyPowers || card != null;
            }
            else {
                return false;
            }
        }

        PCLAttackVFX attackVFX = PCLAttackVFX.get(this.attackEffect);
        if (attackVFX != null) {
            attackVFX.attack(source, target, pitchMin, pitchMax, vfxColor);
        }

        if (applyPowers) {
            if (card != null) {
                card.calculateCardDamage(GameUtilities.asMonster(target));
                this.info.output = card.damage;
            }
            else {
                this.info.applyPowers(this.info.owner, target);
                if (orb != null) {
                    this.info.output = CombatManager.playerSystem.modifyOrbOutput(this.info.output, target, orb);
                }
            }
            applyPowers = false;
        }

        if (!canKill) {
            info.output = Math.max(0, Math.min(GameUtilities.getHP(target, true, true) - 1, info.output));
        }
        DamageHelper.applyTint(target, enemyTint, attackVFX);
        DamageHelper.dealDamage(target, info, bypassBlock, bypassThorns);

        if (GameUtilities.areMonstersBasicallyDead()) {
            GameUtilities.clearPostCombatActions();
            return false;
        }

        return true;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            if (amount > 0 && updateAttack()) {
                amount -= 1;
                duration = startDuration;
                isDone = false;
            }
            else {
                complete(target);
            }
        }
    }
}
