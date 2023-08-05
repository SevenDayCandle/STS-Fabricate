package pinacolada.ui.debug;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.EUIGameUtils;
import extendedui.EUIUtils;
import extendedui.debug.*;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.patches.eui.CardPoolScreenPatches;
import pinacolada.ui.editor.card.PCLCustomCardEditCardScreen;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugCardPanel {
    protected static final String ALL = "Any";
    protected static final String BASE_GAME = "Base";
    protected static final String CUSTOM = "Custom";
    protected AbstractCard lastCard;
    protected ArrayList<AbstractCard> originalSorted = new ArrayList<>();
    protected ArrayList<AbstractCard> sorted = originalSorted;
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter filter = new ImGuiTextFilter();
    protected DEUITabItem cards = new DEUITabItem("Cards");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);
    protected DEUIFilteredSuffixListBox<AbstractCard> cardList = new DEUIFilteredSuffixListBox<AbstractCard>("##all cards",
            sorted, p -> p.cardID, p -> p.name, this::passes);
    protected DEUIIntInput cardCount = new DEUIIntInput("Count", 1, 1, Integer.MAX_VALUE);
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIButton addToHand = new DEUIButton("Add to hand");
    protected DEUIButton addToDeck = new DEUIButton("Add to deck");
    protected DEUIButton edit = new DEUIButton("Edit");
    protected PCLCustomCardSlot slot;
    protected boolean wasCancelOpen;

    public PCLDebugCardPanel() {
        regenerate();
    }

    private static String getModID(AbstractCard c) {
        if (c instanceof PCLDynamicCard) {
            return CUSTOM;
        }
        int idx = c.cardID.indexOf(':');
        return idx < 0 ? BASE_GAME : c.cardID.substring(0, idx);
    }

    private void addToDeck() {
        AbstractCard chosen = cardList.get();
        if (chosen != null) {
            for (int i = 0; i < cardCount.get(); i++) {
                PCLEffects.List.add(new ShowCardAndObtainEffect(getCopy(chosen), (float) Settings.WIDTH * 0.5f, (float) Settings.HEIGHT * 0.5f));
            }
        }
    }

    private void addToHand() {
        AbstractCard chosen = cardList.get();
        if (chosen != null) {
            PCLActions.instant.makeCardInHand(getCopy(chosen)).repeat(cardCount.get());
        }
    }

    private void edit() {
        if (slot != null) {
            CardPoolScreenPatches.editFromExternal(slot);
        }
    }

    private AbstractCard getCopy(AbstractCard chosen) {
        AbstractCard copy = chosen.makeCopy();
        if (copy instanceof PCLCard) {
            ((PCLCard) copy).changeForm(formCount.get(), 0);
        }
        for (int j = 0; j < upgradeCount.get(); ++j) {
            copy.upgrade();
        }
        return copy;
    }

    private boolean passes(AbstractCard card) {
        String mod = modList.get();
        return (filter.passFilter(card.cardID) || filter.passFilter(card.name)) && (ALL.equals(mod) || getModID(card).equals(mod));
    }

    public void refresh() {
        originalSorted.clear();
        sortedModIDs.clear();
        regenerate();
    }

    protected void regenerate() {
        originalSorted.addAll(CardLibrary.getAllCards());
        originalSorted.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.getBuilder(0).createImplWithForms(0, 0, false)));
        originalSorted.sort((a, b) -> StringUtils.compare(a.cardID, b.cardID));
        sortedModIDs.add(ALL);
        sortedModIDs.addAll(originalSorted.stream()
                .map(PCLDebugCardPanel::getModID)
                .distinct()
                .collect(Collectors.toList()));
        modList.set(ALL);
    }

    public void render() {
        cards.render(() -> {
            AbstractCard c = cardList.get();
            if (lastCard != c && c != null) {
                lastCard = c;
                slot = PCLCustomCardSlot.get(lastCard.cardID);
            }

            DEUIUtils.withWidth(90, () -> modList.renderInline());
            DEUIUtils.withFullWidth(() ->
            {
                filter.draw("##");
                cardList.render();
            });
            DEUIUtils.withWidth(90, () ->
            {
                cardCount.renderInline();
                upgradeCount.renderInline();
                formCount.render();
            });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || c == null, () ->
            {
                addToHand.renderInline(this::addToHand);
            });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || c == null, () ->
            {
                addToDeck.render(this::addToDeck);
            });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || slot == null, () ->
            {
                edit.render(this::edit);
            });
        });
    }
}
