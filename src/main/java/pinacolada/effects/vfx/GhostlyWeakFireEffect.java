package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import pinacolada.effects.PCLEffect;

// TODO Change image
public class GhostlyWeakFireEffect extends PCLEffect
{
    protected AtlasRegion img;
    protected float x;
    protected float y;
    protected float vX;
    protected float vY;

    public GhostlyWeakFireEffect(float x, float y)
    {
        super(1f);

        int roll = random(0, 2);
        if (roll == 0)
        {
            this.img = ImageMaster.TORCH_FIRE_1;
        }
        else if (roll == 1)
        {
            this.img = ImageMaster.TORCH_FIRE_2;
        }
        else
        {
            this.img = ImageMaster.TORCH_FIRE_3;
        }

        this.x = x + random(-1f, 1f) * Settings.scale - (this.img.packedWidth / 2f);
        this.y = y + random(-1f, 1f) * Settings.scale - (this.img.packedHeight / 2f);
        this.vX = random(-2f, 2f) * Settings.scale;
        this.vY = random(0f, 80f) * Settings.scale;
        this.color = Color.SKY.cpy();
        this.color.a = 0f;
        this.scale = random(2f, 3f);
    }

    public void render(SpriteBatch sb)
    {
        renderImage(sb, img, x, y);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        super.updateInternal(deltaTime);

        x += vX * deltaTime;
        y += vY * deltaTime;
        color.a = duration / 2f;
    }
}
