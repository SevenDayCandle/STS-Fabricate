package pinacolada.ui.editor.relic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.RelicKeywordFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.screen.PCLGenericSelectRelicEffect;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

import java.util.ArrayList;

public class PCLCustomRelicSelectorScreen extends PCLCustomSelectorScreen<RelicInfo, PCLCustomRelicSlot, RelicKeywordFilters.RelicFilters> {
    private ArrayList<AbstractRelic> getAvailableRelicsToCopy() {
        return EUIUtils.mapAsNonnull(PCLRelicData.getTemplates(),
                data -> {
                    PCLRelic relic = data.create();
                    UnlockTracker.markRelicAsSeen(data.ID);
                    relic.isSeen = true;
                    return PCLCustomRelicSlot.canFullyCopy(relic) ? relic : null;
                });
    }

    @Override
    protected GenericFilters<RelicInfo, RelicKeywordFilters.RelicFilters, ?> getFilters() {
        return EUI.relicFilters;
    }

    @Override
    protected EUIItemGrid<RelicInfo> getGrid() {
        return new EUIRelicGrid();
    }

    @Override
    protected RelicKeywordFilters.RelicFilters getSavedFilters() {
        return new RelicKeywordFilters.RelicFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomRelicSlot, ?> getScreen(PCLCustomRelicSlot slot) {
        return new PCLCustomRelicEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomRelicSlot> getSlots(AbstractCard.CardColor co) {
        return PCLCustomRelicSlot.getRelics(co);
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectRelicEffect(this.getAvailableRelicsToCopy()).addCallback(card -> {
                if (card instanceof PCLPointerRelic) {
                    PCLCustomRelicSlot slot = new PCLCustomRelicSlot((PCLPointerRelic) card, currentColor);
                    currentDialog = new PCLCustomRelicEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomRelicSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });

        }
    }

    @Override
    protected RelicInfo makeItem(PCLCustomRelicSlot slot) {
        AbstractRelic relic = slot.make();
        UnlockTracker.markRelicAsSeen(relic.relicId);
        return new RelicInfo(relic);
    }

    @Override
    protected PCLCustomRelicSlot makeSlot(AbstractCard.CardColor co) {
        return new PCLCustomRelicSlot(co);
    }

    @Override
    protected PCLCustomRelicSlot makeSlot(PCLCustomRelicSlot other) {
        return new PCLCustomRelicSlot(other);
    }

    @Override
    protected PCLCustomRelicSlot makeSlot(PCLCustomRelicSlot other, AbstractCard.CardColor co) {
        return new PCLCustomRelicSlot(other, co);
    }

    @Override
    protected void onAdd(PCLCustomRelicSlot slot) {
        PCLCustomRelicSlot.addSlot(slot);
    }

    @Override
    protected void onEdit(PCLCustomRelicSlot slot, String oldID) {
        PCLCustomRelicSlot.editSlot(slot, oldID);
    }

    @Override
    protected void onReload() {
        PCLCustomRelicSlot.initialize();
    }

    @Override
    protected void onRemove(PCLCustomRelicSlot slot) {
        PCLCustomRelicSlot.deleteSlot(slot);
    }
}
