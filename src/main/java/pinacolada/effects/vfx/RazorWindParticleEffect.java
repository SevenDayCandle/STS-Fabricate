package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.resources.pcl.PCLCoreImages;

public class RazorWindParticleEffect extends PCLEffect
{
    protected static final int SIZE = 96;
    protected static final TextureCache[] images = {PCLCoreImages.Effects.airTrail1, PCLCoreImages.Effects.airTrail2, PCLCoreImages.Effects.airTrail3};

    protected float x;
    protected float y;
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float rotationSpeed;
    protected float alpha;
    protected Texture image;

    public RazorWindParticleEffect(float x, float y, float horizontalSpeed, float verticalSpeed)
    {
        super(MathUtils.random(0.4F, 0.8F));

        final float offsetX = MathUtils.random(-16.0F, 16.0F) * Settings.scale;
        final float offsetY = MathUtils.random(-16.0F, 16.0F) * Settings.scale;
        if (offsetX > 0.0F)
        {
            this.renderBehind = true;
        }

        this.x = x + offsetX;
        this.y = y + offsetY;
        this.horizontalSpeed = horizontalSpeed * Settings.scale;
        this.verticalSpeed = verticalSpeed * Settings.scale;
        this.scale = random(0.04f, 0.6f);
        this.alpha = random(0.3F, 1.0F);
        this.color = new Color(MathUtils.random(0.8f, 1f), 1f, MathUtils.random(0.8f, 1f), 1);
        this.color.a = this.alpha;
        this.rotation = random(-10f, 10f);
        this.rotationSpeed = random(500f, 800f);
        this.image = EUIUtils.random(images).texture();

        if (randomBoolean())
        {
            this.rotationSpeed *= -1;
        }
    }

    public void render(SpriteBatch sb)
    {
        renderImage(sb, image, x, y, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        x += horizontalSpeed * deltaTime;
        y += verticalSpeed * deltaTime;
        rotation += rotationSpeed * deltaTime;

        final float halfDuration = startingDuration * 0.5f;
        if (this.duration < halfDuration)
        {
            this.color.a = Interpolation.exp5In.apply(0.0F, this.alpha, this.duration / halfDuration);
        }
    }
}
