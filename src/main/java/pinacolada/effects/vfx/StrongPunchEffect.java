package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.DamageImpactCurvyEffect;
import com.megacrit.cardcrawl.vfx.combat.DamageImpactLineEffect;
import com.megacrit.cardcrawl.vfx.combat.StrikeEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.VFX;
import pinacolada.resources.pcl.PCLCoreImages;

public class StrongPunchEffect extends VisualEffect {
    protected float vfxTimer = 1;
    protected float baseScale;
    protected boolean triggered = false;

    public StrongPunchEffect(float x, float y, float baseScale) {
        super(1f, x, y, 300f, Math.max(baseScale, 1));
        this.baseScale = this.scale;
        this.vRot = 800f;
        this.color = Color.WHITE.cpy();
    }

    public void render(SpriteBatch sb) {
        renderImage(sb, PCLCoreImages.Effects.punch.texture(), x, y, false, false);
    }

    @Override
    protected void updateInternal(float deltaTime) {

        if ((1f - duration) < 0.1f) {
            color.a = Interpolation.fade.apply(0.1f, 1f, (1f - duration) * 7f);
        }
        else {
            color.a = Interpolation.pow2Out.apply(0.1f, 1f, duration);
        }

        vfxTimer -= deltaTime / duration;
        if (vfxTimer < 0f) {
            if (!triggered) {
                // TODO use generic render effect
                int i = 0;
                for(i = 0; i < 18; ++i) {
                    PCLEffects.Queue.add(new DamageImpactLineEffect(x, y));
                }
                for(i = 0; i < 5; ++i) {
                    PCLEffects.Queue.add(new DamageImpactCurvyEffect(x, y));
                }
                PCLSFX.play(PCLSFX.PCL_PUNCH, 0.7f, 0.8f);
                triggered = true;
            }
            else {
                x += Interpolation.sine.apply(-25f, 25f, this.duration * 50);
                y += Interpolation.sine.apply(-25f, 25f, this.duration * 50);
            }
        }
        else {
            this.rotation += vRot * deltaTime / duration;
            this.scale = Interpolation.linear.apply(0.02f, this.baseScale, duration);
        }

        super.updateInternal(deltaTime);
    }
}
