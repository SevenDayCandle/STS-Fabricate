package pinacolada.cards.pcl.colorless;

import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;

@VisibleCard
public class DarkShackles extends PCLCard
{
    public static final String ATLAS_URL = "colorless/skill/darkshackles";
    public static final PCLCardData DATA = register(DarkShackles.class)
            .setImagePathFromAtlasUrl(ATLAS_URL)
            .setSkill(0, CardRarity.UNCOMMON, PCLCardTarget.Single)
            .setAffinities(PCLAffinity.Purple)
            .setTags(PCLCardTag.Exhaust)
            .setColorless();

    public DarkShackles()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(PMove.applyTemporaryToSingle(-9, PCLPowerHelper.Strength).setUpgrade(-6));
    }
}