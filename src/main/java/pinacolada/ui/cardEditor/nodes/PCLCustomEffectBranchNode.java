package pinacolada.ui.cardEditor.nodes;

import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;

import java.util.ArrayList;

public class PCLCustomEffectBranchNode extends PCLCustomEffectNode {

    public ArrayList<PCLCustomEffectNode> subNodes = new ArrayList<>();

    public PCLCustomEffectBranchNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }
}
