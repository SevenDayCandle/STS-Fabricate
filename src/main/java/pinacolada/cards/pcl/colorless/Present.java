package pinacolada.cards.pcl.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

@VisibleCard
public class Present extends PCLCard
{
    public static final PCLCardData DATA = register(Present.class)
            .setSkill(1, CardRarity.RARE, PCLCardTarget.Single)
            .setRTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Star)
            .setColorless();

    public Present()
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
        AbstractCard random = GameUtilities.getRandomCard();
        if (random != null)
        {
            PCLActions.bottom.playCard(random, info.target)
                    .addCallback(() -> PCLActions.bottom.makeCardInDiscardPile(random.makeStatEquivalentCopy()));
        }
    }
}