package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.base.modifiers.PMod_PerCard;
import pinacolada.skills.skills.base.moves.PMove_GainTempHP;
import pinacolada.skills.skills.base.moves.PMove_LoseHP;

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