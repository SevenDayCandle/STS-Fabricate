package pinacolada.ui.cardEditor.nodes;

import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;

import java.util.ArrayList;
import java.util.List;

public class PCLCustomEffectMultiNode extends PCLCustomEffectNode {

    public ArrayList<PCLCustomEffectNode> subnodes = new ArrayList<>();

    public PCLCustomEffectMultiNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }

    @Override
    protected void receiveNode(PCLCustomEffectNode node) {
        node.extractSelf();
        node.skill.setChild((PSkill<?>) null);
        addSubnode(node);
    }

    // When initializing this node through createTree, also create nodes for the subeffects
    @Override
    public PCLCustomEffectNode makeSkillChild() {
        if (skill instanceof PMultiBase)
        {
            List<? extends PSkill<?>> subEffects = ((PMultiBase<?>) skill).getSubEffects();
            float offsetX = subEffects.size() * SIZE_X * -0.5f;
            for (PSkill<?> subskill : subEffects)
            {
                addSubnode(getNodeForSkill(editor, subskill, new OriginRelativeHitbox(hb, SIZE_X, SIZE_Y, offsetX, DISTANCE_Y)));
                offsetX += SIZE_X;
            }
        }
        return super.makeSkillChild();
    }

    public void addSubnode(PCLCustomEffectNode node)
    {
        subnodes.add(node);
        node.parent = this;
        addEffect(node.skill);
    }

    public void addEffect(PSkill<?> effect)
    {
        if (skill instanceof PMultiSkill)
        {
            ((PMultiSkill) skill).addEffect(effect);
        }
        else if (skill instanceof PMultiCond && effect instanceof PCond)
        {
            ((PMultiCond) skill).addEffect((PCond<?>) effect);
        }
    }


}
