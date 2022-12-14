package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.base.modifiers.PMod_IncreaseOnUse;
import pinacolada.skills.skills.base.moves.PMove_StealGold;

public class Curse_Avarice extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Avarice.class)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Dark);

    public Curse_Avarice()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), new PMove_StealGold(PCLCardTarget.RandomEnemy, 3), new PMod_IncreaseOnUse(1), PMove.takeDamage(2));
    }
}