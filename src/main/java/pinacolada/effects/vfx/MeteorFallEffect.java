package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.DarkSmokePuffEffect;
import com.megacrit.cardcrawl.vfx.GenericSmokeEffect;
import com.megacrit.cardcrawl.vfx.combat.SmokingEmberEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;

public class MeteorFallEffect extends AbstractGameEffect {
    private static final float DUR = 0.5f;
    private final float x;
    private final float y;
    private final AtlasRegion img;
    private boolean playedSound = false;

    public MeteorFallEffect(float x, float y) {
        this.img = ImageMaster.VERTICAL_IMPACT;
        this.x = x - (float) this.img.packedWidth / 2f;
        this.y = y - (float) this.img.packedHeight * 0.01f;
        this.startingDuration = DUR;
        this.duration = DUR;
        this.scale = Settings.scale;
        this.rotation = MathUtils.random(40f, 50f);
        this.color = Color.SCARLET.cpy();
        this.renderBehind = false;
    }

    public void update() {
        if (duration == DUR) {
            for (int i = 0; i < 50; ++i) {
                PCLEffects.Queue.add(new GenericSmokeEffect(x + MathUtils.random(-280f, 250f) * Settings.scale, y - 80f * Settings.scale));
            }
        }

        if (this.duration < 0f) {
            CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.HIGH, ScreenShake.ShakeDur.SHORT, false);

            if (!Settings.DISABLE_EFFECTS) {
                PCLEffects.Queue.add(new DarkSmokePuffEffect(this.x, this.y));

                for (int i = 0; i < 12; ++i) {
                    PCLEffects.Queue.add(new SmokingEmberEffect(this.x + MathUtils.random(-50f, 50f) * Settings.scale, this.y + MathUtils.random(-50f, 50f) * Settings.scale));
                }
            }

            this.isDone = true;
        }

        if (this.duration < 0.5f && !this.playedSound) {
            this.playRandomSfX();
            this.playedSound = true;
        }

        if (this.duration > 0.2f) {
            this.color.a = Interpolation.fade.apply(0.5f, 0f, (this.duration - 0.34f) * 5f);
        }
        else {
            this.color.a = Interpolation.fade.apply(0f, 0.5f, this.duration * 5f);
        }

        this.scale = Interpolation.fade.apply(Settings.scale * 1.1f, Settings.scale * 1.05f, this.duration / 0.6f);

        this.duration -= Gdx.graphics.getDeltaTime();
    }

    private void playRandomSfX() {
        SFX.play(SFX.ORB_LIGHTNING_EVOKE, 0.5f, 0.7f);
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.3f, this.scale * 0.8f, this.rotation - 18f);
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.3f, this.scale * 0.8f, this.rotation + MathUtils.random(12f, 18f));
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.4f, this.scale * 0.5f, this.rotation - MathUtils.random(-10f, 14f));
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.7f, this.scale * 0.9f, this.rotation + MathUtils.random(20f, 28f));
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 1.5f, this.scale * MathUtils.random(1.4f, 1.6f), this.rotation);
        Color c = Color.GOLD.cpy();
        c.a = this.color.a;
        sb.setColor(c);
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale, this.scale * MathUtils.random(0.8f, 1.2f), this.rotation);
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale, this.scale * MathUtils.random(0.4f, 0.6f), this.rotation);
        sb.draw(this.img, this.x + MathUtils.random(-10f, 10f) * Settings.scale, this.y, (float) this.img.packedWidth / 2f, 0f, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.5f, this.scale * 0.7f, this.rotation + MathUtils.random(20f, 28f));
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}
