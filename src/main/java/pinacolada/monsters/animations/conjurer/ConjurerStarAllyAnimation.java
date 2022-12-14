package pinacolada.monsters.animations.conjurer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameEffects;
import pinacolada.utilities.PCLRenderHelpers;
import pinacolada.utilities.RandomizedList;

public class ConjurerStarAllyAnimation extends PCLAllyAnimation
{
    private static final TextureCache[] particles = {PGR.core.images.monsters.fireParticle1, PGR.core.images.monsters.fireParticle2, PGR.core.images.monsters.fireParticle3};
    private static final TextureCache[] FRAMES = {PGR.core.images.monsters.chaos1, PGR.core.images.monsters.chaos2, PGR.core.images.monsters.chaos3};
    private static final RandomizedList<TextureCache> textures = new RandomizedList<>();
    protected static final float BASE_PROJECTILE_TIMER = 0.1F;
    public static final float RADIUS = 320;

    private float projVfxTimer = BASE_PROJECTILE_TIMER;

    public static Texture getRandomTexture()
    {
        if (textures.size() <= 1) // Adds some randomness but still ensures all textures are cycled through
        {
            textures.addAll(particles);
        }

        return textures.retrieveUnseeded(true).texture();
    }

    public ConjurerStarAllyAnimation(PCLCreature creature)
    {
        super(creature);
    }

    public void update(float deltaTime, float x, float y)
    {
        super.update(deltaTime, x, y);
        this.projVfxTimer -= deltaTime;
        if (this.projVfxTimer < 0.0F)
        {
            float r = MathUtils.random(0, 360f);
            GameEffects.Queue.add(new FadingParticleEffect(PGR.core.images.monsters.chaosOrbital.texture(), x, y)
                    .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                    .setScale(scale * MathUtils.random(0.03f, 0.15f))
                    .setRotation(0f, MathUtils.random(150f, 360f))
                    .setTargetPosition(x + RADIUS * MathUtils.cos(r), y + RADIUS * MathUtils.sin(r))).setDuration(1f, false)
                    .renderBehind = true;
            this.projVfxTimer = BASE_PROJECTILE_TIMER;
        }
    }


    public void updateImpl(float deltaTime, float x, float y)
    {
        GameEffects.Queue.add(new FadingParticleEffect(getRandomTexture(), x + MathUtils.random(-64, 64), y + MathUtils.random(-32, 4))
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setFlip(MathUtils.randomBoolean(), false)
                .setScale(MathUtils.random(0.09f, 0.64f))
                .setRotation(0, MathUtils.random(400f, 600f))
                .setTargetPosition(x, y + RADIUS, 100f)).setDuration(0.6f, false);
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
        int size = FRAMES[0].texture().getHeight();
        int hSize = size / 2;
        sb.setColor(this.renderColor);
        float by = owner.getBobEffect().y;
        sb.setBlendFunction(770, 1);
        float scale1 = Interpolation.sine.apply(0.8f, 1f, angle / 105);
        sb.draw(FRAMES[0].texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, scale1, scale1, angle, 0, 0, 96, 96, hFlip, vFlip);
        this.shineColor.a = Interpolation.sine.apply(0.4f, 0.7f, -angle / 65);
        sb.setColor(this.shineColor);
        float scale2 = Interpolation.sine.apply(0.8f, 1f, -angle / 125);
        sb.draw(FRAMES[1].texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, scale2, scale2, angle * 0.5f, 0, 0, 96, 96, !hFlip, vFlip);
        this.shineColor.a = Interpolation.sine.apply(0.4f, 0.7f, angle / 95);
        sb.setColor(this.shineColor);
        sb.draw(FRAMES[2].texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, scale2, scale2, -angle, 0, 0, 96, 96, !hFlip, vFlip);

        sb.setColor(Color.WHITE);
    }
}
