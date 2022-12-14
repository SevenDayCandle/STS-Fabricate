package pinacolada.monsters.animations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class PCLIntervalAnimation extends PCLAnimation
{
    protected float vfxTimerStartMin = 0.3F;
    protected float vfxTimerStartMax = 0.4F;

    public PCLIntervalAnimation()
    {
        this(0.3f, 0.4f);
    }

    public PCLIntervalAnimation(float vfxTimer)
    {
        this(vfxTimer, vfxTimer);
    }

    public PCLIntervalAnimation(float vfxTimerStartMin, float vfxTimeStartMax)
    {
        this.vfxTimerStartMin = vfxTimerStartMin;
        this.vfxTimerStartMax = Math.max(vfxTimerStartMin, vfxTimeStartMax);
        this.vfxTimer = MathUtils.random(this.vfxTimerStartMin, this.vfxTimerStartMax);
    }

    public void setFlip(boolean horizontal, boolean vertical)
    {
        hFlip = horizontal;
        vFlip = vertical;
    }

    public void update(float deltaTime, float x, float y)
    {
        this.vfxTimer -= deltaTime;
        if (this.vfxTimer < 0.0F)
        {
            updateImpl(x, y);
            this.vfxTimer = MathUtils.random(vfxTimerStartMin, vfxTimerStartMax);
        }
    }

    public void updateImpl(float x, float y)
    {

    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {

    }

    @Override
    public Type type()
    {
        return Type.SPRITE;
    }
}
