package pinacolada.ui.editor.nodes;

import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectPage;

public class PCLCustomEffectMultiBranchNode extends PCLCustomEffectMultiCondNode {

    public PCLCustomEffectMultiBranchNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }

    protected float getOffsetX()
    {
        return SIZE_X * 2.2f;
    }

    protected float getOffsetY()
    {
        return SIZE_Y * 0.5f;
    }
}
