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
public class PCLLoadoutsContainer
{
    public static final int MINIMUM_CARDS = 75; // 75
    public static final int MINIMUM_COMMON = 25;  // 30
    public static final int MINIMUM_UNCOMMON = 20;  // 25
    public static final int MINIMUM_RARE = 7;  // 8
    public static final int CHANCE_COMMON = 50;
    public static final int CHANCE_UNCOMMON = 40;
    public static final int CHANCE_RARE = 10;

    public final ArrayList<AbstractCard> allCards = new ArrayList<>();
    public final HashMap<PCLCard, PCLLoadout> loadoutMap = new HashMap<>();
    public final HashMap<AbstractCard.CardRarity, Integer> rarityCount = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public int currentCardLimit;
    public int totalCardsInPool = 0;
    public PCLCard currentSeriesCard;
    private PCLAbstractPlayerData data;

    public void commitChanges(PCLAbstractPlayerData data)
    {
        data.selectedLoadout = find(currentSeriesCard);
        data.resources.config.bannedCards.set(bannedCards, true);
        data.resources.config.cardsCount.set(Math.max(MINIMUM_CARDS, currentCardLimit), true);

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.resources.config.bannedCards.get().size());
        EUIUtils.logInfoIfDebug(this, "Cards Size: " + data.resources.config.cardsCount.get());
    }

    public void createCards(PCLAbstractPlayerData data)
    {
        this.data = data;
        allCards.clear();
        loadoutMap.clear();
        bannedCards.clear();

        bannedCards.addAll(data.resources.config.bannedCards.get());
        for (PCLLoadout series : data.getEveryLoadout())
        {
            // Add series representation to the grid selection
            final PCLCard gridCard = series.buildCard();
            if (gridCard != null)
            {
                loadoutMap.put(gridCard, series);
                gridCard.targetTransparency = 1f;

                if (series.id == (data.selectedLoadout.id))
                {
                    currentSeriesCard = gridCard;
                    gridCard.setCardRarity(AbstractCard.CardRarity.RARE);
                    gridCard.beginGlowing();
                }
            }
            else
            {
                EUIUtils.logError(this, "BuildCard() failed, " + series.getName());
            }

            // Add this series cards to the total list of available cards
            for (PCLCardData cData : series.cardData)
            {
                AbstractCard card = CardLibrary.getCard(cData.ID);
                if (card != null)
                {
                    allCards.add(card);
                }

                // Ensure that banned cards are not banned
                bannedCards.remove(cData.ID);
            }
        }
        calculateCardCounts();
    }

    public PCLLoadout find(PCLCard card)
    {
        return loadoutMap.get(card);
    }

    public Collection<AbstractCard> getAllCards()
    {
        return loadoutMap.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name, b.name)).collect(Collectors.toList());
    }

    public Collection<PCLLoadout> getAllLoadouts()
    {
        return loadoutMap.values();
    }

    public Collection<String> toggleCards(PCLLoadout loadout, boolean value)
    {
        Collection<String> cardIds = EUIUtils.map(loadout.cardData, l -> l.ID);
        if (value)
        {
            cardIds.forEach(bannedCards::remove);
        }
        else
        {
            bannedCards.addAll(cardIds);
        }
        calculateCardCounts();
        return cardIds;
    }

    // You cannot select core loadout cards
    public boolean selectCard(PCLCard card)
    {
        if (loadoutMap.containsKey(card) && card.type != PCLLoadout.UNSELECTABLE_TYPE)
        {
            currentSeriesCard = card;
            for (PCLCard c : loadoutMap.keySet())
            {
                c.stopGlowing();
                c.setCardRarity(AbstractCard.CardRarity.COMMON);
            }
            card.setCardRarity(AbstractCard.CardRarity.RARE);
            card.beginGlowing();
            return true;
        }
        return false;
    }

    public boolean isValid()
    {
        return totalCardsInPool >= MINIMUM_CARDS
                && rarityCount.getOrDefault(AbstractCard.CardRarity.COMMON, 0) >= MINIMUM_COMMON
                && rarityCount.getOrDefault(AbstractCard.CardRarity.UNCOMMON, 0) >= MINIMUM_UNCOMMON
                && rarityCount.getOrDefault(AbstractCard.CardRarity.RARE, 0) >= MINIMUM_RARE;
    }

    public int getMinimum(AbstractCard.CardRarity rarity)
    {
        switch (rarity)
        {
            case RARE:
                return MINIMUM_RARE;
            case UNCOMMON:
                return MINIMUM_UNCOMMON;
            case COMMON:
                return MINIMUM_COMMON;
        }
        return MINIMUM_CARDS;
    }

    // Calculate the number of cards in each set, then update the loadout representative with that amount
    public void calculateCardCounts()
    {
        rarityCount.clear();
        totalCardsInPool = 0;

        for (Map.Entry<PCLCard, PCLLoadout> entry : loadoutMap.entrySet())
        {
            int selectedAmount = 0;
            for (PCLCardData data : entry.getValue().cardData)
            {
                if (!bannedCards.contains(data.ID))
                {
                    selectedAmount += 1;
                    totalCardsInPool += 1;
                    rarityCount.merge(data.cardRarity, 1, Integer::sum);
                }
            }

            for (PSkill<?> s : entry.getKey().getFullEffects())
            {
                s.setAmount(selectedAmount);
            }
        }

        if (data != null)
        {
            currentCardLimit = MathUtils.clamp(data.resources.config.cardsCount.get(), MINIMUM_CARDS, totalCardsInPool);
        }
    }
}
