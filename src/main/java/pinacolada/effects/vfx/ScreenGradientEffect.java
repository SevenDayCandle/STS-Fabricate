package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;

public class ScreenGradientEffect extends PCLEffect {
    private final ShapeRenderer renderer;
    private final Color color2;
    private final Color color3;
    private final Color color4;
    private Color targetColor;
    private Color targetColor2;
    private Color targetColor3;
    private Color targetColor4;
    private Color actualColor;
    private Color actualColor2;
    private Color actualColor3;
    private Color actualColor4;
    private boolean looping;

    public ScreenGradientEffect(float duration, Color color1, Color color2) {
        this(duration, color1, color2, color1, color2);
    }

    public ScreenGradientEffect(float duration, Color color1, Color color2, Color color3, Color color4) {
        this(duration, color1, color2, color3, color4, color1, color2, color3, color4);
    }

    public ScreenGradientEffect(float duration, Color color1, Color color2, Color color3, Color color4, Color targetColor, Color targetColor2, Color targetColor3, Color targetColor4) {
        super(duration);
        renderer = new ShapeRenderer();
        this.color = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.targetColor = targetColor;
        this.targetColor2 = targetColor2;
        this.targetColor3 = targetColor3;
        this.targetColor4 = targetColor4;
        this.actualColor = color.cpy();
        this.actualColor2 = color2.cpy();
        this.actualColor3 = color3.cpy();
        this.actualColor4 = color4.cpy();
    }

    protected float getLerpAmount() {
        return 1 + 0.5f * MathUtils.sin(MathUtils.PI * duration / startingDuration);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.end();
        renderer.setProjectionMatrix(sb.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(0, 0, (float) Settings.WIDTH, (float) Settings.HEIGHT, actualColor, actualColor2, actualColor3, actualColor4);
        renderer.end();
        sb.begin();
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (looping) {
            duration -= deltaTime;
        }
        else {
            super.updateInternal(deltaTime);
        }
        super.updateInternal(deltaTime);
        float lerp = getLerpAmount();
        this.actualColor = EUIColors.lerp(color, targetColor, lerp);
        this.actualColor2 = EUIColors.lerp(color2, targetColor2, lerp);
        this.actualColor3 = EUIColors.lerp(color3, targetColor3, lerp);
        this.actualColor4 = EUIColors.lerp(color4, targetColor4, lerp);
    }

    public ScreenGradientEffect setLooping(boolean loop) {
        this.looping = loop;
        return this;
    }

    public ScreenGradientEffect setTargetColors(Color targetColor, Color targetColor2, Color targetColor3, Color targetColor4) {
        this.targetColor = targetColor;
        this.targetColor2 = targetColor2;
        this.targetColor3 = targetColor3;
        this.targetColor4 = targetColor4;
        return this;
    }
}
