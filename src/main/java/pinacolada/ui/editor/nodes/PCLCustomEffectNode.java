package pinacolada.ui.editor.nodes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.*;
import pinacolada.skills.skills.*;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.editor.PCLCustomEffectHologram;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PCLCustomEffectNode extends EUIButton {
    public static final float BUTTON_SIZE = scale(40);
    public static final float DISTANCE_Y = scale(-100);
    public static final float SIZE_X = scale(120);
    public static final float SIZE_Y = scale(60);
    private boolean dragging;
    protected List<PSkill<?>> effects;
    protected PCLCustomEffectHologram hologram;
    protected EUIHitbox dropZone;
    public PSkill<?> skill;
    public PCLCustomEffectPage editor;
    public PCLCustomEffectNode parent;
    public PCLCustomEffectNode child;
    public NodeType type;
    public EUIButton deleteButton;
    public EUIImage warningImage;
    public int index = -1;

    // Call getNodeForSkill instead to get the correct node constructor for the given skill type
    protected PCLCustomEffectNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox hb) {
        super(type.getTexture(), hb);
        this.dropZone = new OriginRelativeHitbox(hb, hb.width * 1.1f, hb.height * 5.5f, -hb.width * 0.05f, hb.height * -5f);
        this.setColor(type.getColor());
        this.setShaderMode(EUIRenderHelpers.ShaderMode.Colorize);
        this.label = new EUILabel(EUIFontHelper.buttonFont, hb, 0.45f, 0.68f, 0.4f, true);
        this.label.setSmartText(true, true);
        this.editor = editor;
        this.type = type;
        this.skill = skill;
        this.warningImage = new EUIImage(EUIRM.images.warning.texture(), new RelativeHitbox(hb, BUTTON_SIZE, BUTTON_SIZE, hb.width * -0.2f, hb.height * 0.4f));
        this.warningImage.setActive(false);
        this.deleteButton = new EUIButton(EUIRM.images.xButton.texture(), new RelativeHitbox(hb, BUTTON_SIZE, BUTTON_SIZE, hb.width * 1.2f, hb.height * 0.4f));
        this.deleteButton.setOnClick(() -> {
            if (canRemove()) {
                deleteSelf();
            }
        });

        if (this.skill == null) {
            initializeDefaultSkill();
        }

        if (this.skill != null) {
            refresh();
        }
        setOnClick(this::startEdit);

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
            if (parent.skill instanceof PMultiBase && ((PMultiBase<?>) parent.skill).getSubEffects().remove(this.skill)) {
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
        else if (editor.rootEffect == this.skill) {
            editor.rootEffect = new PRoot();
            if (child != null) {
                editor.rootEffect.setChild(child.skill);
            }
        }
        skill.setChild((PSkill<?>) null);
    }

    public List<PSkill<?>> getEffects() {
        if (effects == null) {
            effects = new ArrayList<>();
            for (PSkill<?> sk : type.getSkills(editor.screen.getBuilder().getCardColor())) {
                if (sk.data.sourceTypes == null || EUIUtils.any(sk.data.sourceTypes, s -> s.isSourceAllowed(editor))) {
                    if (skill != null && sk.effectID.equals(skill.effectID)) {
                        skill.scanForTips(sk.getSampleText(editor.rootEffect, parent != null ? parent.skill : null));
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

    protected EUIHeaderlessTooltip getWarningTooltip() {
        StringJoiner sj = new StringJoiner(EUIUtils.SPLIT_LINE);

        if (!(editor.rootEffect == null || skill instanceof PPrimary || editor.rootEffect.isSkillAllowed(skill, editor))) {
            sj.add(PGR.core.strings.cetut_primaryWarning);
        }

        if (skill != null && skill.hasChildWarning()) {
            sj.add(PGR.core.strings.cetut_childWarning);
        }

        String res = sj.toString();
        return StringUtils.isEmpty(res) ? null : new EUIHeaderlessTooltip(res);
    }

    public void initializeDefaultSkill() {
        getEffects();
        this.skill = EUIUtils.find(effects, ef -> editor.rootEffect.isSkillAllowed(ef, editor));

        if (this.skill == null) {
            try {
                this.skill = effects.size() > 0 ? effects.get(0) : type.getSkillClass().newInstance();
            }
            catch (Exception e) {
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

    protected void onClickStart() {
        super.onClickStart();
        dragging = true;
    }

    protected void onHologramRelease(PCLCustomEffectHologram hologram) {
        if (hologram.highlighted != this && hologram.highlighted != null) {
            hologram.highlighted.receiveNode(this);
            editor.fullRebuild();
        }
        this.hologram = null;
        dragging = false;
    }

    protected void onLeftClick() {
        super.onLeftClick();
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
            reassignChild(node);
        }
    }

    public void refresh() {
        String text = skill.getSampleText(null, null);
        if (text != null) {
            setTextAndAlign(StringUtils.capitalize(text), 0.4f, 0.4f);
        }
        this.tooltip = new EUITooltip(type.getTitle(), skill.getPowerText(null));

        warningImage.tooltip = getWarningTooltip();
        warningImage.setActive(warningImage.tooltip != null);
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
            child.renderImpl(sb);
        }
        super.renderImpl(sb);
        deleteButton.render(sb);
        warningImage.render(sb);
    }

    public void replaceSkill(PSkill<?> skill) {
        skill.setChild(this.skill.getChild());
        this.skill = skill;
        if (parent != null) {
            if (!(this.parent.skill instanceof PMultiBase) || !((PMultiBase<?>) this.parent.skill).tryReplaceEffect(skill, index)) {
                this.parent.skill.setChild(skill);
            }
        }
        else if (this.skill instanceof PPrimary) {
            this.editor.rootEffect = (PPrimary<?>) this.skill;
        }
    }

    public boolean shouldReject(PCLCustomEffectHologram current) {
        switch (type) {
            case Trigger:
                if (skill instanceof PTrigger_When) {
                    switch (current.type) {
                        case Cond:
                        case Multicond:
                        case Branchcond:
                            return false;
                        default:
                            return true;
                    }
                }
                return false;
            case Multicond:
                return current.type != NodeType.Cond;
        }
        return false;
    }

    public void startEdit() {
        editor.startEdit(this);
        dragging = false;
    }

    public PCLCustomEffectNode tryHoverHologram() {
        if (hb.hovered && hologram != PCLCustomEffectHologram.current) {
            return this;
        }
        if (child != null) {
            return child.tryHoverHologram();
        }
        return null;
    }

    public PCLCustomEffectNode tryHoverPostHologram() {
        if (child != null) {
            PCLCustomEffectNode res = child.tryHoverPostHologram();
            if (res != null) {
                return res;
            }
        }
        if (dropZone.hovered && hologram != PCLCustomEffectHologram.current) {
            return this;
        }
        return null;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        dropZone.update();
        if (child != null) {
            child.updateImpl();
        }
        if (dragging && !hb.hovered && hologram == null) {
            hologram = PCLCustomEffectHologram.queue(this.background, this.type, this::onHologramRelease);
        }
        deleteButton.update();
        warningImage.update();
    }

    public enum NodeType {
        Cond,
        Multicond,
        Branchcond,
        Mod,
        Move,
        Multimove,
        Trait,
        Delay,
        Limit,
        Trigger,
        Attack,
        Block,
        Proxy,
        Root,
        ;

        // Overriding classes are listed later in the enum
        public static NodeType getTypeForSkill(PSkill<?> skill) {
            NodeType cur = Root;
            for (NodeType type : values()) {
                if (type.matchesNode(skill)) {
                    cur = type;
                }
            }
            return cur;
        }

        public boolean canRemove() {
            switch (this) {
                case Root:
                case Proxy:
                    return false;
            }
            return true;
        }

        public Color getColor() {
            switch (this) {
                case Cond:
                    return Color.SKY.cpy();
                case Multicond:
                    return Color.NAVY.cpy();
                case Branchcond:
                    return Color.BLUE.cpy();
                case Mod:
                    return Color.FOREST.cpy();
                case Move:
                    return Settings.GOLD_COLOR.cpy();
                case Multimove:
                    return Color.ORANGE.cpy();
                case Trait:
                    return Color.OLIVE.cpy();
                case Delay:
                    return Settings.RED_TEXT_COLOR.cpy();
                case Limit:
                case Trigger:
                case Attack:
                case Block:
                    return Color.SALMON.cpy();
                case Proxy:
                    return PCLCustomEffectProxyNode.FADE_COLOR.cpy();
            }
            return Color.WHITE.cpy();
        }

        public String getDescription() {
            switch (this) {
                case Cond:
                    return PGR.core.strings.cetut_effectCondition;
                case Multicond:
                    return PGR.core.strings.cetut_effectMultiCondition;
                case Branchcond:
                    return PGR.core.strings.cetut_effectBranchCondition;
                case Mod:
                    return PGR.core.strings.cetut_effectModifier;
                case Move:
                    return PGR.core.strings.cetut_effectEffect;
                case Multimove:
                    return PGR.core.strings.cetut_effectChoices;
                case Trait:
                    return PGR.core.strings.cetut_effectTrait;
                case Delay:
                    return PGR.core.strings.cetut_effectTurnDelay;
                case Limit:
                case Trigger:
                case Attack:
                case Block:
                    return PGR.core.strings.cetut_effectPrimary;
            }
            return "";
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
                case Trait:
                    return PTrait.class;
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
            }
            return PRoot.class;
        }

        @SuppressWarnings("rawtypes")
        public List<? extends PSkill> getSkills(AbstractCard.CardColor color) {
            switch (this) {
                case Limit:
                    return (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(PPrimary.class, PLimit.class, PTrigger.class, PShift.class) : PSkill.getEligibleEffects(color, PPrimary.class, PLimit.class, PTrigger.class, PShift.class));
                case Trigger:
                    return (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(PPrimary.class, PTrigger.class, PShift.class) : PSkill.getEligibleEffects(color, PPrimary.class, PTrigger.class, PShift.class));
            }
            return (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(getSkillClass()) : PSkill.getEligibleEffects(color, getSkillClass()));
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
                case Multimove:
                    return PCLCoreImages.Menu.nodeSquare2.texture();
                case Trait:
                    return PCLCoreImages.Menu.nodeDiamond.texture();
            }
            return PCLCoreImages.Menu.nodeCircleSmall.texture();
        }

        public String getTitle() {
            switch (this) {
                case Cond:
                    return PGR.core.strings.cedit_condition;
                case Multicond:
                    return PGR.core.strings.cedit_multiCondition;
                case Branchcond:
                    return PGR.core.strings.cedit_branchCondition;
                case Mod:
                    return PGR.core.strings.cedit_modifier;
                case Move:
                    return PGR.core.strings.cedit_effect;
                case Multimove:
                    return PGR.core.strings.cedit_multiEffect;
                case Trait:
                    return PGR.core.strings.cedit_trait;
                case Delay:
                    return PGR.core.strings.cedit_turnDelay;
                case Trigger:
                    return PGR.core.strings.cedit_trigger;
                case Limit:
                case Attack:
                case Block:
                    return PGR.core.strings.cedit_primary;
            }
            return "";
        }

        public EUITooltip getTooltip() {
            return new EUITooltip(getTitle(), getDescription());
        }

        public boolean matchesNode(PSkill<?> skill) {
            switch (this) {
                case Limit:
                    return getSkillClass().isInstance(skill) || skill instanceof PShift || skill instanceof PTrigger;
                case Trigger:
                    return getSkillClass().isInstance(skill) || skill instanceof PShift;
            }
            return getSkillClass().isInstance(skill);
        }
    }
}
