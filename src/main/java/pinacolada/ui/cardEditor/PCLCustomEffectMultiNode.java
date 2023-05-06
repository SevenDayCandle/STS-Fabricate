package pinacolada.ui.cardEditor;

import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PSkill;

import java.util.ArrayList;

public class PCLCustomEffectMultiNode extends PCLCustomEffectNode {

    public ArrayList<PCLCustomEffectNode> subNodes = new ArrayList<>();

    public PCLCustomEffectMultiNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }
}
