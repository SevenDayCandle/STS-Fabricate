package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;

import java.util.ArrayList;

public class PCLCustomEffectSelectorPane extends EUIImage {
    protected static final float SIZE_X = scale(40);
    protected static final float SIZE_Y = scale(30);


    protected PCLCustomEffectHologram hologram;
    public final PCLCustomEffectPage editor;
    public ArrayList<EUIButton> nodeCreateButtons = new ArrayList<>();

    public PCLCustomEffectSelectorPane(PCLCustomEffectPage editor) {
        super(EUIRM.images.rectangularButton.texture(), new EUIHitbox(editor.hb.x + scale(250), editor.hb.y - scale(20), editor.hb.width * 2, editor.hb.height * 1.5f));
        this.editor = editor;

        initializeButtons();
    }

    public void initializeButtons()
    {
        addNodeButton(PCLCustomEffectNode.NodeType.Move);
        addNodeButton(PCLCustomEffectNode.NodeType.Multimove);
        addNodeButton(PCLCustomEffectNode.NodeType.Mod);
        addNodeButton(PCLCustomEffectNode.NodeType.Cond);
        addNodeButton(PCLCustomEffectNode.NodeType.Multicond);
        addNodeButton(PCLCustomEffectNode.NodeType.Delay);
    }

    protected void addNodeButton(PCLCustomEffectNode.NodeType type)
    {
        nodeCreateButtons.add(new EUIButton(type.getTexture(), new RelativeHitbox(hb, SIZE_X, SIZE_Y, (nodeCreateButtons.size() + 1) * SIZE_X * 1.1f, SIZE_Y * 0.9f))
                        .setColor(type.getColor())
                        .setTooltip(type.getTitle(), "")
                        .setOnPreClick((button) -> this.startHologram(button, type)));
    }

    protected void startHologram(EUIButton button, PCLCustomEffectNode.NodeType type)
    {
        PCLCustomEffectHologram.queue(button.background, (h) -> this.onHologramRelease(h, type));
    }

    protected void onHologramRelease(PCLCustomEffectHologram hologram, PCLCustomEffectNode.NodeType type) {
        if (hologram.highlighted != null)
        {
            PCLCustomEffectNode node = PCLCustomEffectNode.getNodeForType(editor, null, type, editor.hb);
            hologram.highlighted.reassignChild(node);
            editor.fullRebuild();
        }
        this.hologram = null;
    }

    public void updateImpl()
    {
        super.updateImpl();
        for (EUIButton nodeButton : nodeCreateButtons)
        {
            nodeButton.updateImpl();
        }
    }

    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        for (EUIButton nodeButton : nodeCreateButtons)
        {
            nodeButton.render(sb);
        }
    }

}
