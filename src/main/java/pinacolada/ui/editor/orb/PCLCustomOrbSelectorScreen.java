package pinacolada.ui.editor.orb;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.editor.power.PCLCustomPowerEditScreen;
import pinacolada.ui.menu.PCLOrbKeywordFilters;
import pinacolada.ui.menu.PCLPowerKeywordFilters;

import java.util.Collections;
import java.util.List;

public class PCLCustomOrbSelectorScreen extends PCLCustomSelectorScreen<PCLOrbRenderable, PCLCustomOrbSlot, PCLOrbKeywordFilters.OrbFilters> {
    // Does not use colors
    @Override
    protected List<AbstractCard.CardColor> getAllColors() {
        return Collections.emptyList();
    }

    @Override
    protected GenericFilters<PCLOrbRenderable, PCLOrbKeywordFilters.OrbFilters, ?> getFilters() {
        return PGR.orbFilters;
    }

    @Override
    protected String getFolder() {
        return PCLCustomOrbSlot.getFolder();
    }

    @Override
    protected PCLGenericItemGrid<PCLOrbRenderable> getGrid() {
        return new PCLGenericItemGrid<PCLOrbRenderable>(PCLOrbRenderable.BASE_SCALE, PCLOrbRenderable.BASE_SCALE * 1.5f);
    }

    @Override
    protected PCLOrbKeywordFilters.OrbFilters getSavedFilters() {
        return new PCLOrbKeywordFilters.OrbFilters();
    }

    @Override
    protected PCLCustomOrbEditScreen getScreen(PCLCustomOrbSlot slot) {
        return new PCLCustomOrbEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomOrbSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomOrbSlot.getAll().values(), c -> !c.getIsInternal());
    }

    @Override
    protected PCLOrbRenderable makeItem(PCLCustomOrbSlot slot) {
        return slot.makeRenderable();
    }

    @Override
    protected PCLCustomOrbSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomOrbSlot();
    }

    @Override
    protected PCLCustomOrbSlot makeSlot(PCLCustomOrbSlot other) {
        return new PCLCustomOrbSlot(other);
    }

    @Override
    protected PCLCustomOrbSlot makeSlot(PCLCustomOrbSlot other, AbstractCard.CardColor co) {
        return new PCLCustomOrbSlot(other);
    }

    @Override
    protected void onAdd(PCLCustomOrbSlot slot) {
        PCLCustomOrbSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomOrbSlot slot, String oldID) {
        PCLCustomOrbSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomOrbSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomOrbSlot slot) {
        PCLCustomOrbSlot.deleteSlot(slot);
    }
}
