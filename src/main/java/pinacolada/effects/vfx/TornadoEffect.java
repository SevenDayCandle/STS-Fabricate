package pinacolada.effects.vfx;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.GameEffects;

public class TornadoEffect extends PCLEffect
{
    protected float x;
    protected float y;
    protected float spreadX = 10f * Settings.scale;
    protected float spreadY = 10f * Settings.scale;
    protected float scaleLower = 0.2f;
    protected float scaleUpper = 1f;
    protected float vfxTimer;
    protected float vfxFrequency = 0.01f;

    public TornadoEffect(float startX, float startY)
    {
        super(0.5f);

        this.x = startX;
        this.y = startY;
    }

    public TornadoEffect setFrequency(float frequency)
    {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public TornadoEffect setScale(float scaleLower, float scaleUpper)
    {
        this.scaleLower = scaleLower;
        this.scaleUpper = scaleUpper;

        return this;
    }

    public TornadoEffect setSpread(float spreadX, float spreadY)
    {
        this.spreadX = spreadX;
        this.spreadY = spreadY;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        vfxTimer -= deltaTime;

        if (vfxTimer < 0f)
        {
            final float x = this.x + random(-spreadX, spreadX);
            final float y = this.y + random(-spreadY, spreadY);
            GameEffects.Queue.add(new TornadoParticleEffect(x, y,
                    random(15f, 39f) * Settings.scale));
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }
}
