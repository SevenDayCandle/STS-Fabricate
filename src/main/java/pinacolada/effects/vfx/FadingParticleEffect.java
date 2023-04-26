package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.PCLRenderHelpers;

public class FadingParticleEffect extends PCLEffect {
    protected final ColoredTexture texture;
    protected Color targetColor;
    protected float colorSpeed = 1;
    protected PCLRenderHelpers.BlendingMode blendingMode = PCLRenderHelpers.BlendingMode.Normal;
    protected float x;
    protected float y;
    protected float rot;
    protected float scale;
    protected float vX;
    protected float vY;
    protected float vRot;
    protected float vScale;
    protected float alpha;
    protected boolean flipX;
    protected boolean flipY;

    public FadingParticleEffect(Texture texture, float x, float y) {
        this(texture, x, y, 0, 1);
    }

    public FadingParticleEffect(Texture texture, float x, float y, float rot, float scale) {
        super(Settings.ACTION_DUR_FAST, false);
        this.color = Color.WHITE.cpy();
        this.targetColor = this.color.cpy();
        this.texture = new ColoredTexture(texture);
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.scale = scale;
        this.alpha = 1.0F;
    }

    public FadingParticleEffect setBlendingMode(PCLRenderHelpers.BlendingMode blendingMode) {
        this.blendingMode = blendingMode;
        return this;
    }

    public FadingParticleEffect setColor(Float r, Float g, Float b, Float a) {
        super.setColor(new Color(r, g, b, a));
        texture.setColor(r, g, b, a);
        this.targetColor = this.color.cpy();
        return this;
    }

    public FadingParticleEffect setColor(Color color, Color targetColor, float colorSpeed) {
        setColor(color);
        texture.setColor(color);
        this.targetColor = targetColor;
        this.colorSpeed = colorSpeed;
        return this;
    }

    public FadingParticleEffect setColor(Color color) {
        super.setColor(color);
        texture.setColor(color);
        this.targetColor = this.color.cpy();
        return this;
    }

    public FadingParticleEffect setScale(float scale) {
        this.scale = scale;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation) {
        this.rot = startRotation;

        return this;
    }

    @Override
    public void render(SpriteBatch sb) {
        if (blendingMode != PCLRenderHelpers.BlendingMode.Normal) {
            blendingMode.apply(sb);
            PCLRenderHelpers.drawCentered(sb, color, texture.texture, x, y, texture.getWidth(), texture.getHeight(), scale, rot, flipX, flipY);
            PCLRenderHelpers.BlendingMode.Normal.apply(sb);
        }
        else {
            PCLRenderHelpers.drawCentered(sb, color, texture.texture, x, y, texture.getWidth(), texture.getHeight(), scale, rot, flipX, flipY);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        this.x += vX * deltaTime * Settings.scale;
        this.y += vY * deltaTime * Settings.scale;
        this.rot += vRot * deltaTime;
        this.scale += vScale * deltaTime;
        this.color = EUIColors.lerp(this.color, targetColor, deltaTime * colorSpeed);

        final float halfDuration = startingDuration * 0.5f;
        if (this.duration < halfDuration) {
            this.color.a = Interpolation.exp5In.apply(0.0F, this.alpha, this.duration / halfDuration);
        }
    }

    public FadingParticleEffect setFlip(boolean flipX, boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;

        return this;
    }

    public FadingParticleEffect setOpacity(float alpha) {
        this.alpha = alpha;
        this.color.a = this.alpha;

        return this;
    }

    public FadingParticleEffect setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation, float speed) {
        this.rot = startRotation;
        this.vRot = speed;

        return this;
    }

    public FadingParticleEffect setRotation(float startRotation, float target, float speed) {
        this.rot = startRotation;
        this.vScale = (target - startRotation) / speed;

        return this;
    }

    public FadingParticleEffect setScale(float scale, float speed) {
        this.scale = scale;
        this.vScale = speed;

        return this;
    }

    public FadingParticleEffect setScale(float scale, float target, float speed) {
        this.scale = scale;
        this.vScale = (target - scale) / speed;

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
}
