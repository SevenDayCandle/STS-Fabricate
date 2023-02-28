package pinacolada.monsters.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import extendedui.EUI;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.monsters.PCLCreature;

public class PCLAllyAnimation extends PCLIntervalAnimation
{
    public static final float RADIUS = 320;
    public PCLCreature owner;
    protected Color shineColor;
    protected float scale = 0.45f;
    protected float angle;
    protected float rate = 40f;
    protected float transitionAlpha;
    protected float targetTransitionAlpha = 1f;

    public static Texture getRandomTexture()
    {
        return null;
    }

    public PCLAllyAnimation(PCLCreature owner)
    {
        super();
        renderColor = Color.WHITE.cpy();
        renderColor.a = transitionAlpha;
        this.shineColor = renderColor.cpy();
        this.owner = owner;
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
        super.update(deltaTime, x, y);
        angle += deltaTime * rate;
        renderColor.a = transitionAlpha = MathUtils.lerp(transitionAlpha, targetTransitionAlpha, EUI.delta() * 12f);
    }


    public void playActAnimation(float x, float y)
    {
        PCLEffects.TopLevelQueue.add(VFX.circularWave(x, y).setScale(0.25f, 12f));
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
    }
}
