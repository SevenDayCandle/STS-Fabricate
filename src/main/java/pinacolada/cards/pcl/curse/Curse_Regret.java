package pinacolada.cards.pcl.curse;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.modifiers.PMod_PerCard;
import pinacolada.skills.skills.base.moves.PMove_GainTempHP;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

@VisibleCard
public class Curse_Regret extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Regret.class)
            .setCurse(-2, PCLCardTarget.None, false, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Regret()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onTurnEnd(), new PMod_PerCard(1, PCLCardGroupHelper.Hand), new PMove_LoseHP(1));
        addUseMove(PCond.onExhaust(), new PMove_GainTempHP(2));
    }
}