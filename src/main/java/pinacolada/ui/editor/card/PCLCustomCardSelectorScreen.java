package pinacolada.ui.editor.card;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.cardFilter.GenericFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectCardEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;
import pinacolada.utilities.GameUtilities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        return new PCLCustomCardEditCardScreen(slot);
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
