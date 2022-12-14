package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.modifiers.PMod_SelectPerCard;

public class Curse_Torment extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Torment.class)
            .setCurse(1, PCLCardTarget.None, false)
            .setTags(PCLCardTag.Exhaust)
            .setAffinities(PCLAffinity.Dark);

    public Curse_Torment()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), new PMod_SelectPerCard(1, PCLCardGroupHelper.Hand).setCardTypes(CardType.CURSE), PMove.obtainDiscardPile(1).useParent(true));
    }
}