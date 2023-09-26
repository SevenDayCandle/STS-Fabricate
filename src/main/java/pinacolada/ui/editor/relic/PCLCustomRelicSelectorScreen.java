package pinacolada.ui.editor.relic;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
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
import extendedui.ui.cardFilter.RelicKeywordFilters;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.screen.PCLCustomCopyConfirmationEffect;
import pinacolada.effects.screen.PCLCustomDeletionConfirmationEffect;
import pinacolada.effects.screen.PCLGenericSelectRelicEffect;
import pinacolada.misc.PCLCustomLoadable;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLPointerRelic;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomSelectorScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PCLCustomRelicSelectorScreen extends PCLCustomSelectorScreen<RelicInfo, PCLCustomRelicSlot, RelicKeywordFilters.RelicFilters> {
    @Override
    protected GenericFilters<RelicInfo, RelicKeywordFilters.RelicFilters, ?> getFilters() {
        return EUI.relicFilters;
    }

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
    protected EUIItemGrid<RelicInfo> getGrid() {
        return new EUIRelicGrid(1f);
    }

    @Override
    protected RelicKeywordFilters.RelicFilters getSavedFilters() {
        return new RelicKeywordFilters.RelicFilters();
    }

    @Override
    protected PCLCustomEditEntityScreen<PCLCustomRelicSlot, ?> getScreen(PCLCustomRelicSlot slot) {
        return new PCLCustomRelicEditRelicScreen(slot);
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
                    currentDialog = new PCLCustomRelicEditRelicScreen(slot)
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
        relic.isSeen = true;
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
