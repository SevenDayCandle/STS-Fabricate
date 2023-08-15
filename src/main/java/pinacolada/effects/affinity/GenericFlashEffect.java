package pinacolada.effects.affinity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.ui.controls.EUIButton;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.vfx.VisualEffect;
import pinacolada.ui.AffinityKeywordButton;

public class GenericFlashEffect extends VisualEffect {
    private final Texture img;
    private float baseScale;

    public GenericFlashEffect(AffinityKeywordButton button, boolean playSfx) {
        this(button.type.getIcon(), button.backgroundButton.hb.cX, button.backgroundButton.hb.cY, playSfx);
    }

    public GenericFlashEffect(Texture icon, float x, float y, boolean playSfx) {
        this.img = icon;

        if (playSfx) {
            PCLSFX.play(PCLSFX.BUFF_1);
        }

        this.duration = 1.5f;
        this.startingDuration = 1.5f;
        this.baseScale = this.scale = Settings.scale;
        this.color = new Color(1f, 1f, 1f, 0.5f);
        this.x = x;
        this.y = y;
    }

    public GenericFlashEffect(EUIButton button, boolean playSfx) {
        this(button.background.texture, button.hb.cX, button.hb.cY, playSfx);
    }

    public GenericFlashEffect setScale(float scale) {
        this.baseScale = this.scale = scale;

        return this;
    }

    public void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        if (this.duration > 0.5f) {
            this.scale = Interpolation.exp5Out.apply(2f * baseScale, baseScale * 0.5f, -(this.duration - 2f) / 1.5f);
        }
        else {
            this.color.a = Interpolation.fade.apply(0.5f, 0f, 1f - this.duration);
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        renderImage(sb, this.img, x, y, false, false);
    }

    public void dispose() {
    }
}

