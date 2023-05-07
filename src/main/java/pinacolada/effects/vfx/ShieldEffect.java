package pinacolada.effects.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.PCLRenderHelpers;

public class ShieldEffect extends PCLEffect {
    private final float x;
    private final float sY;
    private final float tY;
    public final TextureRegion image;
    private float y;

    public ShieldEffect(float x, float y) {
        super(0.6f, true);

        this.image = PCLAttackVFX.SHIELD.texture;
        this.color = Color.WHITE.cpy();
        this.scale = 1;
        this.x = x;
        this.y = this.sY = y + (80.0F * Settings.scale);
        this.tY = y;
    }

    @Override
    protected void firstUpdate() {
        updateInternal(getDeltaTime());
    }

    @Override
    public void render(SpriteBatch sb) {
        if (this.image != null) {
            PCLRenderHelpers.drawCentered(sb, color, image, x, y, image.getRegionWidth(), image.getRegionHeight(), scale, rotation);
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    protected void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);

        if (isDone) {
            this.color.a = 0.0f;
        }
        else if (this.duration < 0.2f) {
            this.color.a = this.duration * 5.0f;
        }
        else {
            this.color.a = Interpolation.fade.apply(1.0f, 0.0f, duration * 0.75f / startingDuration);
        }

        this.y = Interpolation.exp10In.apply(tY, sY, duration / startingDuration);
    }
}
