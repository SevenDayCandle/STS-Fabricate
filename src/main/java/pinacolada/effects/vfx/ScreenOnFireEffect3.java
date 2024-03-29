package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import com.megacrit.cardcrawl.vfx.combat.GiantFireEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;

public class ScreenOnFireEffect3 extends PCLEffect {
    private static final float INTERVAL = 0.05F;
    private float timer = 0.0F;
    protected int times = 8;

    public ScreenOnFireEffect3() {
        this.duration = 3.0F;
        this.startingDuration = this.duration;
    }

    public void dispose() {
    }

    public void render(SpriteBatch sb) {
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            CardCrawlGame.sound.play("GHOST_FLAMES");
            PCLEffects.Queue.add(new BorderLongFlashEffect(Color.FIREBRICK));
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            for (int i = 0; i < times; i++) {
                PCLEffects.Queue.add(new GiantFireEffect());
            }
            this.timer = 0.05F;
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }
}
