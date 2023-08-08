package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;

public class TornadoEffect extends VisualEffect {
    public static final TextureCache[] IMAGES_AIR = {PCLCoreImages.Effects.airTrail1, PCLCoreImages.Effects.airTrail2, PCLCoreImages.Effects.airTrail3};
    public static final TextureCache[] IMAGES_WIND = {PCLCoreImages.Effects.airTornado1, PCLCoreImages.Effects.airTornado2};
    protected float spreadX = 10f * Settings.scale;
    protected float spreadY = 10f * Settings.scale;
    protected float vfxTimer;
    protected float vfxFrequency = 0.01f;

    public TornadoEffect(float startX, float startY) {
        super(startX, startY);
    }

    protected void onTrail(TrailingParticleEffect effect) {
        PCLEffects.Queue.add(new LightFlareParticleEffect(this.x, this.y, Color.LIME.cpy()));
    }

    public TornadoEffect setFrequency(float frequency) {
        this.vfxFrequency = MathUtils.clamp(frequency, 0.01f, startingDuration / 5f);

        return this;
    }

    public TornadoEffect setParticleSpread(float spreadX, float spreadY) {
        this.spreadX = spreadX;
        this.spreadY = spreadY;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        vfxTimer -= deltaTime;

        if (vfxTimer < 0f) {
            float pX = this.x + random(-spreadX, spreadX);
            float pY = this.y + random(-spreadY, spreadY);
            final float speed = random(15f, 39f) * Settings.scale;
            PCLEffects.Queue.trail(randomTexture(IMAGES_WIND), this::onTrail, pX, pY)
                    .setBlendingMode(EUIRenderHelpers.BlendingMode.Glowing)
                    .setColor(new Color(MathUtils.random(0.9f, 1f), 1f, MathUtils.random(0.9f, 1f), 1))
                    .setSpeed(speed, speed)
                    .setRotation(random(-300f, 300f), random(500f, 800f))
                    .setScale(random(0.06f, 0.9f))
                    .setRadial(true)
                    .setDuration(0.75f, true);
            pX = this.x + random(-spreadX, spreadX);
            pY = this.y + random(-spreadY, spreadY);
            PCLEffects.Queue.particle(EUIUtils.random(IMAGES_AIR).texture(), pX, pY)
                    .setSpeed(random(-300f, 300f), random(-300f, 300f))
                    .setScale(random(0.04f, 0.6f))
                    .setColor(new Color(MathUtils.random(0.8f, 1f), 1f, MathUtils.random(0.8f, 1f), 1))
                    .setBlendingMode(EUIRenderHelpers.BlendingMode.Glowing)
                    .setOpacity(random(0.3F, 1.0F))
                    .setRotation(random(-10f, 10f), randomBoolean() ? random(-800f, -500f) : random(500f, 800f));
            vfxTimer = vfxFrequency;
        }

        super.updateInternal(deltaTime);
    }
}
