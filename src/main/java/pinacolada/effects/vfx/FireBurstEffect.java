package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.combat.LightFlareParticleEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.utilities.GameEffects;

public class FireBurstEffect extends PCLEffect
{
    protected Color secondaryColor;
    protected float x;
    protected float y;
    protected float startX;
    protected float startY;
    protected float targetX;
    protected float targetY;
    protected float vfxTimer;

    public FireBurstEffect(float startX, float startY, float targetX, float targetY)
    {
        super(0.5f);

        this.color = Color.CHARTREUSE.cpy();
        this.secondaryColor = color.cpy();
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX + MathUtils.random(-20f, 20f) * Settings.scale;
        this.targetY = targetY + MathUtils.random(-20f, 20f) * Settings.scale;
        this.x = startX;
        this.y = startY;
    }

    public FireBurstEffect setColor(Color mainColor, Color secondaryColor)
    {
        this.color = mainColor.cpy();
        this.secondaryColor = secondaryColor.cpy();

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        x = Interpolation.fade.apply(targetX, startX, duration / startingDuration);
        y = Interpolation.fade.apply(targetY, startY, duration / startingDuration);

        vfxTimer -= deltaTime;
        if (vfxTimer < 0f)
        {
            vfxTimer = 0.016f;
            GameEffects.Queue.add(new LightFlareParticleEffect(x, y, color));
            GameEffects.Queue.add(new FireIgniteEffect(x, y).setColor(color)).setRealtime(isRealtime);
        }

        if (tickDuration(deltaTime))
        {
            GameEffects.Queue.add(new FireIgniteEffect(x, y).setColor(color)).setRealtime(isRealtime);
            GameEffects.Queue.playEFX(PCLEffekseerEFX.FIRE10, x, y);
            complete();
        }
    }
}
