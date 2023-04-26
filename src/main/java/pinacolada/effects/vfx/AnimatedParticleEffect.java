package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pinacolada.utilities.PCLRenderHelpers;

public class AnimatedParticleEffect extends FadingParticleEffect {
    protected final int columns;
    protected final int rows;
    protected final int width;
    protected final int height;
    public AnimationMode mode;
    public int totalFrames;
    public int frame;
    protected TextureRegion region;
    protected float frameTimer;
    protected float frameDelay;

    public AnimatedParticleEffect(Texture texture, float x, float y, int rows, int columns) {
        this(texture, x, y, 0, 1, rows, columns, 0.03F);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns, float frameDuration) {
        this(texture, x, y, rot, scale, rows, columns, frameDuration, rows * columns);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns, float frameDuration, int maxFrames) {
        super(texture, x, y, rot, scale);

        this.width = getCellSize(texture.getWidth(), columns);
        this.height = getCellSize(texture.getHeight(), columns);
        this.totalFrames = Math.min(maxFrames, rows * columns);
        this.frameTimer = this.frameDelay = frameDuration;
        this.mode = AnimationMode.Remain;
        this.columns = columns;
        this.rows = rows;
        this.frame = 0;
    }

    private static int getCellSize(int totalWidth, int cells) {
        if (totalWidth % cells != 0) {
            throw new RuntimeException("The texture can't be evenly divided");
        }

        return totalWidth / cells;
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, int rows, int columns, float frameDuration) {
        this(texture, x, y, 0, 1, rows, columns, frameDuration);
    }

    public AnimatedParticleEffect(Texture texture, float x, float y, float rot, float scale, int rows, int columns) {
        this(texture, x, y, rot, scale, rows, columns, 0.03F);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (region == null) {
            this.region = getFrameRegion(frame);
        }

        PCLRenderHelpers.drawCentered(sb, color, region, x, y, width, height, scale, rot, flipX, flipY);
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


        return new TextureRegion(texture.texture, (clampedFrame % columns) * width, (clampedFrame / columns) * height, width, height);
    }

    @Override
    public void updateInternal(float delta) {
        super.updateInternal(delta);

        this.frameTimer -= delta;
        if (this.frameTimer < 0f) {
            this.frame += 1;
            this.frameTimer = this.frameDelay;
            this.region = null;
        }
    }

    public enum AnimationMode {
        Loop,
        Remain,
        Reverse
    }
}
