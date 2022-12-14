package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import pinacolada.interfaces.markers.CardRewardActionProvider;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class PCLCardRewardAction extends EUIBase
{
    protected final ArrayList<PCLCardRewardActionButton> buttons = new ArrayList<>();

    protected final ActionT1<AbstractCard> onCardAct;
    protected final ActionT1<AbstractCard> onCardAdded;
    protected CardRewardActionProvider actionProvider;
    protected boolean canReroll;
    protected RewardItem rewardItem;
    private boolean shouldClose; // Needed to prevent comodification errors

    public PCLCardRewardAction(ActionT1<AbstractCard> onCardAdded,
                               ActionT1<AbstractCard> onCardAct)
    {
        this.onCardAct = onCardAct;
        this.onCardAdded = onCardAdded;
    }

    public void action(PCLCardRewardActionButton button)
    {
        final int cardIndex = button.getIndex();
        final AbstractCard targetCard = button.getCard(false);
        if (targetCard == null || cardIndex > rewardItem.cards.size())
        {
            return;
        }

        final AbstractCard returnedCard = actionImpl(button, targetCard, cardIndex);
        onCardReroll(targetCard);
        onCardAdded(returnedCard);

        button.setActive(false);
        setActive(actionProvider.canAct());
    }

    abstract AbstractCard actionImpl(PCLCardRewardActionButton button, AbstractCard card, int cardIndex);

    public void close()
    {
        setActive(false);
        buttons.clear();
    }

    abstract PCLCardRewardActionButton getButton(int index);

    abstract Class<? extends CardRewardActionProvider> getTargetClass();

    protected void onCardAdded(AbstractCard card)
    {
        if (onCardAdded != null)
        {
            onCardAdded.invoke(card);
        }
    }

    protected void onCardReroll(AbstractCard card)
    {
        if (onCardAct != null)
        {
            onCardAct.invoke(card);
        }
    }

    public void open(RewardItem rItem, ArrayList<AbstractCard> cards, boolean canActivate)
    {
        buttons.clear();
        rewardItem = rItem;
        isActive = false;

        if (canActivate)
        {
            actionProvider = GameUtilities.getRelic(getTargetClass());
            if (actionProvider != null && actionProvider.canActivate(rItem))
            {
                isActive = true;

                for (int i = 0; i < rItem.cards.size(); i++)
                {
                    buttons.add(getButton(i));
                }
            }
        }
    }

    protected void takeReward()
    {
        AbstractDungeon.combatRewardScreen.rewards.remove(rewardItem);
        AbstractDungeon.combatRewardScreen.positionRewards();
        if (AbstractDungeon.combatRewardScreen.rewards.isEmpty())
        {
            AbstractDungeon.combatRewardScreen.hasTakenAll = true;
            AbstractDungeon.overlayMenu.proceedButton.show();
        }
        shouldClose = true;
    }

    @Override
    public void updateImpl()
    {
        for (PCLCardRewardActionButton banButton : buttons)
        {
            banButton.tryUpdate();
        }
        if (shouldClose)
        {
            shouldClose = false;
            AbstractDungeon.closeCurrentScreen();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        for (PCLCardRewardActionButton banButton : buttons)
        {
            banButton.tryRender(sb);
        }
    }
}
