package pinacolada.actions.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.GridCardSelectScreenHelper;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.PCLMultiCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.cards.pcl.special.MysteryCard;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ChooseMulticardAction extends PCLAction<PCLMultiCard>
{
    protected final ArrayList<AbstractCard> selectedCards = new ArrayList<>();
    private final PCLMultiCard multicard;

    public ChooseMulticardAction(PCLMultiCard multicard, int amount)
    {
        super(ActionType.CARD_MANIPULATION, Settings.ACTION_DUR_FAST);

        this.multicard = multicard;
        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        GridCardSelectScreenHelper.clear(true);

        CardGroup cardGroupPlaceholder = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

        for (int i = 0; i < amount; i++) {
            MysteryCard placeholder = new MysteryCard(true);
            placeholder.isSeen = true;
            cardGroupPlaceholder.addToBottom(placeholder);
        }
        GridCardSelectScreenHelper.addGroup(cardGroupPlaceholder);


        CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.MASTER_DECK);
        for (AbstractCard c : player.masterDeck.getPurgeableCards().group)
        {
            if (!isBanned(c))
            {
                cardGroup.addToBottom(c);
            }
        }
        GridCardSelectScreenHelper.addGroup(cardGroup);
        CardGroup mergedGroup = GridCardSelectScreenHelper.getCardGroup();

        AbstractDungeon.gridSelectScreen.open(mergedGroup, amount, PGR.core.strings.grid_chooseCards(amount), false, false , false, true);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() >= 2)
        {
            for (AbstractCard card : AbstractDungeon.gridSelectScreen.selectedCards)
            {
                AbstractDungeon.player.masterDeck.removeCard(card);
                AbstractCard newCard = card.makeCopy();
                multicard.addInheritedCard(newCard);
            }

            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            GridCardSelectScreenHelper.clear(true);
            multicard.initializeDescription();
            complete(multicard);
        }
    }

    public boolean isBanned(AbstractCard c) {
        return c.cost < 0
                || c.purgeOnUse
                || PCLCardTag.Fleeting.has(c)
                || c instanceof PCLMultiCard
                || multicard.isBanned(c);
    }
}
