package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.pcl.PCLCoreImages;

public class SmokeEffect extends PCLEffect {
    public static final TextureCache[] IMAGES = {PCLCoreImages.Effects.smoke1, PCLCoreImages.Effects.smoke2, PCLCoreImages.Effects.smoke3};
    protected int particles = 65;
    protected int sparks = 80;
    protected float x;
    protected float y;
    protected float r = 120;
    protected float vXY = Settings.scale * 150;
    protected float vR = 500;
    protected float s = 1.1f;

    public SmokeEffect(float x, float y) {
        this(x, y, null);
    }

    public SmokeEffect(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        setColor(color);
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        PCLSFX.play(PCLSFX.ATTACK_WHIFF_2);

        for (int i = 0; i < particles; i++) {
            float opac = MathUtils.random(0.64f, 0.8f);
            PCLEffects.Queue.particle(EUIUtils.random(IMAGES).texture(), x, y)
                    .setColor(opac, opac, opac, 1f)
                    .setScale(random(s / 3, s))
                    .setFlip(randomBoolean(0.5f), false)
                    .setRotation(random(-100f, 100f), vR + random(0, vR / 4))
                    .setSpeed(random(-vXY, vXY), random(-vXY, vXY))
                    .setDuration(1f, false);

        }
        if (color != null) {
            for (int i = 0; i < sparks; i++) {
                LightFlareParticleEffect ef = new LightFlareParticleEffect(this.x, this.y, color);
                ef.duration = MathUtils.random(0.8F, 1.55F);
                PCLEffects.Queue.add(ef);
            }
        }

        complete();
    }

    public SmokeEffect setParameters(float vX, float vY) {
        return setParameters(vX, vY, 500, 1.1f);
    }

    public SmokeEffect setParameters(float r, float vXY, float vR, float s) {
        this.r = r;
        this.vXY = vXY;
        this.vR = vR;
        this.s = s;

        return this;
    }

    public SmokeEffect setParameters(float vX, float vY, float vR) {
        return setParameters(vX, vY, vR, 1.1f);
    }

    public SmokeEffect setParticleCount(int particles) {
        this.particles = particles;

        return this;
    }
}
