package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;

public class SnowballEffect extends PCLEffect {
    public static final int SIZE = 128;
    protected Color secondaryColor;
    protected float x;
    protected float y;
    protected float startX;
    protected float startY;
    protected float targetX;
    protected float targetY;
    protected float vfxTimer;

    public SnowballEffect(float startX, float startY, float targetX, float targetY) {
        super(0.5f);

        this.color = Color.SKY.cpy();
        this.secondaryColor = color.cpy();
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX + MathUtils.random(-20f, 20f) * Settings.scale;
        this.targetY = targetY + MathUtils.random(-20f, 20f) * Settings.scale;
        this.x = startX;
        this.y = startY;
    }

    public SnowballEffect setColor(Color mainColor, Color secondaryColor) {
        this.color = mainColor.cpy();
        this.secondaryColor = secondaryColor.cpy();

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        x = Interpolation.fade.apply(targetX, startX, duration / startingDuration);
        y = Interpolation.fade.apply(targetY, startY, duration / startingDuration);

        vfxTimer -= deltaTime;
        if (vfxTimer < 0f) {
            vfxTimer = 0.016f;
            PCLEffects.Queue.add(new LightFlareParticleEffect(x, y, secondaryColor));
            PCLEffects.Queue.add(new SnowBurstEffect(x, y));
        }

        if (tickDuration(deltaTime)) {
            PCLEffects.Queue.add(new SnowballImpactEffect(x, y).setColor(color)).setRealtime(isRealtime);
            complete();
        }
    }
}
