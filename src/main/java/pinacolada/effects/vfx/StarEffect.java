package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import extendedui.ui.TextureCache;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;

public class StarEffect extends PCLEffect {
    protected static final ArrayList<Float> RGB = new ArrayList<>(3);
    protected static final TextureCache image = PCLCoreImages.Effects.star;

    protected float vfxFrequency = 0.015f;
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float rotationSpeed;
    protected float x;
    protected float y;
    protected float vfxTimer;

    public StarEffect(float x, float y, float horizontalSpeed, float verticalSpeed) {
        this(x, y, horizontalSpeed, verticalSpeed, random(-600f, 600f), random(0.5f, 3.0f));
    }

    public StarEffect(float x, float y, float horizontalSpeed, float verticalSpeed, float rotationSpeed, float scale) {
        super(random(0.5f, 1f));

        this.x = x;
        this.y = y;
        this.scale = scale;
        this.rotation = random(-10f, 10f);
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.rotationSpeed = rotationSpeed;

        if (randomBoolean()) {
            this.rotation *= -1;
        }

        setRandomColor();
    }

    public StarEffect setRandomColor() {
        RGB.clear();
        RGB.add(0.48f);
        RGB.add(1f);
        RGB.add(random(0.48f, 1f));

        this.color = new Color(RGB.remove(random(0, 2)), RGB.remove(random(0, 1)), RGB.remove(0), 0.15f);

        return this;
    }

    public void render(SpriteBatch sb) {
        renderImage(sb, image.texture(), x, y, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        x += horizontalSpeed * deltaTime;
        y += verticalSpeed * deltaTime;
        rotation += rotationSpeed * deltaTime;

        if (scale > 0.3f) {
            scale -= deltaTime * 2f;
        }

        if ((1f - duration) < 0.1f) {
            color.a = Interpolation.fade.apply(0.1f, 1f, (1f - duration) * 10f);
        }
        else {
            color.a = Interpolation.pow2Out.apply(0.1f, 1f, duration);
        }

        vfxTimer -= deltaTime;
        if (vfxTimer < 0f) {
            PCLEffects.Queue.add(new StarParticleEffect(x, y, random(-120f, -30f) * Math.signum(horizontalSpeed), random(-60f, 60f), random(0.01f, 0.28f) * Math.min(1f, this.scale), EUIColors.random(0.83f, 1f, false)));
            if (randomBoolean(0.72f) && this.scale >= 0.7f) {
                PCLEffects.Queue.add(new StarEffect(x, y, horizontalSpeed * -0.25f, random(-horizontalSpeed * 0.05f, horizontalSpeed * 0.05f), random(-1000f, 1000f), random(0.05f, Math.min(0.5f, this.scale))));
            }
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }

    public StarEffect setFrequency(float frequency) {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }
}
