package pinacolada.effects.vfx.megacritCopy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.SFX;

@Deprecated
public class BiteEffect2 extends PCLEffect {
    private static final float DUR = 1.0F;
    private static AtlasRegion top;
    private static AtlasRegion bot;
    private final float x;
    private final float sY;
    private final float dY;
    private final float sY2;
    private final float dY2;
    private float y;
    private float y2;
    private boolean playedSfx;

    public BiteEffect2(float x, float y) {
        this(x, y, new Color(0.7F, 0.9F, 1.0F, 0.0F));
    }

    public BiteEffect2(float x, float y, Color c) {
        if (top == null) {
            top = ImageMaster.vfxAtlas.findRegion("combat/biteTop");
            bot = ImageMaster.vfxAtlas.findRegion("combat/biteBot");
        }

        this.playedSfx = false;
        this.x = x - (float) top.packedWidth / 2.0F;
        this.sY = y - (float) top.packedHeight / 2.0F + 150.0F * Settings.scale;
        this.dY = y - 0.0F * Settings.scale;
        this.y = this.sY;
        this.sY2 = y - (float) (top.packedHeight / 2) - 100.0F * Settings.scale;
        this.dY2 = y - 90.0F * Settings.scale;
        this.y2 = this.sY2;
        this.startingDuration = 1.0F;
        this.duration = 0.7F;
        this.color = c.cpy();
        this.scale = Settings.scale;
    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(top, this.x, this.y, (float) top.packedWidth / 2.0F, (float) top.packedHeight / 2.0F, (float) top.packedWidth, (float) top.packedHeight, this.scale + MathUtils.random(-0.05F, 0.05F), this.scale + MathUtils.random(-0.05F, 0.05F), 0.0F);
        sb.draw(bot, this.x, this.y2, (float) top.packedWidth / 2.0F, (float) top.packedHeight / 2.0F, (float) top.packedWidth, (float) top.packedHeight, this.scale + MathUtils.random(-0.05F, 0.05F), this.scale + MathUtils.random(-0.05F, 0.05F), 0.0F);
        sb.setBlendFunction(770, 771);
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();

        final float actualDuration = duration + 0.3f;

        if (!this.playedSfx && actualDuration < this.startingDuration - 0.3F) {
            SFX.play(SFX.EVENT_VAMP_BITE, 0.9f, 1.1f);
            this.playedSfx = true;
        }

        if (actualDuration > this.startingDuration / 2.0F) {
            this.color.a = Interpolation.fade.apply(1.0F, 0.0F, (actualDuration - 0.5F) * 2.0F);
            this.y = Interpolation.bounceIn.apply(this.dY, this.sY, (actualDuration - 0.5F) * 2.0F);
            this.y2 = Interpolation.bounceIn.apply(this.dY2, this.sY2, (actualDuration - 0.5F) * 2.0F);
        }
        else {
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, actualDuration * 2.0F);
            this.y = Interpolation.fade.apply(this.sY, this.dY, actualDuration * 2.0F);
            this.y2 = Interpolation.fade.apply(this.sY2, this.dY2, actualDuration * 2.0F);
        }

        if (actualDuration <= 0) {
            this.isDone = true;
        }
    }

    public void dispose() {
    }
}
