package pinacolada.monsters.animations.pcl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.monsters.PCLCreature;
import pinacolada.monsters.animations.PCLAllyAnimation;
import pinacolada.resources.pcl.PCLCoreImages;

public class PCLGeneralAllyAnimation extends PCLAllyAnimation {
    public static final float RADIUS = 320;

    public PCLGeneralAllyAnimation(PCLCreature creature) {
        super(creature);
    }

    public void renderSprite(SpriteBatch sb, float x, float y) {
        sb.setColor(this.renderColor);
        Texture metal = PCLCoreImages.Monsters.metal.texture();
        int size = metal.getHeight();
        float rSize = Settings.scale * size;
        float hSize = rSize / 2;
        float scaleExt = owner.getBobEffect().y / 535f;
        float scaleInt = -(owner.getBobEffect().y / 550f);
        float angleExt = this.angle;
        float angleInt = -(this.angle);

        sb.draw(metal, x - hSize, y - hSize * 0.6f, hSize, hSize, rSize, rSize, this.scale + scaleExt, this.scale + scaleExt, angleExt, 0, 0, size, size, hFlip, vFlip);
        sb.setBlendFunction(770, 1);
        this.shineColor.a = Interpolation.sine.apply(0.1f, 0.42f, angleExt / 185) * this.transitionAlpha;
        sb.setColor(this.shineColor);
        sb.draw(metal, x - hSize, y - hSize * 0.6f, hSize, hSize, rSize, rSize, this.scale + scaleInt, this.scale + scaleInt, angleInt, 0, 0, size, size, hFlip, vFlip);

        sb.setColor(Color.WHITE);
    }
}
