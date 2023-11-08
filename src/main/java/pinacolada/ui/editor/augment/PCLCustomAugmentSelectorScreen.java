package pinacolada.ui.editor.augment;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.menu.PCLAugmentKeywordFilters;

import java.util.Collections;
import java.util.List;

public class PCLCustomAugmentSelectorScreen extends PCLCustomSelectorScreen<PCLAugmentRenderable, PCLCustomAugmentSlot, PCLAugmentKeywordFilters.AugmentFilters> {

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
