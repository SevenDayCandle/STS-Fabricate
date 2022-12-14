package pinacolada.resources.pcl;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardAffinities;
import pinacolada.cards.base.PCLCardData;
import pinacolada.utilities.RotatingList;

import java.util.ArrayList;

public class PCLCardSlot
{
    public static final int MAX_LIMIT = 6;
    public transient final PCLLoadoutData container;
    public transient final RotatingList<Item> cards;

    public Item selected;
    public int amount;
    public int currentMax;
    public int max;
    public int min;

    public PCLCardSlot(PCLLoadoutData container)
    {
        this(container, 0, MAX_LIMIT);
    }

    public PCLCardSlot(PCLLoadoutData container, int min, int max)
    {
        if (min > max)
        {
            throw new RuntimeException("Min can't be greater than max.");
        }

        this.cards = new RotatingList<>();
        this.container = container;
        this.min = min;
        this.max = max;
    }

    public void add()
    {
        if (amount < max && amount < currentMax)
        {
            amount += 1;
        }
    }

    public void addItem(PCLCardData data, int estimatedValue)
    {
        cards.add(new Item(data, estimatedValue));
    }

    public boolean canAdd()
    {
        return (selected != null) && amount < max && amount < currentMax;
    }

    public boolean canDecrement()
    {
        return (selected != null) && amount > 1 && amount > min;
    }

    public boolean canRemove()
    {
        return (selected != null) && min <= 0;
    }

    public PCLCardSlot clear()
    {
        return select(null);
    }

    public void decrement()
    {
        if (amount > 1)
        {
            amount -= 1;
        }
    }

    public PCLCardAffinities getAffinities()
    {
        PCLCard card = EUIUtils.safeCast(getCard(false), PCLCard.class);
        return card != null ? card.affinities : null;
    }

    public PCLCard getCard(boolean refresh)
    {
        return selected != null ? selected.getCard(refresh) : null;
    }

    public PCLCardData getData()
    {
        return selected != null ? selected.data : null;
    }

    public int getEstimatedValue()
    {
        return amount * (selected == null ? 0 : selected.estimatedValue);
    }

    public ArrayList<PCLCard> getSelectableCards()
    {
        final ArrayList<PCLCard> cards = new ArrayList<>();
        for (Item item : this.cards)
        {
            boolean add = true;
            for (PCLCardSlot slot : container.cardSlots)
            {
                if (slot != this && slot.getData() == item.data)
                {
                    add = false;
                }
            }

            if (add)
            {
                cards.add(item.getCard(true));
            }
        }

        return cards;
    }

    public int getSlotIndex()
    {
        return container.cardSlots.indexOf(this);
    }

    public PCLCardSlot makeCopy(PCLLoadoutData container)
    {
        final PCLCardSlot copy = new PCLCardSlot(container, min, max);
        for (Item item : cards)
        {
            copy.cards.add(item);
        }
        if (selected != null)
        {
            copy.select(selected.data, amount);
        }

        return copy;
    }

    public void markAllSeen()
    {
        for (Item item : cards)
        {
            item.data.markSeen();
        }
    }

    public void markCurrentSeen()
    {
        if (selected != null)
        {
            selected.data.markSeen();
        }
    }

    public void next()
    {
        if (selected == null)
        {
            select(cards.current());
        }
        else
        {
            select(cards.next(true));
        }

        int i = 0;
        while (true)
        {
            int currentIndex = i;
            for (PCLCardSlot s : container.cardSlots)
            {
                if (s != this && selected.data == s.getData())
                {
                    select(cards.next(true));
                    i += 1;
                    break;
                }
            }

            if (currentIndex == i)
            {
                return;
            }
            else if (i >= cards.count())
            {
                select(null);
                return;
            }
        }
    }

    public PCLCardSlot select(Item item)
    {
        return select(item, item == null ? 0 : 1);
    }

    public PCLCardSlot select(String id, int amount)
    {
        int i = 0;
        for (Item item : cards)
        {
            if (item.data.ID.equals(id))
            {
                return select(i, amount);
            }
            i += 1;
        }

        return null;
    }

    public PCLCardSlot select(PCLCardData data, int amount)
    {
        int i = 0;
        for (Item item : cards)
        {
            if (item.data == data)
            {
                return select(i, amount);
            }
            i += 1;
        }

        return null;
    }

    public PCLCardSlot select(int index, int amount)
    {
        return select(cards.setIndex(index), amount);
    }

    public PCLCardSlot select(Item item, int amount)
    {
        selected = item;
        if (item == null)
        {
            if (min > 0)
            {
                throw new RuntimeException("Tried to deselect an item, but at least 1 card needs to be selected.");
            }
            this.amount = 0;
        }
        else
        {
            if (max <= 0)
            {
                throw new RuntimeException("Tried to select an item, but no cards are allowed in this slot.");
            }

            currentMax = Math.min(max, selected.data.maxCopies >= min ? selected.data.maxCopies : max);
            if (currentMax <= 0)
            {
                currentMax = MAX_LIMIT;
            }
            this.amount = MathUtils.clamp(amount, min, currentMax);
        }

        return this;
    }

    public static class Item
    {
        public final PCLCardData data;
        public final int estimatedValue;

        protected PCLCard card;

        public Item(PCLCardData data, int estimatedValue)
        {
            this.data = data;
            this.estimatedValue = estimatedValue;
        }

        public PCLCard getCard(boolean forceRefresh)
        {
            if (card == null || forceRefresh)
            {
                PCLCard eCard = EUIUtils.safeCast(CardLibrary.getCard(data.ID), PCLCard.class);
                if (eCard != null)
                {
                    card = (PCLCard) CardLibrary.getCard(data.ID).makeCopy();
                    if (data.isNotSeen())
                    {
                        card.isSeen = false;
                    }
                }
            }

            return card;
        }
    }
}
