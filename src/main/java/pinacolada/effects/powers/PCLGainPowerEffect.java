package pinacolada.effects.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.powers.PCLPower;

import static pinacolada.effects.powers.PCLFlashPowerEffect.REGION_HALF;
import static pinacolada.effects.powers.PCLFlashPowerEffect.REGION_SIZE;

public class PCLGainPowerEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 2f;
    private final Texture img;
    private final AtlasRegion region;

    public PCLGainPowerEffect(PCLPower power, boolean playSfx) {
        this.img = power.img;
        this.region = power.region128;

        if (playSfx) {
            power.playApplyPowerSfx();
        }

        this.duration = 2f;
        this.startingDuration = 2f;
        this.scale = 1f;
        this.color = new Color(1f, 1f, 1f, 0.5f);
    }

    public void dispose() {
    }

    public void render(SpriteBatch sb, float x, float y) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        if (this.region != null) {
            sb.draw(this.region, x - REGION_HALF, y - REGION_HALF, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale, scale, 0f);
        }
        else {
            sb.draw(this.img, x - REGION_HALF, y - REGION_HALF, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale, scale, 0f, 0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
        }

        sb.setBlendFunction(770, 771);
    }

    public void render(SpriteBatch sb) {
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > 0.5f) {
            this.scale = Interpolation.exp5Out.apply(1.5f, 0.3f, -(this.duration - 2f) / 1.5f);
        }
        else {
            this.color.a = Interpolation.fade.apply(0.5f, 0f, 1f - this.duration);
        }

    }
}

