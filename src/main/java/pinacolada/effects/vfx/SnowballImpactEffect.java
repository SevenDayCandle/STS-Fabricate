package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;

public class SnowballImpactEffect extends PCLEffect {
    protected int particles = 40;
    protected float x;
    protected float y;

    public SnowballImpactEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.color = Color.SKY.cpy();
    }

    @Override
    protected void firstUpdate() {
        for (int i = 0; i < particles; i++) {
            PCLEffects.Queue.add(new SnowballParticleEffect(this.x, this.y, color).setDuration(0.75f, isRealtime));
            PCLEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, color));
        }

        complete();
    }

    public SnowballImpactEffect setParticleCount(int particles) {
        this.particles = particles;

        return this;
    }
}
