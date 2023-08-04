package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.effects.PCLEffects;

public class TrailingParticleEffect extends FadingParticleEffect {
    private static final TrailingParticleEffect.ParticlePool effectPool = new TrailingParticleEffect.ParticlePool();
    private ActionT1<TrailingParticleEffect> onTrail;
    protected float vfxTimer;
    protected float vfxFrequencyMax = 3f;
    protected float vfxFrequencyMin = 0.3f;

    // Pool only
    protected TrailingParticleEffect() {
        super();
    }

    public static TrailingParticleEffect obtain(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y) {
        return obtain(texture, onTrail, x, y,0, 1);
    }

    public static TrailingParticleEffect obtain(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
        return effectPool.obtain(texture, onTrail, x, y, rot, scale);
    }

    protected void initialize(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
        super.initialize(texture, x, y, rot, scale);
        this.onTrail = onTrail;
    }

    public TrailingParticleEffect setVFXFrequency(float min, float max) {
        vfxFrequencyMin = min;
        vfxFrequencyMax = max;
        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);

        if (vfxTimer < 0f && onTrail != null) {
            onTrail.invoke(this);
            vfxTimer = random(vfxFrequencyMin, vfxFrequencyMax);
        }
    }

    protected static class ParticlePool extends Pool<TrailingParticleEffect> {

        public ParticlePool() {
            super(40, 3000);
        }

        @Override
        protected TrailingParticleEffect newObject() {
            return new TrailingParticleEffect();
        }

        public TrailingParticleEffect obtain(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
            TrailingParticleEffect particle = obtain();
            particle.initialize(texture, onTrail, x, y, rot, scale);
            return particle;
        }
    }
}
