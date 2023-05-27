package pinacolada.ui.characterSelection;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;

import java.util.*;
import java.util.stream.Collectors;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutsContainer {
    public static final int MINIMUM_CARDS = 75; // 75
    public static final int CHANCE_COMMON = 50;
    public static final int CHANCE_UNCOMMON = 40;
    public static final int CHANCE_RARE = 10;

    public final ArrayList<AbstractCard> allCards = new ArrayList<>();
    public final HashMap<PCLCard, PCLLoadout> loadoutMap = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    private PCLAbstractPlayerData data;
    public int currentCardLimit;
    public int totalCardsInPool = 0;
    public PCLCard currentSeriesCard;

    // Calculate the number of cards in each set, then update the loadout representative with that amount
    public void calculateCardCounts() {
        totalCardsInPool = 0;

        for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
            if (entry.getValue().isLocked()) {
                continue;
            }
            int selectedAmount = 0;
            for (PCLCardData data : entry.getValue().cardDatas) {
                if (!bannedCards.contains(data.ID)) {
                    selectedAmount += 1;
                    totalCardsInPool += 1;
                }
            }

            for (PSkill<?> s : entry.getKey().getFullEffects()) {
                s.setAmount(selectedAmount);
            }
        }

        if (data != null) {
            currentCardLimit = MathUtils.clamp(data.config.cardsCount.get(), MINIMUM_CARDS, totalCardsInPool);
        }
    }

    public void commitChanges(PCLAbstractPlayerData data) {
        data.selectedLoadout = find(currentSeriesCard);
        data.config.bannedCards.set(bannedCards, true);
        data.config.cardsCount.set(Math.max(MINIMUM_CARDS, currentCardLimit), true);

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.config.bannedCards.get().size());
        EUIUtils.logInfoIfDebug(this, "Cards Size: " + data.config.cardsCount.get());
    }

    public void createCards(PCLAbstractPlayerData data) {
        this.data = data;
        allCards.clear();
        loadoutMap.clear();
        bannedCards.clear();

        bannedCards.addAll(data.config.bannedCards.get());
        for (PCLLoadout series : data.getEveryLoadout()) {
            // Add series representation to the grid selection
            final PCLCard gridCard = series.buildCard();
            if (gridCard != null) {
                loadoutMap.put(gridCard, series);
                gridCard.targetTransparency = 1f;

                if (Objects.equals(series.ID, data.selectedLoadout.ID)) {
                    currentSeriesCard = gridCard;
                    gridCard.setCardRarity(AbstractCard.CardRarity.RARE);
                    gridCard.beginGlowing();
                }
            }
            else {
                EUIUtils.logError(this, "BuildCard() failed, " + series.getName());
            }

            // Add this series cards to the total list of available cards
            for (PCLCardData cData : series.cardDatas) {
                AbstractCard card = CardLibrary.getCard(cData.ID);
                if (card != null) {
                    allCards.add(card);
                }

                // Ensure that banned cards are not banned
                bannedCards.remove(cData.ID);
            }
        }
        calculateCardCounts();
    }

    public PCLLoadout find(PCLCard card) {
        return loadoutMap.get(card);
    }

    public Collection<AbstractCard> getAllCards() {
        return loadoutMap.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name, b.name)).collect(Collectors.toList());
    }

    public Collection<PCLLoadout> getAllLoadouts() {
        return loadoutMap.values();
    }

    public boolean isValid() {
        return totalCardsInPool >= MINIMUM_CARDS;
    }

    // You cannot select core loadout cards
    public boolean selectCard(PCLCard card) {
        if (loadoutMap.containsKey(card) && card.type == PCLLoadout.SELECTABLE_TYPE) {
            currentSeriesCard = card;
            for (PCLCard c : loadoutMap.keySet()) {
                c.stopGlowing();
                c.setCardRarity(AbstractCard.CardRarity.COMMON);
            }
            card.setCardRarity(AbstractCard.CardRarity.RARE);
            card.beginGlowing();
            return true;
        }
        return false;
    }

    public Collection<String> toggleCards(PCLLoadout loadout, boolean value) {
        Collection<String> cardIds = EUIUtils.map(loadout.cardDatas, l -> l.ID);
        if (value) {
            cardIds.forEach(bannedCards::remove);
        }
        else {
            bannedCards.addAll(cardIds);
        }
        calculateCardCounts();
        return cardIds;
    }
}
