package pinacolada.ui.editor.potion;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUI;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.PotionKeywordFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.utilities.PotionInfo;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

public class PCLCustomPotionSelectorScreen extends PCLCustomSelectorScreen<PotionInfo, PCLCustomPotionSlot, PotionKeywordFilters.PotionFilters> {
    @Override
    protected GenericFilters<PotionInfo, PotionKeywordFilters.PotionFilters, ?> getFilters() {
        return EUI.potionFilters;
    }

    @Override
    protected EUIItemGrid<PotionInfo> getGrid() {
        return new EUIPotionGrid();
    }

    @Override
    protected PotionKeywordFilters.PotionFilters getSavedFilters() {
        return new PotionKeywordFilters.PotionFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomPotionSlot, ?> getScreen(PCLCustomPotionSlot slot) {
        return new PCLCustomPotionEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomPotionSlot> getSlots(AbstractCard.CardColor co) {
        return PCLCustomPotionSlot.getPotions(co);
    }

    @Override
    protected PotionInfo makeItem(PCLCustomPotionSlot slot) {
        AbstractPotion potion = slot.make();
        return new PotionInfo(potion);
    }

    @Override
    protected PCLCustomPotionSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomPotionSlot(co);
    }

    @Override
    protected PCLCustomPotionSlot makeSlot(PCLCustomPotionSlot other) {
        return new PCLCustomPotionSlot(other);
    }

    @Override
    protected PCLCustomPotionSlot makeSlot(PCLCustomPotionSlot other, AbstractCard.CardColor co) {
        return new PCLCustomPotionSlot(other, co);
    }

    @Override
    protected void onAdd(PCLCustomPotionSlot slot) {
        PCLCustomPotionSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomPotionSlot slot, String oldID) {
        PCLCustomPotionSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomPotionSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomPotionSlot slot) {
        PCLCustomPotionSlot.deleteSlot(slot);
    }
}
