package pinacolada.ui.editor.nodes;

import com.badlogic.gdx.graphics.Color;
import extendedui.EUIRenderHelpers;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.utilities.EUIColors;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectHologram;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomEffectProxyNode extends PCLCustomEffectNode {
    protected static final Color FADE_COLOR = EUIColors.white(0.5f);
    public final PCLCustomEffectMultiCondNode controller;

    public PCLCustomEffectProxyNode(PCLCustomEffectPage editor, PCLCustomEffectMultiCondNode controller, EUIHitbox hb) {
        super(editor, null, NodeType.Proxy, hb);
        this.controller = controller;
        this.hb.resize(SIZE_Y, SIZE_Y);
        this.hb.update();
        this.deleteButton.setActive(false);
        setShaderMode(EUIRenderHelpers.ShaderMode.Normal);
        setOnClick(this::startEdit);
        this.setTargetColor(FADE_COLOR);
    }

    @Override
    public void initializeDefaultSkill() {
    }

    @Override
    public PCLCustomEffectNode makeSkillChild() {
        return this.child;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (PCLCustomEffectHologram.current != null) {
            this.setTargetColor(Color.WHITE);
        }
        else {
            this.setTargetColor(FADE_COLOR);
        }
    }

    @Override
    public void reassignChild(PCLCustomEffectNode node) {
    }

    @Override
    public void receiveNode(PCLCustomEffectNode node) {
        node.extractSelf();
        controller.skill.setChild(node.skill);
    }

    @Override
    public void refresh() {
        this.tooltip = new EUIHeaderlessTooltip(PGR.core.strings.cetut_blankProxy);
    }

    @Override
    public boolean shouldReject(PCLCustomEffectHologram hologram) {
        if (controller.type == NodeType.Branchcond) {
            switch (hologram.type) {
                case Delay:
                case Branchcond:
                case Trait:
                    return true;
            }
        }
        return false;
    }

    @Override
    public void startEdit() {
    }
}
