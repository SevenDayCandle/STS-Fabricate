package pinacolada.ui.cardEditor.nodes;

import com.badlogic.gdx.graphics.Color;
import extendedui.EUIRenderHelpers;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIColors;
import pinacolada.skills.PSkill;
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
    }

    @Override
    public PCLCustomEffectNode makeSkillChild() {
        return this.child;
    }

    @Override
    public void reassignChild(PCLCustomEffectNode node) {
    }

    @Override
    public void startEdit() {
    }

    @Override
    public void initializeDefaultSkill()
    {
    }

    @Override
    public void refresh() {
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
