package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.skills.special.moves.PMove_StealTempHP;

@VisibleCard
public class Curse_Parasite extends PCLCard
{
    public static final String ATLAS_URL = "curse/parasite";
    public static final PCLCardData DATA = register(Curse_Parasite.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setCurse(-2, PCLCardTarget.None, false, true)
            .setTags(PCLCardTag.Unplayable)
            .setAffinities(PCLAffinity.Purple);

    public Curse_Parasite()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addUseMove(PCond.onCreate(), PMove.gain(3, PCLPowerHelper.DelayedDamage));
        addUseMove(PCond.onExhaust(), new PMove_StealTempHP(2, AbstractGameAction.AttackEffect.NONE, PCLCardTarget.RandomEnemy));
    }
}