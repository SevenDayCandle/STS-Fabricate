package pinacolada.ui.cardEditor.nodes;

import extendedui.EUIUtils;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;
import pinacolada.ui.cardEditor.PCLCustomPowerEffectPage;

import java.util.ArrayList;
import java.util.List;

public class PCLCustomEffectRootNode extends PCLCustomEffectNode {

    public PCLCustomEffectRootNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
        this.hb.resize(SIZE_Y, SIZE_Y);
        this.hb.update();
        this.showText = false;
        this.deleteButton.setActive(false);
        setOnClick(this::startEdit);
    }

    public PCLCustomEffectNode makeSkillChild() {
        PSkill<?> sc = skill.getChild();
        if (sc != null) {
            this.child = getNodeForSkill(editor, sc, new RelativeHitbox(hb, SIZE_X, SIZE_Y, SIZE_Y / 2f, DISTANCE_Y));
            child.parent = this;
        }
        return this.child;
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

    public void startEdit() {
        ArrayList<? extends PPrimary> effects = EUIUtils.mapAsNonnull(getEffects(), s -> EUIUtils.safeCast(s, PPrimary.class));
        if (effects.size() > 0)
        {
            this.editor.rootEffect = effects.get(0);
            editor.updateRootEffect();
            editor.startEdit(editor.root);
        }
    }

    public void initializeDefaultSkill()
    {
        getEffects();
        this.skill = new PRoot();
    }
}
