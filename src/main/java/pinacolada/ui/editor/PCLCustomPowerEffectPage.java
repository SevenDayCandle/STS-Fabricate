package pinacolada.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.moves.PMove_StackCustomPower;
import pinacolada.ui.editor.nodes.PCLCustomEffectNode;

public class PCLCustomPowerEffectPage extends PCLCustomEffectPage {

    protected EUIButton quickAddButton;
    protected EUIContextMenu<Integer> quickAddMenu;

    public PCLCustomPowerEffectPage(PCLCustomEditEntityScreen<?, ?> screen, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate) {
        super(screen, hb, index, title, onUpdate);
        quickAddMenu = new EUIContextMenu<Integer>(new EUIHitbox(0, 0, 0, 0), this::getNameForEffect)
                .setOnChange(options -> {
                    for (Integer i : options) {
                        startPowerHologram(i);
                    }
                })
                .setCanAutosizeButton(true);
        quickAddButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(hb.x + MENU_WIDTH * 5f, hb.y - scale(20), MENU_WIDTH, MENU_HEIGHT))
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.8f)
                .setText(PGR.core.strings.cedit_addToEffect)
                .setOnClick(this::openDropdown);
    }

    protected void openDropdown() {
        Integer[] list = EUIUtils.range(0, screen.effectPages.size() - 1);
        quickAddMenu.setItems(list);
        quickAddMenu.positionToOpen();
    }

    protected void startPowerHologram(int i) {
        PCLCustomEffectPage page = screen.effectPages.get(i);
        screen.openPageAtIndex(screen.pages.indexOf(page));
        PMove_StackCustomPower applyPower = new PMove_StackCustomPower(PCLCardTarget.Self, -1, i);
        PCLCustomEffectNode node = PCLCustomEffectNode.getNodeForType(page, applyPower, PCLCustomEffectNode.NodeType.Move, page.hb);
        page.root.receiveNode(node);
        page.fullRebuild();
    }

    protected String getNameForEffect(int i) {
        return EUIUtils.format(PGR.core.strings.cedit_effectX, i + 1);
    }

    @Override
    public TextureCache getTextureCache() {
        return PCLCoreImages.Menu.editorPower;
    }

    @Override
    public PSkill<?> getSourceEffect() {
        PSkill<?> base = screen.currentPowers.get(editorIndex);
        return base != null ? base.makeCopy() : null;
    }

    @Override
    public void updateInner() {
        super.updateInner();
        quickAddButton.tryUpdate();
        quickAddMenu.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        quickAddButton.tryRender(sb);
        super.renderImpl(sb);
        quickAddMenu.tryRender(sb);
    }
}
