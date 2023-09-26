package pinacolada.ui.editor.power;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLDynamicPower;
import pinacolada.powers.PCLPowerRenderable;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLPowerGrid;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.ui.menu.PCLPowerKeywordFilters;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PCLCustomPowerSelectorScreen extends PCLCustomSelectorScreen<PCLPowerRenderable, PCLCustomPowerSlot, PCLPowerKeywordFilters.PowerFilters> {
    @Override
    protected GenericFilters<PCLPowerRenderable, PCLPowerKeywordFilters.PowerFilters, ?> getFilters() {
        return PGR.powerFilters;
    }

    // Does not use colors
    @Override
    protected List<AbstractCard.CardColor> getAllColors() {
        return Collections.emptyList();
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
        return new PCLCustomPowerEditPowerScreen(slot);
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
