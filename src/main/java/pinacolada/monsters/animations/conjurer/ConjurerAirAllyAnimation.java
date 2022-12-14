package pinacolada.monsters.animations.conjurer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import extendedui.ui.TextureCache;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;
import pinacolada.utilities.RandomizedList;

public class ConjurerAirAllyAnimation extends PCLAllyAnimation
{
    private static final TextureCache[] particles = {PGR.core.images.monsters.airCloud1, PGR.core.images.monsters.airCloud2};
    private static final RandomizedList<TextureCache> textures = new RandomizedList<>();
    public static final float RADIUS = 320;

    public static Texture getRandomTexture()
    {
        if (textures.size() <= 1) // Adds some randomness but still ensures all textures are cycled through
        {
            textures.addAll(particles);
        }

        return textures.retrieveUnseeded(true).texture();
    }

    public ConjurerAirAllyAnimation(PCLCreature creature)
    {
        super(creature);
    }

    public void updateImpl(float deltaTime, float x, float y)
    {
        PCLEffects.List.add(new FadingParticleEffect(getRandomTexture(), x, y)
                .setColor(new Color(MathUtils.random(0.7f, 1f), 1, MathUtils.random(0.8f, 1f), MathUtils.random(0.5f, 0.8f)))
                .setBlendingMode(MathUtils.randomBoolean() ? PCLRenderHelpers.BlendingMode.Overlay : PCLRenderHelpers.BlendingMode.Normal)
                .setScale(scale * MathUtils.random(0.11f, 0.32f)).setRotation(0f, MathUtils.random(150f, 250f))
                .setTargetPosition(x + RADIUS * MathUtils.cos(angle), y + RADIUS * MathUtils.sin(angle), 100f)
        ).setDuration(1f, false).renderBehind = true;
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
        sb.setColor(this.renderColor);
        float scaleExt1 = owner.getBobEffect().y / 77f;
        float scaleExt2 = owner.getBobEffect().y / 65f;
        float angleExt1 = this.angle * 1.3f;
        float angleExt2 = this.angle * 1.1f;
        int size = PGR.core.images.monsters.air1.texture().getHeight();
        int hSize = size / 2;

        sb.draw(PGR.core.images.monsters.air1.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt1, this.scale + scaleExt1, angleExt1, 0, 0, 96, 96, hFlip, vFlip);
        sb.draw(PGR.core.images.monsters.air2.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt2, this.scale + scaleExt2, angleExt2, 0, 0, 96, 96, !hFlip, vFlip);

        sb.setBlendFunction(770, 1);
        this.shineColor.a = Interpolation.sine.apply(0.1f, 0.33f, angleExt2 / 185);
        sb.setColor(this.shineColor);
        sb.draw(PGR.core.images.monsters.air1.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt1, this.scale + scaleExt1, this.angle * 1.8f, 0, 0, 96, 96, hFlip, vFlip);

        this.shineColor.a = Interpolation.sine.apply(0.1f, 0.33f, angleExt1 / 135);
        sb.setColor(this.shineColor);
        sb.draw(PGR.core.images.monsters.air3.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt1, this.scale + scaleExt1, this.angle * 1.2f, 0, 0, 96, 96, hFlip, vFlip);
        sb.setBlendFunction(770, 771);

        sb.setColor(Color.WHITE);
    }
}
