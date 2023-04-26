package pinacolada.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.panels.energyorb.EnergyOrbInterface;
import extendedui.ui.TextureCache;
import extendedui.utilities.EUIColors;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLEnergyOrb implements EnergyOrbInterface {
    public static final TextureCache[] DEFAULT_TEXTURES =
            {
                    PCLCoreImages.CardUI.orbBaseLayer,
                    PCLCoreImages.CardUI.orbTopLayer1,
                    PCLCoreImages.CardUI.orbTopLayer2,
                    PCLCoreImages.CardUI.orbTopLayer3,
                    PCLCoreImages.CardUI.orbTopLayer4,
                    };
    protected static final TextureCache DEFAULT_FLASH = PCLCoreImages.CardUI.orbFlash;
    protected static final float BASE_MULT = 3f;
    protected static final float ORB_IMG_SCALE = 1.15F * Settings.scale;
    protected static final TextureCache BASE_BORDER = PCLCoreImages.CardUI.orbBaseBorder;
    protected TextureCache flash;
    protected TextureCache[] images;
    protected float[] angleMults;
    protected float angle;

    public PCLEnergyOrb() {
        this(DEFAULT_TEXTURES, DEFAULT_FLASH, null);
    }

    public PCLEnergyOrb(TextureCache[] images, TextureCache flash, float[] angleMults) {
        this.images = images;
        this.flash = flash;
        assert images != null && images.length >= 1;
        if (angleMults != null) {
            this.angleMults = angleMults;
            assert angleMults.length == images.length;
        }
        else {
            this.angleMults = new float[images.length];
            float mult = BASE_MULT;
            for (int i = 0; i < this.angleMults.length; i++) {
                this.angleMults[i] = mult;
                mult *= -2;
            }
        }
    }

    public PCLEnergyOrb(TextureCache[] images, TextureCache flash) {
        this(images, flash, null);
    }

    public Texture getEnergyImage() {
        return this.flash.texture();
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        sb.draw(BASE_BORDER.texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, this.angle, 0, 0, 128, 128, false, false);
        if (enabled) {
            this.renderOrbLayer(sb, current_x, current_y);
        }
        else {
            PCLRenderHelpers.drawGrayscale(sb, (s) ->
                    this.renderOrbLayer(s, current_x, current_y));
        }
    }

    @Override
    public void updateOrb(int energyCount) {
        this.angle += energyCount == 0 ? Gdx.graphics.getDeltaTime() * 0.5f : Gdx.graphics.getDeltaTime() * 2;
    }

    protected void renderOrbLayer(SpriteBatch sb, float current_x, float current_y) {
        sb.draw(this.images[0].texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, this.angle * this.angleMults[0], 0, 0, 128, 128, false, false);
        sb.setColor(EUIColors.white(0.26f));
        PCLRenderHelpers.drawBlended(sb, PCLRenderHelpers.BlendingMode.Normal, (s) -> {
            for (int i = 1; i < images.length; i++) {
                s.draw(this.images[i].texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, this.angle * this.angleMults[i], 0, 0, 128, 128, false, false);
            }
        });
        sb.setColor(EUIColors.white(0.17f));
        PCLRenderHelpers.drawBlended(sb, PCLRenderHelpers.BlendingMode.Glowing, (s) -> {
            for (int i = images.length - 2; i < images.length; i++) {
                s.draw(this.images[i].texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 128.0F, 128.0F, ORB_IMG_SCALE, ORB_IMG_SCALE, -this.angle * this.angleMults[i], 0, 0, 128, 128, false, false);
                s.draw(this.images[i].texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 64.0F, 64.0F, ORB_IMG_SCALE / 2, ORB_IMG_SCALE / 2, this.angle * this.angleMults[i], 0, 0, 128, 128, false, false);
                s.draw(this.images[i].texture(), current_x - 64.0F, current_y - 64.0F, 64.0F, 64.0F, 32, 32, ORB_IMG_SCALE / 4, ORB_IMG_SCALE / 4, -this.angle * this.angleMults[i] + 90f, 0, 0, 128, 128, false, false);
            }
        });
    }
}
