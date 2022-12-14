package pinacolada.skills.skills.special.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.utilities.GameUtilities;

// Only used for augments
public class PTrait_Affinity extends PTrait implements Hidden
{

    public static final PSkillData DATA = register(PTrait_Affinity.class, PCLEffectType.Affinity);

    public PTrait_Affinity()
    {
        this(1, (PCLAffinity) null);
    }

    public PTrait_Affinity(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_Affinity(PCLAffinity... affinities)
    {
        this(1, affinities);
    }

    public PTrait_Affinity(int amount, PCLAffinity... affinities)
    {
        super(DATA, amount, affinities);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        for (PCLAffinity af : affinities)
        {
            GameUtilities.modifyAffinityLevel(c, af, conditionMet ? amount : -amount, true);
        }
        if (c instanceof PCLCard)
        {
            ((PCLCard) c).affinities.updateSortedList();
        }
    }

    @Override
    public String getSubDescText()
    {
        return getAffinityAndString();
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.affinityGeneral.title;
    }
}
