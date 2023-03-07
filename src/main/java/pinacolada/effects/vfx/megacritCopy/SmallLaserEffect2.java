package pinacolada.effects.vfx.megacritCopy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pinacolada.effects.PCLEffect;

@SuppressWarnings("FieldCanBeLocal")
@Deprecated
public class SmallLaserEffect2 extends PCLEffect
{
    private static AtlasRegion img;
    private final float sX;
    private final float sY;
    private final float dX;
    private final float dY;
    private final float dst;
    private final Color color2;

    public SmallLaserEffect2(float sX, float sY, float dX, float dY)
    {
        super(0.5f);

        if (img == null)
        {
            img = ImageMaster.vfxAtlas.findRegion("combat/laserThin");
        }

        this.sX = sX;
        this.sY = sY;
        this.dX = dX;
        this.dY = dY;
        this.dst = Vector2.dst(this.sX, this.sY, this.dX, this.dY) / Settings.scale;
        this.color = Color.CYAN.cpy();
        this.color2 = new Color(0.3f, 0.3f, 1f, this.color.a);
        this.rotation = MathUtils.atan2(dX - sX, dY - sY);
        this.rotation *= 57.295776f;
        this.rotation = -this.rotation + 90f;
    }

    public SmallLaserEffect2 setColors(Color color1, Color color2)
    {
        this.color.set(color1);
        this.color2.set(color2);

        return this;
    }

    public void render(SpriteBatch sb)
    {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(img, this.sX, this.sY - (float) img.packedHeight / 2f + 10f * Settings.scale, 0f, (float) img.packedHeight / 2f, this.dst, 50f, this.scale + random(-0.01f, 0.01f), this.scale, this.rotation);
        color2.a = color.a;
        sb.setColor(color2);
        sb.draw(img, this.sX, this.sY - (float) img.packedHeight / 2f, 0f, (float) img.packedHeight / 2f, this.dst, random(50f, 90f), this.scale + random(-0.02f, 0.02f), this.scale, this.rotation);
        sb.setBlendFunction(770, 771);
    }

    public void update()
    {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > this.startingDuration / 2f)
        {
            this.color.a = Interpolation.pow2In.apply(1f, 0f, (this.duration - 0.25f) * 4f);
        }
        else
        {
            this.color.a = Interpolation.bounceIn.apply(0f, 1f, this.duration * 4f);
        }

        if (this.duration < 0f)
        {
            this.isDone = true;
        }
    }
}
