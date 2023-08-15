package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;
import extendedui.interfaces.delegates.ActionT1;

public class TrailingParticleEffect extends FadingParticleEffect {
    private static final TrailingParticleEffect.ParticlePool trailPool = new TrailingParticleEffect.ParticlePool();
    private ActionT1<TrailingParticleEffect> onTrail;
    protected float vfxTimer;
    protected float vfxFrequencyMax = 0.6f;
    protected float vfxFrequencyMin = 0.2f;

    // Pool only
    protected TrailingParticleEffect() {
        super();
    }

    public static TrailingParticleEffect obtainTrail(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y) {
        return obtainTrail(texture, onTrail, x, y, 0, 1);
    }

    public static TrailingParticleEffect obtainTrail(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
        return trailPool.obtain(texture, onTrail, x, y, rot, scale);
    }

    @Override
    protected void free() {
        trailPool.free(this);
    }

    protected void initialize(Texture texture, ActionT1<TrailingParticleEffect> onTrail, float x, float y, float rot, float scale) {
        super.initialize(texture, x, y, rot, scale);
        this.onTrail = onTrail;
        this.vfxFrequencyMax = 0.6f;
        this.vfxTimer = this.vfxFrequencyMin = 0.2f;
    }

    public TrailingParticleEffect setVFXFrequency(float min, float max) {
        this.vfxTimer = vfxFrequencyMin = min;
        vfxFrequencyMax = max;
        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);

        vfxTimer -= deltaTime;
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
