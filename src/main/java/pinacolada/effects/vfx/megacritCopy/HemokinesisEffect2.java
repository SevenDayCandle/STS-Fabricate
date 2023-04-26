package pinacolada.effects.vfx.megacritCopy;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.HemokinesisParticleEffect;

@Deprecated
public class HemokinesisEffect2 extends PCLEffect {
    private final float x;
    private final float y;
    private final float tX;
    private final float tY;

    public HemokinesisEffect2(float x, float y, float targetX, float targetY) {
        super(0.5f);

        this.x = x;
        this.y = y;
        this.tX = targetX;
        this.tY = targetY;
        this.scale = 0.12f;
    }

    public void update() {
        this.scale -= Gdx.graphics.getDeltaTime();
        if (this.scale < 0f) {
            PCLEffects.Queue.add(new HemokinesisParticleEffect(this.x + random(60f, -60f) * Settings.scale, this.y + random(60f, -60f) * Settings.scale, this.tX, this.tY, this.x > this.tX));
            this.scale = 0.04f;
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0f) {
            this.isDone = true;
        }
    }
}
