package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.*;
import pinacolada.skills.skills.PLimit;
import pinacolada.skills.skills.PMultiCond;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.List;

public class PCLCustomEffectNode extends EUIButton {
    public static final float SIZE_X = scale(150);
    public static final float SIZE_Y = scale(75);
    public static final float DISTANCE_Y = scale(-100);

    public PSkill<?> skill;
    public PCLCustomEffectPage editor;
    public PCLCustomEffectNode parent;
    public PCLCustomEffectNode child;
    public NodeType type;
    public EUIButton deleteButton;
    protected List<PSkill> effects;
    protected PCLCustomEffectHologram hologram;
    private boolean dragging;

    public static PCLCustomEffectNode createTree(PCLCustomEffectPage editor, PSkill<?> skill, EUIHitbox hb)
    {
        PCLCustomEffectNode root = getNodeForSkill(editor, skill, hb);
        PCLCustomEffectNode cur = root;
        while (cur != null)
        {
            cur = cur.makeSkillChild();
        }
        return root;
    }

    public static PCLCustomEffectNode getNodeForSkill(PCLCustomEffectPage editor, PSkill<?> skill, EUIHitbox hb)
    {
        return getNodeForType(editor, skill, NodeType.getTypeForSkill(skill), hb);
    }

    public static PCLCustomEffectNode getNodeForType(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox hb)
    {
        switch (type)
        {
            case Misc:
                return new PCLCustomEffectFakeRootNode(editor, skill, type, hb);
            case Multicond:
            case Multimove:
                return new PCLCustomEffectMultiNode(editor, skill, type, hb);
        }
        return new PCLCustomEffectNode(editor, skill, type, hb);
    }

    // Call getNodeForSkill instead to get the correct node constructor for the given skill type
    protected PCLCustomEffectNode(PCLCustomEffectPage editor, PSkill<?> skill, NodeType type, EUIHitbox hb) {
        super(type.getTexture(), hb);
        this.setColor(type.getColor());
        this.editor = editor;
        this.type = type;
        this.skill = skill;

        if (this.skill == null)
        {
            getEffects();
            // TODO single method for checking eligibility
            switch (type)
            {
                case Cond:
                case Multicond:
                    this.skill = EUIUtils.find(effects, ef -> editor.rootEffect.isCondAllowed(ef));
                    break;
                case Mod:
                    this.skill = EUIUtils.find(effects, ef -> editor.rootEffect.isModAllowed(ef));
                    break;
                default:
                    this.skill = EUIUtils.find(effects, ef -> editor.rootEffect.isMoveAllowed(ef));
            }

            if (this.skill == null)
            {
                this.skill = effects.size() > 0 ? effects.get(0) : new PRoot();
            }
        }

        this.text = StringUtils.capitalize(this.skill.getSampleText(null));
        setSmartText(true);
        setOnClick(this::startEdit);
        this.fontScale = 0.66f;

        this.deleteButton = new EUIButton(EUIRM.images.x.texture(), new RelativeHitbox(hb, 48, 48, hb.width * 1.2f, hb.height * 0.4f));
        this.deleteButton.setOnClick(() -> {
            if (canRemove())
            {
                deleteSelf();
            }
        });
    }

    public PCLCustomEffectNode makeSkillChild()
    {
        PSkill<?> sc = skill.getChild();
        if (sc != null)
        {
            this.child = getNodeForSkill(editor, sc, new OriginRelativeHitbox(hb, SIZE_X, SIZE_Y, 0, DISTANCE_Y));
            child.parent = this;
        }
        return this.child;
    }

    public void reassignChild(PCLCustomEffectNode newChild)
    {
        if (newChild.parent != this)
        {
            newChild.extractSelf();
            newChild.skill.setChild((PSkill<?>) null);

            skill.setChild(newChild.skill);
            if (child != null)
            {
                newChild.skill.setChild(child.skill.makeCopy());
            }
            child = newChild;
        }
    }

    protected void extractSelf()
    {
        if (parent != null)
        {
            if (child != null)
            {
                parent.skill.setChild(child.skill);
            }
            else
            {
                parent.skill.setChild((PSkill<?>) null);
            }
        }
    }

    public void deleteSelf()
    {
        extractSelf();
        editor.fullRebuild();
    }

    public void startEdit()
    {
        editor.startEdit(this);
        dragging = false;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        if (child != null)
        {
            child.update();
        }
        if (dragging && !hb.hovered && hologram == null)
        {
            hologram = PCLCustomEffectHologram.queue(this.background, this::onHologramRelease);
        }
        if (hb.hovered && hologram != PCLCustomEffectHologram.current)
        {
            PCLCustomEffectHologram.setHighlighted(this);
        }
        deleteButton.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (child != null)
        {
            PCLRenderHelpers.drawCurve(sb, ImageMaster.TARGET_UI_ARROW, Color.SCARLET.cpy(), this.hb, child.hb, 0, 0.3f, 0f, 5);
            child.render(sb);
        }
        super.renderImpl(sb);
        deleteButton.renderImpl(sb);
    }

    public void replaceSkill(PSkill<?> skill)
    {
        skill.setChild(this.skill.getChild());
        this.skill = skill;
        if (parent != null)
        {
            this.parent.skill.setChild(skill);
        }
    }

    public void refresh()
    {
        this.text = skill.getSampleText(null);
    }

    public void refreshAll()
    {
        refresh();
        if (child != null)
        {
            child.refreshAll();
        }
    }

    public boolean isDragging()
    {
        return dragging || (child != null && child.isDragging());
    }

    public boolean canRemove()
    {
        return type.canRemove();
    }

    @SuppressWarnings("rawtypes")
    public List<PSkill> getEffects() {
        if (effects == null)
        {
            effects = EUIUtils.map(type.getSkills(editor.screen.getBuilder().getCardColor()),
                    bc -> skill != null && bc.effectID.equals(skill.effectID) ? skill : bc.scanForTips(bc.getSampleText(editor.rootEffect)));
        }
        return effects;
    }

    protected void onClickStart() {
        super.onClickStart();
        dragging = true;
    }

    protected void onLeftClick() {
        super.onLeftClick();
        dragging = false;
    }

    protected void onHologramRelease(PCLCustomEffectHologram hologram) {
        if (hologram.highlighted != this && hologram.highlighted != null)
        {
            hologram.highlighted.reassignChild(this);
            editor.fullRebuild();
        }
        this.hologram = null;
        dragging = false;
    }

    public enum NodeType {
        Cond,
        Multicond,
        Mod,
        Move,
        Multimove,
        Delay,
        Limit,
        Trigger,
        Attack,
        Block,
        CustomPower,
        Misc;

        public Texture getTexture()
        {
            switch (this)
            {
                case Cond:
                case Multicond:
                    return PCLCoreImages.Menu.nodeCircle.texture();
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
                case CustomPower:
                    return PCLCoreImages.Menu.nodeSquare.texture();
            }
            return PCLCoreImages.Menu.nodeCircle.texture();
        }

        public Color getColor()
        {
            switch (this)
            {
                case Cond:
                    return Color.NAVY;
                case Multicond:
                    return Color.BLUE;
                case Mod:
                    return Color.FOREST;
                case Move:
                    return Color.GOLDENROD;
                case Multimove:
                    return Color.ORANGE;
                case Delay:
                    return Color.SALMON;
                case Limit:
                case Trigger:
                case Attack:
                case Block:
                    return Color.CORAL;
            }
            return Color.WHITE;
        }

        public String getTitle()
        {
            switch (this)
            {
                case Cond:
                    return PGR.core.strings.cedit_condition;
                case Multicond:
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

        public boolean canRemove()
        {
            return !PPrimary.class.isAssignableFrom(getSkillClass());
        }

        // Because PCond/PMod/PMove cannot be reified further
        @SuppressWarnings("rawtypes")
        public Class<? extends PSkill> getSkillClass()
        {
            switch (this)
            {
                case Cond:
                    return PCond.class;
                case Multicond:
                    return PMultiCond.class;
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
        public List<? extends PSkill> getSkills(AbstractCard.CardColor color)
        {
            return (PGR.config.showIrrelevantProperties.get() ? PSkill.getEligibleEffects(getSkillClass()) : PSkill.getEligibleEffects(getSkillClass(), color));
        }

        // Overriding classes are listed later in the enum
        public static NodeType getTypeForSkill(PSkill<?> skill)
        {
            NodeType cur = Misc;
            for (NodeType type : values())
            {
                if (type.getSkillClass().isInstance(skill))
                {
                    cur = type;
                }
            }
            return cur;
        }
    }
}
