package pinacolada.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.ShieldParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.*;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.utilities.EUIClassUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.vfx.*;
import pinacolada.resources.PCLEnum;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;

public class PCLAttackVFX {
    private static final HashMap<AttackEffect, PCLAttackVFX> ALL = new HashMap<>();

    public static final PCLAttackVFX BITE = new PCLAttackVFX(PCLEnum.AttackEffect.BITE, null, (sx, sy, cx, cy) -> new BiteEffect(cx, cy, Color.WHITE.cpy()), PCLSFX.EVENT_VAMP_BITE);
    public static final PCLAttackVFX BLUNT_HEAVY = new PCLAttackVFX(AttackEffect.BLUNT_HEAVY, ImageMaster.ATK_BLUNT_HEAVY, PCLSFX.BLUNT_FAST);
    public static final PCLAttackVFX BLUNT_LIGHT = new PCLAttackVFX(AttackEffect.BLUNT_LIGHT, ImageMaster.ATK_BLUNT_LIGHT, PCLSFX.BLUNT_HEAVY);
    public static final PCLAttackVFX BURN = new PCLAttackVFX(PCLEnum.AttackEffect.BURN, null, Color.RED, (sx, sy, cx, cy) -> new FlameBarrierEffect(cx, cy), PCLSFX.ATTACK_FIRE);
    public static final PCLAttackVFX CLAW = new PCLAttackVFX(PCLEnum.AttackEffect.CLAW, null, (sx, sy, cx, cy) -> new ClawEffect(cx, cy, Color.VIOLET.cpy(), Color.WHITE.cpy()), PCLSFX.ATTACK_DAGGER_5, PCLSFX.ATTACK_DAGGER_6);
    public static final PCLAttackVFX DAGGER = new PCLAttackVFX(PCLEnum.AttackEffect.DAGGER, ImageMaster.ATK_SLASH_H, PCLSFX.ATTACK_DAGGER_1, PCLSFX.ATTACK_DAGGER_2);
    public static final PCLAttackVFX DARKNESS = new PCLAttackVFX(PCLEnum.AttackEffect.DARKNESS, null, Color.VIOLET, (sx, sy, cx, cy) -> new DarknessEffect(cx, cy), PCLSFX.PCL_DARKNESS);
    public static final PCLAttackVFX EARTH = new PCLAttackVFX(PCLEnum.AttackEffect.EARTH, null, Color.BROWN, (sx, sy, cx, cy) -> new RockBurstEffect(cx, cy, 1).setDuration(1.5f, true), PCLSFX.PCL_ORB_EARTH_CHANNEL);
    public static final PCLAttackVFX ELECTRIC = new PCLAttackVFX(PCLEnum.AttackEffect.ELECTRIC, null, Color.YELLOW, (sx, sy, cx, cy) -> new ElectricityEffect(cx, cy), PCLSFX.ORB_LIGHTNING_CHANNEL);
    public static final PCLAttackVFX FIRE = new PCLAttackVFX(AttackEffect.FIRE, ImageMaster.ATK_FIRE, Color.RED, PCLSFX.ATTACK_FIRE);
    public static final PCLAttackVFX GHOST = new PCLAttackVFX(PCLEnum.AttackEffect.GHOST, null, Color.VIOLET, (sx, sy, cx, cy) -> VFX.ghost(cx, cy), PCLSFX.ORB_DARK_CHANNEL);
    public static final PCLAttackVFX GUNSHOT = new PCLAttackVFX(PCLEnum.AttackEffect.GUNSHOT, null, (sx, sy, cx, cy) -> VFX.gunshot(cx, cy), PCLSFX.PCL_SUPPORT_DAMAGE);
    public static final PCLAttackVFX HEMOKINESIS = new PCLAttackVFX(PCLEnum.AttackEffect.HEMOKINESIS, null, Color.RED, HemokinesisEffect::new, PCLSFX.ATTACK_REAPER);
    public static final PCLAttackVFX ICE = new PCLAttackVFX(PCLEnum.AttackEffect.ICE, null, Color.SKY, (sx, sy, cx, cy) -> new SnowballImpactEffect(cx, cy), PCLSFX.ORB_FROST_CHANNEL);
    public static final PCLAttackVFX IRON = new PCLAttackVFX(PCLEnum.AttackEffect.IRON, null, Color.WHITE, (sx, sy, cx, cy) -> new IronWaveEffect(sx, sy, cx), PCLSFX.ORB_FROST_CHANNEL);
    public static final PCLAttackVFX LIGHTNING = new PCLAttackVFX(AttackEffect.LIGHTNING, null, Color.YELLOW, (sx, sy, cx, cy) -> new LightningEffect(cx, cy), PCLSFX.ATTACK_DAGGER_6);
    public static final PCLAttackVFX POISON = new PCLAttackVFX(AttackEffect.POISON, ImageMaster.ATK_POISON, Color.CHARTREUSE, PCLSFX.ATTACK_POISON, PCLSFX.ATTACK_POISON2);
    public static final PCLAttackVFX PSYCHOKINESIS = new PCLAttackVFX(PCLEnum.AttackEffect.PSYCHOKINESIS, null, Color.MAGENTA, (sx, sy, cx, cy) -> new PsychokinesisEffect(cx, cy), PCLSFX.PCL_PSI);
    public static final PCLAttackVFX PUNCH = new PCLAttackVFX(PCLEnum.AttackEffect.PUNCH, null, null, (sx, sy, cx, cy) -> new StrongPunchEffect(cx, cy, 2), PCLSFX.RAGE);
    public static final PCLAttackVFX SHIELD = new PCLAttackVFX(AttackEffect.SHIELD, ImageMaster.ATK_SHIELD, (sx, sy, cx, cy) -> new ShieldParticleEffect(cx, cy));
    public static final PCLAttackVFX SLASH_DIAGONAL = new PCLAttackVFX(AttackEffect.SLASH_DIAGONAL, ImageMaster.ATK_SLASH_D, PCLSFX.ATTACK_FAST);
    public static final PCLAttackVFX SLASH_HEAVY = new PCLAttackVFX(AttackEffect.SLASH_HEAVY, ImageMaster.ATK_SLASH_HEAVY, PCLSFX.ATTACK_HEAVY);
    public static final PCLAttackVFX SLASH_HORIZONTAL = new PCLAttackVFX(AttackEffect.SLASH_HORIZONTAL, ImageMaster.ATK_SLASH_H, PCLSFX.ATTACK_FAST);
    public static final PCLAttackVFX SLASH_VERTICAL = new PCLAttackVFX(AttackEffect.SLASH_VERTICAL, ImageMaster.ATK_SLASH_V, PCLSFX.ATTACK_FAST);
    public static final PCLAttackVFX SMALL_LASER = new PCLAttackVFX(PCLEnum.AttackEffect.SMALL_LASER, null, Color.BLUE, SmallLaserEffect::new, PCLSFX.ATTACK_MAGIC_BEAM_SHORT);
    public static final PCLAttackVFX SMALL_EXPLOSION = new PCLAttackVFX(PCLEnum.AttackEffect.SMALL_EXPLOSION, null, Color.SCARLET, (sx, sy, cx, cy) -> new ExplosionSmallEffect(cx, cy), PCLSFX.ATTACK_FLAME_BARRIER);
    public static final PCLAttackVFX SMASH = new PCLAttackVFX(AttackEffect.SMASH, null, (sx, sy, cx, cy) -> new ViceCrushEffect(cx, cy), PCLSFX.ATTACK_MAGIC_FAST_3);
    public static final PCLAttackVFX SPARK = new PCLAttackVFX(PCLEnum.AttackEffect.SPARK, null, Color.YELLOW, (sx, sy, cx, cy) -> new SparkImpactEffect(cx, cy), PCLSFX.ORB_LIGHTNING_CHANNEL);
    public static final PCLAttackVFX VERTICAL_IMPACT = new PCLAttackVFX(PCLEnum.AttackEffect.VERTICAL_IMPACT, null, (sx, sy, cx, cy) -> new VerticalImpactEffect(cx, cy), PCLSFX.ATTACK_WHIFF_1);
    public static final PCLAttackVFX WATER = new PCLAttackVFX(PCLEnum.AttackEffect.WATER, null, Color.BLUE, (sx, sy, cx, cy) -> VFX.water(cx, cy), PCLSFX.PCL_ORB_WATER_EVOKE);
    public static final PCLAttackVFX WAVE = new PCLAttackVFX(PCLEnum.AttackEffect.WAVE, null, Color.LIME, (sx, sy, cx, cy) -> VFX.circularWave(cx, cy), PCLSFX.PCL_PING);
    public static final PCLAttackVFX WEIGHTY_IMPACT = new PCLAttackVFX(PCLEnum.AttackEffect.WEIGHTY_IMPACT, null, (sx, sy, cx, cy) -> new WeightyImpactEffect(cx, cy), PCLSFX.ATTACK_AXE);
    public static final PCLAttackVFX WIND = new PCLAttackVFX(PCLEnum.AttackEffect.WIND, null, Color.FOREST, (sx, sy, cx, cy) -> new TornadoEffect(cx, cy), PCLSFX.POWER_FLIGHT);
    protected final String[] sounds;
    protected final FuncT4<AbstractGameEffect, Float, Float, Float, Float> createVFX;
    public final AttackEffect key;
    public final TextureRegion texture;
    public final Color damageTint;

    public PCLAttackVFX(AttackEffect key) {
        this(key, null);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, String... sfx) {
        this(key, texture, null, null, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, Color damageTint, FuncT4<AbstractGameEffect, Float, Float, Float, Float> createVFX, String... sfx) {
        ALL.put(key, this);
        this.key = key;
        this.texture = texture;
        this.damageTint = damageTint;
        this.createVFX = createVFX;
        this.sounds = sfx;
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, Color damageTint, String... sfx) {
        this(key, texture, damageTint, null, sfx);
    }

    public PCLAttackVFX(AttackEffect key, TextureRegion texture, FuncT4<AbstractGameEffect, Float, Float, Float, Float> createVFX, String... sfx) {
        this(key, texture, null, createVFX, sfx);
    }

    public static boolean flipHorizontally() {
        return AbstractDungeon.player.flipHorizontal || AbstractDungeon.getMonsters().shouldFlipVfx();
    }

    public static PCLAttackVFX get(AttackEffect key) {
        return ALL.get(key);
    }

    public static List<AttackEffect> keys() {
        return ALL.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name(), b.name())).collect(Collectors.toList());
    }

    public static float randomX(Hitbox hb, float variance) {
        return hb.cX + (variance == 0 ? 0 : (MathUtils.random(-variance, variance) * hb.width));
    }

    public static float randomY(Hitbox hb, float variance) {
        return hb.cY + (variance == 0 ? 0 : (MathUtils.random(-variance, variance) * hb.height));
    }

    public AbstractGameEffect attack(AbstractCreature source, AbstractCreature target, float pitchMin, float pitchMax) {
        return attack(source, target, pitchMin, pitchMax, null);
    }

    public AbstractGameEffect attack(AbstractCreature source, AbstractCreature target, float pitchMin, float pitchMax, Color vfxColor) {
        return attack(source, target, pitchMin, pitchMax, vfxColor, source == target ? 0 : 0.15f);
    }

    public AbstractGameEffect attack(AbstractCreature source, AbstractCreature target, float pitchMin, float pitchMax, Color vfxColor, float spread) {
        playSound(pitchMin, pitchMax);
        return attackWithoutSound(source, target, vfxColor, spread);
    }

    public AbstractGameEffect attackWithoutSound(AbstractCreature source, AbstractCreature target, Color vfxColor, float spread) {
        AbstractGameEffect effect;
        if (source != null) {
            effect = PCLEffects.List.add(getVFX(source.hb.cX, source.hb.cY, randomX(target.hb, spread), randomY(target.hb, spread)));
        }
        else {
            effect = PCLEffects.List.add(getVFX(randomX(target.hb, spread), randomY(target.hb, spread)));
        }
        if (vfxColor != null) {
            EUIClassUtils.setField(effect, "color", vfxColor);
        }

        return effect;
    }

    public String getSound() {
        return sounds.length == 0 ? null : sounds.length == 1 ? sounds[0] : EUIUtils.random(sounds);
    }

    public AbstractGameEffect getVFX(float sourceCx, float sourceCy, float targetCx, float targetCy) {
        if (createVFX != null) {
            return createVFX.invoke(sourceCx, sourceCy, targetCx, targetCy);
        }

        if (texture != null) {
            return new GenericRenderEffect(texture, targetCx, targetCy)
                    .setRotation(key == AttackEffect.BLUNT_HEAVY ? MathUtils.random(0, 360) : MathUtils.random(-12f, 12f));
        }

        return new GenericRenderEffect((TextureRegion) null, targetCx, targetCy);
    }

    public AbstractGameEffect getVFX(float targetCx, float targetCy) {
        return getVFX(0, 0, targetCx, targetCy);
    }

    public void playSound(float pitchMin, float pitchMax) {
        PCLSFX.play(getSound(), pitchMin, pitchMax);
    }
}
