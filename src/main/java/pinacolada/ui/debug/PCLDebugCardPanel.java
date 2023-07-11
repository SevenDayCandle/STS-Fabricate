package pinacolada.ui.debug;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import extendedui.EUIUtils;
import extendedui.debug.*;
import imgui.ImGuiTextFilter;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.PCLEffects;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugCardPanel {
    protected static final String ALL = "Any";
    protected static final String BASE_GAME = "Base";
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

    public PCLDebugCardPanel() {
        originalSorted.addAll(CardLibrary.getAllCards());
        originalSorted.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.getBuilder(0).createImplWithForms(false)));
        originalSorted.sort((a, b) -> StringUtils.compare(a.cardID, b.cardID));
        sortedModIDs.add(ALL);
        sortedModIDs.addAll(originalSorted.stream()
                .map(c -> getModID(c.cardID))
                .distinct()
                .collect(Collectors.toList()));
        modList.set(ALL);
    }

    private static String getModID(String cardID) {
        int idx = cardID.indexOf(':');
        return idx < 0 ? BASE_GAME : cardID.substring(0, idx);
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
        return (filter.passFilter(card.cardID) || filter.passFilter(card.name)) && (ALL.equals(mod) || getModID(card.cardID).equals(mod));
    }

    public void refresh() {
        originalSorted.clear();
        originalSorted.addAll(CardLibrary.getAllCards());
        originalSorted.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.getBuilder(0).createImplWithForms(false)));
        originalSorted.sort((a, b) -> StringUtils.compare(a.cardID, b.cardID));
    }

    public void render() {
        cards.render(() -> {
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
            DEUIUtils.disabledIf(AbstractDungeon.player == null || cardList.get() == null, () ->
            {
                addToHand.renderInline(this::addToHand);
            });
            DEUIUtils.disabledIf(AbstractDungeon.player == null || cardList.get() == null, () ->
            {
                addToDeck.render(this::addToDeck);
            });
        });
    }
}
