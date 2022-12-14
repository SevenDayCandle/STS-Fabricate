package pinacolada.monsters.animations;

import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PCLAnimation extends AbstractAnimation
{
    public boolean hFlip;
    public boolean vFlip;
    public float alpha;
    protected float vfxTimer;
    protected Color renderColor;

    public PCLAnimation()
    {
    }

    public void setFlip(boolean horizontal, boolean vertical)
    {
        hFlip = horizontal;
        vFlip = vertical;
    }

    public void update(float deltaTime, float x, float y)
    {
        this.vfxTimer -= deltaTime;
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {

    }

    @Override
    public Type type()
    {
        return Type.SPRITE;
    }
}
