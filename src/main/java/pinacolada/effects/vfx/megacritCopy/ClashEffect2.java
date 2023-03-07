package pinacolada.effects.vfx.megacritCopy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.UpgradeShineParticleEffect;
import com.megacrit.cardcrawl.vfx.combat.AnimatedSlashEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;

@Deprecated
public class ClashEffect2 extends PCLEffect
{
    private final float x;
    private final float y;
    private final Color c11 = Color.SCARLET.cpy();
    private final Color c12 = Color.GOLD.cpy();
    private final Color c21 = Color.SKY.cpy();
    private final Color c22 = Color.CYAN.cpy();

    public ClashEffect2(float x, float y)
    {
        super(0.4f);
        this.x = x;
        this.y = y;
    }

    public ClashEffect2 setColors(Color c11, Color c12, Color c21, Color c22)
    {
        this.c11.set(c11);
        this.c12.set(c12);
        this.c21.set(c21);
        this.c22.set(c22);

        return this;
    }

    public void render(SpriteBatch sb)
    {
    }

    public void dispose()
    {
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        SFX.play(SFX.ATTACK_WHIFF_1, 1.4f);
        SFX.play(SFX.ATTACK_IRON_1, 0.9f);
        SFX.play(SFX.ATTACK_IRON_3, 0.9f);
        PCLEffects.Queue.add(new AnimatedSlashEffect(this.x, this.y - 30.0F * Settings.scale, -500.0F, -500.0F, 135.0F, 4.0F, c11, c12));
        PCLEffects.Queue.add(new AnimatedSlashEffect(this.x, this.y - 30.0F * Settings.scale, 500.0F, -500.0F, 225.0F, 4.0F, c21, c22));

        for (int i = 0; i < 15; ++i)
        {
            PCLEffects.Queue.add(new UpgradeShineParticleEffect(this.x + random(-40.0F, 40.0F) * Settings.scale, this.y + random(-40.0F, 40.0F) * Settings.scale));
        }
    }
}
