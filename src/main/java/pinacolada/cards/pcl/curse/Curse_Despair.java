package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.fields.PField_CardGeneric;

@VisibleCard
public class Curse_Despair extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Despair.class)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Despair()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onDraw(), PMove.gain(2, PCLPowerHelper.Shackles));
        addUseMove(PCond.onExhaust(), PMove.modifyCost(1, -1, PCLCardGroupHelper.Hand).edit(PField_CardGeneric::setRandom));
    }
}