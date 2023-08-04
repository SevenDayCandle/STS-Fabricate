package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.PCLRenderHelpers;

public class VisualEffect extends PCLEffect {
    protected float vX;
    protected float vY;
    protected float vRot;
    protected float vScale;
    protected float aX;
    protected float aY;
    protected float aRot;
    protected float aScale;
    protected boolean flipX;
    protected boolean flipY;
    protected boolean radial;
    public float x;
    public float y;

    public VisualEffect() {
        this(0, 0);
    }

    public VisualEffect(float x, float y) {
        this(x, y, 0, 1);
    }

    public VisualEffect(float x, float y, float rot, float scale) {
        this(Settings.ACTION_DUR_FAST, x, y, rot, scale);
    }

    public VisualEffect(float duration, float x, float y, float rot, float scale) {
        super(duration, false);
        this.x = x;
        this.y = y;
        this.rotation = rot;
        this.scale = scale;
    }

    public static Texture randomTexture(TextureCache[] caches) {
        return EUIUtils.random(caches).texture();
    }

    protected void initialize(float x, float y, float rot, float scale) {
        this.x = x;
        this.y = y;
        this.rotation = rot;
        this.scale = scale;
        this.vX = this.vY = this.vRot = this.vScale = this.aX = this.aY = this.aRot = this.aScale = 0;
        this.flipX = this.flipY = this.radial = false;
        this.duration = startingDuration;
        this.isDone = false;
    }

    public VisualEffect setAcceleration(float aX, float aY) {
        this.aX = aX;
        this.aY = aY;

        return this;
    }

    public VisualEffect setFlip(boolean flipX, boolean flipY) {
        this.flipX = flipX;
        this.flipY = flipY;

        return this;
    }

    public VisualEffect setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public VisualEffect setRadial(boolean val) {
        this.radial = val;
        return this;
    }

    public VisualEffect setRotation(float startRotation, float speed) {
        this.rotation = startRotation;
        this.vRot = speed;

        return this;
    }

    public VisualEffect setRotation(float startRotation, float speed, float accel) {
        this.rotation = startRotation;
        this.vRot = speed;
        this.aRot = accel;

        return this;
    }

    public VisualEffect setRotation(float startRotation) {
        this.rotation = startRotation;

        return this;
    }

    public VisualEffect setRotationTarget(float startRotation, float target, float speed) {
        this.rotation = startRotation;
        this.vRot = (target - startRotation) / speed;

        return this;
    }

    public VisualEffect setScale(float scale) {
        this.scale = scale;

        return this;
    }

    public VisualEffect setScale(float scale, float speed) {
        this.scale = scale;
        this.vScale = speed;

        return this;
    }

    public VisualEffect setScale(float scale, float speed, float accel) {
        this.scale = scale;
        this.vScale = speed;
        this.aScale = accel;

        return this;
    }

    public VisualEffect setScaleTarget(float scale, float target, float speed) {
        this.scale = scale;
        this.vScale = (target - scale) * speed;

        return this;
    }

    public VisualEffect setSpeed(float vX, float vY) {
        this.vX = vX;
        this.vY = vY;

        return this;
    }

    public VisualEffect setTargetPosition(float tX, float tY) {
        return setTargetPosition(tX, tY, 100);
    }

    public VisualEffect setTargetPosition(float tX, float tY, float speed) {
        float angle = PCLRenderHelpers.getAngleRadians(x, y, tX, tY);
        this.vX = MathUtils.cos(angle) * speed;
        this.vY = MathUtils.sin(angle) * speed;

        return this;
    }

    public void updateParameters(float deltaTime) {
        float deltaScale = deltaTime * Settings.scale;
        if (radial) {
            this.x += vX * deltaScale * MathUtils.cos(rotation);
            this.y += vY * deltaScale * MathUtils.sin(rotation);
        }
        else {
            this.x += vX * deltaScale;
            this.y += vY * deltaScale;
        }

        this.rotation += vRot * deltaTime;
        this.scale += vScale * deltaTime;
        this.vX += aX * deltaScale;
        this.vY += aY * deltaScale;
        this.vRot += aRot * deltaTime;
        this.vScale += aScale * deltaTime;
    }
}
