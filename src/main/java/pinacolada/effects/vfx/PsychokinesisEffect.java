package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

public class PsychokinesisEffect extends PCLEffect {
    protected float x;
    protected float y;
    protected float spreadX = 10f * Settings.scale;
    protected float spreadY = 10f * Settings.scale;
    protected float spreadGrowth = 2.2f * Settings.scale;
    protected float scaleLower = 0.4f;
    protected float scaleUpper = 1.4f;
    protected float scaleGrowth = -0.020f;
    protected float vfxTimer;
    protected float vfxFrequency = 0.04f;
    protected float vfxFrequencyGrowth = -0.006f;

    public PsychokinesisEffect(float startX, float startY) {
        super(0.5f);

        this.x = startX;
        this.y = startY;
    }

    public PsychokinesisEffect setFrequency(float frequency) {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public PsychokinesisEffect setScale(float scaleLower, float scaleUpper) {
        this.scaleLower = scaleLower;
        this.scaleUpper = scaleUpper;

        return this;
    }

    public PsychokinesisEffect setSpread(float spreadX, float spreadY) {
        this.spreadX = spreadX;
        this.spreadY = spreadY;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        vfxTimer -= deltaTime;
        spreadX += spreadGrowth;
        spreadY += spreadGrowth;
        scaleLower += scaleGrowth;
        scaleUpper += scaleGrowth;

        if (vfxTimer < 0f) {
            final float x = this.x + random(-spreadX, spreadX);
            final float y = this.y + random(-spreadY, spreadY);
            final float scale = random(scaleLower, this.scaleUpper);
            final Color color = new Color(MathUtils.random(0.8f, 1f), MathUtils.random(0.7f, 1f), 1, 1);
            if (randomBoolean(0.2f)) {
                PCLEffects.Queue.particle(PCLCoreImages.Effects.circle.texture(), x, y)
                        .setColor(EUIColors.random(0.83f, 1f, false))
                        .setScaleTarget(this.scaleLower * 0.05f, scale * 0.2f, 5f);
            }
            else {
                PCLEffects.Queue.add(new AnimatedParticleEffect(PCLCoreImages.Effects.psi.texture(), x, y, 5, 5, 0.011f)
                        .setScaleTarget(this.scaleLower * 0.05f, scale, 5f)
                        .setColor(EUIColors.random(0.83f, 1f, false)));
            }

            vfxFrequency += vfxFrequencyGrowth;
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }
}
