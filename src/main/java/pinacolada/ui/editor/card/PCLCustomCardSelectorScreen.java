package pinacolada.ui.editor.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUIItemGrid;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.effects.screen.PCLGenericSelectCardEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLCustomCardSelectorScreen extends PCLCustomSelectorScreen<AbstractCard, PCLCustomCardSlot, CardKeywordFilters.CardFilters> {
    private ArrayList<AbstractCard> getAvailableCardsToCopy() {
        ArrayList<AbstractCard> cards = EUIUtils.mapAsNonnull(TemplateCardData.getTemplates(),
                data -> {
                    PCLCard card = data.create();
                    GameUtilities.forceMarkCardAsSeen(data.ID);
                    card.isSeen = true;
                    card.isLocked = false;
                    // Hide the affinities for colorless cards
                    if (!PGR.config.showIrrelevantProperties.get() && GameUtilities.isColorlessCardColor(currentColor)) {
                        card.affinities.sorted.clear();
                    }
                    return PCLCustomCardSlot.canFullyCopy(card) ? card : null;
                });
        cards.sort((a, b) -> StringUtils.compare(a.name, b.name));
        return cards;
    }

    @Override
    protected GenericFilters<AbstractCard, CardKeywordFilters.CardFilters, ?> getFilters() {
        return EUI.cardFilters;
    }

    @Override
    protected String getFolder() {
        return PCLCustomCardSlot.getFolder();
    }

    @Override
    protected EUIItemGrid<AbstractCard> getGrid() {
        return new EUICardGrid(0.42f);
    }

    @Override
    protected String getInfoText() {
        return PGR.core.strings.cetut_selectorCard;
    }

    @Override
    protected CardKeywordFilters.CardFilters getSavedFilters() {
        return new CardKeywordFilters.CardFilters();
    }

    @Override
    protected PCLCustomCardEditScreen getScreen(PCLCustomCardSlot slot) {
        return new PCLCustomCardEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomCardSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomCardSlot.getCards(co), c -> !c.getIsInternal());
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            currentDialog = new PCLGenericSelectCardEffect(this.getAvailableCardsToCopy()).addCallback(card -> {
                if (card instanceof PCLCard) {
                    PCLCustomCardSlot slot = new PCLCustomCardSlot((PCLCard) card, currentColor);
                    currentDialog = new PCLCustomCardEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomCardSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
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
