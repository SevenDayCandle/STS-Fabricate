package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.RandomizedList;

public class SnowballParticleEffect extends PCLEffect
{
    protected static final float GRAVITY = 180f * Settings.scale;
    protected static final int SIZE = 96;
    private static final TextureCache[] images = {PCLCoreImages.Effects.frostSnow1, PCLCoreImages.Effects.frostSnow2, PCLCoreImages.Effects.frostSnow3, PCLCoreImages.Effects.frostSnow4};
    private static final RandomizedList<TextureCache> textures = new RandomizedList<>();
    protected float vfxFrequency = 0.75f;
    protected Texture img;
    protected float floor;
    protected float x;
    protected float y;
    protected float vX;
    protected float vY;
    protected float vR;
    protected float vfxTimer;
    protected boolean flip;
    protected boolean enableFloor = true;
    protected boolean hasTrail = false;

    public SnowballParticleEffect(float x, float y, Color color)
    {
        super(random(0.5f, 1f));

        this.img = randomElement(textures, images).texture();
        this.x = x - (float) (SIZE / 2);
        this.y = y - (float) (SIZE / 2);
        this.rotation = random(-10f, 10f);
        this.scale = random(0.2f, 1.5f) * Settings.scale;
        this.vX = random(-650f, 650f) * Settings.scale;
        this.vY = random(-650f, 650f) * Settings.scale;
        this.floor = random(100f, 250f) * Settings.scale;
        this.vR = random(-600f, 600f);
        this.flip = randomBoolean(0.5f);

        setColor(color);
    }

    public SnowballParticleEffect disableFloor()
    {
        enableFloor = false;
        return this;
    }

    public SnowballParticleEffect enableTrail()
    {
        hasTrail = true;
        return this;
    }

    public SnowballParticleEffect setColor(Color color, float variance)
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

    public SnowballParticleEffect setScale(float scale)
    {
        this.scale = scale;

        return this;
    }

    public void render(SpriteBatch sb)
    {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.setColor(this.color);
        sb.draw(this.img, x, y, SIZE * 0.5f, SIZE * 0.5f, SIZE, SIZE, scale, scale, rotation, 0, 0, SIZE, SIZE, flip, false);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        vY += GRAVITY / scale * deltaTime;
        x += vX * deltaTime;
        y += vY * deltaTime;
        rotation += vR * deltaTime;
        if (scale > 0.3f * Settings.scale)
        {
            scale -= deltaTime * 2f;
        }

        if (enableFloor && y < floor)
        {
            vY = -vY * 0.35f;
            y = floor + 0.1f;
            vX *= 1.1f;
        }

        if ((1f - duration) < 0.1f)
        {
            color.a = Interpolation.fade.apply(0f, 1f, (1f - duration) * 10f);
        }
        else
        {
            color.a = Interpolation.pow2Out.apply(0f, 1f, duration);
        }

        if (hasTrail)
        {
            vfxTimer -= deltaTime;
            if (vfxTimer < 0f)
            {
                if (MathUtils.randomBoolean())
                {
                    PCLEffects.Queue.add(new LightFlareParticleEffect(x, y, color));
                }
                vfxTimer = vfxFrequency;
            }
        }

        super.updateInternal(deltaTime);
    }

    public SnowballParticleEffect setSpeed(float vX, float vY)
    {
        this.vX = vX;
        this.vY = vY;
        return this;
    }
}
