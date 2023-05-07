package pinacolada.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.vfx.combat.*;
import pinacolada.effects.utility.CombinedEffect;
import pinacolada.effects.vfx.*;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

// Copied and modified from STS-AnimatorMod
// TODO merge with PCLEffects
public class VFX {
    public static BleedEffect bleed(Hitbox target) {
        return new BleedEffect(target.cX, target.cY - (50.0F * Settings.scale), 32);
    }

    public static CircularWaveEffect circularWave(Hitbox target) {
        return circularWave(target.cX, target.cY);
    }

    public static CircularWaveEffect circularWave(float cX, float cY) {
        return new CircularWaveEffect(cX, cY);
    }

    public static CleaveEffect cleave(boolean fromPlayer) {
        return new CleaveEffect(fromPlayer);
    }

    public static DaggerSprayEffect daggerSpray() {
        return new DaggerSprayEffect(flipHorizontally());
    }

    public static DarknessEffect darkness(Hitbox target, float spread) {
        return darkness(randomX(target, spread), randomY(target, spread));
    }

    public static DarknessEffect darkness(float cX, float cY) {
        return new DarknessEffect(cX, cY);
    }

    public static EffekseerEffect eFX(EffekseerEFK key) {
        return eFX(key, Settings.WIDTH * 0.75f, AbstractDungeon.player != null ? AbstractDungeon.player.hb.cY : Settings.HEIGHT * 0.35f);
    }

    public static EffekseerEffect eFX(EffekseerEFK key, float x, float y) {
        return new EffekseerEffect(key, x, y);
    }

    public static EffekseerEffect eFX(EffekseerEFK key, Hitbox hb) {
        return eFX(key, hb.cX, hb.cY);
    }

    public static ElectricityEffect electric(Hitbox target, float spread) {
        return electric(target.cX, target.cY).setSpread(spread);
    }

    public static ElectricityEffect electric(float cX, float cY) {
        return new ElectricityEffect(cX, cY);
    }

    public static FallingIceEffect fallingIce(int frostCount) {
        return new FallingIceEffect(frostCount, flipHorizontally());
    }

    public static FireBurstParticleEffect fireBurstParticle(float cX, float cY) {
        return new FireBurstParticleEffect(cX, cY, Color.RED);
    }

    public static FireBurstEffect fireball(Hitbox source, Hitbox target) {
        return new FireBurstEffect(source.cX, source.cY, target.cX, target.cY).setColor(Color.RED, Color.ORANGE);
    }

    public static FlameBarrierEffect flameBarrier(Hitbox source) {
        return new FlameBarrierEffect(source.cX, source.cY);
    }

    public static boolean flipHorizontally() {
        return AbstractDungeon.player.flipHorizontal || AbstractDungeon.getMonsters().shouldFlipVfx();
    }

    public static CombinedEffect ghost(Hitbox hb, int variance) {
        return ghost(randomX(hb, variance), randomY(hb, variance));
    }

    public static CombinedEffect ghost(float cX, float cY) {
        final CombinedEffect effect = new CombinedEffect();
        effect.add(new OrbFlareNotActuallyNeedingOrbEffect(cX, cY).setColors(OrbFlareEffect.OrbFlareColor.DARK)).renderBehind = false;
        for (int i = 0; i < 4; i++) {
            effect.add(new DarkOrbActivateParticle(cX, cY)).renderBehind = false;
        }

        return effect;
    }

    public static AnimatedParticleEffect gunshot(Hitbox target, float spread) {
        return gunshot(randomX(target, spread), randomY(target, spread));
    }

    public static AnimatedParticleEffect gunshot(float cX, float cY) {
        return new AnimatedParticleEffect(PCLCoreImages.Effects.shot.texture(), cX, cY, 4, 4);
    }

    public static HemokinesisEffect hemokinesis(float tX, float tY, float sX, float sY) {
        return new HemokinesisEffect(tX, tY, sX, sY);
    }

    public static IntimidateEffect intimidate(Hitbox source) {
        return new IntimidateEffect(source.cX, source.cY);
    }

    public static IronWaveEffect ironWave(float sX, float sY, float tX) {
        return new IronWaveEffect(sX, sY, tX);
    }

    public static LaserBeamEffect laser(float cX, float cY) {
        return new LaserBeamEffect(cX, cY);
    }

    public static MeteorFallEffect meteorFall(Hitbox hb) {
        return new MeteorFallEffect(randomX(hb, 0.2f), randomY(hb, 0.2f));
    }

    public static PsychokinesisEffect psychokinesis(Hitbox target) {
        return psychokinesis(target.cX, target.cY);
    }

    public static PsychokinesisEffect psychokinesis(float cX, float cY) {
        return new PsychokinesisEffect(cX, cY);
    }

    public static float randomX(Hitbox hb, float variance) {
        return hb.cX + (variance == 0 ? 0 : (MathUtils.random(-variance, variance) * hb.width));
    }

    public static float randomY(Hitbox hb, float variance) {
        return hb.cY + (variance == 0 ? 0 : (MathUtils.random(-variance, variance) * hb.height));
    }

    public static RazorWindEffect razorWind(Hitbox source) {
        return razorWind(source, source, MathUtils.random(1000.0F, 1200.0F), MathUtils.random(-20.0F, 20.0F));
    }

    public static RazorWindEffect razorWind(Hitbox source, Hitbox target, float horizontalSpeed, float horizontalAcceleration) {
        return new RazorWindEffect(source.cX, source.cY, randomY(target, 0.33f), horizontalSpeed, horizontalAcceleration);
    }

    public static RockBurstEffect rockBurst(Hitbox target, float scale) {
        return new RockBurstEffect(target.cX, target.cY, scale);
    }

    public static RockBurstEffect rockBurst(float cX, float cY) {
        return new RockBurstEffect(cX, cY, 1);
    }

    public static ShieldEffect shield(Hitbox target) {
        return shield(target.cX, target.cY);
    }

    public static ShieldEffect shield(float cX, float cY) {
        return new ShieldEffect(cX, cY);
    }

    public static ShockWaveEffect shockWave(Hitbox source, Color color) {
        return shockWave(source, color, ShockWaveEffect.ShockWaveType.ADDITIVE);
    }

    public static ShockWaveEffect shockWave(Hitbox source, Color color, ShockWaveEffect.ShockWaveType type) {
        return new ShockWaveEffect(source.cX, source.cY, color.cpy(), type);
    }

    public static ShootingStarsEffect shootingStars(Hitbox source, float spreadY) {
        return new ShootingStarsEffect(source.cX, source.cY).setSpread(0, spreadY).flipHorizontally(flipHorizontally());
    }

    public static SmallLaserEffect smallLaser(Hitbox source, Hitbox target) {
        return smallLaser(source, target, 0.2f);
    }

    public static SmallLaserEffect smallLaser(Hitbox source, Hitbox target, float variance) {
        return new SmallLaserEffect(source.cX, source.cY, randomX(target, variance), randomY(target, variance));
    }

    public static SnowballEffect snowball(Hitbox source, Hitbox target) {
        return new SnowballEffect(source.cX, source.cY, randomX(target, 0.15f), randomY(target, 0.15f)).setColor(Color.SKY, Color.NAVY);
    }

    public static SnowballImpactEffect snowballImpact(Hitbox target, float spread) {
        return new SnowballImpactEffect(randomX(target, spread), randomY(target, spread));
    }

    public static SnowballImpactEffect snowballImpact(float cX, float cY) {
        return new SnowballImpactEffect(cX, cY);
    }

    public static SparkImpactEffect sparkImpact(Hitbox target, float spread) {
        return new SparkImpactEffect(randomX(target, spread), randomY(target, spread));
    }

    public static SparkImpactEffect sparkImpact(float cX, float cY) {
        return new SparkImpactEffect(cX, cY);
    }

    public static StrongPunchEffect strongPunch(Hitbox target) {
        return strongPunch(target.cX, target.cY);
    }

    public static StrongPunchEffect strongPunch(float x, float y) {
        return (StrongPunchEffect) new StrongPunchEffect(x, y, 2).setDuration(1f, true);
    }

    public static ColoredSweepingBeamEffect sweepingBeam(AbstractCreature source) {
        return sweepingBeam(source.hb, source.flipHorizontal, Color.CYAN);
    }

    public static ColoredSweepingBeamEffect sweepingBeam(Hitbox source, boolean flipHorizontal, Color color) {
        return new ColoredSweepingBeamEffect(source.cX, source.cY, flipHorizontal, color);
    }

    public static TornadoEffect tornado(Hitbox source) {
        return tornado(source.cX, source.cY);
    }

    public static TornadoEffect tornado(float cX, float cY) {
        return new TornadoEffect(cX, cY);
    }

    public static VerticalImpactEffect verticalImpact(float cX, float cY) {
        return new VerticalImpactEffect(cX, cY);
    }

    public static FadingParticleEffect water(Hitbox target, float spread) {
        return water(randomX(target, spread), randomY(target, spread));
    }

    public static FadingParticleEffect water(float cX, float cY) {
        return (FadingParticleEffect) new FadingParticleEffect(PCLCoreImages.Effects.waterSplash1.texture(), cX, cY).setColor(Color.WHITE)
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setOpacity(MathUtils.random(0.7f, 1f))
                .setDuration(1.3f, false);
    }

    public static FadingParticleEffect water2(Hitbox target, float spread) {
        return water2(randomX(target, spread), randomY(target, spread));
    }

    public static FadingParticleEffect water2(float cX, float cY) {
        return (FadingParticleEffect) new FadingParticleEffect(PCLCoreImages.Effects.waterSplash2.texture(), cX, cY).setColor(Color.WHITE)
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setOpacity(MathUtils.random(0.7f, 1f))
                .setDuration(1.3f, false);
    }

    public static WeightyImpactEffect weightyImpact(Hitbox target) {
        return weightyImpact(target, new Color(1.0F, 1.0F, 0.1F, 0.0F));
    }

    public static WeightyImpactEffect weightyImpact(Hitbox target, Color color) {
        return new WeightyImpactEffect(target.cX, target.cY);
    }

    public static AnimatedParticleEffect whack(Hitbox target, float spread) {
        return whack(randomX(target, spread), randomY(target, spread));
    }

    public static AnimatedParticleEffect whack(float cX, float cY) {
        return new AnimatedParticleEffect(PCLCoreImages.Effects.whack.texture(), cX, cY, 4, 4);
    }

    public static WhirlwindEffect whirlwind() {
        return whirlwind(new Color(0.9F, 0.9F, 1.0F, 1.0F), false);
    }

    public static WhirlwindEffect whirlwind(Color color, boolean reverse) {
        return new WhirlwindEffect(color, reverse);
    }
}
