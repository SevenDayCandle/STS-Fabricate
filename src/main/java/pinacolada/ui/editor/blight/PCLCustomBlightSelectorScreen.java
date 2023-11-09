package pinacolada.ui.editor.blight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.BlightKeywordFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.controls.EUIBlightGrid;
import extendedui.utilities.BlightTier;
import pinacolada.blights.PCLBlight;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.blights.PCLDynamicBlightData;
import pinacolada.blights.PCLPointerBlight;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.screen.PCLGenericSelectBlightEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

import java.util.Collections;

public class PCLCustomBlightSelectorScreen extends PCLCustomSelectorScreen<AbstractBlight, PCLCustomBlightSlot, BlightKeywordFilters.BlightFilters> {

    @Override
    protected GenericFilters<AbstractBlight, BlightKeywordFilters.BlightFilters, ?> getFilters() {
        return EUI.blightFilters;
    }

    @Override
    protected EUIItemGrid<AbstractBlight> getGrid() {
        return new EUIBlightGrid();
    }

    @Override
    protected String getInfoText() {
        return PGR.core.strings.cetut_selectorBlight;
    }

    @Override
    protected BlightKeywordFilters.BlightFilters getSavedFilters() {
        return new BlightKeywordFilters.BlightFilters();
    }

    @Override
    protected PCLCustomBlightEditScreen getScreen(PCLCustomBlightSlot slot) {
        return new PCLCustomBlightEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomBlightSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomBlightSlot.getBlights(co), c -> !c.getIsInternal());
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            PCLDynamicBlightData sample = new PCLDynamicBlightData(EUIUtils.EMPTY_STRING);
            sample.setText(PGR.core.strings.menu_blight)
                    .setTier(BlightTier.BOSS);
            sample.addPSkill(PTrigger.when(3, PCond.onDiscard(), PMove.modifyCost(1).useParentForce()));

            currentDialog = new PCLGenericSelectBlightEffect(Collections.singleton(sample.create())).addCallback(card -> {
                if (card instanceof PCLPointerBlight) {
                    PCLCustomBlightSlot slot = new PCLCustomBlightSlot((PCLPointerBlight) card, currentColor);
                    currentDialog = new PCLCustomBlightEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomBlightSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
    }

    @Override
    protected String getFolder() {
        return PCLCustomBlightSlot.getFolder();
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
