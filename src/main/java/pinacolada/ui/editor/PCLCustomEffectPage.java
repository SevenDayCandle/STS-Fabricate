package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

import static pinacolada.ui.editor.nodes.PCLCustomEffectNode.SIZE_X;
import static pinacolada.ui.editor.nodes.PCLCustomEffectNode.SIZE_Y;

public class PCLCustomEffectPage extends PCLCustomGenericPage {
    public static final float MENU_WIDTH = scale(200);
    public static final float MENU_HEIGHT = scale(40);
    public static final float OFFSET_EFFECT = -MENU_HEIGHT * 1.25f;
    public static final float OFFSET_AMOUNT = scale(10);
    public static final float START_Y = Settings.HEIGHT * (0.8f);

    public final PCLCustomEditEntityScreen<?, ?, ?> screen;
    protected int editorIndex;
    protected EUIHitbox hb;
    protected EUILabel header;
    protected EUITextBox info;
    protected PCLCustomEffectEditingPane currentEditingSkill;
    protected PCLCustomEffectSelectorPane buttonsPane;
    public PPrimary<?> rootEffect;
    public PCLCustomEffectNode root;

    public PCLCustomEffectPage(PCLCustomEditEntityScreen<?, ?, ?> screen, EUIHitbox hb, int index, String title) {
        this.screen = screen;
        this.editorIndex = index;
        this.hb = hb;
        this.scrollBar.setPosition(screenW(0.95f), screenH(0.5f));
        this.upperScrollBound = scale(550);
        this.header = new EUILabel(EUIFontHelper.cardTitleFontLarge, hb)
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(title);
        this.buttonsPane = new PCLCustomEffectSelectorPane(this);
        this.info = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(screenW(0.25f), screenH(0.11f), screenW(0.5f), screenH(0.15f)))
                .setLabel(PGR.core.strings.cetut_nodeTutorial)
                .setAlignment(0.75f, 0.05f, true)
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 0.9f);
        this.canDragScreen = false;

        initializeEffects();
    }

    protected PPrimary<?> deconstructEffect() {
        PSkill<?> sourceEffect = getSourceEffect();
        // Ensure that root is a PPrimary, so that we do not have to put in a separate button for adding primaries and so that the user can just click on the root node to choose between primaries
        PPrimary<?> rootEffect = sourceEffect instanceof PPrimary ? (PPrimary<?>) sourceEffect : new PRoot();
        if (rootEffect != sourceEffect) {
            rootEffect.setChild(sourceEffect);
        }
        root = PCLCustomEffectNode.createTree(this, rootEffect, new RelativeHitbox(hb, SIZE_X, SIZE_Y, scale(350), scale(-1200)));
        return rootEffect;
    }

    public void fullRebuild() {
        screen.updateEffect(rootEffect, editorIndex);
        initializeEffects();
    }

    @Override
    public String getIconText() {
        return String.valueOf(editorIndex + 1);
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen.currentEffects.get(editorIndex);
        return base != null ? base.makeCopy() : null;
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorEffect;
    }

    @Override
    public String getTitle() {
        return header.text;
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                new EUITourTooltip(buttonsPane.hb, getTitle(), PGR.core.strings.cetut_topBarTutorial)
                        .setFlash(buttonsPane)
        );
    }

    public void initializeEffects() {
        rootEffect = deconstructEffect();

        refresh();
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourEditorEffect,
                getTour()
        );
    }

    @Override
    public void refresh() {
        if (currentEditingSkill != null) {
            currentEditingSkill.refresh();
        }
        if (root != null) {
            root.refreshAll();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        if (this.root != null) {
            this.root.render(sb);
        }
        PCLCustomEffectHologram.updateAndRenderCurrent(sb);
        this.header.tryRender(sb);
        this.buttonsPane.tryRender(sb);
        this.info.tryRender(sb);

        if (this.currentEditingSkill != null) {
            this.currentEditingSkill.render(sb);
        }
    }

    public void startEdit(PCLCustomEffectNode node) {
        currentEditingSkill = new PCLCustomEffectEditingPane(this, node, new EUIHitbox(Settings.WIDTH * 0.35f, Settings.HEIGHT * 0.7f, MENU_WIDTH, MENU_HEIGHT));
    }

    @Override
    public void updateImpl() {
        if (this.currentEditingSkill != null) {
            this.currentEditingSkill.update();
        }
        else {
            updateInner();
        }
    }

    protected void updateInner() {
        if (PCLCustomEffectHologram.current == null && (this.root == null || !this.root.isDragging())) {
            super.updateImpl();
            this.buttonsPane.tryUpdate();
        }

        if (this.root != null) {
            this.root.hb.targetCy = START_Y + (scale(scrollDelta));
            this.root.update();
            // Do drop zone checks later because actual node hitboxes take priority
            PCLCustomEffectNode toHover = this.root.tryHoverHologram();
            if (toHover != null) {
                PCLCustomEffectHologram.setHighlighted(toHover);
            }
            else {
                toHover = this.root.tryHoverPostHologram();
            }
            PCLCustomEffectHologram.setHighlighted(toHover);
        }

        this.header.tryUpdate();
        this.info.tryUpdate();
    }

    public void updateRootEffect() {
        screen.updateEffect(rootEffect, editorIndex);
        refresh();
    }

}
