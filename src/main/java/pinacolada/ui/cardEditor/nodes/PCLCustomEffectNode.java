package pinacolada.ui.cardEditor.nodes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.*;
import pinacolada.skills.skills.*;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.cardEditor.PCLCustomEffectHologram;
import pinacolada.ui.cardEditor.PCLCustomEffectPage;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.List;

public class PCLCustomEffectNode extends EUIButton {
    public static final float SIZE_X = scale(120);
    public static final float SIZE_Y = scale(60);
    public static final float DISTANCE_Y = scale(-100);
    private boolean dragging;
    protected List<PSkill> effects;
    protected PCLCustomEffectHologram hologram;
    public PSkill<?> skill;
    public PCLCustomEffectPage editor;
    public PCLCustomEffectNode parent;
    public PCLCustomEffectNode child;
    public NodeType type;
    public EUIButton deleteButton;
    public int index = -1;

    // Call getNodeForSkill instead to get the correct node constructor for the given skill type
    protected PCLCustomEffectNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox hb) {
        super(type.getTexture(), hb);
        this.setColor(type.getColor());
        this.setShaderMode(EUIRenderHelpers.ShaderMode.Colorize);
        this.editor = editor;
        this.type = type;
        this.skill = skill;

        if (this.skill == null) {
            initializeDefaultSkill();
        }

        this.text = StringUtils.capitalize(this.skill.getSampleText(null));
        this.fontScale = 0.5f;
        setSmartText(true, true);
        setOnClick(this::startEdit);

        this.deleteButton = new EUIButton(EUIRM.images.x.texture(), new RelativeHitbox(hb, 48, 48, hb.width * 1.2f, hb.height * 0.4f));
        this.deleteButton.setOnClick(() -> {
            if (canRemove()) {
                deleteSelf();
            }
        });
    }

    public static PCLCustomEffectNode createTree(PCLCustomEffectPage editor, PSkill<?> skill, EUIHitbox hb) {
        PCLCustomEffectNode root = getNodeForSkill(editor, skill, hb);
        PCLCustomEffectNode cur = root;
        while (cur != null) {
            cur = cur.makeSkillChild();
        }
        return root;
    }

    public static PCLCustomEffectNode getNodeForSkill(PCLCustomEffectPage editor, PSkill<?> skill, EUIHitbox hb) {
        return getNodeForType(editor, skill, NodeType.getTypeForSkill(skill), hb);
    }

    public static PCLCustomEffectNode getNodeForType(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox hb) {
        switch (type) {
            case Root:
                return new PCLCustomEffectRootNode(editor, skill, type, hb);
            case Multicond:
                return new PCLCustomEffectMultiCondNode(editor, skill, type, hb);
            case Multimove:
                return new PCLCustomEffectMultiNode(editor, skill, type, hb);
            case Branchcond:
                return new PCLCustomEffectMultiBranchNode(editor, skill, type, hb);
        }
        return new PCLCustomEffectNode(editor, skill, type, hb);
    }

    public boolean canRemove() {
        return type.canRemove();
    }

    public void deleteSelf() {
        extractSelf();
        editor.fullRebuild();
    }

    protected void extractSelf() {
        if (parent != null) {
            if (parent.skill instanceof PMultiBase && ((PMultiBase<?>) parent.skill).getSubEffects().remove(this.skill))
            {
                if (child != null) {
                    ((PMultiBase<?>) parent.skill).tryAddEffect(child.skill);
                }
            }
            else if (child != null) {
                parent.skill.setChild(child.skill);
            }
            else {
                parent.skill.setChild((PSkill<?>) null);
            }
        }
        else if (editor.rootEffect == this.skill)
        {
            editor.rootEffect = editor.makeRootSkill();
            if (child != null)
            {
                editor.rootEffect.setChild(child.skill);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public List<PSkill> getEffects() {
        if (effects == null) {
            effects = EUIUtils.map(type.getSkills(editor.screen.getBuilder().getCardColor()),
                    bc -> skill != null && bc.effectID.equals(skill.effectID) ? skill : bc.scanForTips(bc.getSampleText(editor.rootEffect)));
        }
        return effects;
    }

    public void initializeDefaultSkill()
    {
        getEffects();
        this.skill = EUIUtils.find(effects, ef -> editor.rootEffect.isSkillAllowed(ef));

        if (this.skill == null) {
            try {
                this.skill = effects.size() > 0 ? effects.get(0) : type.getSkillClass().newInstance();
            }
            catch (Exception e)
            {
                this.skill = new PRoot();
            }
        }
    }

    public boolean isDragging() {
        return dragging || (child != null && child.isDragging());
    }

    public PCLCustomEffectNode makeSkillChild() {
        PSkill<?> sc = skill.getChild();
        if (sc != null) {
            this.child = getNodeForSkill(editor, sc, new RelativeHitbox(hb, SIZE_X, SIZE_Y, SIZE_X / 2f, DISTANCE_Y));
            child.parent = this;
        }
        return this.child;
    }

    protected void onHologramRelease(PCLCustomEffectHologram hologram) {
        if (hologram.highlighted != this && hologram.highlighted != null) {
            hologram.highlighted.receiveNode(this);
            editor.fullRebuild();
        }
        this.hologram = null;
        dragging = false;
    }

    public void reassignChild(PCLCustomEffectNode node) {
        skill.setChild(node.skill);
        if (child != null) {
            PSkill<?> copy = child.skill.makeCopy();
            if (!(node.skill instanceof PMultiBase && ((PMultiBase<?>) node.skill).tryAddEffect(copy))) {
                node.skill.setChild(copy);
            }
        }
        child = node;
    }

    public void receiveNode(PCLCustomEffectNode node) {
        if (node.parent != this) {
            node.extractSelf();
            node.skill.setChild((PSkill<?>) null);
            reassignChild(node);
        }
    }

    public void refresh() {
        this.text = skill.getSampleText(null);
    }

    public void refreshAll() {
        refresh();
        if (child != null) {
            child.refreshAll();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (child != null) {
            PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, child.hb, 0, 0.15f, 0f, 6);
            child.render(sb);
        }
        super.renderImpl(sb);
        deleteButton.render(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (child != null) {
            child.update();
        }
        if (dragging && !hb.hovered && hologram == null) {
            hologram = PCLCustomEffectHologram.queue(this.background, this::onHologramRelease);
        }
        if (hb.hovered && hologram != PCLCustomEffectHologram.current) {
            PCLCustomEffectHologram.setHighlighted(this);
        }
        deleteButton.update();
    }

    protected void onClickStart() {
        super.onClickStart();
        dragging = true;
    }

    protected void onLeftClick() {
        super.onLeftClick();
        dragging = false;
    }

    public void replaceSkill(PSkill<?> skill) {
        skill.setChild(this.skill.getChild());
        this.skill = skill;
        if (parent != null) {
            if (!(this.parent.skill instanceof PMultiBase) || !((PMultiBase<?>) this.parent.skill).tryReplaceEffect(skill, index))
            {
                this.parent.skill.setChild(skill);
            }
        }
        else if (this.skill instanceof PPrimary)
        {
            this.editor.rootEffect = (PPrimary<?>) this.skill;
        }
    }

    public void startEdit() {
        editor.startEdit(this);
        dragging = false;
    }

    public enum NodeType {
        Cond,
        Multicond,
        Branchcond,
        Mod,
        Move,
        Multimove,
        Delay,
        Limit,
        Trigger,
        Attack,
        Block,
        CustomPower,
        Proxy,
        Root,;

        // Overriding classes are listed later in the enum
        public static NodeType getTypeForSkill(PSkill<?> skill) {
            NodeType cur = Root;
            for (NodeType type : values()) {
                if (type.getSkillClass().isInstance(skill)) {
                    cur = type;
                }
            }
            return cur;
        }

        public boolean canRemove() {
            switch (this)
            {
                case Attack:
                case Block:
                case Root:
                case Proxy:
                    return false;
            }
            return true;
        }

        public Color getColor() {
            switch (this) {
                case Cond:
                    return Color.SKY;
                case Multicond:
                    return Color.NAVY;
                case Branchcond:
                    return Color.BLUE;
                case Mod:
                    return Color.FOREST;
                case Move:
                    return Color.GOLDENROD;
                case Multimove:
                    return Color.ORANGE;
                case Delay:
                    return Color.RED;
                case Limit:
                case Trigger:
                case Attack:
                case Block:
                    return Color.SALMON;
                case Proxy:
                    return PCLCustomEffectProxyNode.FADE_COLOR;
            }
            return Color.WHITE;
        }

        // Because PCond/PMod/PMove cannot be reified further
        @SuppressWarnings("rawtypes")
        public Class<? extends PSkill> getSkillClass() {
            switch (this) {
                case Cond:
                    return PCond.class;
                case Multicond:
                    return PMultiCond.class;
                case Branchcond:
                    return PBranchCond.class;
                case Mod:
                    return PMod.class;
                case Move:
                    return PMove.class;
                case Multimove:
                    return PMultiSkill.class;
                case Delay:
                    return PDelay.class;
                case Limit:
                    return PLimit.class;
                case Trigger:
                    return PTrigger.class;
                case Attack:
                    return PCardPrimary_DealDamage.class;
                case Block:
                    return PCardPrimary_GainBlock.class;
                case CustomPower:
                    return PMove_StackCustomPower.class;
            }
            return PRoot.class;
        }

        @SuppressWarnings("rawtypes")
        public List<? extends PSkill> getSkills(AbstractCard.CardColor color) {
            return (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(getSkillClass()) : PSkill.getEligibleEffects(getSkillClass(), color));
        }

        public Texture getTexture() {
            switch (this) {
                case Cond:
                    return PCLCoreImages.Menu.nodeCircle.texture();
                case Multicond:
                case Branchcond:
                    return PCLCoreImages.Menu.nodeCircle2.texture();
                case Mod:
                    return PCLCoreImages.Menu.nodeHexagon.texture();
                case Delay:
                    return PCLCoreImages.Menu.nodeTriangle.texture();
                case Limit:
                case Trigger:
                case Attack:
                case Block:
                    return PCLCoreImages.Menu.nodeOctagon.texture();
                case Move:
                case CustomPower:
                    return PCLCoreImages.Menu.nodeSquare.texture();
                case Multimove:
                    return PCLCoreImages.Menu.nodeSquare2.texture();
            }
            return PCLCoreImages.Menu.nodeCircleSmall.texture();
        }

        public String getTitle() {
            switch (this) {
                case Cond:
                    return PGR.core.strings.cedit_condition;
                case Multicond:
                    return PGR.core.strings.cedit_condition;
                case Branchcond:
                    return PGR.core.strings.cedit_condition;
                case Mod:
                    return PGR.core.strings.cedit_modifier;
                case Move:
                    return PGR.core.strings.cedit_effect;
                case Multimove:
                    return PGR.core.strings.cedit_effect;
                case Delay:
                    return PGR.core.strings.cedit_turnDelay;
                case Limit:
                    return PGR.core.strings.cedit_mainCondition;
                case Trigger:
                    return PGR.core.strings.cedit_trigger;
                case Attack:
                case Block:
                    return PGR.core.strings.cedit_mainCondition;
            }
            return "";
        }
    }
}
