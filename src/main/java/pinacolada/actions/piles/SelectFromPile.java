package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.text.EUISmartText;
import extendedui.ui.GridCardSelectScreenHelper;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActions;
import pinacolada.actions.utility.CardFilterAction;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Copied and modified from STS-AnimatorMod
public class SelectFromPile extends CardFilterAction
{
    protected final CardGroup fakeHandGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    protected final CardGroup[] groups;

    protected ListSelection<AbstractCard> origin;
    protected ListSelection<AbstractCard> destination;
    protected ListSelection<AbstractCard> maxChoicesOrigin;
    protected int maxChoices;
    protected boolean hideTopPanel;
    protected boolean canPlayerCancel;
    protected boolean anyNumber = true;
    protected boolean selected;
    protected boolean realtime = false;
    protected boolean showEffect = false;
    protected boolean forTransform;
    protected boolean forUpgrade;
    protected boolean forPurge;

    public SelectFromPile(String sourceName, int amount, CardGroup... groups)
    {
        this(ActionType.CARD_MANIPULATION, sourceName, null, amount, groups);
    }

    public SelectFromPile(String sourceName, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        this(ActionType.CARD_MANIPULATION, sourceName, null, amount, origin, groups);
    }

    public SelectFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        this(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
    }

    public SelectFromPile(String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        this(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
    }

    public SelectFromPile(ActionType type, String sourceName, AbstractCreature target, int amount, CardGroup... groups)
    {
        this(type, sourceName, target, amount, null, groups);
    }

    public SelectFromPile(ActionType type, String sourceName, AbstractCreature target, int amount, ListSelection<AbstractCard> origin, CardGroup... groups)
    {
        super(type);

        this.groups = groups;
        this.origin = origin;
        this.canPlayerCancel = false;
        this.message = PGR.core.strings.grid_chooseCards;

        initialize(player, target, amount, sourceName);
    }

    protected void addCard(CardGroup group, AbstractCard card)
    {
        group.group.add(card);

        if (!card.isSeen)
        {
            UnlockTracker.markCardAsSeen(card.cardID);
            card.isLocked = false;
            card.isSeen = true;
        }
    }

    protected boolean canSelect(AbstractCard card)
    {
        return filter == null || filter.check(card);
    }

    public SelectFromPile cancellableFromPlayer(boolean value)
    {
        this.canPlayerCancel = value;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (hideTopPanel)
        {
            GameUtilities.setTopPanelVisible(false);
        }

        GridCardSelectScreenHelper.clear(true);
        GridCardSelectScreenHelper.setCondition(condition);
        GridCardSelectScreenHelper.setDynamicLabel(dynamicString);
        GridCardSelectScreenHelper.setOnClickCard(onClickCard);

        for (CardGroup group : groups)
        {
            CardGroup temp = new CardGroup(group.type);
            for (AbstractCard card : group.group)
            {
                if (canSelect(card))
                {
                    addCard(temp, card);
                }
            }

            if (temp.type == CardGroup.CardGroupType.HAND && origin == null && maxChoicesOrigin == null)
            {
                fakeHandGroup.group.addAll(temp.group);

                for (AbstractCard c : temp.group)
                {
                    player.hand.removeCard(c);
                    c.stopGlowing();
                }

                GridCardSelectScreenHelper.addGroup(fakeHandGroup);
            }
            else
            {
                if (temp.type == CardGroup.CardGroupType.DRAW_PILE && origin == null && maxChoicesOrigin == null)
                {
                    if (GameUtilities.hasRelicEffect(FrozenEye.ID))
                    {
                        Collections.reverse(temp.group);
                    }
                    else
                    {
                        temp.sortAlphabetically(true);
                        temp.sortByRarityPlusStatusCardType(true);
                    }
                }

                GridCardSelectScreenHelper.addGroup(temp);
            }
        }

        CardGroup mergedGroup = GridCardSelectScreenHelper.getCardGroup();
        if (mergedGroup.isEmpty())
        {
            player.hand.group.addAll(fakeHandGroup.group);
            GridCardSelectScreenHelper.clear(true);
            complete(new ArrayList<>());
            return;
        }

        if (maxChoicesOrigin != null)
        {
            List<AbstractCard> temp = new ArrayList<>(mergedGroup.group);
            CardGroup newMerged = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            getCardSubset(temp, newMerged.group, maxChoicesOrigin, maxChoices);
            GridCardSelectScreenHelper.clear(false);
            GridCardSelectScreenHelper.addGroup(newMerged);
            mergedGroup = GridCardSelectScreenHelper.getCardGroup();
        }

        if (origin != null)
        {
            List<AbstractCard> temp = new ArrayList<>(mergedGroup.group);
            getCardSubset(temp, selectedCards, origin, amount);
            selected = true;
            GridCardSelectScreenHelper.clear(true);
            complete(selectedCards);
        }
        else
        {
            if (canPlayerCancel)
            {
                // Setting canCancel to true does not ensure the cancel button will be shown...
                AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
            }
            if (anyNumber)
            {
                AbstractDungeon.gridSelectScreen.open(mergedGroup, amount, true, dynamicString == null ? updateMessage() : "");
            }
            else
            {
                if (!canPlayerCancel && amount > 1 && amount > mergedGroup.size())
                {
                    AbstractDungeon.gridSelectScreen.selectedCards.addAll(mergedGroup.group);
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(mergedGroup, Math.min(mergedGroup.size(), amount), dynamicString == null ? updateMessage() : "", forUpgrade, forTransform, canPlayerCancel, forPurge);
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0)
        {
            selectedCards.addAll(AbstractDungeon.gridSelectScreen.selectedCards);
            selected = true;

            player.hand.group.addAll(fakeHandGroup.group);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            GridCardSelectScreenHelper.clear(true);
        }

        if (selected)
        {
            if (tickDuration(deltaTime))
            {
                complete(selectedCards);
            }
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) // cancelled
        {
            player.hand.group.addAll(fakeHandGroup.group);
            complete(new ArrayList<>());
        }
    }

    @Override
    protected void completeImpl()
    {
        if (hideTopPanel)
        {
            GameUtilities.setTopPanelVisible(true);
        }

        super.completeImpl();
    }

    protected void getCardSubset(List<AbstractCard> source, List<AbstractCard> dest, ListSelection<AbstractCard> o, int count)
    {
        boolean remove = o.mode.isRandom();
        int max = Math.min(source.size(), count);
        for (int i = 0; i < max; i++)
        {
            final AbstractCard card = o.get(source, i, remove);
            if (card != null)
            {
                dest.add(card);
            }
        }
    }

    @Override
    public String updateMessage()
    {
        return super.updateMessageInternal(PGR.core.strings.act_generic2(getActionMessage(), EUISmartText.parseLogicString(EUIUtils.format(PGR.core.strings.subjects_cardN, amount))));
    }

    public String getActionMessage()
    {
        return PGR.core.tooltips.select.title;
    }

    public SelectFromPile hideTopPanel(boolean hideTopPanel)
    {
        this.hideTopPanel = hideTopPanel;

        return this;
    }

    public void moveToPile(Collection<AbstractCard> result, CardGroup group)
    {
        for (AbstractCard card : result)
        {
            PCLActions.top.moveCard(card, group).showEffect(showEffect, realtime);
        }
    }

    public SelectFromPile setAnyNumber(boolean anyNumber)
    {
        this.anyNumber = anyNumber;

        return this;
    }

    public SelectFromPile setCompletionRequirement(FuncT1<Boolean, ArrayList<AbstractCard>> condition)
    {
        this.condition = GenericCondition.fromT1(condition);

        return this;
    }

    public SelectFromPile setOrigin(PCLCardSelection origin)
    {
        this.origin = origin.toSelection();

        return this;
    }

    public SelectFromPile setOrigin(ListSelection<AbstractCard> origin)
    {
        this.origin = origin;

        return this;
    }

    public SelectFromPile setDestination(PCLCardSelection destination)
    {
        this.destination = destination.toSelection();

        return this;
    }

    public SelectFromPile setDestination(ListSelection<AbstractCard> destination)
    {
        this.destination = destination;

        return this;
    }

    public SelectFromPile setDynamicMessage(FuncT1<String, ArrayList<AbstractCard>> stringFunc)
    {
        this.dynamicString = stringFunc;

        return this;
    }

    public SelectFromPile setFilter(FuncT1<Boolean, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT1(filter);

        return this;
    }

    public <S> SelectFromPile setFilter(S state, FuncT2<Boolean, S, AbstractCard> filter)
    {
        this.filter = GenericCondition.fromT2(filter, state);

        return this;
    }

    public SelectFromPile setMaxChoices(Integer maxChoices)
    {
        return setMaxChoices(maxChoices, PCLCardSelection.Random.toSelection());
    }

    public SelectFromPile setMaxChoices(Integer maxChoices, ListSelection<AbstractCard> origin)
    {
        this.maxChoices = maxChoices;
        this.maxChoicesOrigin = origin;

        return this;
    }

    public SelectFromPile setMessage(String message)
    {
        this.message = message;

        return this;
    }

    public SelectFromPile setMessage(String format, Object... args)
    {
        this.message = EUIUtils.format(format, args);

        return this;
    }

    public SelectFromPile setOnClick(ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard)
    {
        this.onClickCard = onClickCard;

        return this;
    }

    public SelectFromPile setOptions(ListSelection<AbstractCard> origin, boolean anyNumber)
    {
        return setOptions(origin, anyNumber, false, false, false);
    }

    public SelectFromPile setOptions(ListSelection<AbstractCard> origin, boolean anyNumber, boolean forTransform, boolean forUpgrade, boolean forPurge)
    {
        this.anyNumber = anyNumber;
        this.origin = origin;
        this.forTransform = forTransform;
        this.forUpgrade = forUpgrade;
        this.forPurge = forPurge;

        return this;
    }

    public SelectFromPile showEffect(boolean showEffect, boolean isRealtime)
    {
        this.showEffect = showEffect;
        this.realtime = isRealtime;

        return this;
    }
}
