package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PMove;

@VisibleCard
public class CalculatedGamble extends PCLCard
{
    public static final String ATLAS_URL = "green/skill/calculatedgamble";
    public static final PCLCardData DATA = register(CalculatedGamble.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Blue, PCLAffinity.Orange)
            .setRTags(PCLCardTag.Exhaust)
            .setColorless();

    public CalculatedGamble()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PMove.cycle(0));
    }
}