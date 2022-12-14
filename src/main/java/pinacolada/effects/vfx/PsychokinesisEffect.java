package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.VFX;
import pinacolada.utilities.GameEffects;

public class PsychokinesisEffect extends PCLEffect
{
    protected float x;
    protected float y;
    protected float spreadX = 10f * Settings.scale;
    protected float spreadY = 10f * Settings.scale;
    protected float spreadGrowth = 2.2f * Settings.scale;
    protected float scaleLower = 0.2f;
    protected float scaleUpper = 1.35f;
    protected float scaleGrowth = -0.020f;
    protected float vfxTimer;
    protected float vfxFrequency = 0.04f;
    protected float vfxFrequencyGrowth = -0.006f;

    public PsychokinesisEffect(float startX, float startY)
    {
        super(0.5f);

        this.x = startX;
        this.y = startY;
    }

    public PsychokinesisEffect setFrequency(float frequency)
    {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public PsychokinesisEffect setScale(float scaleLower, float scaleUpper)
    {
        this.scaleLower = scaleLower;
        this.scaleUpper = scaleUpper;

        return this;
    }

    public PsychokinesisEffect setSpread(float spreadX, float spreadY)
    {
        this.spreadX = spreadX;
        this.spreadY = spreadY;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        vfxTimer -= deltaTime;
        spreadX += spreadGrowth;
        spreadY += spreadGrowth;
        scaleLower += scaleGrowth;
        scaleUpper += scaleGrowth;

        if (vfxTimer < 0f)
        {
            final float x = this.x + random(-spreadX, spreadX);
            final float y = this.y + random(-spreadY, spreadY);
            final float scale = random(Math.max(0.05f, this.scaleLower), this.scaleUpper);
            final Color color = new Color(MathUtils.random(0.8f, 1f), MathUtils.random(0.7f, 1f), 1, 1);
            if (randomBoolean(0.2f))
            {
                GameEffects.Queue.add(new FadingParticleEffect(VFX.IMAGES.circle.texture(), x, y)
                                .setColor(EUIColors.random(0.83f, 1f, false))
                                .setScale(this.scaleLower * 0.05f, scale * 2, 5f))
                        .setDuration(1.5f, true);
            }
            else
            {
                GameEffects.Queue.add(new AnimatedParticleEffect(VFX.IMAGES.psi.texture(), x, y, 5, 5, 0.01f)
                        .setColor(EUIColors.random(0.83f, 1f, false))
                        .setScale(this.scaleLower * 0.05f, scale, 5f));
            }

            vfxFrequency += vfxFrequencyGrowth;
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }
}
