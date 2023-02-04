package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.moves.PMove_DealDamage;

@VisibleCard
public class Curse_Decay extends PCLCard
{
    public static final String ATLAS_URL = "curse/decay";
    public static final PCLCardData DATA = register(Curse_Decay.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Decay()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onTurnEnd(), new PMove_DealDamage(2, PCLAttackVFX.POISON, PCLCardTarget.Self));
    }
}