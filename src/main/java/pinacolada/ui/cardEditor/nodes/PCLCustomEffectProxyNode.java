package pinacolada.ui.cardEditor.nodes;

import com.badlogic.gdx.graphics.Color;
import extendedui.EUIRenderHelpers;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIColors;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.cardEditor.PCLCustomEffectHologram;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;

public class PCLCustomEffectProxyNode extends PCLCustomEffectNode {
    protected static final Color FADE_COLOR = EUIColors.white(0.5f);
    public final PCLCustomEffectMultiCondNode controller;

    public PCLCustomEffectProxyNode(PCLCustomEffectPage editor, PCLCustomEffectMultiCondNode controller, EUIHitbox hb) {
        super(editor, null, NodeType.Proxy, hb);
        this.controller = controller;
        this.hb.resize(SIZE_Y, SIZE_Y);
        this.hb.update();
        this.showText = false;
        this.deleteButton.setActive(false);
        setShaderMode(EUIRenderHelpers.ShaderMode.Normal);
        setOnClick(this::startEdit);
    }

    @Override
    public void receiveNode(PCLCustomEffectNode node) {
        node.extractSelf();
        node.skill.setChild((PSkill<?>) null);
        controller.skill.setChild(node.skill);
        if (child != null) {
            PSkill<?> copy = child.skill.makeCopy();
            if (!(node.skill instanceof PMultiBase && ((PMultiBase<?>) node.skill).tryAddEffect(copy))) {
                node.skill.setChild(copy);
            }
        }
    }

    @Override
    public void startEdit() {
    }

    @Override
    public void initializeDefaultSkill()
    {
        this.skill = new PRoot();
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (PCLCustomEffectHologram.current != null) {
            this.setTargetColor(Color.WHITE);
        }
        else
        {
            this.setTargetColor(FADE_COLOR);
        }
    }
}
