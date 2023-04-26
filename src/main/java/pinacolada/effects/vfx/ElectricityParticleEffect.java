package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;

public class ElectricityParticleEffect extends PCLEffect {
    protected static final int SIZE = 96;
    private static final TextureCache[] images = {
            PCLCoreImages.Effects.electric1,
            PCLCoreImages.Effects.electric2,
            PCLCoreImages.Effects.electric3,
            PCLCoreImages.Effects.electric4,
            PCLCoreImages.Effects.electric5,
            PCLCoreImages.Effects.electric6,
            PCLCoreImages.Effects.electric7
    };
    private static final ArrayList<Texture> imageTextures = new ArrayList<>();
    protected float animFrequency = 0.005f;
    protected float animTimer;
    protected int imgIndex;
    protected Texture img;
    protected float x;
    protected float y;
    protected float baseX;
    protected float baseY;
    protected float jitter;
    protected float vR;
    protected boolean flip;

    public ElectricityParticleEffect(float x, float y, float jitter, Color color) {
        super(random(0.5f, 1f));

        this.img = getTexture(random(0, images.length - 1));
        this.x = this.baseX = x - (float) (SIZE / 2);
        this.y = this.baseY = y - (float) (SIZE / 2);
        this.jitter = jitter;
        this.rotation = random(-10f, 10f);
        this.scale = random(0.2f, 1.5f) * Settings.scale;
        this.vR = random(-700f, 700f);
        this.flip = randomBoolean(0.5f);

        setColor(color, 0.35f);
    }

    protected Texture getTexture(int currentIndex) {
        if (imageTextures.size() == 0) {
            imageTextures.addAll(EUIUtils.map(images, TextureCache::texture));
        }
        imgIndex = (currentIndex + random(1, images.length - 1)) % images.length;
        return imageTextures.get(imgIndex);
    }

    public ElectricityParticleEffect setColor(Color color, float variance) {
        this.color = color.cpy();
        this.color.a = 0;

        if (variance > 0) {
            this.color.r = Math.max(0, color.r - random(0, variance));
            this.color.g = Math.max(0, color.g - random(0, variance));
            this.color.b = Math.max(0, color.b - random(0, variance));
        }

        return this;
    }

    public ElectricityParticleEffect setScale(float scale) {
        this.scale = scale;

        return this;
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.setColor(this.color);
        sb.draw(this.img, x, y, SIZE * 0.5f, SIZE * 0.5f, SIZE, SIZE, scale, scale, rotation, 0, 0, SIZE, SIZE, flip, false);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (scale > 0.3f * Settings.scale) {
            scale -= deltaTime * 2f;
        }

        if ((1f - duration) < 0.1f) {
            color.a = Interpolation.fade.apply(0f, 1f, (1f - duration) * 10f);
        }
        else {
            color.a = Interpolation.pow2Out.apply(0f, 1f, duration);
        }

        animTimer -= deltaTime;
        if (animTimer < 0) {
            animTimer = animFrequency;
            this.img = getTexture(imgIndex);
            x = baseX + random(-jitter, jitter);
            y = baseY + random(-jitter, jitter);
            rotation += vR * deltaTime;
        }

        super.updateInternal(deltaTime);
    }
}
