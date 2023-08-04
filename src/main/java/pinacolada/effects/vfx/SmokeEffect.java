package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.pcl.PCLCoreImages;

public class SmokeEffect extends PCLEffect {
    public static final TextureCache[] IMAGES = {PCLCoreImages.Effects.smoke1, PCLCoreImages.Effects.smoke2, PCLCoreImages.Effects.smoke3};
    protected int particles = 90;
    protected float x;
    protected float y;
    protected float r = 120;
    protected float vXY = Settings.scale * 180;
    protected float vR = 180;
    protected float s = 1.3f;

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
            float rotation = random(0, 360f);
            PCLEffects.Queue.particle(EUIUtils.random(IMAGES).texture(), x, y)
                    .setColor(this.color.r + MathUtils.random(-0.05F, 0.05F), this.color.g + MathUtils.random(-0.05F, 0.05F), this.color.b + MathUtils.random(-0.05F, 0.05F), 1f)
                    .setScaleTarget(0.2f + MathUtils.random(0, 0.1f), s + MathUtils.random(0, 0.15f), 5f)
                    .setRotation(rotation, random(-vR, vR))
                    .setTargetPosition(x + vXY * MathUtils.cos(rotation), y + vXY * MathUtils.sin(rotation), random(30f, 80f));
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
