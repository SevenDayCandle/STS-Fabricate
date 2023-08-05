package pinacolada.effects.vfx;

import com.badlogic.gdx.math.MathUtils;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.utilities.EUIColors;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.pcl.PCLCoreImages;

public class RockBurstEffect extends PCLEffect {
    public static final TextureCache[] IMAGES = {PCLCoreImages.Effects.earthParticle1, PCLCoreImages.Effects.earthParticle2, PCLCoreImages.Effects.earthParticle3};
    public static final int PROJECTILES = 40;
    public static final float RADIUS = 220;
    protected float x;
    protected float y;

    public RockBurstEffect(float startX, float startY, float scale) {
        super(0.7f, true);

        this.x = startX;
        this.y = startY;
        this.scale = scale;
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        PCLSFX.play(scale > 1 ? PCLSFX.BLUNT_HEAVY : PCLSFX.BLUNT_FAST, 0.9f, 1.1f);

        for (int i = 0; i < PROJECTILES; ++i) {
            float angle = random(-500f, 500f);
            PCLEffects.Queue.particle(EUIUtils.random(IMAGES).texture(), x, y)
                            .setColor(EUIColors.random(0.7f, 1f, true))
                            .setScale(scale * random(0.06f, 0.45f))
                            .setRotation(random(0, 360f), random(550f, 700f))
                            .setTargetPosition(x + RADIUS * MathUtils.cos(angle), y + RADIUS * MathUtils.sin(angle), random(60f, 280f))
                    .setAcceleration(0, -200f)
                    .setDuration(duration, true);
        }

        complete();
    }
}
