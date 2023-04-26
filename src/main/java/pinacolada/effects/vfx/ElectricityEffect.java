package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;

public class ElectricityEffect extends PCLEffect {
    protected int particles = 60;
    protected float spread = 70;
    protected float jitter = 48;
    protected float x;
    protected float y;

    public ElectricityEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.color = Color.GOLDENROD.cpy();
    }

    @Override
    protected void firstUpdate() {
        PCLEffects.Queue.add(new SparkImpactEffect(this.x, this.y));
        for (int i = 0; i < particles; i++) {
            PCLEffects.Queue.add(new ElectricityParticleEffect(x + random(-spread, spread) * Settings.scale,
                    y + random(-spread, spread) * Settings.scale,
                    jitter * Settings.scale,
                    color)
                    .setDuration(MathUtils.random(0.2F, 1.0F), true));
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
}
