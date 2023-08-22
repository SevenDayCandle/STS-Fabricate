package pinacolada.ui.characterSelection;

import com.megacrit.cardcrawl.cards.AbstractCard;
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

    public final ArrayList<AbstractCard> shownCards = new ArrayList<>();
    public final ArrayList<AbstractCard> shownColorlessCards = new ArrayList<>();
    public final HashMap<String, AbstractCard> allCards = new HashMap<>();
    public final HashMap<String, AbstractCard> allColorlessCards = new HashMap<>();
    public final HashMap<PCLCard, PCLLoadout> loadoutMap = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public final HashSet<String> bannedColorless = new HashSet<>();
    public final HashSet<String> selectedLoadouts = new HashSet<>();
    private AbstractPlayerData<?, ?> data;
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
            PCLLoadout loadout = entry.getValue();
            if (loadout.isLocked() || !(loadout.isCore() || selectedLoadouts.contains(entry.getValue().ID) || currentSeriesCard == entry.getKey())) {
                continue;
            }
            int bannedAmount = 0;
            int unlockedAmount = 0;
            for (PCLCardData data : loadout.cardDatas) {
                if (!data.isLocked()) {
                    unlockedAmount += 1;
                }
                if (!bannedCards.contains(data.ID)) {
                    AbstractCard c = allCards.get(data.ID);
                    if (c != null) {
                        shownCards.add(c);
                    }
                }
                else {
                    bannedAmount += 1;
                }
            }

            PSkill<?> unlockEffect = entry.getKey().getEffect(0);
            if (unlockEffect != null) {
                unlockEffect.setAmount(unlockedAmount);
            }
            PSkill<?> bannedEffect = entry.getKey().getEffect(1);
            if (bannedEffect != null) {
                bannedEffect.setAmount(bannedAmount);
            }
        }

        for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(AbstractCard.CardColor.COLORLESS).group) {
            if (PCLLoadoutsContainer.isRarityAllowed(c.rarity, c.type) &&
                    data.resources.containsColorless(c) && !bannedColorless.contains(c.cardID)) {
                shownColorlessCards.add(c);
            }
        }
    }

    public void commitChanges(AbstractPlayerData<?, ?> data) {
        data.selectedLoadout = find(currentSeriesCard);
        HashSet<String> banned = new HashSet<>(bannedCards);
        banned.addAll(bannedColorless);
        data.config.bannedCards.set(banned);
        data.config.selectedLoadouts.set(selectedLoadouts);
        data.saveSelectedLoadout();

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Series Count: " + data.config.selectedLoadouts.get().size());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.config.bannedCards.get().size());
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
        selectedLoadouts.clear();

        selectedLoadouts.addAll(data.config.selectedLoadouts.get());

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
                AbstractCard card = cData.makeCardFromLibrary(0);
                allCards.put(cData.ID, card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
                }
                if (data.config.bannedCards.get().contains(cData.ID)) {
                    bannedCards.add(cData.ID);
                }
            }

            // Colorless bans
            for (PCLCardData cData : series.colorlessData) {
                AbstractCard card = cData.makeCardFromLibrary(0);
                allColorlessCards.put(cData.ID, card);
                if (card instanceof PCLCard) {
                    ((PCLCard) card).affinities.updateSortedList();
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

    // Grid will not actually contain the core series card for now
    public Collection<AbstractCard> getAllCards() {
        return loadoutMap.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isCore())
                .sorted((a, b) -> {
                    PCLLoadout lA = a.getValue();
                    PCLLoadout lB = b.getValue();
                    if (lA.unlockLevel != lB.unlockLevel) {
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
        return shownCards.size() >= PCLSeriesSelectScreen.MINIMUM_CARDS && shownColorlessCards.size() >= PCLSeriesSelectScreen.MINIMUM_COLORLESS;
    }

    // You cannot select core loadout cards
    public boolean selectCard(PCLCard card) {
        if (loadoutMap.containsKey(card) && card.type == PCLLoadout.SELECTABLE_TYPE) {
            currentSeriesCard = card;
            for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet()) {
                PCLCard c = entry.getKey();
                c.stopGlowing();
                c.setCardRarity(entry.getValue().isCore() ? AbstractCard.CardRarity.CURSE :
                        selectedLoadouts.contains(entry.getValue().ID) ? AbstractCard.CardRarity.UNCOMMON :
                        AbstractCard.CardRarity.COMMON);
            }
            card.setCardRarity(AbstractCard.CardRarity.RARE);
            card.beginGlowing();
            return true;
        }
        return false;
    }

    public void toggleCards(PCLLoadout loadout, boolean value) {
        if (value) {
            selectedLoadouts.add(loadout.ID);
        }
        else {
            selectedLoadouts.remove(loadout.ID);
        }
        calculateCardCounts();
    }
}
