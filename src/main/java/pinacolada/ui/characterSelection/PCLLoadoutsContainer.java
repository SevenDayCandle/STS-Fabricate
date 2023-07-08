package pinacolada.ui.characterSelection;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import extendedui.ui.screens.CustomCardLibraryScreen;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.skills.PSkill;

import java.util.*;
import java.util.stream.Collectors;

// Copied and modified from STS-AnimatorMod
public class PCLLoadoutsContainer {
    public static final int MINIMUM_CARDS = 75; // 75
    public static final int MINIMUM_COLORLESS = 40;

    public final ArrayList<AbstractCard> shownCards = new ArrayList<>();
    public final ArrayList<AbstractCard> shownColorlessCards = new ArrayList<>();
    public final HashMap<String, AbstractCard> allCards = new HashMap<>();
    public final HashMap<String, AbstractCard> allColorlessCards = new HashMap<>();
    public final HashMap<PCLCard, PCLLoadout> loadoutMap = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public final HashSet<String> bannedColorless = new HashSet<>();
    private AbstractPlayerData<?, ?> data;
    public int currentCardLimit;
    public PCLCard currentSeriesCard;

    public static boolean isRarityAllowed(AbstractCard.CardRarity rarity, AbstractCard.CardType type) {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
            case RARE:
                return type != AbstractCard.CardType.STATUS && type != AbstractCard.CardType.CURSE;
        }
        return false;
    }

    // Calculate the number of cards in each set, then update the loadout representative with that amount
    public void calculateCardCounts() {
        shownCards.clear();
        shownColorlessCards.clear();

        for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
            if (entry.getValue().isLocked()) {
                continue;
            }
            int selectedAmount = 0;
            for (PCLCardData data : entry.getValue().cardDatas) {
                if (!bannedCards.contains(data.ID)) {
                    selectedAmount += 1;
                    AbstractCard c = allCards.get(data.ID);
                    if (c != null) {
                        shownCards.add(c);
                    }
                }
            }

            for (PSkill<?> s : entry.getKey().getFullEffects()) {
                s.setAmount(selectedAmount);
            }
        }

        for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(AbstractCard.CardColor.COLORLESS).group) {
            if (PCLLoadoutsContainer.isRarityAllowed(c.rarity, c.type) &&
                    data.resources.containsColorless(c) && !bannedColorless.contains(c.cardID)) {
                shownColorlessCards.add(c);
            }
        }


        if (data != null) {
            currentCardLimit = MathUtils.clamp(data.config.cardsCount.get(), MINIMUM_CARDS, shownCards.size());
        }
    }

    public void commitChanges(AbstractPlayerData<?, ?> data) {
        data.selectedLoadout = find(currentSeriesCard);
        HashSet<String> banned = new HashSet<>(bannedCards);
        banned.addAll(bannedColorless);
        data.config.bannedCards.set(banned);
        data.config.cardsCount.set(Math.max(MINIMUM_CARDS, currentCardLimit));
        data.saveSelectedLoadout();

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.config.bannedCards.get().size());
        EUIUtils.logInfoIfDebug(this, "Cards Size: " + data.config.cardsCount.get());
    }

    public void createCards(AbstractPlayerData<?, ?> data) {
        this.data = data;
        allCards.clear();
        allColorlessCards.clear();
        shownCards.clear();
        shownColorlessCards.clear();
        loadoutMap.clear();
        bannedCards.clear();
        bannedColorless.clear();

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
                    allCards.put(cData.ID, card);
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedCards.add(cData.ID);
                }
            }

            // Colorless bans
            for (PCLCardData cData : series.colorlessData) {
                AbstractCard card = CardLibrary.getCard(cData.ID);
                if (card != null) {
                    allColorlessCards.put(cData.ID, card);
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedColorless.add(cData.ID);
                }
            }
        }
        calculateCardCounts();
    }

    public PCLLoadout find(PCLCard card) {
        return loadoutMap.get(card);
    }

    public Collection<AbstractCard> getAllCards() {
        return loadoutMap.entrySet()
                .stream()
                .sorted((a, b) -> {
                    PCLLoadout lA = a.getValue();
                    PCLLoadout lB = b.getValue();
                    if (lA.isCore()) {
                        return -1;
                    }
                    else if (lB.isCore()) {
                        return 1;
                    }
                    else if (lA.unlockLevel != lB.unlockLevel) {
                        return lA.unlockLevel - lB.unlockLevel;
                    }
                    return StringUtils.compare(a.getKey().name, b.getKey().name);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Collection<PCLLoadout> getAllLoadouts() {
        return loadoutMap.values();
    }

    public boolean isValid() {
        return shownCards.size() >= MINIMUM_CARDS && shownColorlessCards.size() >= MINIMUM_COLORLESS;
    }

    // You cannot select core loadout cards
    public boolean selectCard(PCLCard card) {
        if (loadoutMap.containsKey(card) && card.type == PCLLoadout.SELECTABLE_TYPE) {
            currentSeriesCard = card;
            for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
                PCLCard c = entry.getKey();
                c.stopGlowing();
                c.setCardRarity(entry.getValue().isCore() ? AbstractCard.CardRarity.CURSE : AbstractCard.CardRarity.COMMON);
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
