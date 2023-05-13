package pinacolada.ui.cardEditor.nodes;

import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;

import java.util.List;

public class PCLCustomEffectMultiCondNode extends PCLCustomEffectMultiNode {

    public PCLCustomEffectMultiCondNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }

    // When initializing this node through createTree, also create nodes for the subeffects, and for the controller node
    @Override
    public PCLCustomEffectNode makeSkillChild() {
        if (skill instanceof PMultiBase)
        {
            List<? extends PSkill<?>> subEffects = ((PMultiBase<?>) skill).getSubEffects();
            float offsetX = (subEffects.size() - 1) * SIZE_X * -0.7f;
            for (PSkill<?> subskill : subEffects)
            {
                addSubnode(createTree(editor, subskill, new OriginRelativeHitbox(hb, SIZE_X, SIZE_Y, offsetX, DISTANCE_Y)));
                offsetX += SIZE_X * 1.4f;
            }
        }
        PSkill<?> sc = skill.getChild();
        if (sc != null) {
            this.child = getNodeForSkill(editor, sc, new RelativeHitbox(hb, SIZE_X, SIZE_Y, getOffsetX(), getOffsetY()));
        }
        if (this.child == null || this.child instanceof PCLCustomEffectRootNode)
        {
            this.child = new PCLCustomEffectProxyNode(editor, this, new RelativeHitbox(hb, SIZE_X, SIZE_Y, getOffsetX(), getOffsetY()));
        }
        child.parent = this;
        return this.child;
    }

    protected float getOffsetX()
    {
        return SIZE_X * 0.5f;
    }

    protected float getOffsetY()
    {
        return DISTANCE_Y * 2;
    }

}
