package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

public class PCLCustomPowerEffectPage extends PCLCustomEffectPage {

    protected EUIButton quickAddButton;
    protected EUIContextMenu<Integer> quickAddMenu;

    public PCLCustomPowerEffectPage(PCLCustomEditEntityScreen<?, ?, ?, ?> screen, EUIHitbox hb, PSkill<?> skill, String title) {
        super(screen, hb, skill, title);
        quickAddMenu = new EUIContextMenu<>(new EUIHitbox(0, 0, 0, 0), this::getNameForEffect)
                .setOnChange(options -> {
                    for (Integer i : options) {
                        startPowerHologram(i);
                    }
                })
                .setCanAutosizeButton(true);
        quickAddButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(hb.x + MENU_WIDTH * 4.6f, hb.y - scale(20), MENU_WIDTH, MENU_HEIGHT))
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setLabel(EUIFontHelper.cardTitleFontSmall, 0.8f, PGR.core.strings.cedit_addToEffect)
                .setTooltip(PGR.core.strings.cedit_addToEffect, PGR.core.strings.cetut_addToEffect)
                .setOnClick(this::openDropdown);
    }

    @Override
    public void fullRebuild() {
        screen.updatePowerEffect();
        initializeEffects(rootEffect);
    }

    protected String getNameForEffect(int i) {
        return EUIUtils.format(PGR.core.strings.cedit_effectX, i + 1);
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPower;
    }

    @Override
    public String getTitle() {
        return EUIUtils.format(baseText, String.valueOf(screen.powerPages.indexOf(this) + 1));
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                new EUITourTooltip(buttonsPane.hb, getTitle(), PGR.core.strings.cetut_topBarTutorial)
                        .setFlash(buttonsPane),
                descButton.makeTour(true),
                quickAddButton.makeTour(true)
        );
    }

    @Override
    public void onOpen() {
        header.setLabel(getTitle());
        EUITourTooltip.queueFirstView(PGR.config.tourEditorEffect,
                new EUITourTooltip(buttonsPane.hb, getTitle(), PGR.core.strings.cetut_topBarTutorial)
                        .setFlash(buttonsPane)
        );
        EUITourTooltip.queueFirstView(PGR.config.tourEditorPower,
                quickAddButton.makeTour(true)
        );
    }

    protected void openDropdown() {
        Integer[] list = EUIUtils.range(0, screen.effectPages.size() - 1);
        quickAddMenu.setItems(list);
        quickAddMenu.positionToOpen();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        quickAddButton.tryRender(sb);
        super.renderImpl(sb);
        quickAddMenu.tryRender(sb);
    }

    protected void startPowerHologram(int i) {
        PCLCustomEffectPage page = screen.effectPages.get(i);
        screen.openPage(page);
        PMove_StackCustomPower applyPower = new PMove_StackCustomPower(PCLCardTarget.Self, -1, screen.powerPages.indexOf(this));
        PCLCustomEffectNode node = PCLCustomEffectNode.getNodeForType(page, applyPower, PCLCustomEffectNode.NodeType.Move, page.hb);
        page.root.receiveNode(node);
        page.fullRebuild();
    }

    @Override
    public void updateInner() {
        super.updateInner();
        quickAddButton.setColor(rootEffect instanceof PTrigger ? Color.SKY : Color.GRAY).tryUpdate();
        quickAddMenu.tryUpdate();
    }

    @Override
    public void updateRootEffect() {
        screen.updatePowerEffect();
        refresh();
    }
}
