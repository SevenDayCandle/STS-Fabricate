package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;

public class OrbEvokeParticle extends PCLEffect
{
    private static final int W_HALF = 70;
    private static final int W = W_HALF * 2;

    private final float x;
    private final float y;
    private final float rotationSpeed;
    private final boolean flipHorizontal;
    private final boolean flipVertical;
    private float scaleY;

    public OrbEvokeParticle(float x, float y, Color color)
    {
        super(0.25f, true);

        this.x = x;
        this.y = y;
        this.renderBehind = true;
        this.color = EUIColors.copy(color, 0.5f);
        this.rotation = random(-8.0F, 8.0F);
        this.flipHorizontal = randomBoolean();
        this.flipVertical = randomBoolean();
        this.scale = random(1.0F, 2.0F) * Settings.scale;
        this.scaleY = 2.0F * Settings.scale;
        this.rotationSpeed = random(-100.0F, 100.0F);
    }

    @Override
    public void render(SpriteBatch sb)
    {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(ImageMaster.DARK_ORB_ACTIVATE_VFX, x - W_HALF, y - W_HALF, W_HALF, W_HALF, W, W, scale, scaleY, rotation, 0, 0, W, W, flipHorizontal, flipVertical);
        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose()
    {

    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        this.rotation += deltaTime * this.rotationSpeed;
        this.scale = Interpolation.pow4Out.apply(5.0F, 1.0F, this.duration * 4.0F) * Settings.scale;
        this.scaleY = Interpolation.bounceOut.apply(0.2F, 2.0F, this.duration * 4.0F) * Settings.scale;
        this.color.a = Interpolation.pow5Out.apply(0.01F, 0.5F, this.duration * 4.0F);

        super.updateInternal(deltaTime);
    }
}
