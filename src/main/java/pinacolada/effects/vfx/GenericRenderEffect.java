package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import pinacolada.utilities.PCLRenderHelpers;

public class GenericRenderEffect extends VisualEffect {
    public TextureRegion region;
    protected float alpha = 1;

    public GenericRenderEffect(Texture texture, float x, float y) {
        this(new TextureRegion(texture), x, y);
    }

    public GenericRenderEffect(TextureRegion region, float x, float y) {
        super(0.6f, x, y, 0, 1);
        this.isRealtime = true;
        this.region = region;
        this.color = Color.WHITE.cpy();

        if (this.region == null) {
            complete();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.region != null) {
            PCLRenderHelpers.drawCentered(sb, color, region, x, y, region.getRegionWidth(), region.getRegionHeight(), scale, rotation, flipX, flipY);
        }
    }

    public GenericRenderEffect setOpacity(float alpha) {
        this.alpha = alpha;
        this.color.a = this.alpha;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        updateParameters(deltaTime);

        final float halfDuration = startingDuration * 0.5f;
        if (duration > halfDuration) {
            color.a = Interpolation.fade.apply(alpha, 0f, (duration - halfDuration) / halfDuration);
        }
        else {
            color.a = Interpolation.fade.apply(0f, alpha, duration / halfDuration);
        }

    }
}
