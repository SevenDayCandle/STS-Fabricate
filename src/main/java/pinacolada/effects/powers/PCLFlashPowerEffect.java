package pinacolada.effects.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.effects.vfx.VisualEffect;
import pinacolada.powers.PCLPower;

public class PCLFlashPowerEffect extends VisualEffect {
    private final Texture img;
    private final AtlasRegion region128;

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
        if (power.region48 != null) {
            this.region128 = power.region48;
        }
        else {
            this.region128 = power.region128;
        }

        if (this.region128 != null) {
            this.x -= (float) (this.region128.packedWidth / 2);
            this.y -= (float) (this.region128.packedHeight / 2);
        }

        this.duration = this.startingDuration = 0.7f;
        this.color = Color.WHITE.cpy();
    }

    public PCLFlashPowerEffect(Texture img, float x, float y) {
        this.scale = Settings.scale;
        this.x = x;
        this.y = y;
        this.img = img;
        this.region128 = null;
        this.duration = this.startingDuration = 0.7f;
        this.color = Color.WHITE.cpy();
    }

    public void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        if (this.duration < this.startingDuration / 2.0F) {
            this.color.a = this.duration / (this.startingDuration / 2.0F);
        }
        this.scale = Interpolation.exp5In.apply(Settings.scale, Settings.scale * 0.3f, this.duration / this.startingDuration);
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        if (this.region128 != null) {
            float half_w = region128.packedWidth / 2f;
            float half_h = region128.packedHeight / 2f;

            sb.draw(region128, x, y, half_w, half_h, region128.packedWidth, region128.packedHeight, scale, scale, 0f);
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

    public void dispose() {

    }
}

