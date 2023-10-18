package pinacolada.ui.editor.potion;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.PotionKeywordFilters;
import extendedui.ui.controls.EUIItemGrid;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.utilities.PotionInfo;
import pinacolada.effects.screen.PCLGenericSelectPotionEffect;
import pinacolada.effects.screen.PCLGenericSelectRelicEffect;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotion;
import pinacolada.potions.PCLDynamicPotionData;
import pinacolada.potions.PCLPotion;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.editor.relic.PCLCustomRelicEditScreen;

import java.util.Collections;

public class PCLCustomPotionSelectorScreen extends PCLCustomSelectorScreen<PotionInfo, PCLCustomPotionSlot, PotionKeywordFilters.PotionFilters> {
    @Override
    protected GenericFilters<PotionInfo, PotionKeywordFilters.PotionFilters, ?> getFilters() {
        return EUI.potionFilters;
    }

    @Override
    protected String getFolder() {
        return PCLCustomPotionSlot.getFolder();
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
    protected PCLCustomPotionEditScreen getScreen(PCLCustomPotionSlot slot) {
        return new PCLCustomPotionEditScreen(slot);
    }

    @Override
    protected Iterable<PCLCustomPotionSlot> getSlots(AbstractCard.CardColor co) {
        return EUIUtils.filter(PCLCustomPotionSlot.getPotions(co), c -> !c.getIsInternal());
    }

    @Override
    public void loadFromExisting() {
        if (currentDialog == null) {
            PCLDynamicPotionData sample = new PCLDynamicPotionData(EUIUtils.EMPTY_STRING);
            sample.setText(PGR.core.strings.menu_potion)
                    .addPSkill(PMultiSkill.choose(PMove.applyToRandom(2, PCLPowerData.Vulnerable, PCLPowerData.Weak), PMove.gain(3, PCLPowerData.Vigor)));

            currentDialog = new PCLGenericSelectPotionEffect(Collections.singleton(sample.create())).addCallback(card -> {
                if (card instanceof PCLPotion) {
                    PCLCustomPotionSlot slot = new PCLCustomPotionSlot((PCLPotion) card, currentColor);
                    currentDialog = new PCLCustomPotionEditScreen(slot)
                            .setOnSave(() -> {
                                PCLCustomPotionSlot.addSlot(slot);
                                putInList(slot);
                            });
                }
            });
        }
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
