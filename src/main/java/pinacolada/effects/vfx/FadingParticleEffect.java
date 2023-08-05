package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import extendedui.utilities.EUIColors;
import pinacolada.utilities.PCLRenderHelpers;

public class FadingParticleEffect extends VisualEffect {
    private static final ParticlePool effectPool = new ParticlePool();
    protected Color targetColor;
    protected PCLRenderHelpers.BlendingMode blendingMode = PCLRenderHelpers.BlendingMode.Normal;
    protected Texture texture;
    protected float alpha;
    protected float colorSpeed = 1;

    // Pool only
    protected FadingParticleEffect() {
        super();
        this.color = Color.WHITE.cpy();
        this.targetColor = this.color.cpy();
    }

    public static FadingParticleEffect obtain(Texture texture, float x, float y) {
        return obtain(texture, x, y, 0, 1);
    }

    public static FadingParticleEffect obtain(Texture texture, float x, float y, float rot, float scale) {
        return effectPool.obtain(texture, x, y, rot, scale);
    }

    protected void complete() {
        super.complete();
        free();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (blendingMode != PCLRenderHelpers.BlendingMode.Normal) {
            blendingMode.apply(sb);
            PCLRenderHelpers.drawCentered(sb, color, texture, x, y, texture.getWidth(), texture.getHeight(), scale, rotation, flipX, flipY);
            PCLRenderHelpers.BlendingMode.Normal.apply(sb);
        }
        else {
            PCLRenderHelpers.drawCentered(sb, color, texture, x, y, texture.getWidth(), texture.getHeight(), scale, rotation, flipX, flipY);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        updateParameters(deltaTime);
        EUIColors.lerp(this.color, targetColor, deltaTime * colorSpeed);

        final float halfDuration = startingDuration * 0.5f;
        if (duration > halfDuration) {
            color.a = Interpolation.fade.apply(alpha, 0f, (duration - halfDuration) / halfDuration);
        }
        else {
            color.a = Interpolation.fade.apply(0f, alpha, duration / halfDuration);
        }
    }

    protected void free() {
        effectPool.free(this);
    }

    protected void initialize(Texture texture, float x, float y, float rot, float scale) {
        super.initialize(x, y, rot, scale);
        this.texture = texture;
        this.alpha = 1.0F;
        this.colorSpeed = 1f;
        this.blendingMode = PCLRenderHelpers.BlendingMode.Normal;
        this.color.set(EUIColors.white(0));
        this.targetColor.set(this.color);
    }

    public FadingParticleEffect setAcceleration(float aX, float aY) {
        this.aX = aX;
        this.aY = aY;

        return this;
    }

    public FadingParticleEffect setFlip(boolean flipX, boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;

        return this;
    }

    public FadingParticleEffect setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation, float speed) {
        this.rotation = startRotation;
        this.vRot = speed;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation, float speed, float accel) {
        this.rotation = startRotation;
        this.vRot = speed;
        this.aRot = accel;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation) {
        this.rotation = startRotation;

        return this;
    }

    public FadingParticleEffect setScale(float scale) {
        this.scale = scale;

        return this;
    }

    public FadingParticleEffect setRotationTarget(float startRotation, float target, float speed) {
        this.rotation = startRotation;
        this.vRot = (target - startRotation) / speed;

        return this;
    }

    public FadingParticleEffect setScale(float scale, float speed) {
        this.scale = scale;
        this.vScale = speed;

        return this;
    }

    public FadingParticleEffect setScale(float scale, float speed, float accel) {
        this.scale = scale;
        this.vScale = speed;
        this.aScale = accel;

        return this;
    }

    public FadingParticleEffect setScaleTarget(float scale, float target, float speed) {
        this.scale = scale;
        this.vScale = (target - scale) * speed;

        return this;
    }

    public FadingParticleEffect setSpeed(float vX, float vY) {
        this.vX = vX;
        this.vY = vY;

        return this;
    }

    public FadingParticleEffect setTargetPosition(float tX, float tY) {
        return setTargetPosition(tX, tY, 100);
    }

    public FadingParticleEffect setTargetPosition(float tX, float tY, float speed) {
        float angle = PCLRenderHelpers.getAngleRadians(x, y, tX, tY);
        this.vX = MathUtils.cos(angle) * speed;
        this.vY = MathUtils.sin(angle) * speed;

        return this;
    }

    public FadingParticleEffect setBlendingMode(PCLRenderHelpers.BlendingMode blendingMode) {
        this.blendingMode = blendingMode;
        return this;
    }

    public FadingParticleEffect setColor(Color color) {
        super.setColor(color);
        this.targetColor.set(this.color);
        return this;
    }

    public FadingParticleEffect setColor(Float r, Float g, Float b, Float a) {
        super.setColor(r, g, b, a);
        this.targetColor.set(this.color);
        return this;
    }

    public FadingParticleEffect setColor(Color color, Color targetColor, float colorSpeed) {
        setColor(color);
        this.targetColor = targetColor;
        this.colorSpeed = colorSpeed;
        return this;
    }

    public FadingParticleEffect setOpacity(float alpha) {
        this.alpha = alpha;
        this.color.a = this.alpha;

        return this;
    }

    protected static class ParticlePool extends Pool<FadingParticleEffect> {

        public ParticlePool() {
            super(60, 4000);
        }

        @Override
        protected FadingParticleEffect newObject() {
            return new FadingParticleEffect();
        }

        public FadingParticleEffect obtain(Texture texture, float x, float y, float rot, float scale) {
            FadingParticleEffect particle = obtain();
            particle.initialize(texture, x, y, rot, scale);
            return particle;
        }
    }
}
