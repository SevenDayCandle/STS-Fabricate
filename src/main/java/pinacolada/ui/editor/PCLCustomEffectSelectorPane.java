package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

import java.util.ArrayList;

public class PCLCustomEffectSelectorPane extends EUIImage {
    protected static final float SIZE_X = scale(40);
    protected static final float SIZE_Y = scale(30);
    public final PCLCustomEffectPage editor;
    protected PCLCustomEffectHologram hologram;
    public ArrayList<EUIButton> nodeCreateButtons = new ArrayList<>();

    public PCLCustomEffectSelectorPane(PCLCustomEffectPage editor) {
        super(EUIRM.images.longInput.texture(), new EUIHitbox(editor.hb.x + scale(250), editor.hb.y - scale(20), editor.hb.width * 2, editor.hb.height * 1.5f));
        this.editor = editor;

        initializeButtons();
    }

    protected void addNodeButton(PCLCustomEffectNode.NodeType type) {
        nodeCreateButtons.add(new EUIButton(type.getTexture(), new RelativeHitbox(hb, SIZE_X, SIZE_Y, (nodeCreateButtons.size() + 1) * SIZE_X * 1.1f, SIZE_Y * 0.9f))
                .setColor(type.getColor())
                .setShaderMode(EUIRenderHelpers.ShaderMode.Colorize)
                .setTooltip(type.getTooltip())
                .setOnPreClick((button) -> this.startHologram(button, type)));
    }

    public void initializeButtons() {
        addNodeButton(PCLCustomEffectNode.NodeType.Move);
        addNodeButton(PCLCustomEffectNode.NodeType.Multimove);
        addNodeButton(PCLCustomEffectNode.NodeType.Trait);
        addNodeButton(PCLCustomEffectNode.NodeType.Mod);
        addNodeButton(PCLCustomEffectNode.NodeType.Cond);
        addNodeButton(PCLCustomEffectNode.NodeType.Multicond);
        addNodeButton(PCLCustomEffectNode.NodeType.Branchcond);
        addNodeButton(PCLCustomEffectNode.NodeType.Delay);
    }

    protected void onHologramRelease(PCLCustomEffectHologram hologram, PCLCustomEffectNode.NodeType type) {
        if (hologram.highlighted != null) {
            PCLCustomEffectNode node = PCLCustomEffectNode.getNodeForType(editor, null, type, editor.hb);
            hologram.highlighted.receiveNode(node);
            editor.fullRebuild();
        }
        this.hologram = null;
    }

    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        for (EUIButton nodeButton : nodeCreateButtons) {
            nodeButton.render(sb);
        }
    }

    protected void startHologram(EUIButton button, PCLCustomEffectNode.NodeType type) {
        PCLCustomEffectHologram.queue(button.background, type, (h) -> this.onHologramRelease(h, type));
    }

    public void updateImpl() {
        super.updateImpl();
        for (EUIButton nodeButton : nodeCreateButtons) {
            nodeButton.updateImpl();
        }
    }

}
