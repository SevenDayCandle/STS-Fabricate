package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIInputManager;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

public class PCLCustomEffectHologram extends EUIBase {
    public static PCLCustomEffectHologram current;
    protected ActionT1<PCLCustomEffectHologram> onRelease;
    public EUIImage image;
    public PCLCustomEffectNode highlighted;

    public PCLCustomEffectHologram(EUIImage image, ActionT1<PCLCustomEffectHologram> onRelease) {
        this.image = new EUIImage(image);
        this.onRelease = onRelease;
    }

    public static boolean isHighlighted(PCLCustomEffectNode node) {
        return current != null && current.highlighted == node;
    }

    public static PCLCustomEffectHologram queue(EUIImage image, ActionT1<PCLCustomEffectHologram> onRelease) {
        current = new PCLCustomEffectHologram(image, onRelease);
        return current;
    }

    public static void setHighlighted(PCLCustomEffectNode node) {
        if (current != null) {
            current.highlighted = node;
        }
    }

    public static void updateAndRenderCurrent(SpriteBatch sb) {
        if (current != null) {
            current.render(sb);
            current.update();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.image.renderCentered(sb, EUIRenderHelpers.ShaderMode.Colorize, EUIRenderHelpers.BlendingMode.Glowing, image.hb, Color.SKY);
    }

    @Override
    public void updateImpl() {
        image.hb.move(InputHelper.mX, InputHelper.mY);
        if (EUIInputManager.leftClick.isJustReleased()) {
            onRelease.invoke(this);
            current = null;
        }
    }
}
