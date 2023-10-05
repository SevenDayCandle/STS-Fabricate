package pinacolada.ui.editor.power;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIItemGrid;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLPowerGrid;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.menu.PCLPowerKeywordFilters;

import java.util.Collections;
import java.util.List;

public class PCLCustomPowerSelectorScreen extends PCLCustomSelectorScreen<PCLPowerRenderable, PCLCustomPowerSlot, PCLPowerKeywordFilters.PowerFilters> {
    // Does not use colors
    @Override
    protected List<AbstractCard.CardColor> getAllColors() {
        return Collections.emptyList();
    }

    @Override
    protected GenericFilters<PCLPowerRenderable, PCLPowerKeywordFilters.PowerFilters, ?> getFilters() {
        return PGR.powerFilters;
    }

    @Override
    protected EUIItemGrid<PCLPowerRenderable> getGrid() {
        return new PCLPowerGrid(1f);
    }

    @Override
    protected PCLPowerKeywordFilters.PowerFilters getSavedFilters() {
        return new PCLPowerKeywordFilters.PowerFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomPowerSlot, ?> getScreen(PCLCustomPowerSlot slot) {
        return new PCLCustomPowerEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomPowerSlot> getSlots(AbstractCard.CardColor co) {
        return PCLCustomPowerSlot.getAll().values();
    }

    @Override
    protected PCLPowerRenderable makeItem(PCLCustomPowerSlot slot) {
        return slot.makeRenderable();
    }

    @Override
    protected PCLCustomPowerSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomPowerSlot();
    }

    @Override
    protected PCLCustomPowerSlot makeSlot(PCLCustomPowerSlot other) {
        return new PCLCustomPowerSlot(other);
    }

    @Override
    protected PCLCustomPowerSlot makeSlot(PCLCustomPowerSlot other, AbstractCard.CardColor co) {
        return new PCLCustomPowerSlot(other);
    }

    @Override
    protected void onAdd(PCLCustomPowerSlot slot) {
        PCLCustomPowerSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomPowerSlot slot, String oldID) {
        PCLCustomPowerSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomPowerSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomPowerSlot slot) {
        PCLCustomPowerSlot.deleteSlot(slot);
    }
}
