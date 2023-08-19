package pinacolada.effects.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.EUIRenderHelpers;

public class BlightAboveCreatureEffect extends AbstractGameEffect {
    private static final float DUR = 1f;
    private static final float TEXT_DURATION = 1.5f;
    private static final float STARTING_OFFSET_Y;
    private static final float TARGET_OFFSET_Y;
    private static final float LERP_RATE = 5f;
    private static final int W = 128;

    static {
        STARTING_OFFSET_Y = 0f * Settings.scale;
        TARGET_OFFSET_Y = 60f * Settings.scale;
    }

    private final AbstractBlight blight;
    private final Color outlineColor = new Color(0f, 0f, 0f, 0f);
    private final Color shineColor = new Color(1f, 1f, 1f, 0f);
    private final float x;
    private float offsetY;
    private float y;

    public BlightAboveCreatureEffect(float x, float y, AbstractBlight blight) {
        this.blight = blight;
        this.duration = 3f;
        this.startingDuration = 3f;
        this.x = x;
        this.y = y;
        this.color = Color.WHITE.cpy();
        this.offsetY = STARTING_OFFSET_Y;
        this.scale = Settings.scale;
    }

    public void dispose() {
    }

    public void render(SpriteBatch sb) {
        this.outlineColor.a = this.color.a / 2f;
        sb.setColor(this.outlineColor);
        sb.draw(blight.outlineImg, this.x - 64f, this.y - 64f + this.offsetY, 64f, 64f, 128f, 128f, this.scale * (2.5f - this.duration), this.scale * (2.5f - this.duration), this.rotation, 0, 0, 128, 128, false, false);
        sb.setColor(this.color);
        sb.draw(blight.img, this.x - 64f, this.y - 64f + this.offsetY, 64f, 64f, 128f, 128f, this.scale * (2.5f - this.duration), this.scale * (2.5f - this.duration), this.rotation, 0, 0, 128, 128, false, false);
        sb.setBlendFunction(770, 1);
        this.shineColor.a = this.color.a / 4f;
        sb.setColor(this.shineColor);
        sb.draw(blight.img, this.x - 64f, this.y - 64f + this.offsetY, 64f, 64f, 128f, 128f, this.scale * (2.7f - this.duration), this.scale * (2.7f - this.duration), this.rotation, 0, 0, 128, 128, false, false);
        sb.draw(blight.img, this.x - 64f, this.y - 64f + this.offsetY, 64f, 64f, 128f, 128f, this.scale * (3f - this.duration), this.scale * (3f - this.duration), this.rotation, 0, 0, 128, 128, false, false);
        sb.setBlendFunction(770, 771);
    }

    public void update() {
        if (this.duration > 1f) {
            this.color.a = Interpolation.exp5In.apply(1f, 0f, (this.duration - 1f) * 2f);
        }

        super.update();
        if (AbstractDungeon.player.chosenClass == AbstractPlayer.PlayerClass.DEFECT) {
            this.offsetY = MathUtils.lerp(this.offsetY, TARGET_OFFSET_Y + 80f * Settings.scale, Gdx.graphics.getDeltaTime() * 5f);
        }
        else {
            this.offsetY = MathUtils.lerp(this.offsetY, TARGET_OFFSET_Y, Gdx.graphics.getDeltaTime() * 5f);
        }

        this.y += Gdx.graphics.getDeltaTime() * 12f * Settings.scale;
    }
}