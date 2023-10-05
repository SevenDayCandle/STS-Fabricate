package pinacolada.ui.editor.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUI;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUIItemGrid;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

public class PCLCustomCardSelectorScreen extends PCLCustomSelectorScreen<AbstractCard, PCLCustomCardSlot, CardKeywordFilters.CardFilters> {
    @Override
    protected GenericFilters<AbstractCard, CardKeywordFilters.CardFilters, ?> getFilters() {
        return EUI.cardFilters;
    }

    @Override
    protected EUIItemGrid<AbstractCard> getGrid() {
        return new EUICardGrid(0.42f);
    }

    @Override
    protected CardKeywordFilters.CardFilters getSavedFilters() {
        return new CardKeywordFilters.CardFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomCardSlot, ?> getScreen(PCLCustomCardSlot slot) {
        return new PCLCustomCardEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomCardSlot> getSlots(AbstractCard.CardColor co) {
        return PCLCustomCardSlot.getCards(co);
    }

    @Override
    protected AbstractCard makeItem(PCLCustomCardSlot slot) {
        return slot.make();
    }

    @Override
    protected PCLCustomCardSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomCardSlot(co);
    }

    @Override
    protected PCLCustomCardSlot makeSlot(PCLCustomCardSlot other) {
        return new PCLCustomCardSlot(other);
    }

    @Override
    protected PCLCustomCardSlot makeSlot(PCLCustomCardSlot other, AbstractCard.CardColor co) {
        return new PCLCustomCardSlot(other, co);
    }

    @Override
    protected void onAdd(PCLCustomCardSlot slot) {
        PCLCustomCardSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomCardSlot slot, String oldID) {
        PCLCustomCardSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomCardSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomCardSlot slot) {
        PCLCustomCardSlot.deleteSlot(slot);
    }
}
