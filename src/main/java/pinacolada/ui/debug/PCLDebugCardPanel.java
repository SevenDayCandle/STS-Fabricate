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
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PCLDebugCardPanel
{
    protected static final String ALL = "Any";
    protected static final String BASE_GAME = "Base";
    protected ArrayList<AbstractCard> originalSortedCards = new ArrayList<>();
    protected ArrayList<AbstractCard> sortedCards = originalSortedCards;
    protected ArrayList<String> sortedModIDs = new ArrayList<>();
    protected ImGuiTextFilter cardFilter = new ImGuiTextFilter();
    protected DEUITabItem cards = new DEUITabItem("Cards");
    protected DEUICombo<String> modList = new DEUICombo<String>("##modid", sortedModIDs, p -> p);
    protected DEUIIntInput cardCount = new DEUIIntInput("Count", 1, 1, Integer.MAX_VALUE);
    protected DEUIIntInput upgradeCount = new DEUIIntInput("Upgrades", 0, 0, Integer.MAX_VALUE);
    protected DEUIIntInput formCount = new DEUIIntInput("Form", 0, 0, Integer.MAX_VALUE);
    protected DEUIButton addToHand = new DEUIButton("Add to hand");
    protected DEUIButton addToDeck = new DEUIButton("Add to deck");
    protected DEUIFilteredSuffixListBox<AbstractCard> cardList = new DEUIFilteredSuffixListBox<AbstractCard>("##all cards",
            sortedCards, p -> p.cardID, p -> p.name, this::passes);

    private static String getModID(String cardID)
    {
        int idx = cardID.indexOf(':');
        return idx < 0 ? BASE_GAME : cardID.substring(0, idx);
    }

    private boolean passes(AbstractCard card)
    {
        String mod = modList.get();
        return (cardFilter.passFilter(card.cardID) || cardFilter.passFilter(card.name)) && (ALL.equals(mod) || getModID(card.cardID).equals(mod));
    }

    private AbstractCard getCopy(AbstractCard chosen)
    {
        AbstractCard copy = chosen.makeCopy();
        if (copy instanceof PCLCard)
        {
            ((PCLCard) copy).changeForm(formCount.get(), 0);
        }
        for (int j = 0; j < upgradeCount.get(); ++j)
        {
            copy.upgrade();
        }
        return copy;
    }

    private void addToHand()
    {
        AbstractCard chosen = cardList.get();
        if (chosen != null)
        {
            PCLActions.instant.makeCardInHand(getCopy(chosen)).repeat(cardCount.get());
        }
    }

    private void addToDeck()
    {
        AbstractCard chosen = cardList.get();
        if (chosen != null)
        {
            for (int i = 0; i < cardCount.get(); i++)
            {
                PCLEffects.List.add(new ShowCardAndObtainEffect(getCopy(chosen), (float) Settings.WIDTH * 0.5f, (float) Settings.HEIGHT * 0.5f));
            }
        }
    }

    public PCLDebugCardPanel()
    {
        originalSortedCards.addAll(CardLibrary.getAllCards());
        originalSortedCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.getBuilder(0).createImplWithForms(false)));
        originalSortedCards.sort((a, b) -> StringUtils.compare(a.cardID, b.cardID));
        sortedModIDs.add(ALL);
        sortedModIDs.addAll(originalSortedCards.stream()
                .map(c -> getModID(c.cardID))
                .distinct()
                .collect(Collectors.toList()));
        modList.set(ALL);
    }

    public void refreshCards()
    {
        originalSortedCards.clear();
        originalSortedCards.addAll(CardLibrary.getAllCards());
        originalSortedCards.addAll(EUIUtils.map(PCLCustomCardSlot.getCards(null), slot -> slot.getBuilder(0).createImplWithForms(false)));
        originalSortedCards.sort((a, b) -> StringUtils.compare(a.cardID, b.cardID));
    }

    public void render()
    {
        cards.render(() -> {
            DEUIUtils.withWidth(90, () -> modList.renderInline());
            DEUIUtils.withFullWidth(() ->
            {
                cardFilter.draw("##");
                cardList.render();
            });
            DEUIUtils.withWidth(90, () ->
            {
                cardCount.renderInline();
                upgradeCount.renderInline();
                formCount.render();
            });
            DEUIUtils.disabledIf(!GameUtilities.inBattle() || cardList.get() == null, () ->
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
