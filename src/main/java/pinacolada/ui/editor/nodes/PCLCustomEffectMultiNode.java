package pinacolada.ui.editor.nodes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.editor.PCLCustomEffectHologram;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.List;

public class PCLCustomEffectMultiNode extends PCLCustomEffectNode {

    public ArrayList<PCLCustomEffectNode> subnodes = new ArrayList<>();

    public PCLCustomEffectMultiNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox sourceHb) {
        super(editor, skill, type, sourceHb);
    }

    public void addEffect(PSkill<?> effect) {
        if (skill instanceof PMultiBase<?>) {
            ((PMultiBase<?>) skill).tryAddEffect(effect);
        }
    }

    public void addSubnode(PCLCustomEffectNode node) {
        subnodes.add(node);
        node.index = subnodes.size() - 1;
        node.parent = this;
    }

    @Override
    protected void extractSelf() {
        if (child != null) {
            super.extractSelf();
            return;
        }

        PCLCustomEffectNode first = subnodes.size() > 0 ? subnodes.get(0) : null;
        if (first != null) {
            if (parent != null) {
                if (parent.skill instanceof PMultiBase && ((PMultiBase<?>) parent.skill).getSubEffects().remove(this.skill)) {
                    ((PMultiBase<?>) parent.skill).tryAddEffect(first.skill);
                }
                else {
                    parent.skill.setChild(first.skill);
                }
            }
            else if (editor.rootEffect == this.skill) {
                editor.rootEffect = new PRoot();
                editor.rootEffect.setChild(first.skill);
            }
        }
        else {
            super.extractSelf();
        }
    }

    // When initializing this node through createTree, also create nodes for the subeffects
    @Override
    public PCLCustomEffectNode makeSkillChild() {
        if (skill instanceof PMultiBase) {
            List<? extends PSkill<?>> subEffects = ((PMultiBase<?>) skill).getSubEffects();
            float offsetX = (subEffects.size() - 1) * SIZE_X * -0.7f;
            for (PSkill<?> subskill : subEffects) {
                addSubnode(createTree(editor, subskill, new OriginRelativeHitbox(hb, SIZE_X, SIZE_Y, offsetX, DISTANCE_Y)));
                offsetX += SIZE_X * 1.4f;
            }
        }
        return super.makeSkillChild();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (PCLCustomEffectNode subnode : subnodes) {
            PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, subnode.hb, 0, 0.15f, 0f, 6);
            subnode.render(sb);
        }
        super.renderImpl(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        for (PCLCustomEffectNode subnode : subnodes) {
            subnode.update();
        }
    }

    @Override
    public void reassignChild(PCLCustomEffectNode node) {
        addSubnode(node);
        addEffect(node.skill);
    }

    @Override
    public void refreshAll() {
        super.refreshAll();
        for (PCLCustomEffectNode subnode : subnodes) {
            subnode.refreshAll();
        }
    }


}
