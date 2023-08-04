package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class RazorWindEffect extends PCLEffect {
    protected static final Color PARTICLE_COLOR = Color.LIME.cpy();
    public static final TextureCache image = PCLCoreImages.Effects.airSlice;
    protected float x;
    protected float y;
    protected float targetY;
    protected float horizontalAcceleration;
    protected float horizontalSpeed;
    protected float rotationSpeed;
    protected float vfxTimer;
    protected float vfxFrequency;

    public RazorWindEffect(float x, float y, float targetY, float horizontalSpeed, float horizontalAcceleration) {
        super(1f);

        this.x = x;
        this.y = y;
        this.targetY = targetY;
        this.scale = 0.75f;
        this.horizontalSpeed = horizontalSpeed * Settings.scale;
        this.horizontalAcceleration = horizontalSpeed * Settings.scale;
        this.rotation = random(5f, 10f);
        this.rotationSpeed = random(1000f, 1200f);
        this.color = Color.WHITE.cpy();
        this.vfxFrequency = 0.01f;
    }

    public void render(SpriteBatch sb) {
        renderImage(sb, image.texture(), x, y, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        x += horizontalSpeed * deltaTime;
        y = Interpolation.pow2OutInverse.apply(y, targetY, Math.min(1f, (1f - duration) / 2f));
        horizontalSpeed += horizontalAcceleration * deltaTime;
        rotation += rotationSpeed * deltaTime;

        if ((1f - duration) < 0.1f) {
            color.a = Interpolation.fade.apply(0.1f, 1f, (1f - duration) * 7f);
        }
        else {
            color.a = Interpolation.pow2Out.apply(0.1f, 1f, duration);
        }

        vfxTimer -= deltaTime;
        if (vfxTimer < 0f) {
            PCLEffects.Queue.add(new RazorWindParticleEffect(x, y + (random(-100, 100) * Settings.scale),
                    random(-300f, -50f) * Math.signum(horizontalSpeed), random(-200f, 200f)));
            PCLEffects.Queue.particle(image.texture(), x, y)
                    .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                    .setOpacity(0.5f)
                    .setColor(new Color(MathUtils.random(0.8f, 1f), 1f, MathUtils.random(0.8f, 1f), 0.5f))
                    .setRotation(rotation)
                    .setScale(scale).setRotation(0, random(300f, 400f));
            PCLEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, PARTICLE_COLOR));
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }

    public RazorWindEffect setFrequency(float frequency) {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }
}
