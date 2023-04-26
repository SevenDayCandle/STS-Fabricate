package pinacolada.effects.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;

public class BleedEffect extends PCLEffect {
    private static final float RADIUS = 500 * Settings.scale;

    private final float sX;
    private final float sY;
    private int count = 0;
    private float timer = 0.0F;

    public BleedEffect(float sX, float sY, int count) {
        this.sX = sX - 20.0F * Settings.scale;
        this.sY = sY + 80.0F * Settings.scale;
        this.count = count;
    }

    public void render(SpriteBatch sb) {

    }

    public void dispose() {
    }

    @Override
    protected void updateInternal(float deltaTime) {
        this.timer -= Gdx.graphics.getDeltaTime();
        if (this.timer < 0.0F) {
            this.timer += MathUtils.random(0.001F, 0.02F);
            float degrees = MathUtils.random(0f, 360f);
            PCLEffects.Queue.add(new BleedParticleEffect(sX, sY));
            --this.count;
            if (this.count == 0) {
                this.isDone = true;
            }
        }
    }
}
