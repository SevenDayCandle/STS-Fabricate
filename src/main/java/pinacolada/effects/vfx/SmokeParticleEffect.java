package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pinacolada.effects.PCLEffect;

public class SmokeParticleEffect extends PCLEffect {
    protected float x;
    protected float y;
    protected float vX;
    protected float vY;
    protected float vRot;
    protected float startDur;
    protected float targetScale;
    protected TextureAtlas.AtlasRegion img;

    public SmokeParticleEffect(float x, float y) {
        this(x, y, 120, 8, 250, 1, Color.WHITE);
    }

    public SmokeParticleEffect(float x, float y, float r, float vXY, float vRot, float s, Color color) {
        super(MathUtils.random(2.0F, 2.5F));
        setColor(color);
        this.color.r += MathUtils.random(-0.05F, 0.05F);
        this.color.g += MathUtils.random(-0.05F, 0.05F);
        this.color.b += MathUtils.random(-0.05F, 0.05F);
        this.color.clamp();
        if (MathUtils.randomBoolean()) {
            this.img = ImageMaster.EXHAUST_L;
            this.targetScale = MathUtils.random(0.7F, 1.3f) * s;
        }
        else {
            this.img = ImageMaster.EXHAUST_S;
            this.targetScale = MathUtils.random(0.7F, 1f) * s;
        }

        this.startDur = this.duration;
        this.x = x + MathUtils.random(-r * Settings.scale, r * Settings.scale) - (float) this.img.packedWidth / 2.0F;
        this.y = y + MathUtils.random(-r * Settings.scale, r * Settings.scale) - (float) this.img.packedHeight / 2.0F;
        this.scale = 0.01F;
        this.rotation = MathUtils.random(360.0F);
        this.vRot = MathUtils.random(-vRot, vRot);
        float baseSpeed = MathUtils.random(vXY * 0.5f, vXY) * Settings.scale;
        this.vX = MathUtils.cos(rotation) * baseSpeed;
        this.vY = MathUtils.sin(rotation) * baseSpeed;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(this.img, this.x, this.y, (float) this.img.packedWidth / 2.0F, (float) this.img.packedHeight / 2.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale, this.scale, this.rotation);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        this.x += MathUtils.random(-2.0F * Settings.scale, 2.0F * Settings.scale) + this.vX;
        this.y += MathUtils.random(-2.0F * Settings.scale, 2.0F * Settings.scale) + this.vY;
        this.rotation += this.vRot * deltaTime;
        this.scale = Interpolation.exp10Out.apply(this.targetScale / 0.25f, this.targetScale, 1.0F - this.duration / this.startDur);

        if (this.duration < 0.33F) {
            this.color.a = this.duration * 3.0F;
        }
        super.updateInternal(deltaTime);
    }
}
