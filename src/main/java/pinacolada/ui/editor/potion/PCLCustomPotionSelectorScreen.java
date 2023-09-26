package pinacolada.ui.editor.potion;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.cardFilter.PotionKeywordFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectPotionEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLPotion;
import pinacolada.potions.PCLPotionData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomPotionSelectorScreen extends PCLCustomSelectorScreen<PotionInfo, PCLCustomPotionSlot, PotionKeywordFilters.PotionFilters> {
    @Override
    protected GenericFilters<PotionInfo, PotionKeywordFilters.PotionFilters, ?> getFilters() {
        return EUI.potionFilters;
    }

    @Override
    protected EUIItemGrid<PotionInfo> getGrid() {
        return new EUIPotionGrid(1f);
    }

    @Override
    protected PotionKeywordFilters.PotionFilters getSavedFilters() {
        return new PotionKeywordFilters.PotionFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomPotionSlot, ?> getScreen(PCLCustomPotionSlot slot) {
        return new PCLCustomPotionEditPotionScreen(slot);
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
