package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRenderHelpers;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

public class ElectricityEffect extends VisualEffect {
    public static final TextureCache[] images = {
            PCLCoreImages.Effects.electric1,
            PCLCoreImages.Effects.electric2,
            PCLCoreImages.Effects.electric3,
            PCLCoreImages.Effects.electric4,
            PCLCoreImages.Effects.electric5,
            PCLCoreImages.Effects.electric6,
            PCLCoreImages.Effects.electric7
    };
    protected int particles = 60;
    protected float spread = 70 * Settings.scale;
    protected float jitter = 64 * Settings.scale;
    protected float variance = 0.35f;

    public ElectricityEffect(float x, float y) {
        super(x, y);
        this.color = Color.GOLDENROD.cpy();
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        PCLEffects.Queue.add(new SparkImpactEffect(this.x, this.y));
        for (int i = 0; i < particles; i++) {
            PCLEffects.Queue.trail(randomTexture(images), this::onTrail, x + random(-spread, spread), y + random(-spread, spread))
                    .setVFXFrequency(0.0041f, 0.005f)
                    .setScale(random(0.1f, 0.8f))
                    .setRotation(random(-10f, 10f), random(-700f, 700f))
                    .setFlip(randomBoolean(0.5f), randomBoolean(0.5f))
                    .setColor(Math.max(0, color.r - random(0, variance)), Math.max(0, color.g - random(0, variance)), Math.max(0, color.b - random(0, variance)), 1f)
                    .setBlendingMode(EUIRenderHelpers.BlendingMode.Glowing)
                    .setDuration(MathUtils.random(0.2F, 1.0F), true);
        }

        complete();
    }

    public ElectricityEffect setJitter(float jitter) {
        this.jitter = jitter;

        return this;
    }

    public ElectricityEffect setParticleCount(int particles) {
        this.particles = particles;

        return this;
    }

    public ElectricityEffect setSpread(float spread) {
        this.spread = spread;

        return this;
    }

    protected void onTrail(TrailingParticleEffect effect) {
        effect.texture = randomTexture(images);
        effect.x = x + random(-jitter, jitter);
        effect.y = y + random(-jitter, jitter);
    }
}
