package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.PetalEffect;
import pinacolada.effects.PCLEffect;

public class CherryBlossomEffect extends PCLEffect {
    private float timer = 0.1F;

    public CherryBlossomEffect() {
        this.duration = 2.0F;
    }

    public void render(SpriteBatch sb) {
    }

    @Override
    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            this.timer += 0.1F;
            AbstractDungeon.effectsQueue.add(new PetalEffect());
            AbstractDungeon.effectsQueue.add(new PetalEffect());
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void dispose() {
    }
}
