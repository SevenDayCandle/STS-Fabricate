package pinacolada.monsters.animations.pcl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.pcl.PCLCoreImages;

public class PCLGeneralAllyAnimation extends PCLAllyAnimation {
    public static final float RADIUS = 320;

    public PCLGeneralAllyAnimation(PCLCreature creature) {
        super(creature);
    }

    public void renderSprite(SpriteBatch sb, float x, float y) {
        Texture metal = PCLCoreImages.Monsters.metal.texture();
        sb.setColor(this.renderColor);
        float scaleExt = owner.getBobEffect().y / 535f;
        float scaleInt = -(owner.getBobEffect().y / 550f);
        float angleExt = this.angle;
        float angleInt = -(this.angle);
        int size = metal.getHeight();
        int hSize = size / 2;

        sb.draw(metal, x - hSize, y - hSize / 2f, hSize, hSize, size, size, this.scale + scaleExt, this.scale + scaleExt, angleExt, 0, 0, size, size, hFlip, vFlip);
        sb.setBlendFunction(770, 1);
        this.shineColor.a = Interpolation.sine.apply(0.1f, 0.42f, angleExt / 185) * this.transitionAlpha;
        sb.setColor(this.shineColor);
        sb.draw(metal, x - hSize, y - hSize / 2f, hSize, hSize, size, size, this.scale + scaleInt, this.scale + scaleInt, angleInt, 0, 0, size, size, hFlip, vFlip);

        sb.setColor(Color.WHITE);
    }
}
