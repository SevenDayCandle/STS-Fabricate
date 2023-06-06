package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class DarknessEffect extends PCLEffect {
    private static final TextureCache[] images = {PCLCoreImages.Effects.dark1, PCLCoreImages.Effects.dark2, PCLCoreImages.Effects.dark3, PCLCoreImages.Effects.dark4, PCLCoreImages.Effects.dark5};

    protected float x;
    protected float y;

    public DarknessEffect(float startX, float startY) {
        super(0.5f);

        this.x = startX;
        this.y = startY;
    }

    @Override
    protected void updateInternal(float deltaTime) {
        for (int i = images.length - 1; i >= 0; i--) {
            PCLEffects.Queue.add(new FadingParticleEffect(images[i].texture(), x, y)
                    .setColor(new Color(0.47f, 0.35f, 0.6f, 0.4f))
                    .setBlendingMode(i <= 2 ? PCLRenderHelpers.BlendingMode.Glowing : PCLRenderHelpers.BlendingMode.Normal)
                    .setScaleTarget(scale * MathUtils.random(0.2f, i < 2 ? 0.7f : 0.4f), scale * MathUtils.random(1.7f, i < 2 ? 4.3f : 2.6f), 2.2f)
                    .setRotation(MathUtils.random(-800f, 800f), MathUtils.random(400, 600f) * i % 2 == 0 ? -1 : 1)
                    .setDuration(MathUtils.random(0.8F, 1.0F), true));
        }

        super.updateInternal(deltaTime);
    }
}
