package pinacolada.monsters.animations.conjurer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import extendedui.EUIRenderHelpers;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.effects.vfx.SnowBurstEffect;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

public class ConjurerWaterAllyAnimation extends PCLAllyAnimation
{
    public static final float RADIUS = 320;

    public ConjurerWaterAllyAnimation(PCLCreature creature)
    {
        super(creature);
    }

    public void updateImpl(float deltaTime, float x, float y)
    {
        PCLEffects.Queue.add(new FadingParticleEffect(SnowBurstEffect.getRandomTexture(), x, y)
                .setBlendingMode(PCLRenderHelpers.BlendingMode.Glowing)
                .setScale(scale * MathUtils.random(0.38f, 0.62f))
                .setRotation(0, MathUtils.random(150f, 360f))
                .setTargetPosition(x + RADIUS * MathUtils.cos(angle), y + RADIUS * MathUtils.sin(angle), 100f)
        ).setDuration(1f, false)
                .renderBehind = true;
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
        sb.setColor(this.renderColor);
        float scaleExt = owner.getBobEffect().y / 75f;
        float scaleExt2 = owner.getBobEffect().y / 50f;
        float scaleInt = -(owner.getBobEffect().y / 100f);
        float angleExt = this.angle * 3f;
        float angleExt2 = this.angle * 4f;
        float angleInt = -(this.angle / 2f);
        int size = PGR.core.images.monsters.water1.texture().getHeight();
        int hSize = size / 2;

        PCLRenderHelpers.setBlending(sb, EUIRenderHelpers.BlendingMode.Glowing);
        this.shineColor.a = Interpolation.sine.apply(0.05f, 0.55f, angleExt / 135);
        sb.setColor(this.shineColor);
        sb.draw(PGR.core.images.monsters.water3.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt2, this.scale + scaleExt2, angleExt * 1.2f, 0, 0, 96, 96, hFlip, vFlip);

        this.shineColor.a = Interpolation.sine.apply(0.12f, 0.72f, angleExt2 / 185);
        sb.setColor(this.shineColor);
        sb.draw(PGR.core.images.monsters.water4.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt2, this.scale + scaleExt2, -angleExt2 * 0.7f, 0, 0, 96, 96, !hFlip, vFlip);

        PCLRenderHelpers.setBlending(sb, EUIRenderHelpers.BlendingMode.Normal);
        sb.setColor(this.renderColor);
        sb.draw(PGR.core.images.monsters.water1.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleInt, this.scale + scaleInt, angleInt * 1.3f, 0, 0, 96, 96, hFlip, vFlip);

        PCLRenderHelpers.setBlending(sb, EUIRenderHelpers.BlendingMode.Glowing);
        this.shineColor.a = Interpolation.sine.apply(0.22f, 0.42f, angleExt / 165);
        sb.setColor(this.shineColor);
        sb.draw(PGR.core.images.monsters.water2.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale + scaleExt, this.scale + scaleExt, -angleExt * 1.4f, 0, 0, 96, 96, hFlip, vFlip);
        PCLRenderHelpers.setBlending(sb, EUIRenderHelpers.BlendingMode.Normal);

        sb.setColor(Color.WHITE);
    }
}
