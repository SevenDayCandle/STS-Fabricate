package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PLimit;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.PMultiSkill;
import pinacolada.skills.skills.base.moves.PMove_GainEnergy;

@VisibleCard
public class Curse_Devastation extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Devastation.class)
            .setCurse(-2, PCLCardTarget.None, false, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Devastation()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onTurnEnd(), PMultiSkill.join(PMove.selfExhaust(), PMove.gain(2, PCLPowerHelper.SelfImmolation)));
        addUseMove(PLimit.limited(), PCond.onPurge(), new PMove_GainEnergy(2));
    }
}