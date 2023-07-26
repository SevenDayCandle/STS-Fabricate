package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;
import pinacolada.utilities.PCLRenderHelpers;

public class PCLCustomEffectHologram extends EUIBase {
    public static PCLCustomEffectHologram current;
    protected ActionT1<PCLCustomEffectHologram> onRelease;
    public EUIImage image;
    public PCLCustomEffectNode highlighted;
    public PCLCustomEffectNode.NodeType type;

    public PCLCustomEffectHologram(EUIImage image, ActionT1<PCLCustomEffectHologram> onRelease) {
        this.image = new EUIImage(image).setScale(1.8f, 1.8f);
        this.onRelease = onRelease;
    }

    public static boolean isHighlighted(PCLCustomEffectNode node) {
        return current != null && current.highlighted == node;
    }

    public static PCLCustomEffectHologram queue(EUIImage image, PCLCustomEffectNode.NodeType type, ActionT1<PCLCustomEffectHologram> onRelease) {
        current = new PCLCustomEffectHologram(image, onRelease);
        current.type = type;
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
        if (highlighted != null && highlighted.shouldReject(this)) {
            PCLRenderHelpers.draw(sb, EUIRM.images.x.texture(), image.hb.x - scale(32), image.hb.y, scale(16), scale(16));
        }
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
