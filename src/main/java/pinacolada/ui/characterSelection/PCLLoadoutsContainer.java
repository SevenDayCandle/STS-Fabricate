package pinacolada.ui.characterSelection;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.pcl.PCLLoadout;
import pinacolada.resources.pcl.PCLRuntimeLoadout;

import java.util.*;
import java.util.stream.Collectors;

public class PCLLoadoutsContainer
{
    public static final int MINIMUM_CARDS = 3; // 75
    public static final int MINIMUM_COMMON = 3;  // 30
    public static final int MINIMUM_UNCOMMON = 3;  // 25
    public static final int MINIMUM_RARE = 3;  // 8
    public static final int CHANCE_COMMON = 50;
    public static final int CHANCE_UNCOMMON = 40;
    public static final int CHANCE_RARE = 10;

    public final HashMap<AbstractCard, PCLRuntimeLoadout> loadoutMap = new HashMap<>();
    public final HashMap<AbstractCard.CardRarity, Integer> rarityCount = new HashMap<>();
    public final HashSet<String> bannedCards = new HashSet<>();
    public int currentCardLimit;
    public int totalCardsInPool = 0;
    public AbstractCard currentSeriesCard;
    private PCLAbstractPlayerData data;

    public static void preloadResources(PCLAbstractPlayerData data)
    {
        CardCrawlGame.sound.preload("CARD_SELECT");
        for (PCLLoadout loadout : data.loadouts.values())
        {
            PCLRuntimeLoadout temp = PCLRuntimeLoadout.tryCreate(loadout);
            if (temp != null)
            {
                temp.buildCard();
            }
        }
    }

    public void commitChanges(PCLAbstractPlayerData data)
    {
        data.selectedLoadout = find(currentSeriesCard).loadout;
        data.resources.config.bannedCards.set(bannedCards, true);
        data.resources.config.cardsCount.set(Math.max(MINIMUM_CARDS, currentCardLimit), true);

        EUIUtils.logInfoIfDebug(this, "Selected Loadout: " + data.selectedLoadout.getName());
        EUIUtils.logInfoIfDebug(this, "Banned Size: " + data.resources.config.bannedCards.get().size());
        EUIUtils.logInfoIfDebug(this, "Cards Size: " + data.resources.config.cardsCount.get());
    }

    public void createCards(PCLAbstractPlayerData data)
    {
        this.data = data;
        loadoutMap.clear();
        bannedCards.clear();

        final ArrayList<PCLRuntimeLoadout> seriesSelectionItems = new ArrayList<>();
        for (PCLLoadout loadout : data.getEveryLoadout())
        {
            final PCLRuntimeLoadout card = PCLRuntimeLoadout.tryCreate(loadout);
            if (card != null)
            {
                seriesSelectionItems.add(card);
            }
        }

        bannedCards.addAll(data.resources.config.bannedCards.get());
        for (PCLRuntimeLoadout c : seriesSelectionItems)
        {
            final AbstractCard card = c.buildCard();
            if (card != null)
            {
                loadoutMap.put(card, c);
                card.targetTransparency = 1f;

                if (c.loadout.id == (data.selectedLoadout.id))
                {
                    currentSeriesCard = card;
                    card.rarity = AbstractCard.CardRarity.RARE;
                    card.beginGlowing();
                }
            }
            else
            {
                EUIUtils.logError(this, "BuildCard() failed, " + c.loadout.getName());
            }
        }
        calculateCardCounts();
    }

    public PCLRuntimeLoadout find(AbstractCard card)
    {
        return loadoutMap.get(card);
    }

    public Collection<AbstractCard> getAllCards()
    {
        return loadoutMap.keySet().stream().sorted((a, b) -> StringUtils.compare(a.name, b.name)).collect(Collectors.toList());
    }

    public Collection<PCLRuntimeLoadout> getAllLoadouts()
    {
        return loadoutMap.values();
    }

    public Set<String> toggleCards(PCLRuntimeLoadout loadout, boolean value)
    {
        Set<String> cardIds = loadout.getCardPoolInPlay().keySet();
        if (value)
        {
            bannedCards.removeAll(cardIds);
        }
        else
        {
            bannedCards.addAll(cardIds);
        }
        calculateCardCounts();
        return cardIds;
    }

    public ArrayList<AbstractCard> getAllCardsInPool()
    {
        return EUIUtils.flattenList(EUIUtils.map(loadoutMap.values(), loadout -> loadout.getCardPoolInPlay().values()));
    }

    public boolean selectCard(AbstractCard card)
    {
        if (loadoutMap.containsKey(card) && card.type != AbstractCard.CardType.CURSE)
        {
            currentSeriesCard = card;
            for (AbstractCard c : loadoutMap.keySet())
            {
                c.stopGlowing();
                c.rarity = AbstractCard.CardRarity.COMMON;
            }
            card.rarity = AbstractCard.CardRarity.RARE;
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

    public void calculateCardCounts()
    {
        rarityCount.clear();
        totalCardsInPool = 0;
        for (AbstractCard card : getAllCardsInPool())
        {
            if (!bannedCards.contains(card.cardID))
            {
                totalCardsInPool += 1;
                rarityCount.merge(card.rarity, 1, Integer::sum);
            }
        }
        if (data != null)
        {
            currentCardLimit = MathUtils.clamp(data.resources.config.cardsCount.get(), MINIMUM_CARDS, totalCardsInPool);
        }

        for (PCLRuntimeLoadout loadout : getAllLoadouts())
        {
            loadout.updateCounts(bannedCards);
        }
    }
}
