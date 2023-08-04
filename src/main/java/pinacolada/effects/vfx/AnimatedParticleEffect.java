package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import extendedui.utilities.EUIColors;
import pinacolada.utilities.PCLRenderHelpers;

public class AnimatedParticleEffect extends VisualEffect {
    protected final Texture texture;
    protected final int columns;
    protected final int height;
    protected final int rows;
    protected final int width;
    protected Color targetColor;
    protected PCLRenderHelpers.BlendingMode blendingMode = PCLRenderHelpers.BlendingMode.Normal;
    protected TextureRegion region;
    protected float alpha;
    protected float colorSpeed = 1;
    protected float frameDelay;
    protected float frameTimer;
    public AnimationMode mode;
    public int frame;
    public int totalFrames;

    public AnimatedParticleEffect(Texture texture, float x, float y, int rows, int columns) {
        this(texture, x, y, 0, 1, rows, columns, 0.03F);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns, float frameDuration) {
        this(texture, x, y, rot, scale, rows, columns, frameDuration, rows * columns);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns, float frameDuration, int maxFrames) {
        super(x, y, rot, scale);
        this.color = Color.WHITE.cpy();
        this.targetColor = this.color.cpy();
        this.texture = texture;
        this.alpha = 1.0F;
        this.width = getCellSize(texture.getWidth(), columns);
        this.height = getCellSize(texture.getHeight(), columns);
        this.totalFrames = Math.min(maxFrames, rows * columns);
        this.frameTimer = this.frameDelay = frameDuration;
        this.mode = AnimationMode.Remain;
        this.columns = columns;
        this.rows = rows;
        this.frame = 0;
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, int rows, int columns, float frameDuration) {
        this(texture, x, y, 0, 1, rows, columns, frameDuration);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns) {
        this(texture, x, y, rot, scale, rows, columns, 0.03F);
    }

    private static int getCellSize(int totalWidth, int cells) {
        if (totalWidth % cells != 0) {
            throw new RuntimeException("The texture can't be evenly divided");
        }

        return totalWidth / cells;
    }

    public TextureRegion getFrameRegion(int frame) {
        final int clampedFrame;
        if (mode == AnimationMode.Reverse) {
            final int cycle = (frame / totalFrames) % 2;
            clampedFrame = Math.abs((frame % totalFrames) - ((totalFrames - 1) * cycle));
        }
        else {
            clampedFrame = mode == AnimationMode.Loop ? (frame % totalFrames) : Math.min(frame, totalFrames - 1);
        }


        return new TextureRegion(texture, (clampedFrame % columns) * width, (clampedFrame / columns) * height, width, height);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (region == null) {
            this.region = getFrameRegion(frame);
        }

        PCLRenderHelpers.drawCentered(sb, color, region, x, y, width, height, scale, rotation, flipX, flipY);
    }

    @Override
    public void updateInternal(float delta) {
        super.updateInternal(delta);
        updateParameters(delta);

        EUIColors.lerp(this.color, targetColor, delta * colorSpeed);

        final float halfDuration = startingDuration * 0.5f;
        if (this.duration < halfDuration) {
            float aMult = Interpolation.exp5In.apply(0.0F, this.alpha, this.duration / halfDuration);
            this.color.a = this.color.a * aMult;
        }

        this.frameTimer -= delta;
        if (this.frameTimer < 0f) {
            this.frame += 1;
            this.frameTimer = this.frameDelay;
            this.region = null;
        }
    }

    public AnimatedParticleEffect setOpacity(float alpha) {
        this.alpha = alpha;
        this.color.a = this.alpha;

        return this;
    }

    public enum AnimationMode {
        Loop,
        Remain,
        Reverse
    }
}
