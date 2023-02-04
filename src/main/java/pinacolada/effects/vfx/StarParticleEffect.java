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
import pinacolada.utilities.PCLRenderHelpers;

public class StarParticleEffect extends PCLEffect
{
    protected static final TextureCache[] images = {PCLCoreImages.Effects.sparkle1, PCLCoreImages.Effects.sparkle2, PCLCoreImages.Effects.sparkle3, PCLCoreImages.Effects.sparkle4};

    protected float x;
    protected float y;
    protected float horizontalSpeed;
    protected float verticalSpeed;
    protected float rotationSpeed;
    protected float alpha;
    protected float halfDuration;
    protected boolean flipX;
    protected boolean translucent;
    protected Texture image;

    public StarParticleEffect(float x, float y, float horizontalSpeed, float verticalSpeed, float scale, Color mainColor)
    {
        super(MathUtils.random(0.4F, 0.8F));

        final float offsetX = MathUtils.random(-12.0F, 12.0F) * Settings.scale;
        final float offsetY = MathUtils.random(-12.0F, 12.0F) * Settings.scale;
        if (offsetX > 0.0F)
        {
            this.renderBehind = true;
        }

        this.x = x + offsetX;
        this.y = y + offsetY;
        this.horizontalSpeed = horizontalSpeed * Settings.scale;
        this.verticalSpeed = verticalSpeed * Settings.scale;
        this.image = EUIUtils.random(images).texture();
        this.color = mainColor.cpy();
        this.color.a = this.alpha = random(0.5F, 1.0F);
        this.scale = scale;
        this.flipX = randomBoolean();
        this.rotation = random(-10f, 10f);
        this.rotationSpeed = random(-12f, 12f);
        this.translucent = randomBoolean();
        this.halfDuration = startingDuration * 0.5f;
    }

    public void render(SpriteBatch sb)
    {
        renderImage(sb, image, x, y, flipX, false, this.translucent ? PCLRenderHelpers.BlendingMode.Glowing : PCLRenderHelpers.BlendingMode.Normal);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        this.rotation += this.rotationSpeed;
        if (this.duration < halfDuration)
        {
            this.color.a = Interpolation.exp5In.apply(0.0F, this.alpha, this.duration / halfDuration);
        }
    }
}
