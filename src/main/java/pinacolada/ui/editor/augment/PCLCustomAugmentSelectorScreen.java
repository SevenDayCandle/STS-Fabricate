package pinacolada.ui.editor.augment;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.effects.screen.PCLGenericSelectRenderableEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.menu.PCLAugmentKeywordFilters;

import java.util.ArrayList;

public class PCLCustomAugmentSelectorScreen extends PCLCustomSelectorScreen<PCLAugmentRenderable, PCLCustomAugmentSlot, PCLAugmentKeywordFilters.AugmentFilters> {

    private ArrayList<PCLAugmentRenderable> getAvailableToCopy() {
        ArrayList<PCLAugmentRenderable> cards = EUIUtils.mapAsNonnull(PCLAugmentData.getAvailable(),
                data -> {
                    PCLAugment aug = data.create();
                    return PCLCustomSelectorScreen.canFullyCopy(aug) ? new PCLAugmentRenderable(aug) : null;
                });
        cards.sort((a, b) -> StringUtils.compare(a.item.getName(), b.item.getName()));
        return cards;
    }

    @Override
    protected GenericFilters<PCLAugmentRenderable, PCLAugmentKeywordFilters.AugmentFilters, ?> getFilters() {
        return PGR.augmentFilters;
    }

    @Override
    protected String getFolder() {
        return PCLCustomAugmentSlot.getFolder();
    }

    @Override
    protected PCLGenericItemGrid<PCLAugmentRenderable> getGrid() {
        return new PCLGenericItemGrid<PCLAugmentRenderable>(PCLAugmentRenderable.BASE_SCALE, PCLAugmentRenderable.BASE_SCALE * 1.5f);
    }

    @Override
    protected String getInfoText() {
        return PGR.core.strings.cetut_selectorAugment;
    }

    @Override
    protected PCLAugmentKeywordFilters.AugmentFilters getSavedFilters() {
        return new PCLAugmentKeywordFilters.AugmentFilters();
    }

    @Override
    protected PCLCustomAugmentEditScreen getScreen(PCLCustomAugmentSlot slot) {
        return new PCLCustomAugmentEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomAugmentSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomAugmentSlot.getAugments(co), c -> !c.getIsInternal());
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectRenderableEffect<PCLAugmentRenderable>(this.getAvailableToCopy(), PCLAugmentRenderable.BASE_SCALE, PCLAugmentRenderable.BASE_SCALE * 1.5f).addCallback(aug -> {
                if (aug != null) {
                    PCLCustomAugmentSlot slot = new PCLCustomAugmentSlot(aug.item, currentColor);
                    currentDialog = new PCLCustomAugmentEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomAugmentSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
    }

    @Override
    protected PCLAugmentRenderable makeItem(PCLCustomAugmentSlot slot) {
        return slot.makeRenderable();
    }

    @Override
    protected PCLCustomAugmentSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomAugmentSlot(co);
    }

    @Override
    protected PCLCustomAugmentSlot makeSlot(PCLCustomAugmentSlot other) {
        return new PCLCustomAugmentSlot(other);
    }

    @Override
    protected PCLCustomAugmentSlot makeSlot(PCLCustomAugmentSlot other, AbstractCard.CardColor co) {
        return new PCLCustomAugmentSlot(other);
    }

    @Override
    protected void onAdd(PCLCustomAugmentSlot slot) {
        PCLCustomAugmentSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomAugmentSlot slot, String oldID) {
        PCLCustomAugmentSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomAugmentSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomAugmentSlot slot) {
        PCLCustomAugmentSlot.deleteSlot(slot);
    }
}
