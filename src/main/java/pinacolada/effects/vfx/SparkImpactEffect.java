package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.VFX;
import pinacolada.utilities.GameEffects;
import pinacolada.utilities.PCLRenderHelpers;

public class SparkImpactEffect extends PCLEffect
{
    private static final TextureCache[] images = {VFX.IMAGES.spark1, VFX.IMAGES.spark2};
    protected int particles = 60;
    protected float radius = 400;
    protected float x;
    protected float y;

    public SparkImpactEffect(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.color = Color.GOLDENROD.cpy();
    }

    @Override
    protected void firstUpdate()
    {
        for (int i = 0; i < particles; i++)
        {
            float r = MathUtils.random(0, 360);
            GameEffects.Queue.add(new FadingParticleEffect(EUIUtils.random(images).texture(), x, y)
                    .setColor(EUIColors.random(0.5f, 1f, false))
                    .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                            .setScale(scale * MathUtils.random(0.04f, 0.55f)).setRotation(0f, 1440f)
                            .setTargetPosition(x + radius * MathUtils.cos(r), y + radius * MathUtils.sin(r))
                    ).setDuration(0.8f, false)
                    .setDuration(MathUtils.random(0.5F, 1.0F), true);
            GameEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, color));
        }

        complete();
    }

    public SparkImpactEffect setParticleCount(int particles)
    {
        this.particles = particles;

        return this;
    }

    public SparkImpactEffect setRadius(float radius)
    {
        this.radius = radius;

        return this;
    }
}
