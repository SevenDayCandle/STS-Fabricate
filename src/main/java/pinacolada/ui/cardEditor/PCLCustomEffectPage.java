package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PRoot;

import static pinacolada.ui.cardEditor.PCLCustomEffectNode.SIZE_X;
import static pinacolada.ui.cardEditor.PCLCustomEffectNode.SIZE_Y;

public class PCLCustomEffectPage extends PCLCustomGenericPage {
    public static final float MENU_WIDTH = scale(200);
    public static final float MENU_HEIGHT = scale(40);
    public static final float OFFSET_EFFECT = -MENU_HEIGHT * 1.25f;
    public static final float OFFSET_AMOUNT = scale(10);
    public static final float START_Y = Settings.HEIGHT * (0.8f);

    public final PCLCustomEditEntityScreen<?,?> screen;
    protected int editorIndex;
    protected ActionT1<PSkill<?>> onUpdate;
    protected EUIHitbox hb;
    protected EUILabel header;
    protected PPrimary<?> rootEffect;
    protected PCLCustomEffectNode root;
    protected PCLCustomEffectEditingPane currentEditingSkill; // TODO set this when clicking on a skill node
    protected PCLCustomEffectSelectorPane buttonsPane; // TODO finish pane for selecting nodes to create

    public PCLCustomEffectPage(PCLCustomEditEntityScreen<?,?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        this.screen = screen;
        this.editorIndex = index;
        this.onUpdate = onUpdate;
        this.hb = hb;
        this.scrollBar.setPosition(screenW(0.95f), screenH(0.5f));
        this.upperScrollBound = scale(550);
        this.header = new EUILabel(EUIFontHelper.cardtitlefontLarge, hb)
                .setAlignment(0.5f, 0.0f, false)
                .setFontScale(0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(title);
        this.buttonsPane = new PCLCustomEffectSelectorPane(this);
        this.canDragScreen = false;

        initializeEffects();
    }

    @Override
    public String getIconText() {
        return String.valueOf(editorIndex + 1);
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorEffect;
    }

    public String getTitle() {
        return header.text;
    }

    public void initializeEffects()
    {
        rootEffect = deconstructEffect();

        refresh();
    }

    protected PPrimary<?> deconstructEffect()
    {
        PSkill<?> sourceEffect = getSourceEffect();
        root = rootEffect != null ? PCLCustomEffectNode.createTree(this, rootEffect, new RelativeHitbox(hb, SIZE_X, SIZE_Y, scale(350), scale(-1200))) : null;
        // Ensure that root is a PPrimary, so that we do not have to put in a separate button for adding primaries and so that the user can just click on the root node to choose between primaries
        PPrimary<?> fEffect = null;
        if (root == null || !(root.skill instanceof PPrimary))
        {
            PCLCustomEffectNode prevRoot = root;
            fEffect = makeRootSkill();
            root = PCLCustomEffectNode.getNodeForSkill(this, fEffect, new RelativeHitbox(hb, SIZE_X, SIZE_Y, scale(350), scale(-1200)));
            if (prevRoot != null)
            {
                root.reassignChild(prevRoot);
            }
        }
        else
        {
            fEffect = (PPrimary<?>) root.skill;
        }
        return fEffect;
    }

    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen.currentEffects.get(editorIndex);
        return base != null ? base.makeCopy() : null;
    }

    public void startEdit(PCLCustomEffectNode node)
    {
        currentEditingSkill = new PCLCustomEffectEditingPane(this, node, new EUIHitbox(Settings.WIDTH * 0.35f, Settings.HEIGHT * 0.7f, MENU_WIDTH, MENU_HEIGHT));
    }

    public void updateRootEffect()
    {
        onUpdate.invoke(rootEffect);
        refresh();
    }

    public void fullRebuild()
    {
        onUpdate.invoke(rootEffect);
        initializeEffects();
    }

    @Override
    public void refresh() {
        if (currentEditingSkill != null)
        {
            currentEditingSkill.refresh();
        }
        if (root != null)
        {
            root.refreshAll();
        }
    }

    @Override
    public void updateImpl() {
        if (this.currentEditingSkill != null)
        {
            this.currentEditingSkill.update();
        }
        else
        {
            if (PCLCustomEffectHologram.current == null && (this.root == null || !this.root.isDragging()))
            {
                super.updateImpl();
                this.buttonsPane.tryUpdate();
            }

            if (this.root != null)
            {
                this.root.hb.targetCy = START_Y + (scale(scrollDelta));
                this.root.update();
            }

            this.header.tryUpdate();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        if (this.root != null)
        {
            this.root.render(sb);
        }
        PCLCustomEffectHologram.updateAndRenderCurrent(sb);
        this.header.tryRender(sb);
        this.buttonsPane.tryRender(sb);

        if (this.currentEditingSkill != null)
        {
            this.currentEditingSkill.render(sb);
        }
    }

    public PPrimary<?> makeRootSkill()
    {
        return new PRoot();
    }

}
