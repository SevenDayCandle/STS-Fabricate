package pinacolada.effects.special;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.utilities.EUIColors;
import pinacolada.augments.PCLAugment;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.resources.PGR;

public class ObtainAugmentEffect extends PCLEffectWithCallback<PCLAugment> {
    private final PCLAugment augment;
    private final float x;
    private final float y;

    public ObtainAugmentEffect(PCLAugment augment, float x, float y) {
        this.augment = augment;
        this.duration = this.startingDuration = 1.5f;
        this.color = EUIColors.white(0f);
        this.x = x;
        this.y = y;
        this.renderBehind = false;
    }

    protected void firstUpdate(float deltaTime) {
        PGR.dungeon.addAugment(augment.ID, 1);
    }

    public void render(SpriteBatch sb) {
        EUI.addPriorityPostRender(s -> {
            EUIRenderHelpers.draw(sb, augment.getTexture(), color, x, y, augment.getTexture().getWidth(), augment.getTexture().getHeight());
            FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, augment.getName(), x + augment.getTexture().getWidth() * 2f, y + augment.getTexture().getHeight() / 2f, color);
        });
    }

    public void dispose() {

    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (this.duration > this.startingDuration / 2f) {
            this.color.a = Interpolation.pow2In.apply(1f, 0f, (this.duration - 0.25f) * 4f);
        }
        else {
            this.color.a = Interpolation.pow2Out.apply(0f, 1f, this.duration * 4f);
        }
        super.updateInternal(deltaTime);
    }
}
