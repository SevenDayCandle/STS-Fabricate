package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;

public class ScreenHexagonEffect extends PCLEffect {
    private static final float INTERVAL = 0.05F;
    private static final int ROWS = 6;
    private float timer = 0.0F;

    public ScreenHexagonEffect() {
        this.color = Color.GOLDENROD.cpy();
        this.duration = 1.5F;
        this.startingDuration = this.duration;
    }

    public void makeHexagons() {
        for (int i = 0; i < ROWS; i++) {
            PCLEffects.Queue.add(new HexagonEffect(-100f * Settings.scale, Settings.HEIGHT * 0.65f - i * HexagonEffect.SIZE * 0.9f, color)
                    .setRealtime(isRealtime));
        }
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            PCLSFX.play(PCLSFX.PCL_BOOST, 0.5f, 0.5f);
            PCLSFX.play(PCLSFX.ORB_LIGHTNING_CHANNEL, 0.7f, 0.7f);
            PCLEffects.Queue.add(new BorderLongFlashEffect(Color.GOLDENROD));
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            makeHexagons();
            this.timer = 0.05F;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
