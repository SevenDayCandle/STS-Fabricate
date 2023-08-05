package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

public class SnowballImpactEffect extends PCLEffect {
    public static final TextureCache[] images = {PCLCoreImages.Effects.frostSnow1, PCLCoreImages.Effects.frostSnow2, PCLCoreImages.Effects.frostSnow3, PCLCoreImages.Effects.frostSnow4};
    protected int particles = 40;
    protected float x;
    protected float y;

    public SnowballImpactEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.color = Color.SKY.cpy();
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        for (int i = 0; i < particles; i++) {
            PCLEffects.Queue.particle(EUIUtils.random(images).texture(), this.x, this.y)
                    .setSpeed(random(-500f, 500f) * Settings.scale, random(-500f, 500f) * Settings.scale)
                    .setRotation(random(-100f, 100f), random(-600f, 600f))
                    .setScale(random(0.4f, 1f))
                    .setFlip(randomBoolean(0.5f), false)
                    .setColor(color)
                    .setBlendingMode(EUIRenderHelpers.BlendingMode.Glowing)
                    .setDuration(1f, false);
            PCLEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, color));
        }

        complete();
    }

    public SnowballImpactEffect setParticleCount(int particles) {
        this.particles = particles;

        return this;
    }
}
