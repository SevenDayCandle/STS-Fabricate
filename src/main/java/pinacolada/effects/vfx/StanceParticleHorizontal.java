package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pinacolada.effects.PCLEffect;

public class StanceParticleHorizontal extends PCLEffect {
    private final float dvy;
    private final float dvx;
    private final Texture img;
    private float x;
    private float y;
    private float vX;
    private float vY;

    public StanceParticleHorizontal(Color particleColor) {
        super(random(0.6f, 1f), true);

        final float multi = Settings.scale;
        img = ImageMaster.FROST_ACTIVATE_VFX_1;
        scale = random(0.6f, 1f) * multi;
        color = particleColor.cpy();
        vX = random(-300f, -50f) * multi;
        vY = random(-200f, -100f) * multi;
        x = AbstractDungeon.player.hb.cX + random(100f, 160f) * multi - 32f;
        y = AbstractDungeon.player.hb.cY + random(-50f, 220f) * multi - 32f;
        renderBehind = randomBoolean(0.2f + (scale - 0.5f));
        dvx = 400f * multi * scale;
        dvy = 100f * multi;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(color);
        sb.setBlendFunction(770, 1);
        sb.draw(img, x, y, 32f, 32f, 25f,
                128f, scale, scale + (startingDuration * 0.2f - duration) * Settings.scale, rotation,
                0, 0, 64, 64, false, false);
        sb.setBlendFunction(770, 771);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        x += vX * deltaTime;
        y += vY * deltaTime;
        vY += dvy * deltaTime;
        vX -= dvx * deltaTime;

        //noinspection SuspiciousNameCombination
        rotation = -(57.295776f * MathUtils.atan2(vX, vY));

        final float halfDuration = startingDuration * 0.5f;
        if (duration > halfDuration) {
            color.a = Interpolation.fade.apply(1f, 0f, (duration - halfDuration) / halfDuration);
        }
        else {
            color.a = Interpolation.fade.apply(0f, 1f, duration / halfDuration);
        }

        vY += deltaTime * 40f * Settings.scale;

        super.updateInternal(deltaTime);
    }
}
