package pinacolada.effects.affinity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLEffect;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.ui.AffinityKeywordButton;
import pinacolada.utilities.PCLRenderHelpers;

public class AffinityGlowEffect extends PCLEffect
{

    public static final Color FALLBACK_COLOR = Color.valueOf("30c8dcff");
    protected AffinityKeywordButton source;
    protected Texture img;
    protected float scale;

    public AffinityGlowEffect(AffinityKeywordButton source)
    {
        this(source, FALLBACK_COLOR.cpy());
    }

    public AffinityGlowEffect(AffinityKeywordButton source, Color gColor)
    {
        this.duration = 1.4F;
        this.color = gColor != null ? gColor : FALLBACK_COLOR.cpy();
        this.color.a = 0.45f;
        this.scale = 0.73f;
        this.img = PCLCoreImages.Core.borderSilhouette.texture();
        this.source = source;
    }

    public void render(SpriteBatch sb)
    {
        if (!this.isDone && this.duration >= 0.0F)
        {
            sb.setBlendFunction(PCLRenderHelpers.BlendingMode.Glowing.srcFunc, PCLRenderHelpers.BlendingMode.Glowing.dstFunc);
            PCLRenderHelpers.drawCentered(sb, color, this.img, source.backgroundButton.hb.cX, source.backgroundButton.hb.cY, source.backgroundButton.hb.width, source.backgroundButton.hb.height, scale, 0, false, false);
            sb.setBlendFunction(PCLRenderHelpers.BlendingMode.Normal.srcFunc, PCLRenderHelpers.BlendingMode.Normal.dstFunc);
        }
    }

    public void update()
    {
        if (this.duration < 0.0F)
        {
            complete();
            this.color.a = 0;
            this.scale = 0;
        }
        else
        {
            this.duration -= Gdx.graphics.getDeltaTime();
            this.scale = (0.73F + Interpolation.fade.apply(0F, 0.37F, Math.max(0, 1.4F - this.duration))) * Settings.scale;
            this.color.a = Interpolation.fade.apply(0.5F, 0F, Math.max(0, 1.4F - this.duration));
        }

    }

    public void dispose()
    {
    }

    @Override
    protected void firstUpdate()
    {
        this.color.a = 0.45f;
        this.scale = 0.73f;
    }
}