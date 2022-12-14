package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;

public class TornadoParticleEffect extends PCLEffect
{
    protected static final int SIZE = 230;
    protected static final TextureCache[] images = {VFX.IMAGES.airTornado1, VFX.IMAGES.airTornado2};
    protected static final Color PARTICLE_COLOR = Color.LIME.cpy();

    protected float x;
    protected float y;
    protected float tX;
    protected float tY;
    protected float radius;
    protected float radiusSpeed;
    protected float rotationSpeed;
    protected float alpha;
    protected float vfxTimer = 0.3f;
    protected Color secondaryColor;
    protected Texture image;

    public TornadoParticleEffect(float x, float y, float radiusSpeed)
    {
        super(MathUtils.random(0.4F, 0.8F));

        final float offsetX = MathUtils.random(-16.0F, 16.0F) * Settings.scale;
        final float offsetY = MathUtils.random(-16.0F, 16.0F) * Settings.scale;
        if (offsetX > 0.0F)
        {
            this.renderBehind = true;
        }

        this.x = this.tX = x + offsetX;
        this.y = this.tY = y + offsetY;
        this.radius = random(0f, 10f);
        this.radiusSpeed = radiusSpeed * Settings.scale;
        this.scale = random(0.04f, 0.75f);
        this.alpha = random(0.3F, 1.0F);
        this.color = new Color(MathUtils.random(0.9f, 1f), 1f, MathUtils.random(0.9f, 1f), 1);
        this.color.a = this.alpha;
        this.rotation = random(-200f, 200f);
        this.rotationSpeed = random(500f, 800f);
        this.duration = 0.75f;
        this.image = EUIUtils.random(images).texture();

        if (randomBoolean())
        {
            this.rotationSpeed *= -1;
        }
    }

    public void render(SpriteBatch sb)
    {
        renderImage(sb, image, tX, tY, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        vfxTimer -= deltaTime;
        radius += radiusSpeed * deltaTime;
        rotation += rotationSpeed * deltaTime;
        tX = x + radius * MathUtils.cos(rotation);
        tY = y + radius * MathUtils.sin(rotation);

        final float halfDuration = startingDuration * 0.5f;
        if (this.duration < halfDuration)
        {
            this.color.a = Interpolation.exp5In.apply(0.0F, this.alpha, this.duration / halfDuration);
        }

        if (vfxTimer < 0f)
        {
            PCLEffects.Queue.add(new RazorWindParticleEffect(tX, tY,
                    random(-300f, 300f), random(-300f, 300f)));
            PCLEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, PARTICLE_COLOR));
            vfxTimer = random(0.3f, 3f);
        }
    }
}
