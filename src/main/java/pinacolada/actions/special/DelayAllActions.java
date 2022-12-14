package pinacolada.actions.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;

import java.util.ArrayList;

public class DelayAllActions extends PCLAction
{
    protected final ArrayList<AbstractGameAction> actions = new ArrayList<>();
    protected FuncT1<Boolean, AbstractGameAction> except;
    protected boolean currentOnly = false;

    public DelayAllActions()
    {
        this(false);
    }

    public DelayAllActions(boolean currentOnly)
    {
        super(AbstractGameAction.ActionType.SPECIAL, 0.01f);

        if (currentOnly)
        {
            createList();
        }
    }

    public DelayAllActions except(FuncT1<Boolean, AbstractGameAction> except)
    {
        this.except = except;

        return this;
    }

    @Override
    protected void firstUpdate()
    {
        if (!currentOnly)
        {
            createList();
        }

        AbstractDungeon.actionManager.actions.removeAll(actions);
        if (actions.size() > 0)
        {
            PCLActions.last.callback(() -> AbstractDungeon.actionManager.actions.addAll(actions));
        }

        complete();
    }

    protected void createList()
    {
        for (AbstractGameAction action : AbstractDungeon.actionManager.actions)
        {
            if (action != this && (except == null || !except.invoke(action)))
            {
                actions.add(action);
            }
        }
    }
}
