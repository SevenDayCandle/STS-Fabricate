package pinacolada.actions.affinity;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.*;
import extendedui.ui.GridCardSelectScreenHelper;
import extendedui.utilities.GenericCallback;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.*;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TryChooseChoice<T> extends PCLActionWithCallback<ArrayList<ChoiceCard<T>>>
{
    protected final ArrayList<ChoiceCard<T>> selectedCards = new ArrayList<>();
    protected final CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

    protected ArrayList<GenericCallback<ArrayList<ChoiceCard<T>>>> conditionalCallbacks = new ArrayList<>();
    protected ArrayList<ChoiceBuilder<T>> builders = new ArrayList<>();
    protected FuncT1<String, ArrayList<AbstractCard>> dynamicString;
    protected ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard;
    protected ListSelection<AbstractCard> origin;
    protected int cost;
    protected boolean hideTopPanel;
    protected boolean canPlayerCancel;
    protected boolean anyNumber;
    protected boolean selected;

    public static TryChooseChoice<PCLAffinity> chooseAffinity(String name, int choices, AbstractCreature source, AbstractCreature target, PCLAffinity... affinities)
    {
        return chooseAffinity(name, choices, source, target, Arrays.asList(affinities));
    }

    public static TryChooseChoice<PCLAffinity> chooseAffinity(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PCLAffinity> affinities)
    {
        return new TryChooseChoice<PCLAffinity>(ActionType.CARD_MANIPULATION, name, source, choices, -2,
                EUIUtils.map(affinities, ChoiceBuilder::affinity));
    }

    public static TryChooseChoice<PCLAffinity> useAffinitySkill(String name, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills)
    {
        return useAffinitySkill(name, choices, -2, source, target, skills);
    }

    public static TryChooseChoice<PCLAffinity> useAffinitySkill(String name, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills)
    {
        return new TryChooseChoice<PCLAffinity>(ActionType.CARD_MANIPULATION, name, source, choices, cost,
                EUIUtils.map(skills, ChoiceBuilder::skillAffinity))
                .addConditionalCallback(choiceCards -> {
                    for (ChoiceCard<PCLAffinity> card : choiceCards)
                    {
                        card.onUse(CombatManager.playerSystem.generateInfo(card, source, target));
                    }
                });
    }

    public static TryChooseChoice<PSkill<?>> useSkill(PCLCardData sourceData, int choices, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills)
    {
        return useSkill(sourceData, choices, -2, source, target, skills);
    }

    public static TryChooseChoice<PSkill<?>> useSkill(PCLCardData sourceData, int choices, int cost, AbstractCreature source, AbstractCreature target, Collection<PSkill<?>> skills)
    {
        return new TryChooseChoice<PSkill<?>>(ActionType.CARD_MANIPULATION, sourceData.strings.NAME, source, choices, cost,
                EUIUtils.map(skills, i -> ChoiceBuilder.skill(sourceData, i)))
                .addConditionalCallback(choiceCards -> {
                    for (ChoiceCard<PSkill<?>> card : choiceCards)
                    {
                        card.value.use(CombatManager.playerSystem.generateInfo(card, source, target));
                    }
                });
    }

    public static TryChooseChoice<PSkill<?>> useSkillWithTargeting(PCLCardData sourceData, int choices, AbstractCreature source, Collection<PSkill<?>> skills)
    {
        return useSkillWithTargeting(sourceData, choices, -2, source, skills);
    }

    public static TryChooseChoice<PSkill<?>> useSkillWithTargeting(PCLCardData sourceData, int choices, int cost, AbstractCreature source, Collection<PSkill<?>> skills)
    {
        return new TryChooseChoice<PSkill<?>>(ActionType.CARD_MANIPULATION, sourceData.strings.NAME, source, choices, cost,
                EUIUtils.map(skills, i -> ChoiceBuilder.skill(sourceData, i)))
                .addConditionalCallback(choiceCards -> {
                    for (ChoiceCard<PSkill<?>> card : choiceCards)
                    {
                        PCLActions.top.selectCreature(card).addCallback(target -> {
                            card.value.use(CombatManager.playerSystem.generateInfo(card, source, target));
                        });
                    }
                });
    }

    @SafeVarargs
    public TryChooseChoice(PCLCardData sourceData, AbstractCreature source, int cost, T... items)
    {
        this(ActionType.CARD_MANIPULATION, sourceData, source, 1, cost, items);
    }

    public TryChooseChoice(PCLCardData sourceData, AbstractCreature source, int cost, Iterable<T> items)
    {
        this(ActionType.CARD_MANIPULATION, sourceData, source, 1, cost, items);
    }

    @SafeVarargs
    public TryChooseChoice(ActionType type, PCLCardData sourceData, AbstractCreature source, int amount, int cost, T... items)
    {
        this(type, sourceData.strings.NAME, source, amount, cost, EUIUtils.map(items, i -> ChoiceBuilder.create(sourceData, i)));
    }

    public TryChooseChoice(ActionType type, PCLCardData sourceData, AbstractCreature source, int amount, int cost, Iterable<T> items)
    {
        this(type, sourceData.strings.NAME, source, amount, cost, EUIUtils.map(items, i -> ChoiceBuilder.create(sourceData, i)));
    }

    @SafeVarargs
    public TryChooseChoice(ActionType type, PCLCardData sourceData, AbstractCreature source, int amount, int cost, ChoiceBuilder<T>... items)
    {
        this(type, sourceData.strings.NAME, source, amount, cost, Arrays.asList(items));
    }

    public TryChooseChoice(ActionType type, String name, AbstractCreature source, int amount, int cost, Collection<ChoiceBuilder<T>> items)
    {
        super(type);

        this.builders.addAll(items);
        this.canPlayerCancel = false;
        this.cost = cost;
        this.message = PGR.core.strings.gridSelection.chooseCards;
        this.source = source;

        initialize(amount, name);
    }

    public <S> PCLActionWithCallback<ArrayList<ChoiceCard<T>>> addConditionalCallback(S state, ActionT2<S, ArrayList<ChoiceCard<T>>> onCompletion)
    {
        conditionalCallbacks.add(GenericCallback.fromT2(onCompletion, state));

        return this;
    }

    public TryChooseChoice<T> addConditionalCallback(ActionT1<ArrayList<ChoiceCard<T>>> onCompletion)
    {
        conditionalCallbacks.add(GenericCallback.fromT1(onCompletion));

        return this;
    }

    public TryChooseChoice<T> addConditionalCallback(ActionT0 onCompletion)
    {
        conditionalCallbacks.add(GenericCallback.fromT0(onCompletion));

        return this;
    }

    public TryChooseChoice<T> cancellableFromPlayer(boolean value)
    {
        this.canPlayerCancel = value;

        return this;
    }

    public TryChooseChoice<T> setDynamicMessage(FuncT1<String, ArrayList<AbstractCard>> stringFunc)
    {
        this.dynamicString = stringFunc;

        return this;
    }

    public TryChooseChoice<T> setOnClick(ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard)
    {
        this.onClickCard = onClickCard;

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
        GridCardSelectScreenHelper.setDynamicLabel(dynamicString);
        GridCardSelectScreenHelper.setOnClickCard(onClickCard);

        for (ChoiceBuilder<T> builder : builders)
        {
            group.addToTop(builder.buildPCL());
        }

        if (group.isEmpty())
        {
            complete();
            return;
        }

        if (origin != null)
        {
            List<AbstractCard> temp = new ArrayList<>(group.group);

            boolean remove = origin.mode.isRandom();
            int max = Math.min(temp.size(), amount);
            for (int i = 0; i < max; i++)
            {
                final ChoiceCard<T> card = (ChoiceCard<T>) origin.get(temp, i, remove);
                if (card != null)
                {
                    selectedCards.add(card);
                }
            }

            selected = true;
            GridCardSelectScreenHelper.clear(true);
            complete(selectedCards);
        }
        else
        {
            if (anyNumber)
            {
                AbstractDungeon.gridSelectScreen.open(group, amount, true, updateMessage());
            }
            else
            {
                if (canPlayerCancel)
                {
                    // Setting canCancel to true does not ensure the cancel button will be shown...
                    AbstractDungeon.overlayMenu.cancelButton.show(GridCardSelectScreen.TEXT[1]);
                }
                else if (amount > 1 && amount > group.size())
                {
                    AbstractDungeon.gridSelectScreen.selectedCards.addAll(group.group);
                    return;
                }

                AbstractDungeon.gridSelectScreen.open(group, Math.min(group.size(), amount), updateMessage(), false, false, canPlayerCancel, false);
            }
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0)
        {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards)
            {
                if (c instanceof ChoiceCard)
                {
                    selectedCards.add((ChoiceCard<T>) c);
                }
            }
            selected = true;

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            GridCardSelectScreenHelper.clear(true);
        }

        if (selected)
        {
            if (tickDuration(deltaTime))
            {
                for (GenericCallback<ArrayList<ChoiceCard<T>>> callback : conditionalCallbacks)
                {
                    callback.complete(selectedCards);
                }
                complete(selectedCards);
            }
            return;
        }

        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.GRID) // cancelled
        {
            complete();
        }
    }

    @Override
    protected void complete()
    {
        if (hideTopPanel)
        {
            GameUtilities.setTopPanelVisible(true);
        }

        super.complete();
    }

    public TryChooseChoice<T> hideTopPanel(boolean hideTopPanel)
    {
        this.hideTopPanel = hideTopPanel;

        return this;
    }

    public TryChooseChoice<T> setMessage(String message)
    {
        this.message = message;

        return this;
    }

    public TryChooseChoice<T> setMessage(String format, Object... args)
    {
        this.message = EUIUtils.format(format, args);

        return this;
    }

    public TryChooseChoice<T> setOptions(boolean isRandom, boolean anyNumber)
    {
        return setOptions(isRandom ? PCLCardSelection.Random.toSelection() : null, anyNumber);
    }

    public TryChooseChoice<T> setOptions(ListSelection<AbstractCard> origin, boolean anyNumber)
    {
        this.anyNumber = anyNumber;
        this.origin = origin;

        return this;
    }
}
