package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.modifiers.PMod_SelectPerCard;

@VisibleCard
public class Curse_Torment extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Torment.class)
            .setCurse(1, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Torment()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), new PMod_SelectPerCard(1, PCLCardGroupHelper.Hand).edit(f -> f.setType(CardType.CURSE).setRandom()), PMove.obtainDiscardPile(1).useParent(true));
    }
}