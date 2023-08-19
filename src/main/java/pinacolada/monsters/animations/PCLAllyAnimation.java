package pinacolada.monsters.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.monsters.PCLCreature;

public class PCLAllyAnimation extends PCLIntervalAnimation {
    public static final float RADIUS = 320;
    protected Color shineColor;
    protected float scale = 0.45f;
    protected float angle;
    protected float rate = 40f;
    protected float transitionAlpha;
    protected float targetTransitionAlpha = 1f;
    public PCLCreature owner;

    public PCLAllyAnimation(PCLCreature owner) {
        this(owner, 0.3f, 0.4f);
    }

    public PCLAllyAnimation(PCLCreature owner, float intMin, float intMax) {
        super(intMin, intMax);
        renderColor = Color.WHITE.cpy();
        renderColor.a = transitionAlpha;
        this.shineColor = renderColor.cpy();
        this.owner = owner;
    }

    public static Texture getRandomTexture() {
        return null;
    }

    public void fadeIn() {
        renderColor.a = transitionAlpha = 0f;
        targetTransitionAlpha = 1f;
    }

    public float getTransitionRate() {
        return 5f;
    }

    public void highlight() {
        targetTransitionAlpha = 1f;
    }

    public void playActAnimation(float x, float y) {
        PCLEffects.TopLevelQueue.add(VFX.circularWave(x, y).setScale(0.25f, 12f));
    }

    public void renderSprite(SpriteBatch sb, float x, float y) {
    }

    public void unhighlight() {
        targetTransitionAlpha = 0f;
    }

    public void update(float deltaTime, float x, float y) {
        super.update(deltaTime, x, y);
        angle += deltaTime * rate;
        renderColor.a = transitionAlpha = MathUtils.lerp(transitionAlpha, targetTransitionAlpha, EUI.delta() * getTransitionRate());
    }
}
