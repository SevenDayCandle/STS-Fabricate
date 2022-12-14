package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.SFX;
import pinacolada.utilities.GameEffects;

public class ScreenFreezingEffect extends PCLEffect
{
    private static final float INTERVAL = 0.05F;
    private static final int TIMES = 9;
    private float timer = 0.0F;

    public ScreenFreezingEffect()
    {
        this.color = Color.SKY.cpy();
        this.duration = 3.0F;
        this.startingDuration = this.duration;
    }

    public void makeSnowflake()
    {
        GameEffects.Queue.add(new SnowballParticleEffect(MathUtils.random(0.0F, (float) Settings.WIDTH), MathUtils.random(900.0F, 1100.0F) * Settings.scale, color)
                .setSpeed(MathUtils.random(-70.0F, 70.0F) * Settings.scale, MathUtils.random(-1100.0F, -450.0F) * Settings.scale)
                .enableTrail()
                .setRealtime(isRealtime));
    }

    public void render(SpriteBatch sb)
    {
    }

    public void update()
    {
        if (this.duration == this.startingDuration)
        {
            SFX.play(SFX.HEAL_3);
            GameEffects.Queue.add(new BorderLongFlashEffect(Color.NAVY));
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F)
        {
            for (int i = 0; i < TIMES; i++)
            {
                makeSnowflake();
            }
            this.timer = 0.05F;
        }

        if (this.duration < 0.0F)
        {
            this.isDone = true;
        }

    }

    public void dispose()
    {
    }
}
