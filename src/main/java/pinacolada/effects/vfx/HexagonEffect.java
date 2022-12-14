package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.utilities.PCLRenderHelpers;

public class HexagonEffect extends PCLEffect
{
    protected static final int SIZE = 96;
    private static final TextureCache image = VFX.IMAGES.hexagon;
    protected float vfxFrequency = 0.03f;
    protected Texture img;
    protected float x;
    protected float y;
    protected float vX;
    protected float vY;
    protected float vR;
    protected float flashFrequency;
    protected float vfxTimer;
    protected boolean flip;

    public HexagonEffect(float x, float y, Color color)
    {
        super(random(0.5f, 1f));

        this.img = image.texture();
        this.x = x - (float) (SIZE / 2);
        this.y = y - (float) (SIZE / 2);
        this.scale = 1f;
        this.vX = 1400f * Settings.scale;
        this.vR = random(-600f, 600f);
        this.flashFrequency = this.duration * 7f;

        setColor(color, 0.28f);
    }

    public HexagonEffect setColor(Color color, float variance)
    {
        this.color = color.cpy();
        this.color.a = 0;

        if (variance > 0)
        {
            this.color.r = Math.max(0, color.r - random(0, variance));
            this.color.g = Math.max(0, color.g - random(0, variance));
            this.color.b = Math.max(0, color.b - random(0, variance));
        }

        return this;
    }

    public HexagonEffect setFlashFrequency(float flashFrequency)
    {
        this.flashFrequency = flashFrequency;

        return this;
    }

    public HexagonEffect setScale(float scale)
    {
        this.scale = scale;

        return this;
    }

    public void render(SpriteBatch sb)
    {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.setColor(this.color);
        sb.draw(this.img, x, y, 0, 0, SIZE, SIZE, scale, scale, rotation, 0, 0, SIZE, SIZE, flip, false);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        x += vX * deltaTime;
        y += vY * deltaTime;
        rotation += vR * deltaTime;

        if ((1f - duration) < 0.1f)
        {
            color.a = Interpolation.fade.apply(0f, 1f, (1f - duration) * 10f);
        }
        else
        {
            color.a = Interpolation.sine.apply(0.8f, 1f, this.flashFrequency);
            vfxTimer -= deltaTime;
            if (vfxTimer < 0f)
            {
                PCLEffects.Queue.add(new FadingParticleEffect(this.img, this.x + SIZE * MathUtils.cos(rotation), this.y + SIZE * MathUtils.sin(rotation))
                        .setOpacity(0.75f)
                        .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                        .setColor(this.color.cpy())
                                .setRotation(36000f, vR)
                                .setScale(scale * 0.75f, 0f, 3f))
                        .setDuration(1f, true);
                vfxTimer = vfxFrequency;
            }
        }

        super.updateInternal(deltaTime);
    }

    public HexagonEffect setSpeed(float vX, float vY, float vR)
    {
        this.vX = vX;
        this.vY = vY;
        this.vR = vR;
        return this;
    }
}
