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
    private final Texture img;
    private final AtlasRegion region;

    public PCLFlashPowerEffect(PCLPower power) {
        this.scale = Settings.scale;

        if (power.owner != null && !power.owner.isDeadOrEscaped()) {
            this.x = power.owner.hb.cX;
            this.y = power.owner.hb.cY;
        }
        else {
            this.x = power.hb.cX;
            this.y = power.hb.cY;
        }

        this.img = power.img;
        if (power.region128 != null) {
            this.region = power.region128;
        }
        else {
            this.region = power.region48;
        }

        if (this.region != null) {
            this.x -= (float) (this.region.packedWidth / 2);
            this.y -= (float) (this.region.packedHeight / 2);
        }

        this.duration = this.startingDuration = 0.7f;
        this.color = Color.WHITE.cpy();
    }

    public PCLFlashPowerEffect(Texture img, float x, float y) {
        this.scale = Settings.scale;
        this.x = x;
        this.y = y;
        this.img = img;
        this.region = null;
        this.duration = this.startingDuration = 0.7f;
        this.color = Color.WHITE.cpy();
    }

    public void dispose() {

    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        if (this.region != null) {
            float half_w = region.packedWidth / 2f;
            float half_h = region.packedHeight / 2f;

            sb.draw(region, x, y, half_w, half_h, region.packedWidth, region.packedHeight, scale, scale, 0f);
        }
        else if (this.img != null) {
            int width = this.img.getWidth();
            if (width >= 48) {
                int height = this.img.getHeight();
                sb.draw(img, x - 16f, y - 16f, 16f, 16f, 32f, 32f, scale * 8f, scale * 8f, 0f, 0, 0, width, height, false, false);
                sb.draw(img, x - 16f, y - 16f, 16f, 16f, 32f, 32f, scale * 6f, scale * 6f, 0f, 0, 0, width, height, false, false);
                sb.draw(img, x - 16f, y - 16f, 16f, 16f, 32f, 32f, scale * 4f, scale * 4f, 0f, 0, 0, width, height, false, false);
                sb.draw(img, x - 16f, y - 16f, 16f, 16f, 32f, 32f, scale * 2f, scale * 2f, 0f, 0, 0, width, height, false, false);
            }
            else {
                this.isDone = true;
            }
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
        this.scale = Interpolation.exp5In.apply(Settings.scale, Settings.scale * 0.3f, this.duration / this.startingDuration);
    }
}

