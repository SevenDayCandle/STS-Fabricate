package pinacolada.actions.piles;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PlayFromPile extends SelectFromPile {
    public PlayFromPile(String sourceName, int amount, CardGroup... groups) {
        this(sourceName, null, amount, groups);
    }

    public PlayFromPile(String sourceName, AbstractCreature target, int amount, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, groups);
    }

    public PlayFromPile(String sourceName, int amount, PCLCardSelection origin, CardGroup... groups) {
        this(sourceName, null, amount, origin, groups);
    }

    public PlayFromPile(String sourceName, AbstractCreature target, int amount, PCLCardSelection origin, CardGroup... groups) {
        super(ActionType.CARD_MANIPULATION, sourceName, target, amount, origin, groups);
    }

    @Override
    protected void addCard(CardGroup group, AbstractCard card) {
        super.addCard(group, card);

        card.calculateCardDamage(GameUtilities.asMonster(target));
    }

    @Override
    protected void complete(ArrayList<AbstractCard> result) {
        for (AbstractCard card : result) {
            PCLActions.top.playCard(card, target)
                    .setSourcePile(GameUtilities.findCardGroup(card, false));
        }

        super.complete(result);
    }

    @Override
    protected void completeImpl() {
        super.completeImpl();

        for (CardGroup group : groups) {
            if (group.type != CardGroup.CardGroupType.HAND) {
                for (AbstractCard card : group.group) {
                    card.resetAttributes();
                }
            }
        }
    }

    @Override
    public String updateMessage() {
        return super.updateMessageInternal(CardRewardScreen.TEXT[0]);
    }
}
