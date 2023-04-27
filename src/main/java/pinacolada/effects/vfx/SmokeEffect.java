package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;

public class SmokeEffect extends PCLEffect {
    protected int particles = 90;
    protected float x;
    protected float y;
    protected float r = 120;
    protected float vXY = 2.2f;
    protected float vR = 180;
    protected float s = 0.75f;

    public SmokeEffect(float x, float y) {
        this(x, y, Color.GRAY);
    }

    public SmokeEffect(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        setColor(color);
    }

    @Override
    protected void firstUpdate() {
        PCLSFX.play(PCLSFX.ATTACK_WHIFF_2);

        for (int i = 0; i < particles; ++i) {
            PCLEffects.Queue.add(new SmokeParticleEffect(x, y, r, vXY, vR, s, color.cpy()));
        }

        complete();
    }

    public SmokeEffect setParameters(float vX, float vY) {
        return setParameters(vX, vY, 250, 0.75f);
    }

    public SmokeEffect setParameters(float r, float vXY, float vR, float s) {
        this.r = r;
        this.vXY = vXY;
        this.vR = vR;
        this.s = s;

        return this;
    }

    public SmokeEffect setParameters(float vX, float vY, float vR) {
        return setParameters(vX, vY, vR, 0.75f);
    }

    public SmokeEffect setParticleCount(int particles) {
        this.particles = particles;

        return this;
    }
}
