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

import static pinacolada.effects.PCLEffect.random;

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

    public static FallingIceEffect fallingIce(int frostCount) {
        return new FallingIceEffect(frostCount, PCLAttackVFX.flipHorizontally());
    }

    public static CombinedEffect ghost(Hitbox hb, int variance) {
        return ghost(PCLAttackVFX.randomX(hb, variance), PCLAttackVFX.randomY(hb, variance));
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
        return gunshot(PCLAttackVFX.randomX(target, spread), PCLAttackVFX.randomY(target, spread));
    }

    public static AnimatedParticleEffect gunshot(float cX, float cY) {
        return (AnimatedParticleEffect) new AnimatedParticleEffect(PCLCoreImages.Effects.shot.texture(), cX, cY, 4, 4)
                .setScale(2f);
    }

    public static IntimidateEffect intimidate(Hitbox source) {
        return new IntimidateEffect(source.cX, source.cY);
    }

    public static LaserBeamEffect laser(float cX, float cY) {
        return new LaserBeamEffect(cX, cY);
    }

    public static ShockWaveEffect shockWave(Hitbox source, Color color) {
        return shockWave(source, color, ShockWaveEffect.ShockWaveType.ADDITIVE);
    }

    public static ShockWaveEffect shockWave(Hitbox source, Color color, ShockWaveEffect.ShockWaveType type) {
        return new ShockWaveEffect(source.cX, source.cY, color.cpy(), type);
    }

    public static ColoredSweepingBeamEffect sweepingBeam(AbstractCreature source) {
        return sweepingBeam(source.hb, source.flipHorizontal, Color.CYAN);
    }

    public static ColoredSweepingBeamEffect sweepingBeam(Hitbox source, boolean flipHorizontal, Color color) {
        return new ColoredSweepingBeamEffect(source.cX, source.cY, flipHorizontal, color);
    }

    public static FadingParticleEffect water(Hitbox target, float spread) {
        return water(PCLAttackVFX.randomX(target, spread), PCLAttackVFX.randomY(target, spread));
    }

    public static FadingParticleEffect water(float cX, float cY) {
        return (FadingParticleEffect) FadingParticleEffect.obtain(PCLCoreImages.Effects.waterSplash1.texture(), cX, cY).setColor(Color.WHITE)
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setOpacity(MathUtils.random(0.7f, 1f))
                .setDuration(1.3f, false);
    }

    public static FadingParticleEffect water2(Hitbox target, float spread) {
        return water2(PCLAttackVFX.randomX(target, spread), PCLAttackVFX.randomY(target, spread));
    }

    public static FadingParticleEffect water2(float cX, float cY) {
        return (FadingParticleEffect) FadingParticleEffect.obtain(PCLCoreImages.Effects.waterSplash2.texture(), cX, cY).setColor(Color.WHITE)
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setOpacity(MathUtils.random(0.7f, 1f))
                .setDuration(1.3f, false);
    }

    public static WhirlwindEffect whirlwind() {
        return whirlwind(new Color(0.9F, 0.9F, 1.0F, 1.0F), false);
    }

    public static WhirlwindEffect whirlwind(Color color, boolean reverse) {
        return new WhirlwindEffect(color, reverse);
    }
}
