package pinacolada.ui.editor.orb;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.effects.screen.PCLGenericSelectRenderableEffect;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.conditions.PCond_OnRemove;
import pinacolada.skills.skills.base.modifiers.PMod_PerOrbEvoke;
import pinacolada.skills.skills.base.modifiers.PMod_PerOrbPassive;
import pinacolada.skills.skills.base.moves.PMove_IncreaseOrbEvoke;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.PCLGenericItemGrid;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.menu.PCLOrbKeywordFilters;

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
    protected String getInfoText() {
        return PGR.core.strings.cetut_selectorGeneric;
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
    public void loadFromExisting() {
        if (currentDialog == null) {
            PCLDynamicOrbData sample = new PCLDynamicOrbData(EUIUtils.EMPTY_STRING);
            sample.setBasePassive(6)
                    .setBaseEvoke(6)
                    .setApplyFocusToEvoke(false);
            sample.setText(PGR.core.tooltips.dark.title)
                    .addPSkill(new PRoot().setChain(new PMod_PerOrbPassive(1), new PMove_IncreaseOrbEvoke(1).edit(f -> f.setNot(true))))
                    .addPSkill(new PRoot().setChain(new PCond_OnRemove(), new PMod_PerOrbEvoke(1), PMove.dealDamageToRandom(1)));
            sample.setTooltip(new EUIKeywordTooltip(PGR.core.tooltips.dark.title, sample.getEffectTextForTip()));

            currentDialog = new PCLGenericSelectRenderableEffect<PCLOrbRenderable>(Collections.singleton(sample.makeRenderable()), PCLAugmentRenderable.BASE_SCALE, PCLAugmentRenderable.BASE_SCALE * 1.5f).addCallback(aug -> {
                if (aug != null && aug.item instanceof PCLDynamicOrbData) {
                    PCLCustomOrbSlot slot = new PCLCustomOrbSlot((PCLDynamicOrbData) aug.item);
                    currentDialog = new PCLCustomOrbEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomOrbSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
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
