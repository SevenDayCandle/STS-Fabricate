package pinacolada.effects.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlyingSpikeEffect;
import com.megacrit.cardcrawl.vfx.combat.PowerExpireTextEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.VisualEffect;

import static pinacolada.effects.powers.PCLFlashPowerEffect.REGION_HALF;
import static pinacolada.effects.powers.PCLFlashPowerEffect.REGION_SIZE;

public class PCLExpirePowerEffect extends VisualEffect {
    protected static final float TEXT_DURATION = 2.0F;
    protected static final float STARTING_OFFSET_Y = 80.0F * Settings.scale;
    protected static final float TARGET_OFFSET_Y = 160.0F * Settings.scale;
    protected static final float IMG_OFFSET_X = Settings.scale * 64;
    private final AtlasRegion region;
    private final String msg;
    private final Texture img;
    private float h;
    private float offsetY;
    private boolean spikeEffectTriggered = false;

    public PCLExpirePowerEffect(AbstractPower power, float x, float y, String msg) {
        super(2f, x - IMG_OFFSET_X, y, 0, 1f);

        this.msg = msg;
        this.offsetY = STARTING_OFFSET_Y;

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
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.losePowerFont, this.msg, this.x, this.y + this.offsetY, this.color);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.losePowerFont, PowerExpireTextEffect.TEXT[0], this.x, this.y + this.offsetY - 40.0F * Settings.scale, this.color);
        if (this.region != null) {
            sb.setColor(this.color);
            sb.setBlendFunction(770, 1);
            sb.draw(this.region, x - REGION_HALF - IMG_OFFSET_X, y + h + offsetY - REGION_HALF - 30.0F * Settings.scale, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, this.scale, this.scale, this.rotation);
            sb.setBlendFunction(770, 771);
        }
        else if (this.img != null) {
            sb.setColor(this.color);
            sb.setBlendFunction(770, 1);
            sb.draw(img, x - REGION_HALF - IMG_OFFSET_X, y + h + offsetY - REGION_HALF - 30.0F * Settings.scale, REGION_HALF, REGION_HALF, REGION_SIZE, REGION_SIZE, scale, scale, 0f, 0, 0, img.getWidth(), img.getHeight(), false, false);
            sb.setBlendFunction(770, 771);
        }
    }

    public void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        if (this.duration < this.startingDuration * 0.8F && !this.spikeEffectTriggered && !Settings.DISABLE_EFFECTS) {
            this.spikeEffectTriggered = true;

            int i;
            for (i = 0; i < 10; ++i) {
                PCLEffects.Queue.add(new FlyingSpikeEffect(this.x - MathUtils.random(20.0F) * Settings.scale + 70.0F * Settings.scale, this.y + MathUtils.random(STARTING_OFFSET_Y, TARGET_OFFSET_Y) * Settings.scale, 0.0F, MathUtils.random(50.0F, 400.0F) * Settings.scale, 0.0F, Settings.BLUE_TEXT_COLOR));
            }

            for (i = 0; i < 10; ++i) {
                PCLEffects.Queue.add(new FlyingSpikeEffect(this.x + MathUtils.random(20.0F) * Settings.scale, this.y + MathUtils.random(STARTING_OFFSET_Y, TARGET_OFFSET_Y) * Settings.scale, 0.0F, MathUtils.random(-400.0F, -50.0F) * Settings.scale, 0.0F, Settings.BLUE_TEXT_COLOR));
            }
        }

        this.offsetY = Interpolation.exp10In.apply(TARGET_OFFSET_Y, STARTING_OFFSET_Y, this.duration / 2.0F);
        this.color.a = Interpolation.exp10Out.apply(0.0F, 1.0F, this.duration / 2.0F);
    }
}

