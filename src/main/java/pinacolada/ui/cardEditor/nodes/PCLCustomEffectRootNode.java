package pinacolada.ui.cardEditor.nodes;

import extendedui.EUIUtils;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;
import pinacolada.ui.cardEditor.PCLCustomPowerEffectPage;

import java.util.List;

public class PCLCustomEffectRootNode extends PCLCustomEffectNode {

    public PCLCustomEffectRootNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }

    // For root nodes, we should show triggers while underneath a power or relic
    public List<PSkill> getEffects() {
        if (effects == null) {
            NodeType targetType = editor instanceof PCLCustomPowerEffectPage ? NodeType.Trigger : NodeType.Limit;
            effects = EUIUtils.map(targetType.getSkills(editor.screen.getBuilder().getCardColor()),
                    bc -> bc.scanForTips(bc.getSampleText(editor.rootEffect)));
        }
        return effects;
    }

    public void initializeDefaultSkill()
    {
        getEffects();
        this.skill = new PRoot();
    }
}
