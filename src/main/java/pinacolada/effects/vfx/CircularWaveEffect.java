package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class CircularWaveEffect extends PCLEffect {
    protected float x;
    protected float y;
    protected float scaleLower = 0.2f;
    protected float scaleUpper = 3.10f;
    protected float vfxTimer;
    protected float vfxFrequency = 0.2f;
    protected Color endColor;

    public CircularWaveEffect(float startX, float startY) {
        super(1.0f);

        this.x = startX;
        this.y = startY;
        this.color = Color.WHITE;
        this.endColor = Color.LIME;
    }

    public CircularWaveEffect setColors(Color startColor, Color endColor) {
        this.color = startColor;
        this.endColor = endColor;
        return this;
    }

    public CircularWaveEffect setFrequency(float frequency) {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public CircularWaveEffect setScale(float scaleLower, float scaleUpper) {
        this.scaleLower = scaleLower;
        this.scaleUpper = scaleUpper;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        vfxTimer -= deltaTime;

        if (vfxTimer < 0f) {
            PCLEffects.Queue.particle(PCLCoreImages.Effects.circle2.texture(), x, y)
                            .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                            .setColor(color, endColor, 2f)
                            .setScaleTarget(scaleLower, scaleUpper, 1f)
                    .setDuration(startingDuration * 1.5f, true);
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }
}
