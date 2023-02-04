package pinacolada.monsters.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLSlotAnimation extends PCLAnimation
{
    protected static final float RATE = 0.85f * (float) Math.PI;
    protected float transitionAlpha;
    public float targetTransitionAlpha;

    public PCLSlotAnimation()
    {
        super();
        renderColor = Color.WHITE.cpy();
        renderColor.a = transitionAlpha;
    }

    public void highlight()
    {
        targetTransitionAlpha = 1f;
    }

    public void unhighlight()
    {
        targetTransitionAlpha = 0f;
    }

    public void update(float deltaTime, float x, float y)
    {
        this.vfxTimer -= deltaTime;
        if (transitionAlpha > targetTransitionAlpha)
        {
            transitionAlpha = Math.max(transitionAlpha - deltaTime, targetTransitionAlpha);
        }
        else if (transitionAlpha < targetTransitionAlpha)
        {
            transitionAlpha = Math.min(transitionAlpha + deltaTime, targetTransitionAlpha);
        }
        alpha = (0.5f + 0.5f * MathUtils.sin(vfxTimer * RATE)) * transitionAlpha;
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
        renderColor.a = alpha;
        Texture t = PCLCoreImages.Monsters.emptyShadow.texture();
        PCLRenderHelpers.drawGlowing(sb, s -> PCLRenderHelpers.drawCentered(s, renderColor, t, x, y + t.getHeight() * 0.25f, t.getWidth(), t.getHeight(), 0.8f, 0f));
    }
}
