package pinacolada.cards.pcl.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.utilities.GenericCondition;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

@VisibleCard
public class Discovery extends PCLCard
{
    public static final PCLCardData DATA = register(Discovery.class)
            .setSkill(1, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setRTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Blue)
            .setColorless();

    public Discovery()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addSpecialMove(0, this::action, 1);
    }

    public void action(PSpecialSkill move, PCLUseInfo info)
    {
        CardGroup choices = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        RandomizedList<AbstractCard> unseenCards = new RandomizedList<>(GameUtilities.getAvailableCards(GenericCondition.fromT1(c -> !c.isSeen && GameUtilities.isObtainableInCombat(c))));
        if (unseenCards.size() < move.amount)
        {
            unseenCards.addAll(GameUtilities.getAvailableCards(GenericCondition.fromT1(c -> c.isSeen)));
        }

        while (choices.size() < move.amount && !unseenCards.isEmpty())
        {
            choices.addToBottom(unseenCards.retrieve(rng, true));
        }

        PCLActions.bottom.selectFromPile(getName(), 1, choices)
                .addCallback((cards) -> {
                    for (AbstractCard c : cards)
                    {
                        AbstractCard copy = c.makeStatEquivalentCopy();
                        GameUtilities.modifyCostForTurn(copy, 0, false);
                        PCLActions.bottom.makeCardInHand(copy);
                    }
                });
    }
}