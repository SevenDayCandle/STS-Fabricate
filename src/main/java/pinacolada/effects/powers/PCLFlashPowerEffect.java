package pinacolada.effects.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.vfx.VisualEffect;
import pinacolada.powers.PCLPower;

public class PCLFlashPowerEffect extends VisualEffect {
    protected static final float REGION_SIZE = Settings.scale * 84;
    protected static final float REGION_HALF = REGION_SIZE / 2;
    private final Texture img;
    private final AtlasRegion region;

    public PCLFlashPowerEffect(PCLPower power) {
        super(0.7f, power.hb.cX - REGION_HALF, power.hb.cY - REGION_HALF, 0, 1);

        if (power.owner != null && !power.owner.isDeadOrEscaped()) {
            this.x = power.owner.hb.cX - REGION_HALF;
            this.y = power.owner.hb.cY - REGION_HALF;
        }

        this.img = power.img;
        if (power.region128 != null) {
            this.region = power.region128;
        }
        else {
            this.region = power.region48;
        }

        this.color = Color.WHITE.cpy();
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        if (this.region != null) {
            float half_w = region.packedWidth / 2f;
            float half_h = region.packedHeight / 2f;

            sb.draw(region, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 4f, scale * 4f, 0f);
            sb.draw(region, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 3f, scale * 3f, 0f);
            sb.draw(region, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 2f, scale * 2f, 0f);
            sb.draw(region, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale, scale, 0f);
        }
        else if (this.img != null) {
            int width = this.img.getWidth();
            int height = this.img.getHeight();
            sb.draw(img, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 4f, scale * 4f, 0f, 0, 0, width, height, false, false);
            sb.draw(img, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 3f, scale * 3f, 0f, 0, 0, width, height, false, false);
            sb.draw(img, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale * 2f, scale * 2f, 0f, 0, 0, width, height, false, false);
            sb.draw(img, x, y, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale, scale, 0f, 0, 0, width, height, false, false);
        }
        else {
            this.isDone = true;
        }

        sb.setBlendFunction(770, 771);
    }

    public void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        if (this.duration < this.startingDuration / 2.0F) {
            this.color.a = this.duration / (this.startingDuration / 2.0F);
        }
        this.scale = Interpolation.exp5In.apply(1f, 0.3f, this.duration / this.startingDuration);
    }
}

