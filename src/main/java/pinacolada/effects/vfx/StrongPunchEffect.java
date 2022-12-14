package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.VFX;

public class StrongPunchEffect extends PCLEffect
{
    public static final TextureCache image = VFX.IMAGES.punch;

    protected float x;
    protected float y;
    protected float rotationSpeed = 600f;
    protected float vfxTimer = 1;
    protected float baseScale;
    protected boolean triggered = false;

    public StrongPunchEffect(float x, float y, float baseScale)
    {
        super(1f);

        this.x = x;
        this.y = y;
        this.scale = this.baseScale = Math.max(baseScale, 1);
        this.rotation = 300f;
        this.color = Color.WHITE.cpy();
    }

    public void render(SpriteBatch sb)
    {
        renderImage(sb, image.texture(), x, y, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {

        if ((1f - duration) < 0.1f)
        {
            color.a = Interpolation.fade.apply(0.1f, 1f, (1f - duration) * 7f);
        }
        else
        {
            color.a = Interpolation.pow2Out.apply(0.1f, 1f, duration);
        }

        vfxTimer -= deltaTime / duration;
        if (vfxTimer < 0f)
        {
            if (!triggered)
            {
                PCLEffects.Queue.add(VFX.whack(x, y).setColor(Color.SCARLET)).setScale(2);
                SFX.play(SFX.PCL_PUNCH, 0.7f, 0.8f);
                triggered = true;
            }
            else
            {
                x += Interpolation.sine.apply(-25f, 25f, this.duration * 50);
                y += Interpolation.sine.apply(-25f, 25f, this.duration * 50);
            }
        }
        else
        {
            this.rotation += rotationSpeed * deltaTime / duration;
            this.scale = Interpolation.linear.apply(1, this.baseScale, duration);
        }

        super.updateInternal(deltaTime);
    }
}
