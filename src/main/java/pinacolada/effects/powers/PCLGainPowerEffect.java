package pinacolada.effects.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import pinacolada.powers.PCLPower;

public class PCLGainPowerEffect extends AbstractGameEffect {
    private static final float EFFECT_DUR = 2f;
    private final Texture img;
    private final AtlasRegion region48;

    public PCLGainPowerEffect(PCLPower power, boolean playSfx) {
        this.img = power.img;
        this.region48 = power.region48;

        if (playSfx) {
            power.playApplyPowerSfx();
        }

        this.duration = 2f;
        this.startingDuration = 2f;
        this.scale = Settings.scale;
        this.color = new Color(1f, 1f, 1f, 0.5f);
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration > 0.5f) {
            this.scale = Interpolation.exp5Out.apply(3f * Settings.scale, Settings.scale, -(this.duration - 2f) / 1.5f);
        }
        else {
            this.color.a = Interpolation.fade.apply(0.5f, 0f, 1f - this.duration);
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void render(SpriteBatch sb, float x, float y) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        if (this.region48 != null) {
            float half_w = region48.packedWidth / 2f;
            float half_h = region48.packedHeight / 2f;
            sb.draw(this.region48, x - half_w, y - half_h, half_w, half_h, region48.packedWidth, region48.packedHeight, scale * 0.1f, scale * 0.1f, 0f);
        }
        else {
            sb.draw(this.img, x - 16f, y - 16f, 16f, 16f, 32f, 32f, scale, scale, 0f, 0, 0, this.img.getWidth(), this.img.getHeight(), false, false);
        }

        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}

