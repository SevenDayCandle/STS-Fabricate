package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.BorderLongFlashEffect;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

public class CataclysmEffect extends PCLEffect
{
    protected final static Hitbox sky_hb1 = new Hitbox(Settings.WIDTH * 0.52f, Settings.HEIGHT * 0.7f, 2, 2);
    protected final static Hitbox sky_hb2 = new Hitbox(Settings.WIDTH * 0.48f, Settings.HEIGHT * 0.7f, 2, 2);
    protected final RandomizedList<AbstractMonster> enemies = new RandomizedList<>();
    protected float delay1;
    protected float delay2;
    protected float delay3;
    protected boolean vertical;

    public CataclysmEffect()
    {
        super(4f, true);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        PCLEffects.Queue.add(new BorderLongFlashEffect(Color.VIOLET, false));
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        if (enemies.size() == 0)
        {
            this.enemies.addAll(GameUtilities.getEnemies(true));
        }

        delay1 -= deltaTime;
        if (delay1 < 0)
        {
            delay1 = random(0.1f, 0.3f);

            AbstractMonster target = enemies.retrieve(RNG);
            if (target != null)
            {
                PCLEffects.Queue.add(VFX.fireball(sky_hb1, target.hb).setColor(Color.RED, Color.ORANGE));
                PCLEffects.Queue.add(VFX.fireball(sky_hb2, target.hb).setColor(Color.GOLDENROD, Color.VIOLET));
                PCLEffects.Queue.add(VFX.meteorFall(target.hb));
            }
        }

        delay2 -= deltaTime;
        if (delay2 < 0)
        {
            delay2 = random(0.6f, 1f);
            PCLEffects.Queue.add(new BorderFlashEffect(randomBoolean(0.5f) ? Color.RED : Color.ORANGE));
            PCLEffects.Queue.add(VFX.whirlwind());
        }

        delay3 -= deltaTime;
        if (delay3 < 0)
        {
            delay3 = random(0.4f, 0.75f);
            if (randomBoolean(0.5f))
            {
                for (int f = 0; f < 18; f++)
                {
                    PCLEffects.Queue.add(VFX.fallingIce(18));
                }
            }
            else
            {
                for (AbstractMonster m : GameUtilities.getEnemies(true))
                {
                    PCLEffects.Queue.add(VFX.lightning(m.hb));
                }
            }
        }
    }
}
