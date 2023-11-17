package pinacolada.ui.editor.power;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.effects.screen.PCLGenericSelectRenderableEffect;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLDynamicPowerData;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PCond;
import pinacolada.skills.PMod;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.conditions.PCond_OnRemove;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.editor.orb.PCLCustomOrbEditScreen;
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
    protected String getFolder() {
        return PCLCustomPowerSlot.getFolder();
    }

    @Override
    protected PCLGenericItemGrid<PCLPowerRenderable> getGrid() {
        return new PCLGenericItemGrid<PCLPowerRenderable>(PCLPowerRenderable.BASE_SCALE, PCLPowerRenderable.BASE_SCALE * 1.5f);
    }

    @Override
    protected String getInfoText() {
        return PGR.core.strings.cetut_selectorGeneric;
    }

    @Override
    protected PCLPowerKeywordFilters.PowerFilters getSavedFilters() {
        return new PCLPowerKeywordFilters.PowerFilters();
    }

    @Override
    protected PCLCustomPowerEditScreen getScreen(PCLCustomPowerSlot slot) {
        return new PCLCustomPowerEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomPowerSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomPowerSlot.getAll().values(), c -> !c.getIsInternal());
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            PCLDynamicPowerData sample = new PCLDynamicPowerData(EUIUtils.EMPTY_STRING);
            sample.setEndTurnBehavior(PCLPowerData.Behavior.TurnBased);
            sample.setText(PGR.core.tooltips.poison.title)
                    .addPSkill(PTrigger.when(PCond.onTurnStart(), PMove.loseHp(0).setUpgrade(1)));
            sample.setTooltip(new EUIKeywordTooltip(PGR.core.tooltips.dark.title, sample.getEffectTextForTip()));

            currentDialog = new PCLGenericSelectRenderableEffect<PCLPowerRenderable>(Collections.singleton(sample.makeRenderable()), PCLAugmentRenderable.BASE_SCALE, PCLAugmentRenderable.BASE_SCALE * 1.5f).addCallback(aug -> {
                if (aug != null && aug.item instanceof PCLDynamicPowerData) {
                    PCLCustomPowerSlot slot = new PCLCustomPowerSlot((PCLDynamicPowerData) aug.item);
                    currentDialog = new PCLCustomPowerEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomPowerSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
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
