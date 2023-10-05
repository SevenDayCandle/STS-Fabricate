package pinacolada.ui.editor.blight;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUI;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.BlightKeywordFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.controls.EUIBlightGrid;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

public class PCLCustomBlightSelectorScreen extends PCLCustomSelectorScreen<AbstractBlight, PCLCustomBlightSlot, BlightKeywordFilters.BlightFilters> {

    @Override
    protected GenericFilters<AbstractBlight, BlightKeywordFilters.BlightFilters, ?> getFilters() {
        return EUI.blightFilters;
    }

    @Override
    protected EUIItemGrid<AbstractBlight> getGrid() {
        return new EUIBlightGrid(1f);
    }

    @Override
    protected BlightKeywordFilters.BlightFilters getSavedFilters() {
        return new BlightKeywordFilters.BlightFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomBlightSlot, ?> getScreen(PCLCustomBlightSlot slot) {
        return new PCLCustomBlightEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomBlightSlot> getSlots(AbstractCard.CardColor co) {
        return PCLCustomBlightSlot.getBlights(co);
    }

    @Override
    protected AbstractBlight makeItem(PCLCustomBlightSlot slot) {
        AbstractBlight blight = slot.make();
        blight.isSeen = true;
        return blight;
    }

    @Override
    protected PCLCustomBlightSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomBlightSlot(co);
    }

    @Override
    protected PCLCustomBlightSlot makeSlot(PCLCustomBlightSlot other) {
        return new PCLCustomBlightSlot(other);
    }

    @Override
    protected PCLCustomBlightSlot makeSlot(PCLCustomBlightSlot other, AbstractCard.CardColor co) {
        return new PCLCustomBlightSlot(other, co);
    }

    @Override
    protected void onAdd(PCLCustomBlightSlot slot) {
        PCLCustomBlightSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomBlightSlot slot, String oldID) {
        PCLCustomBlightSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomBlightSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomBlightSlot slot) {
        PCLCustomBlightSlot.deleteSlot(slot);
    }
}
