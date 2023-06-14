package pinacolada.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.utilities.PCLRenderHelpers;
import pinacolada.utilities.RandomizedList;

// Copied and modified from STS-AnimatorMod
public abstract class PCLEffect extends AbstractGameEffect {
    public final static Hitbox SKY_HB_L = new Hitbox(Settings.WIDTH * 0.48f, Settings.HEIGHT * 0.7f, 2, 2);
    public final static Hitbox SKY_HB_R = new Hitbox(Settings.WIDTH * 0.52f, Settings.HEIGHT * 0.7f, 2, 2);
    public final static Hitbox SKY_HB_LOW_L = new Hitbox(Settings.WIDTH * 0.48f, Settings.HEIGHT * 0.5f, 2, 2);
    public final static Hitbox SKY_HB_LOW_R = new Hitbox(Settings.WIDTH * 0.52f, Settings.HEIGHT * 0.5f, 2, 2);
    public static final Random RNG = new Random();
    public boolean isRealtime;
    public int ticks;

    public PCLEffect() {
        this(Settings.ACTION_DUR_FAST);
    }

    public PCLEffect(float duration) {
        this(duration, false);
    }

    public PCLEffect(float duration, boolean isRealtime) {
        this.isRealtime = isRealtime;
        this.duration = this.startingDuration = duration;
    }

    protected static int random(int min, int max) {
        return MathUtils.random(min, max);
    }

    protected static float random(float min, float max) {
        return MathUtils.random(min, max);
    }

    protected static boolean randomBoolean(float chance) {
        return MathUtils.randomBoolean(chance);
    }

    protected static boolean randomBoolean() {
        return MathUtils.randomBoolean();
    }

    @SafeVarargs
    protected static <T> T randomElement(RandomizedList<T> container, T... source) {
        if (container.size() <= 1) {
            container.addAll(source);
        }

        return container.retrieveUnseeded(true);
    }

    public PCLEffect addDuration(float duration, boolean isRealtime) {
        this.isRealtime = isRealtime;
        this.duration = (this.startingDuration += duration);

        return this;
    }

    protected void complete() {
        PCLEffects.UnlistedEffects.remove(this);
        this.isDone = true;
    }

    protected void firstUpdate() {

    }

    protected float getDeltaTime() {
        return isRealtime ? Gdx.graphics.getRawDeltaTime() : Gdx.graphics.getDeltaTime();
    }

    public PCLEffect renderBehind(boolean value) {
        renderBehind = value;

        return this;
    }

    protected void renderImage(SpriteBatch sb, TextureAtlas.AtlasRegion img, float x, float y) {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        PCLRenderHelpers.drawCentered(sb, color, img, x, y, img.packedWidth, img.packedHeight, scale, rotation);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    protected void renderImage(SpriteBatch sb, Texture img, float x, float y, boolean flipX, boolean flipY) {
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        PCLRenderHelpers.drawCentered(sb, color, img, x, y, img.getWidth(), img.getHeight(), scale, rotation, flipX, flipY);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    protected void renderImage(SpriteBatch sb, Texture img, float x, float y, boolean flipX, boolean flipY, PCLRenderHelpers.BlendingMode blendingMode) {
        sb.setBlendFunction(blendingMode.srcFunc, blendingMode.dstFunc);
        PCLRenderHelpers.drawCentered(sb, color, img, x, y, img.getWidth(), img.getHeight(), scale, rotation, flipX, flipY);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public PCLEffect setColor(Color color) {
        if (color != null) {
            if (this.color == null) {
                this.color = new Color(color.r, color.g, color.b, 1f);
            }
            else {
                this.color.r = color.r;
                this.color.g = color.g;
                this.color.b = color.b;
                // do not set alpha
            }
        }

        return this;
    }

    public PCLEffect setDuration(float duration, boolean isRealtime) {
        this.isRealtime = isRealtime;
        this.duration = this.startingDuration = duration;

        return this;
    }

    public PCLEffect setRealtime(boolean isRealtime) {
        this.isRealtime = isRealtime;

        return this;
    }

    public PCLEffect setRotation(float degrees) {
        this.rotation = degrees;

        return this;
    }

    public PCLEffect setScale(float scale) {
        this.scale = scale;

        return this;
    }

    protected boolean tickDuration(float deltaTime) {
        this.ticks += 1;
        this.duration -= deltaTime;

        if (this.duration < 0f && ticks >= 3) // ticks are necessary for SuperFastMode at 1000% speed
        {
            this.isDone = true;
        }

        return isDone;
    }

    @Override
    public void update() {
        if (duration == startingDuration) {
            firstUpdate();

            if (!this.isDone) {
                tickDuration(getDeltaTime());
            }
        }
        else {
            updateInternal(getDeltaTime());
        }
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }

    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            complete();
        }
    }
}
