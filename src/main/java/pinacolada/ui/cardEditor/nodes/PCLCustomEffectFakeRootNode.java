package pinacolada.ui.cardEditor.nodes;

import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;

public class PCLCustomEffectFakeRootNode extends PCLCustomEffectNode {

    public PCLCustomEffectFakeRootNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }
}
