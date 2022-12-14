package pinacolada.effects.vfx;


import com.badlogic.gdx.math.MathUtils;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.GameEffects;

public class ShootingStarsEffect extends PCLEffect
{
    protected float x;
    protected float y;
    protected float spreadX;
    protected float spreadY;
    protected float vfxTimer;
    protected float vfxFrequency = 0.020f;
    protected float horizontalSpeedMin = 2150;
    protected float horizontalSpeedMax = 2650;
    protected float verticalSpeedMin;
    protected float verticalSpeedMax;
    protected boolean flipHorizontally;

    public ShootingStarsEffect(float startX, float startY)
    {
        super(0.5f);

        this.x = startX;
        this.y = startY;
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        if (flipHorizontally)
        {
            horizontalSpeedMin = -horizontalSpeedMin;
            horizontalSpeedMax = -horizontalSpeedMax;
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        vfxTimer -= deltaTime;
        if (vfxTimer < 0f)
        {
            final float x = this.x + random(-spreadX, spreadX);
            final float y = this.y + random(-spreadY, spreadY);
            final float h_speed = random(horizontalSpeedMin, horizontalSpeedMax);
            final float v_speed = random(verticalSpeedMin, verticalSpeedMax);
            GameEffects.Queue.add(new StarEffect(x, y, h_speed, v_speed));
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }

    public ShootingStarsEffect flipHorizontally(boolean flip)
    {
        this.flipHorizontally = flip;

        return this;
    }

    public ShootingStarsEffect setFrequency(float frequency)
    {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public ShootingStarsEffect setHorizontalSpeed(float min, float max)
    {
        this.horizontalSpeedMin = min;
        this.horizontalSpeedMax = max;

        return this;
    }

    public ShootingStarsEffect setSpread(float spreadX, float spreadY)
    {
        this.spreadX = spreadX;
        this.spreadY = spreadY;

        return this;
    }

    public ShootingStarsEffect setVerticalSpeed(float min, float max)
    {
        this.horizontalSpeedMin = min;
        this.horizontalSpeedMax = max;

        return this;
    }
}
