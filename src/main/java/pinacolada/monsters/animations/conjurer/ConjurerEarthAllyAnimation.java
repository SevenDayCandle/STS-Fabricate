package pinacolada.monsters.animations.conjurer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import pinacolada.effects.vfx.FadingParticleEffect;
import pinacolada.effects.vfx.RockBurstEffect;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameEffects;

public class ConjurerEarthAllyAnimation extends PCLAllyAnimation
{
    public static final float RADIUS = 320;

    public ConjurerEarthAllyAnimation(PCLCreature creature)
    {
        super(creature);
    }

    public void updateImpl(float deltaTime, float x, float y)
    {
        GameEffects.Queue.add(new FadingParticleEffect(RockBurstEffect.getRandomTexture(), x + MathUtils.random(-64, 64), y + MathUtils.random(-32, 4))
                .setFlip(MathUtils.randomBoolean(), false)
                .setScale(MathUtils.random(0.09f, 0.64f))
                .setRotation(0, MathUtils.random(400f, 600f))
                .setTargetPosition(x, y + RADIUS, 50f)).setDuration(0.6f, false);
    }

    public void renderSprite(SpriteBatch sb, float x, float y)
    {
        sb.setColor(this.renderColor);
        float angleExt = this.angle / 13f;
        int size = PGR.core.images.monsters.earth1.texture().getHeight();
        int hSize = size / 2;

        sb.draw(PGR.core.images.monsters.earth1.texture(), x - hSize, y - hSize, 48f, 48f, 96f, 96f, this.scale, this.scale, angleExt, 0, 0, 96, 96, hFlip, vFlip);

        sb.setColor(Color.WHITE);
    }
}
