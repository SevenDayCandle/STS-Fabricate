package pinacolada.cards.pcl.replacement;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

public class Madness extends PCLCard
{
    public static final PCLCardData DATA = register(Madness.class)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Star)
            .setTags(PCLCardTag.Haste.make(0, -1))
            .setColorless();

    public Madness()
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
        PCLActions.bottom.selectFromPile(name, player.hand.size(), player.hand)
                .setOptions(true, true)
                .addCallback(cards -> {
                    int totalCost = 0;
                    for (AbstractCard card : cards)
                    {
                        if (card.costForTurn >= 0)
                        {
                            totalCost += card.costForTurn;
                        }
                    }
                    for (AbstractCard card : cards)
                    {
                        if (card.costForTurn >= 0)
                        {
                            int newCost = MathUtils.random(0, Math.min(totalCost, 3));
                            totalCost -= newCost;
                            GameUtilities.modifyCostForCombat(card, newCost, false);
                            card.flash();
                        }
                    }
                });
    }
}