package pinacolada.ui.editor.nodes;

import extendedui.EUIUtils;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.editor.PCLCustomEffectHologram;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.ui.editor.PCLCustomPowerEffectPage;
import pinacolada.ui.editor.card.PCLCustomAttackEffectPage;
import pinacolada.ui.editor.card.PCLCustomBlockEffectPage;
import pinacolada.ui.editor.relic.PCLCustomRelicEditRelicScreen;

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

    // For root nodes, we should show triggers while underneath a power or relic
    public List<PSkill<?>> getEffects() {
        if (effects == null) {
            effects = new ArrayList<>();
            NodeType targetType =
                    editor instanceof PCLCustomAttackEffectPage ? NodeType.Attack :
                            editor instanceof PCLCustomBlockEffectPage ? NodeType.Block :
                                    editor instanceof PCLCustomPowerEffectPage || editor.screen instanceof PCLCustomRelicEditRelicScreen ? NodeType.Trigger : NodeType.Limit;
            for (PSkill<?> sk : targetType.getSkills(editor.screen.getBuilder().getCardColor())) {
                if (!EUIUtils.any(sk.data.sourceTypes, s -> !s.isSourceAllowed(this))) {
                    if (skill != null && sk.effectID.equals(skill.effectID)) {
                        effects.add(skill);
                    }
                    else {
                        sk.scanForTips(sk.getSampleText(editor.rootEffect, parent != null ? parent.skill : null));
                        effects.add(sk);
                    }
                }
            }
        }
        return effects;
    }

    public void initializeDefaultSkill() {
        getEffects();
        this.skill = new PRoot();
    }

    public PCLCustomEffectNode makeSkillChild() {
        PSkill<?> sc = skill.getChild();
        if (sc != null) {
            this.child = getNodeForSkill(editor, sc, new RelativeHitbox(hb, SIZE_X, SIZE_Y, SIZE_Y / 2f, DISTANCE_Y));
            child.parent = this;
        }
        return this.child;
    }

    public void refresh() {
        setText(skill.getSampleText(null, null));
        this.tooltip = new EUIHeaderlessTooltip(EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, PGR.core.strings.cetut_blankPrimary, PGR.core.strings.cetut_effectPrimary));
    }

    public void startEdit() {
        ArrayList<? extends PPrimary> effects = EUIUtils.mapAsNonnull(getEffects(), s -> EUIUtils.safeCast(s, PPrimary.class));
        if (effects.size() > 0) {
            replaceSkill(effects.get(0));
            editor.fullRebuild();
            editor.startEdit(editor.root);
        }
    }

    // On power pages, we should not be able to put in anything unless we have a primary set
    public boolean shouldReject(PCLCustomEffectHologram current) {
        return editor instanceof PCLCustomPowerEffectPage;
    }
}
